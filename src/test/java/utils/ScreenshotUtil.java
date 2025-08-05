package utils;

import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * 截图工具类，用于在测试过程中捕获屏幕截图
 */
public class ScreenshotUtil {

    /**
     * 捕获当前页面截图并附加到Cucumber报告
     * @param scenario Cucumber场景对象
     */
    public static void captureStepScreenshot(Scenario scenario, String stepName) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                // 截图描述包含步骤名称，便于报告关联
                String description = "步骤截图: " + (stepName != null ? stepName : "当前步骤");
                scenario.attach(screenshot, "image/png", description);
            }
        } catch (Exception e) {
            scenario.log("步骤截图失败: " + e.getMessage());
        }
    }
}
