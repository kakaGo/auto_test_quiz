package runner;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * 支持多浏览器的Cucumber测试运行器
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/report.html",
                "json:target/cucumber-reports/report.json"
                // 注册自定义事件监听器（需实现Plugin接口）
//                "hooks.StepEventListener"
        },
        monochrome = true
)
public class TestRunner {
}