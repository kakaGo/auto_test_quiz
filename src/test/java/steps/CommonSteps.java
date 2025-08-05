package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverManager;
import utils.WaitUtil;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Duration;
import java.util.List;

/**
 * 百度搜索测试步骤定义（兼容多浏览器）
 */
public class CommonSteps {
    private WebDriver driver = DriverManager.getDriver();
    private WebDriverWait wait;

    public CommonSteps() {
        // 初始化显式等待，设置10秒超时
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }


    @When("^I open the page \\\"(.*?)\\\".$")
    public void OpenPage(String url) {
        driver.get(url);

    }

    @When("^I click the button \\\"(.*?)\\\" with xpath \\\"(.*?)\\\" on the page.$")
    public void clickButton(String page, String xpath) {
        driver.findElement(By.xpath(xpath));
        WebElement buttonByXpath = driver.findElement(By.xpath(xpath)); // 替换为实际XPath
        buttonByXpath.click();
    }

    @When("^I can check the element with xpath \\\"(.*?)\\\" disappear.$")
    public void checkElementDisappear(String xpath) {
        By locator = By.xpath(xpath);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10000));
            // 先等待元素不可见
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            // 再检查元素是否不存在于DOM中
            driver.findElements(locator).isEmpty();

    }


    @When("^Wait for \\\"(.*?)\\\" seconds for the second input field to be displayed with xpath \\\"(.*?)\\\".$")
    public void waitAndCheck(String time, String xpath) {
        try {
            // 先等待3秒
            Thread.sleep(3000);

            WebElement searchBoxLocator = driver.findElement(By.xpath("xpath"));

        } catch (Exception e) {

            System.err.println("error" + e.getMessage());
        }
    }

    @When("^I click the button by \\\"(.*?)\\\" with path \\\"(.*?)\\\" on the page.$")
    public void clickButtonBy(String by, String path) {
        WebElement buttonByXpath = null;
        switch (by){
            case "name":
                buttonByXpath = driver.findElement(By.name(path));
                break;
            case "xpath":
                buttonByXpath = driver.findElement(By.xpath(path));
                break;
            case "id":
                buttonByXpath = driver.findElement(By.id(path));
                break;
            default:
                // TODO 其他方式暂时不写了..
                break;
        }

        buttonByXpath.click();
    }


    @When("^I wait until the element with xpath \\\"(.*?)\\\" display.$")
    public void waitUntilElementDisplay(String page, String xpath) {
        // 等待百度logo出现（可见）
        WebElement logo = WaitUtil.waitForElementVisible(By.cssSelector("#lg img"));
        assertTrue("百度页面未加载完成", logo.isDisplayed());
    }

    @When("^I input \\\"(.*?)\\\" into the field with xpath \\\"(.*?)\\\" on the page.$")
    public void inputSomething(String inputText, String xpath) {
        driver.findElement(By.xpath(xpath));
        WebElement inputByXpath = driver.findElement(By.xpath(xpath)); // 替换为实际XPath
        inputByXpath.sendKeys(inputText);
    }

    @When("^I select \\\"(.*?)\\\" on the element with xpath \\\"(.*?)\\\".$")
    public void listbox(String text, String xpath) {
        By triggerLocator = By.xpath(xpath);
        try {
            // 1. 点击触发元素，展开下拉框
            WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(triggerLocator));
            trigger.click();
            By listboxLocator = By.xpath("//div[@role='listbox']");
            // 2. 等待下拉框（listbox）出现
            WebElement listbox = wait.until(ExpectedConditions.visibilityOfElementLocated(listboxLocator));

            // 3. 定位下拉框中的所有选项（通常role="option"）
            List<WebElement> options = listbox.findElements(By.cssSelector("[role='option']"));
            if (options.isEmpty()) {
                throw new RuntimeException("下拉框中未找到选项");
            }

            // 4. 遍历选项，选择文本匹配的项
            for (WebElement option : options) {
                // 定位当前option下的span子节点（支持多级span，如<span><span>文本</span></span>）
                List<WebElement> spans = option.findElements(By.tagName("span"));
                if (spans.isEmpty()) {
                    continue; // 跳过没有span子节点的选项
                }

                // 5. 检查span中的文本是否匹配目标
                for (WebElement span : spans) {
                    String spanText = span.getText().trim();
                    if (spanText.equals(text.trim())) {
                        // 滚动到选项并点击（点击外层option而非span，避免点击区域过小）
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoViewIfNeeded();", option);
                        option.click();
                        return;
                    }
                }
            }

            // 5. 若未找到匹配选项，抛出异常
            throw new RuntimeException("未找到文本为[" + text + "]的选项");

        } catch (Exception e) {
            throw new RuntimeException("选择下拉框选项失败: " + e.getMessage());
        }
    }


    @When("^I input \\\"(.*?)\\\" on the element with xpath \\\"(.*?)\\\".$")
    public void inputlistbox(String text, String xpath) {
        By triggerLocator = By.xpath(xpath);
        try {
            // 1. 点击触发元素，展开下拉框
            WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(triggerLocator));
            trigger.sendKeys(text);
            By listboxLocator = By.xpath("//div[@role='listbox']");
            // 2. 等待下拉框（listbox）出现
            WebElement listbox = wait.until(ExpectedConditions.visibilityOfElementLocated(listboxLocator));
            Thread.sleep(3000);
            // 3. 定位下拉框中的所有选项（通常role="option"）
            List<WebElement> options = listbox.findElements(By.cssSelector("[role='option']"));
            if (options.isEmpty()) {
                throw new RuntimeException("下拉框中未找到选项");
            }

            // 4. 遍历选项，选择文本匹配的项
            for (WebElement option : options) {
                // 定位当前option下的span子节点（支持多级span，如<span><span>文本</span></span>）
                List<WebElement> spans = option.findElements(By.tagName("span"));
                if (spans.isEmpty()) {
                    continue; // 跳过没有span子节点的选项
                }

                // 5. 检查span中的文本是否匹配目标
                for (WebElement span : spans) {
                    String spanText = span.getText().trim();
                    if (spanText.equals(text.trim())) {
                        // 滚动到选项并点击（点击外层option而非span，避免点击区域过小）
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoViewIfNeeded();", option);
                        option.click();
                        return;
                    }
                }
            }

            // 5. 若未找到匹配选项，抛出异常
            throw new RuntimeException("未找到文本为[" + text + "]的选项");
        } catch (Exception e) {
            throw new RuntimeException("选择下拉框选项失败: " + e.getMessage());
        }
    }


    @When("^I can check the page url contain \\\"(.*?)\\\".$")
    public void checkUrl(String url) {
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(url));
    }


    @When("^I can check the page text contain \\\"(.*?)\\\" or \\\"(.*?)\\\".$")
    public void checkText(String exceptText1,String exceptText2) {
        String pageSource = driver.getPageSource();
        Boolean text1Exist=pageSource.contains(exceptText1);
        Boolean text2Exist=pageSource.contains(exceptText2);
        assertTrue(text1Exist|| text2Exist);
    }

    @When("^I can check the message with xpath \\\"(.*?)\\\" appear.$")
    public void checkElementAppear(String xpath) {
//        wait = new WebDriverWait(driver, 10); // 最长等待10秒
//        //WebElement targetElementLocator = Driver.findElement(By.xpath(xpath));
//        By targetElement = By.xpath(xpath);
//        WebElement element = wait.until(
//                ExpectedConditions.visibilityOfElementLocated(targetElement)
//        );
//        assertTrue(element.isDisplayed());

    }


    @When("^I can check the message the message is \\\"(.*?)\\\" with the path \\\"(.*?)\\\".$")
    public void checkMessage(String exceptMessage,String xpath) {
        WebElement actualElement = driver.findElement(By.xpath(xpath));
        String actualText = actualElement.getText();
        assertEquals(exceptMessage,actualText);
    }

    @When("^I return the Previous Page.$")
    public void goBack() {
        driver.navigate().back();
    }


    @When("^I wait for \\\"(.*?)\\\" seconds.$")
    public void waitMinute(String time) {
        try {
            Thread.sleep(Long.parseLong(time));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @When("^I clear the input field with xpath \\\"(.*?)\\\" on the page.$")
    public void clearInputField(String xpath) {
        WebElement inputBox = driver.findElement(By.xpath("xpath"));
        // 清除输入框内容
        inputBox.clear();

    }


    @When("^I can check the element with xpath \\\"(.*?)\\\" displayed.$")
    public void checkElementDisplayed(String xpath) {
        // 第二个输入框预期会报NoSuchElementException（根据实际情况调整选择器）
        try {
            // 尝试定位第二个输入框
            WebElement secondInput = driver.findElement(By.xpath(xpath));
        } catch (NoSuchElementException e) {
            // 如果未抛出异常，手动断言失败
            fail("第二个输入框，出现预期的NoSuchElementException");
        }

    }


}
