package runner;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/t.feature",
        glue = {"steps","hooks"},
        plugin = {
                "pretty", // 控制台输出格式化
                "html:target/cucumber-reports/html-report.html", // 生成Cucumber HTML报告
                "json:target/cucumber-reports/cucumber.json", // 生成JSON报告
                "junit:target/cucumber-reports/cucumber.xml", // 生成JUnit风格报告
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm", // Allure报告插件
                "listener.CucumberListener"  // 自定义监听器
        },
        monochrome = true
)
public class TestRunner {
}