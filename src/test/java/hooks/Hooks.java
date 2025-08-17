package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import listener.CucumberListener;
import org.openqa.selenium.WebDriver;
import utils.MyDriverManager;


public class Hooks {
    private static WebDriver driver;
    @Before
    public void setup(Scenario scenario) {
        driver = MyDriverManager.getDriver();

        if (driver == null) {
            throw new RuntimeException("Driver初始化失败，为 null！");
        }
        CucumberListener.setDriver(driver);
        System.out.println("Driver 注入监听器成功，实例：" + driver);
    }


    @After
    public void teardown(Scenario scenario) {
        if (driver != null) {
            try {
                Thread.sleep(3000);
                System.out.println("延迟 3 秒后关闭 Driver");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            driver = null;
            MyDriverManager.quitDriver();
            CucumberListener.setDriver(null);
        }
    }
}
