package hooks;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.*;
import io.cucumber.plugin.event.Status;
import utils.DriverManager;
import utils.ScreenshotUtil;
/**
 * 增强的测试钩子类，支持多浏览器信息记录
 */
public class TestHooks {

    private String currentStepName; // 存储当前步骤名称

    @Before
    public void beforeScenario(Scenario scenario) {
        DriverManager.initializeDriver();
        scenario.log("使用浏览器: " + DriverManager.getBrowserType());
    }

    // 关键：通过事件监听获取当前步骤名称（无需绑定@Given/@When/@Then）
    public void setCurrentStepName(TestCase testCase, TestStep testStep) {
        if (testStep instanceof PickleStepTestStep) {
            // 提取步骤文本
            currentStepName = ((PickleStepTestStep) testStep).getStep().getText();
        }
    }

    // 每个步骤执行后截图
    @AfterStep
    public void afterStep(Scenario scenario) {
        // 使用当前步骤名称生成截图描述
        ScreenshotUtil.captureStepScreenshot(scenario, currentStepName);
        currentStepName = null; // 重置，避免干扰下一个步骤
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            scenario.log("场景失败: " + scenario.getName());
        }
//        DriverManager.quitDriver();
    }
}
