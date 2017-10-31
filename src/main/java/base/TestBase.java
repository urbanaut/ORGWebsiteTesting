package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentXReporter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utils.Helpers;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TestBase {

    protected static WebDriver driver;

    private static ExtentXReporter extentX;
    private static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeSuite
    public void init() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("src/main/java/output/ORG_Test_Report.html");
        htmlReporter.loadXMLConfig(new File("src/main/resources/extent/extent-config.xml"));
        htmlReporter.setAppendExisting(true);

//        extentX = new ExtentXReporter("192.168.99.100"); // ExtentX Docker IP
//        extentX.config().setProjectName("Operation Rio Grande Website Test");
//        extentX.config().setReportName(this.getClass().getSimpleName());
//        extentX.config().setServerUrl("http://192.168.99.100:1337");

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
//        extent.attachReporter(htmlReporter, extentX);

        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--start-maximized");
        options.addArguments("chrome.switches","--disable-extensions"); //Removes popup reminder for disabling extensions
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        options.setBinary("C:\\Program Files (x86)\\Google\\Chrome\\Application\\Chrome.exe");
        driver = new ChromeDriver(options);
        driver.navigate().to("http://operationriogrande.utah.gov");
    }

    @BeforeMethod
    public void beforeMethod(Method method) throws Exception {
        Thread.sleep(500);
        Helpers h = new Helpers();
        test = extent.createTest((h.splitCamelCase(StringUtils.capitalize(method.getName()))), method.getName());
        test.assignAuthor("Bill Witt");
        test.assignCategory("Operation Rio Grande Website Test");
    }

    @AfterSuite
    public void afterSuite() {
        driver.quit();
        extent.flush();
    }
}
