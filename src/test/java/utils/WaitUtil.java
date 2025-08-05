package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * 等待工具类：封装元素等待相关方法
 */
public class WaitUtil {

    private static WebDriverWait wait;

    // 初始化等待对象（默认超时时间10秒，轮询间隔500毫秒）
    public static void init(WebDriver driver) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10), Duration.ofMillis(500));
    }

    /**
     * 等待元素出现（可见）
     * @param locator 元素定位器（如By.id("kw")）
     * @return 找到的元素
     */
    public static WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * 等待元素可点击
     * @param locator 元素定位器
     * @return 找到的元素
     */
    public static WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * 等待元素存在于DOM中（不一定可见）
     * @param locator 元素定位器
     * @return 找到的元素
     */
    public static WebElement waitForElementPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}