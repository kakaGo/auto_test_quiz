package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;


public class MyDriverManager {
    private static WebDriver driver ;
    private MyDriverManager() {
    }

    public static void initializeDriver() {

            String defaultBrowser = ConfigReader.getDefaultBrowser();
            BrowserType browserType = BrowserType.valueOf(defaultBrowser.toUpperCase());
            switch (browserType){
                case EDGE_IE_MODE:
                    //TODO 上传代码把注释解开，第二行注释掉，自动获取驱动
//                    WebDriverManager.iedriver().setup();
                    System.setProperty("webdriver.ie.driver", ConfigReader.getIEDriverPath());
                    InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                    //启用 Edge Chromium 内核（IE Mode 关键开关）
                    ieOptions.setCapability("ie.edgechromium", true);
                    ieOptions.setCapability("ie.edgepath", "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe");
                    ieOptions.setCapability("ignoreProtectedModeSettings", true);
                    ieOptions.setCapability("ignoreZoomSetting", true);
                    ieOptions.setCapability("browserAttachTimeout", 5000);
                    ieOptions.setCapability("ie.forceCreateProcessApi", true);
                    ieOptions.setCapability("ie.forceShellWindowsApi", true);
                    driver= new InternetExplorerDriver(ieOptions);
                    driver.manage().window().maximize();
                    break;
                case CHROME:
                default:
                    //TODO 上传代码把注释解开，第二行注释掉，自动获取驱动
//        WebDriverManager.chromedriver().setup();
                    System.setProperty("webdriver.chrome.driver", ConfigReader.getChromeDriverPath());
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--start-maximized");
                    driver = new ChromeDriver(options);
                    break;
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
