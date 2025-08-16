package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.File;


public class MyDriverManager {
    private static WebDriver driver ;

    private MyDriverManager() {
    }

    public static void initializeDriver() {
        try {
        //TODO 上传代码把注释解开，第二行注释掉，自动获取驱动
//        WebDriverManager.chromedriver().setup();
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        // 配置Chrome选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-infobars");
//        options.addArguments("--headless=new"); // 无头模式（可选，测试更稳定）
        options.addArguments("--disable-gpu");

            driver = new ChromeDriver(options);

        System.out.println("MyDriverManager：Driver 初始化成功，实例：" + driver);

        // 验证 Driver 可截图（关键：提前发现截图能力问题）
        byte[] testScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        if (testScreenshot == null || testScreenshot.length == 0) {
            throw new RuntimeException("MyDriverManager：Driver 初始化成功，但不支持截图！");
        }
//        / 测试截图能力（生成1个测试PNG，直接存到result目录，验证Driver是否能生成文件）
            File testScreenshotlll = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File("target/allure-results/test-screenshot.png");
            FileUtils.copyFile(testScreenshotlll, destFile);
            System.out.println("测试截图已生成：" + destFile.getAbsolutePath());

    } catch (Exception e) {
        System.err.println("MyDriverManager：Driver 初始化失败：" + e.getMessage());
        throw new RuntimeException("Driver 初始化失败，无法继续测试", e);
    }
    }


    public static WebDriver getDriver() {
        if (driver == null) {
            initializeDriver();
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();

        }
    }


}
