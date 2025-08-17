package utils;

import java.io.FileInputStream;
import java.util.Properties;
public class ConfigReader {
    private static Properties prop;

    static {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config/browser.properties");prop = new Properties();
            prop.load(fis);
        } catch (Exception e) {
            throw new RuntimeException("读取浏览器配置文件失败：" + e.getMessage());
        }
    }

    public static String getDefaultBrowser() {
        return prop.getProperty("default.browser");
    }

    public static String getChromeDriverPath() {
        return prop.getProperty("chrome.driver.path");
    }

    public static String getIEDriverPath() {
        return prop.getProperty("ie.driver.path");
    }
}