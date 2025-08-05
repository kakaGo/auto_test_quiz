package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class DriverManager {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static BrowserType browserType;

    // 本地驱动存放路径（根据实际项目结构调整）
    private static final String DRIVER_PATH = "src/test/resources/drivers/";

    static {
        String browser = System.getProperty("browser", "chrome");
        browserType = BrowserType.fromString(browser);
    }

    private DriverManager() {}

    public static void initializeDriver() {
        if (driver.get() == null) {
            WebDriver webDriver = null;
            String os = System.getProperty("os.name").toLowerCase();

            // 根据操作系统拼接驱动文件名（Windows为.exe，Mac/Linux为无后缀）
            String driverSuffix = os.contains("win") ? ".exe" : "";

            switch (browserType) {
                case EDGE_IE_MODE:
                    // 配置本地Edge驱动路径
                    String edgeDriverPath = DRIVER_PATH + "msedgedriver" + driverSuffix;
                    System.setProperty("webdriver.edge.driver", edgeDriverPath);

                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.setCapability("ie.edgechromium", true);
                    edgeOptions.setCapability("ie.ensureCleanSession", true);
                    edgeOptions.setCapability("ignoreZoomSetting", true);
                    webDriver = new EdgeDriver(edgeOptions);
                    break;

                case CHROME:
                default:
                    // 配置 Chrome 驱动路径
                    String chromeDriverPath = DRIVER_PATH + "chromedriver" + driverSuffix;
                    System.setProperty("webdriver.chrome.driver", chromeDriverPath);

                    // 关键：添加修复标签页崩溃的参数
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    // 禁用沙箱（解决权限相关的崩溃）
                    chromeOptions.addArguments("--no-sandbox");
                    // 禁用GPU加速（解决图形相关的崩溃）
                    chromeOptions.addArguments("--disable-gpu");
                    // 禁用渲染进程限制（避免资源限制导致崩溃）
                    chromeOptions.addArguments("--disable-renderer-backgrounding");
                    // 禁用扩展（避免扩展冲突）
                    chromeOptions.addArguments("--disable-extensions");

                    webDriver = new ChromeDriver(chromeOptions);
                    break;

            }

            driver.set(webDriver);
        }
    }

    // 其他方法保持不变
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

    public static String getBrowserType() {
        return browserType.name();
    }
}
