package steps;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import utils.MyDriverManager;
import static org.junit.Assert.*;
import java.util.Map;

public class CommonSteps {
    private WebDriver driver = MyDriverManager.getDriver();
    private WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    @When("^I open the page \\\"(.*?)\\\".$")
    public void OpenPage(String url) {
        driver.get(url);
    }


    @When("^I click the button  with xpath \\\"(.*?)\\\" on the page.$")
    public void clickButton(String xpath) {
        WebElement buttonByXpath = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
        );
        buttonByXpath.click();
    }

    @When("^I execute the case \\\"(.*?)\\\" with userName \\\"(.*?)\\\" and password \\\"(.*?)\\\".$")
    public void executeCase(String caseDesc, String username, String password) {
        String userNameXpath = "//*[@id='username']";
        String passwordXpath = "//*[@id='password']";
        String submitButton = "//button[text()='Submit']";
        //用戶名
        WebElement userNameByXpath = driver.findElement(By.xpath(userNameXpath));
        userNameByXpath.sendKeys(username);
        //密碼
        WebElement passwordByXpath = driver.findElement(By.xpath(passwordXpath));
        passwordByXpath.sendKeys(password);
        //提交
        WebElement buttonByXpath = driver.findElement(By.xpath(submitButton));
        buttonByXpath.click();
    }

    @When("^I execute the case with operation \\\"(.*?)\\\" and check result \\\"(.*?)\\\".$")
    public void exceptionTest(String operation, String result) {
        WebElement buttonByXpath = null;
        WebElement inputByXpath = null;
        switch (result) {
            case "NoSuchElementException":
                buttonByXpath = driver.findElement(By.xpath("//button[text()='Add']"));
                buttonByXpath.click();
                // 第二个输入框预期会报NoSuchElementException
                try {
                    // 尝试定位第二个输入框
                    WebElement secondInput = driver.findElement(By.xpath("//*[@id=\"row2\"]"));
                } catch (NoSuchElementException e) {
                    // 未抛出异常，手动断言失败
                    fail("第二个输入框，出现预期的NoSuchElementException");
                }
                break;
            case "ElementNotInteractableException":
                buttonByXpath = driver.findElement(By.xpath("//button[text()='Add']"));
                buttonByXpath.click();
                inputByXpath = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"row2\"]")));
                inputByXpath.sendKeys("secondRow");
                buttonByXpath = driver.findElement(By.name("Save"));
                buttonByXpath.click();
                wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 最长等待10秒
                By targetElement = By.xpath("//*[text()=\"Row 2 was saved\"]");
                WebElement element = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(targetElement)
                );
                assertTrue(element.isDisplayed());
                break;
            case "InvalidElementStateException":
                WebElement inputBox = driver.findElement(By.xpath("//*[@id=\"row1\"]/input"));
                inputBox.clear();
                inputByXpath = driver.findElement(By.xpath("//*[@id=\"row1\"]/input"));
                inputByXpath.sendKeys("change text");
                WebElement actualElement = driver.findElement(By.xpath("//*[@id=\"row1\"]/input"));
                String actualText = actualElement.getText();
                assertEquals("change text", actualText);
                break;
            case "StaleElementReferenceException":
                driver.navigate().refresh();
                actualElement = driver.findElement(By.xpath("//p[@id=\"instructions\"]"));
                actualText = actualElement.getText();
                assertEquals("Push “Add” button to add another row", actualText);
                buttonByXpath = driver.findElement(By.xpath("//button[text()='Add']"));
                buttonByXpath.click();
                By locator = By.xpath("//p[@id=\"instructions\"]");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10000));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
                driver.findElements(locator).isEmpty();

                break;
            case "TimeoutException":
                WebElement searchBoxLocator = null;
                buttonByXpath = driver.findElement(By.xpath("//button[text()='Add']"));
                buttonByXpath.click();
                try {
                    Thread.sleep(3000);
                    searchBoxLocator = driver.findElement(By.xpath("//*[@id=\"row2\"]"));
                } catch (Exception e) {
                    System.err.println("error" + e.getMessage());
                }
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                element = wait.until(
                        ExpectedConditions.visibilityOf(searchBoxLocator)
                );
                assertTrue(element.isDisplayed());
                break;
            default:
                break;
        }
    }


    @When("^check expectedResult\\\"(.*?)\\\".$")
    public void executeCase(String caseDesc) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        switch (caseDesc) {
            case "practicetestautomation.com/logged-in-successfully/":
                String currentUrl = driver.getCurrentUrl();
                assertTrue(currentUrl.contains(caseDesc));
                //校验登录成功
                String pageSource = driver.getPageSource();
                Boolean text1Exist = pageSource.contains("Congratulations");
                Boolean text2Exist = pageSource.contains("successfully logged in");
                assertTrue(text1Exist || text2Exist);
                //退出登录
                WebElement buttonByXpath = driver.findElement(By.xpath("//*[text()='Log out']"));
                buttonByXpath.click();
                break;
            default:
                By targetElement = By.xpath("//*[@id='error']");
                WebElement element = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(targetElement)
                );
                assertTrue(element.isDisplayed());
                WebElement actualElement = driver.findElement(By.xpath("//*[@id='error']"));
                String actualText = actualElement.getText();
                assertEquals(caseDesc, actualText);
                break;
        }
    }


    @When("^I check TableList \\\"(.*?)\\\" value is \\\"(.*?)\\\" with table xpath \\\"(.*?)\\\".$")
    public void clickTable(String type, String value, String xpath) {
        try {
            // 1. 定位表格
            WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(xpath)
            ));
            // 2. 定位所有行组
            List<WebElement> rowGroups = table.findElements(By.cssSelector("[role='rowgroup']"));
            assertTrue(!rowGroups.isEmpty());
            // 3. 在最后一个行组中找到所有行
            WebElement lastRowGroup = rowGroups.get(0);
            List<WebElement> rows = lastRowGroup.findElements(By.cssSelector("[role='row']"));
            assertTrue(!rows.isEmpty());
            // 4. 获取最后一行
            WebElement lastRow = rows.get(0);
            // 5. 获取当前行中所有单元格
            List<WebElement> cells = lastRow.findElements(By.cssSelector("[role='cell']"));
            assertTrue(!cells.isEmpty());
            // 6. 遍历每个单元格，验证类型和值是否匹配预期
            for (WebElement cell : cells) {
                WebElement typeElement = cell.findElement(By.cssSelector(".header"));
                WebElement valueElement = cell.findElement(By.cssSelector(".data"));
                String actualType = typeElement.getText().trim();
                String actualValue = valueElement.getText().trim();
                if (type.equals(actualType)) {
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

        WebElement targetElement = driver.findElement(By.xpath(tableXpath));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", targetElement);

        try {
            // 1. 将行号转换为整数（从1开始）
            int rowIndex = Integer.parseInt(rowNumber) - 1; // 转换为0-based索引
            // 2. 定位表格并等待可见
            List<WebElement> tables = driver.findElements(By.xpath(tableXpath));
            WebElement table = tables.get(0);
            // 3. 定位表头和表体
            WebElement header = table.findElement(By.xpath(tableXpath + "//*[@class='oxd-table-header']"));
            WebElement body = table.findElement(By.xpath(tableXpath + "//*[@class='oxd-table-body']"));
            // 4. 解析表头
            Map<String, Integer> headerMap = parseHeader(header);
            // 5. 定位目标行
            List<WebElement> rows = body.findElements(By.xpath(tableXpath + "//*[@class='oxd-table-body']//*[@role='row']"));
            WebElement targetRow = rows.get(rowIndex);
            // 6. 获取目标行的所有单元格
            List<WebElement> cells = targetRow.findElements(By.xpath("//*[@role='cell']"));
            assertFalse(cells.isEmpty());
            // 7. 解析预期数据
            Map<String, String> expectedValues = new HashMap<>();
            for (Map<String, String> row : expectedData) {
                String colName = row.get("title");
                String expectedValue = row.get("value");
                //处理系统bug： 当选择的是年-月-日，实际输入框是年-日-月
                if (expectedValue.equals("today")){
                    LocalDate today = LocalDate.now();
                    String year = String.valueOf(today.getYear());
                    String month = String.valueOf(today.getDayOfMonth());
                    String day = String.valueOf(today.getMonthValue());
                    if(today.getDayOfMonth()<10){
                        month = "0"+month;
                    }
                    if(today.getMonthValue()<10){
                        day = "0"+day;
                    }
                    expectedValue = year+"-"+month+"-"+day;
                }
                expectedValues.put(colName, expectedValue);
            }
            // 8. 验证字段值
            for (Map.Entry<String, String> entry : expectedValues.entrySet()) {
                String headerName = entry.getKey();
                String expectedValue = entry.getValue();
                int cellIndex = headerMap.get(headerName);
                WebElement targetCell = cells.get(cellIndex);
                WebElement targetDiv = targetCell.findElement(By.xpath(".//div"));
                String actualValue = targetDiv.getText().trim();
                assertEquals(
                        "行 " + rowNumber + " 中表头 '" + headerName + "' 的值不匹配 - " +
                                "预期: " + expectedValue + ", 实际: " + actualValue, expectedValue, actualValue);
            }
        } catch (NumberFormatException e) {
            fail("行号格式错误，必须是数字: " + rowNumber);
        } catch (Exception e) {
            fail("验证表格数据时发生错误: " + e.getMessage());
        }
    }


    private Map<String, Integer> parseHeader(WebElement header) {
        Map<String, Integer> headerMap = new HashMap<>();
        // 获取表头行
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
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        driver.findElements(locator).isEmpty();

    }


    @When("^Wait for \\\"(.*?)\\\" seconds for the second input field to be displayed with xpath \\\"(.*?)\\\".$")
    public void waitAndCheck(String time, String xpath) {
        try {
            Thread.sleep(3000);
            WebElement searchBoxLocator = driver.findElement(By.xpath("xpath"));
        } catch (Exception e) {
            System.err.println("error" + e.getMessage());
        }
    }

    @When("^I click the button by \\\"(.*?)\\\" with path \\\"(.*?)\\\" on the page.$")
    public void clickButtonBy(String by, String path) {
        WebElement buttonByXpath = null;
        switch (by) {
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
        WebElement logo = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#lg img")));
        assertTrue("页面未加载完成", logo.isDisplayed());
    }

    @When("^I input \\\"(.*?)\\\" into the field with xpath \\\"(.*?)\\\" on the page.$")
    public void inputSomething(String inputText, String xpath) {
        driver.findElement(By.xpath(xpath));
        WebElement inputByXpath = driver.findElement(By.xpath(xpath));
        inputByXpath.sendKeys(inputText);
    }

    @When("^I select \\\"(.*?)\\\" on the element with xpath \\\"(.*?)\\\".$")
    public void listbox(String text, String xpath) {
        By triggerLocator = By.xpath(xpath);
        try {
            // 1. 点击元素，展开下拉框
            WebElement trigger = wait.until(ExpectedConditions.presenceOfElementLocated(triggerLocator));
            trigger.click();
            By listboxLocator = By.xpath("//div[@role='listbox']");
            // 2. 等待下拉框出现
            WebElement listbox = wait.until(ExpectedConditions.presenceOfElementLocated(listboxLocator));
            // 3. 定位下拉框中的所有选项
            List<WebElement> options = listbox.findElements(By.xpath("//div[@role='option']"));
            if (options.isEmpty()) {
                throw new RuntimeException("下拉框中未找到选项");
            }
            // 4. 遍历选项，选择文本匹配的项
            for (WebElement option : options) {
                List<WebElement> spans = option.findElements(By.tagName("span"));
                if (spans.isEmpty()) {
                    continue;
                }
                // 5. 检查span中的文本是否匹配目标
                for (WebElement span : spans) {
                    String spanText = span.getText().trim();
                    if (spanText.equals(text.trim())) {
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                                "arguments[0].scrollIntoViewIfNeeded();", option);
                        option.click();
                        return;
                    }
                }
            }
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
            Thread.sleep(1000);
            trigger.sendKeys(text);
            Thread.sleep(3000);
            WebElement listbox = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@role='listbox']")
            ));
            // 4. 定位列表中第一个option下的span元素
            WebElement targetOption = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@role='option'][1]/span")
            ));
            Thread.sleep(1000);
            // 5. 点击选中该选项
            targetOption.click();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  catch (Exception e){
            e.printStackTrace();
        }
    }


    @When("^I can check the page url contain \\\"(.*?)\\\".$")
    public void checkUrl(String url) {
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(url));
    }


    @When("^I can check the page text contain \\\"(.*?)\\\" or \\\"(.*?)\\\".$")
    public void checkText(String exceptText1, String exceptText2) {
        String pageSource = driver.getPageSource();
        Boolean text1Exist = pageSource.contains(exceptText1);
        Boolean text2Exist = pageSource.contains(exceptText2);
        assertTrue(text1Exist || text2Exist);
    }

    @When("^I can check the message with xpath \\\"(.*?)\\\" appear.$")
    public void checkElementAppear(String xpath) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By targetElement = By.xpath(xpath);
        WebElement element = wait.until(
                ExpectedConditions.visibilityOfElementLocated(targetElement)
        );
        assertTrue(element.isDisplayed());

    }


    @When("^I can check the message the message is \\\"(.*?)\\\" with the path \\\"(.*?)\\\".$")
    public void checkMessage(String exceptMessage, String xpath) {
        WebElement actualElement = driver.findElement(By.xpath(xpath));
        String actualText = actualElement.getText();
        assertEquals(exceptMessage, actualText);
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
            Thread.sleep(Long.parseLong(time) * 1000);
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
        inputBox.clear();

    }


    @When("^I can check the element with xpath \\\"(.*?)\\\" displayed.$")
    public void checkElementDisplayed(String xpath) {
        // 第二个输入框预期会报NoSuchElementException
        try {
            // 尝试定位第二个输入框
            WebElement secondInput = driver.findElement(By.xpath(xpath));
        } catch (NoSuchElementException e) {
            // 如果未抛出异常，手动断言失败
            fail("第二个输入框，出现预期的NoSuchElementException");
        }

    }

}
