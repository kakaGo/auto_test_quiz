package listener;

// 在 CucumberListener 类顶部添加导入
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.cucumber.plugin.event.Status; // 确保 Status 也导入了，否则 FAILED 会报错
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.MyDriverManager;

import java.io.ByteArrayInputStream;
import java.io.File;


public class CucumberListener implements ConcurrentEventListener {
    // 1. 定义 WebDriver 成员变量（用于截图）
    private static WebDriver driver;

    // 2. 添加 setDriver 方法：供外部注入 WebDriver 实例
    public static void setDriver(WebDriver webDriver) {
        driver = webDriver;
        // 打印 Driver 状态（是否为 null 或已关闭）
        if (driver == null) {
            System.out.println("CucumberListener.setDriver：注入的 Driver 为 null");
        } else {
            try {
                // 尝试获取当前 URL 验证 Driver 是否存活
                String url = driver.getCurrentUrl();
                System.out.println("CucumberListener.setDriver：注入的 Driver 存活，当前 URL：" + url);
            } catch (Exception e) {
                System.out.println("CucumberListener.setDriver：注入的 Driver 已失效：" + e.getMessage());
            }
        }
    }


    // 3. 初始化监听器（注册事件回调）
    @Override
    public void setEventPublisher(EventPublisher publisher) {
        // 监听「步骤执行后」事件（无论成功/失败，都触发截图）
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
    }

    private void handleTestStepFinished(TestStepFinished event) {
        // 1. 再次验证 Driver 状态（截图前最后检查）
        if (driver == null) {
            System.err.println("handleTestStepFinished：driver 为 null，无法截图");
            return;
        }
        try {
            // 验证 Driver 是否存活（通过执行简单操作）
            driver.getTitle(); // 若 Driver 已关闭，会抛出异常
        } catch (Exception e) {
            System.err.println("handleTestStepFinished：driver 已失效，无法截图：" + e.getMessage());
            return;
        }

        TestStep testStep = event.getTestStep();
        Result result = event.getResult();
        boolean isFailed = result.getStatus() == Status.FAILED;

        if (testStep instanceof PickleStepTestStep) {
            PickleStepTestStep businessStep = (PickleStepTestStep) testStep;
            String stepName = businessStep.getStep().getText();
            System.out.println("处理业务步骤：" + stepName + "，开始截图...");

            // 关键新增：用 Allure.step 标记当前步骤上下文（让截图绑定到这个步骤）
            Allure.step("执行步骤：" + stepName, () -> {
                try {
                    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    if (screenshotBytes != null && screenshotBytes.length > 0) {
                        // 在步骤上下文内添加截图，截图会紧跟该步骤
                        Allure.addAttachment(
                                stepName + " - 截图",
                                "image/png",
                                new ByteArrayInputStream(screenshotBytes),
                                "png"
                        );
                        System.out.println("步骤 [" + stepName + "] 截图已绑定到当前步骤");
                    }
                } catch (Exception e) {
                    System.err.println("步骤 [" + stepName + "] 截图绑定失败：" + e.getMessage());
                }
            });
            // 附加失败日志（不变）
            if (isFailed && result.getError() != null) {
                attachFailureLogToAllure(result.getError());
            }
        }
    }

    // 保留 @Attachment 方法（无需修改）
    @Attachment(value = "{stepName} - 截图", type = "image/png")
    private byte[] attachScreenshotToAllure(String stepName, byte[] screenshotBytes, boolean isFailed) {
        if (screenshotBytes == null || screenshotBytes.length == 0) {
            System.err.println("attachScreenshotToAllure：截图字节流为空");
            return null;
        }
        return screenshotBytes;
    }
    /**
     * 附加失败日志到 Allure 报告
     */
    @Attachment(value = "失败日志", type = "text/plain")
    private String attachFailureLogToAllure(Throwable error) {
        // 转换异常栈信息为字符串
        StringBuilder errorLog = new StringBuilder();
        errorLog.append(error.getMessage()).append("\n");
        for (StackTraceElement stackTrace : error.getStackTrace()) {
            errorLog.append(stackTrace.toString()).append("\n");
        }
        return errorLog.toString();
    }
}
