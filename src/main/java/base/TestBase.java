package base;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TestBase {

    protected static WebDriver driver;
    private static ExtentReports extent;
    protected static ExtentTest test;

    @BeforeSuite
    public void init() {
        extent = new ExtentReports("src/main/java/output/ORG_Test_Report.html", true);
        extent.loadConfig(new File("src/main/resources/extent/extent-config.xml"));

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
    public void beforeMethod(Method method) {
        test = extent.startTest((this.getClass().getSimpleName() + " :: " + method.getName()), method.getName());
        test.assignAuthor("Bill Witt");
        test.assignCategory("Operation Rio Grande Website Test");
        extent.endTest(test);
    }

//    @AfterSuite
//    public void afterSuite() {
//        driver.quit();
//        extent.flush();
//        extent.close();
//    }
}
