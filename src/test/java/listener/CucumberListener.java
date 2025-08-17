package listener;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.ByteArrayInputStream;


public class CucumberListener implements ConcurrentEventListener {

    private static WebDriver driver;
    public static void setDriver(WebDriver webDriver) {
        driver = webDriver;
    }



    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
    }

    private void handleTestStepFinished(TestStepFinished event) {

        if (driver == null) {
            System.err.println("driver 为 null，无法截图");
            return;
        }
        try {
            driver.getTitle();
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

            Allure.step("执行步骤：" + stepName, () -> {
                try {
                    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    if (screenshotBytes != null && screenshotBytes.length > 0) {
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
            if (isFailed && result.getError() != null) {
                attachFailureLogToAllure(result.getError());
            }
        }
    }


    @Attachment(value = "{stepName} - 截图", type = "image/png")
    private byte[] attachScreenshotToAllure(String stepName, byte[] screenshotBytes, boolean isFailed) {
        if (screenshotBytes == null || screenshotBytes.length == 0) {
            System.err.println("attachScreenshotToAllure：截图字节流为空");
            return null;
        }
        return screenshotBytes;
    }

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
