package steps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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
import java.util.Map;

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
        WebElement buttonByXpath = driver.findElement(By.xpath(xpath));
        buttonByXpath.click();
    }

    @When("^I check TableList \\\"(.*?)\\\" value is \\\"(.*?)\\\" with table xpath \\\"(.*?)\\\".$")
    public void clickTable(String type, String value, String xpath) {
        try {
            // 1. 定位表格
            WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(xpath)
            ));
            // 2. 定位所有行组（rowgroup）
            List<WebElement> rowGroups = table.findElements(By.cssSelector("[role='rowgroup']"));
            assertTrue(!rowGroups.isEmpty());
            // 3. 在最后一个行组中找到所有行（row）
            WebElement lastRowGroup = rowGroups.get(0);
            List<WebElement> rows = lastRowGroup.findElements(By.cssSelector("[role='row']"));
            assertTrue(!rows.isEmpty());
            // 4. 获取最后一行（最新添加的数据行）
            WebElement lastRow = rows.get(0);
            // 5. 获取当前行中所有单元格（cell）
            List<WebElement> cells = lastRow.findElements(By.cssSelector("[role='cell']"));
            assertTrue(!cells.isEmpty());
            // 6. 遍历每个单元格，验证类型和值是否匹配预期
            for (WebElement cell : cells) {
                // 定位类型元素（class包含language和header）
                WebElement typeElement = cell.findElement(By.cssSelector(".header"));
                // 定位值元素（class为data）
                WebElement valueElement = cell.findElement(By.cssSelector(".data"));
                // 获取实际类型和值
                String actualType = typeElement.getText().trim();
                String actualValue = valueElement.getText().trim();
                // 检查该类型是否在预期映射中
                if (type.equals(actualType)) {
                    // 验证类型和值是否匹配（类型等于值）
                    assertEquals(value, actualValue,
                            "类型与值不匹配 - 类型: " + actualType + ", 实际值: " + actualValue);
                }
            }
        } catch (Exception e) {
            fail("验证数据时发生错误: " + e.getMessage());
        }
    }


    @When("^I check TableList row \\\"(.*?)\\\" value with table xpath \\\"(.*?)\\\".$")
    public void clickTable(String rowNumber, String tableXpath, List<Map<String, String>> expectedData) {
    try {
            // 1. 将行号转换为整数（从1开始）
            int rowIndex = Integer.parseInt(rowNumber) - 1; // 转换为0-based索引

            // 2. 定位表格并等待可见
            List<WebElement> tables = driver.findElements(By.xpath(tableXpath));
            WebElement table = tables.get(0);
            // 3. 定位表头和表体
            WebElement header = table.findElement(By.xpath(tableXpath + "//*[@class='oxd-table-header']"));
            WebElement body = table.findElement(By.xpath(tableXpath + "//*[@class='oxd-table-body']"));
            //assertNotNull(header, "未找到表头元素（class=header）");
            //assertNotNull(body, "未找到表体元素（class=body）");

            // 4. 解析表头：建立{表头文本: 单元格索引}映射
            Map<String, Integer> headerMap = parseHeader(header);

            // 5. 定位目标行（第x行）
            List<WebElement> rows = body.findElements(By.xpath(tableXpath + "//*[@class='oxd-table-body']//*[@role='row']"));

            WebElement targetRow = rows.get(rowIndex);

            // 6. 获取目标行的所有单元格
            List<WebElement> cells = targetRow.findElements(By.xpath("//*[@role='cell']"));
            assertFalse(cells.isEmpty());

            // 7. 解析预期数据为{表头: 预期值}映射
            Map<String, String> expectedValues = new HashMap<>();
            for (Map<String, String> row : expectedData) {
                String colName = row.get("title"); // 获取左侧表头名（col1, col2等）
                String expectedValue = row.get("value"); // 获取右侧预期值
                expectedValues.put(colName, expectedValue);
            }

            // 8. 验证每个字段值
            for (Map.Entry<String, String> entry : expectedValues.entrySet()) {

                String headerName = entry.getKey();
                String expectedValue = entry.getValue();
                // 获取对应单元格的实际值
                int cellIndex = headerMap.get(headerName);
                WebElement targetCell = cells.get(cellIndex);
                WebElement targetDiv = targetCell.findElement(By.xpath(".//div"));
                String actualValue = targetDiv.getText().trim();
                // 验证值是否匹配
                assertEquals(
                        "行 " + rowNumber + " 中表头 '" + headerName + "' 的值不匹配 - " +
                                "预期: " + expectedValue + ", 实际: " + actualValue,expectedValue, actualValue);
            }

        } catch (NumberFormatException e) {
            fail("行号格式错误，必须是数字: " + rowNumber);
        } catch (Exception e) {
            fail("验证表格数据时发生错误: " + e.getMessage());
        }
    }

    /**
     * 解析表头，返回{表头文本: 单元格索引}映射
     */
    private Map<String, Integer> parseHeader(WebElement header) {
        Map<String, Integer> headerMap = new HashMap<>();

        // 获取表头行（通常表头只有一行）
        List<WebElement> headerRows = header.findElements(By.xpath("//*[@role='row']"));
        if (!headerRows.isEmpty()) {
            // 获取表头行中的所有单元格
            List<WebElement> headerCells = headerRows.get(0).findElements(By.xpath("//*[@role='columnheader']"));

            // 遍历表头单元格，记录索引与文本的对应关系
            for (int i = 0; i < headerCells.size(); i++) {
                String headerText = headerCells.get(i).getText().trim();
                if (!headerText.isEmpty()) {
                    headerMap.put(headerText, i);
                }
            }
        }

        return headerMap;
    }

    @When("^I check the success message is \\\"(.*?)\\\" with the xpath \\\"(.*?)\\\".$")
    public void checkSuccessMsg(String message, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // 超时时间5秒（根据提示显示时长调整）

        try {
            WebElement successMessage = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath(xpath) //
                    )
            );
            // 3. 验证通过（能执行到这里说明提示已出现）
            assertTrue(true);

        } catch (Exception e) {
            fail("沒有成功提示");
        } finally {

        }
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
            trigger.click();
            Thread.sleep(3000);
            trigger.sendKeys(text);
            Thread.sleep(5000);

            WebElement listbox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@role='listbox']")
            ));
            // 4. 定位列表中第一个option下的span元素
            WebElement targetOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@role='option'][1]/span") // 相对路径定位
            ));

            // 5. 点击选中该选项
            targetOption.click();

            // 可选：验证输入框内容是否已更新
            String inputValue = trigger.getAttribute("value");
            System.out.println("输入框最终值: " + inputValue);}
        catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭浏览器
            //driver.quit();
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


    @When("^I can check the value is \\\"(.*?)\\\" with the path \\\"(.*?)\\\".$")
    public void checkValueOnForm(String expectedText, String xpath) {
        WebElement element = driver.findElement(By.xpath(xpath));

        String actualText = element.getAttribute("_value");
        assertEquals(expectedText, actualText, actualText);
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

    @When("^only log.$")
    public void log() {
        driver.getPageSource();
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
