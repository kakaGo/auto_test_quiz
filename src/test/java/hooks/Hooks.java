package hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import listener.CucumberListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.MyDriverManager;

/**
 * @ClassName Hooks
 * @Description rr
 * @Author 83622
 * @Date 2025/8/16 16:35
 */
public class Hooks {
    private static WebDriver driver;
    @Before
    public void setup(Scenario scenario) {
        driver = MyDriverManager.getDriver();
        // 验证 Driver 有效性（关键：确保注入的 Driver 可正常使用）
        if (driver == null) {
            throw new RuntimeException("Hooks.setup：Driver 初始化失败，为 null！");
        }
        CucumberListener.setDriver(driver);
        System.out.println("Hooks.setup：Driver 注入监听器成功，实例：" + driver);
    }


    @After
    public void teardown(Scenario scenario) {
        if (driver != null) {
            // 延长延迟至 3 秒，确保所有步骤截图完成
            try {
                Thread.sleep(3000);
                System.out.println("Hooks.teardown：延迟 3 秒后关闭 Driver");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            driver.quit();
            CucumberListener.setDriver(null);
            driver = null; // 清空引用，避免复用已关闭的 Driver
        }
    }

    // 提供获取 Driver 的方法（供步骤类验证）
    public static WebDriver getDriver() {
        return driver;
    }
}
