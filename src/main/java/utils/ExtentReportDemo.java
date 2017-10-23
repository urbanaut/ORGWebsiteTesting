package utils;

import base.TestBase;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;

public class ExtentReportDemo  {

    private static WebDriver driver;
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeSuite
    public void beforeSuite() {
        extent = new ExtentReports("src/main/java/output/MyReport.html", true);
        extent.loadConfig(new File("src/main/resources/extent/extent-config.xml"));
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        test = extent.startTest((this.getClass().getSimpleName() + " :: " + method.getName()), method.getName());
        test.assignAuthor("Bill Witt");
        test.assignCategory("Regression Test");
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\drivers\\chromedriver.exe");
        driver = new ChromeDriver();
        test.log(LogStatus.INFO, "Browser launched successfully.");
    }

    @Test
    public void enterInvalidEmailId() throws Exception {
        driver.navigate().to("http://www.tothenew.com");
        String currentUrl = driver.getCurrentUrl();
        test.log(LogStatus.INFO, "The current URL of the web page is " + currentUrl);
        driver.findElement(By.id("email1")).sendKeys("myfake@email");
        driver.findElement(By.cssSelector("input[class='submit subscribeButton']")).click();
        WebElement err = driver.findElement(By.xpath("//*[@id='messageBox']/p/b"));
        String actualResult = err.getText();
        String expectedResult = "Enter valid email id";
        if (actualResult.equalsIgnoreCase(expectedResult)) {
            test.log(LogStatus.PASS, "Actual Result: " + actualResult);
        } else {
            test.log(LogStatus.FAIL, "Actual Result: " + actualResult + " Expected Result: " + expectedResult);
        }
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        driver.close();
        driver.quit();
        test.log(LogStatus.PASS, "Browser closed successfully");
        extent.endTest(test);
    }

    @AfterSuite
    public void afterSuite() {
        extent.flush();
        extent.close();
    }
}
