package pages;

import base.TestBase;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class ORGPage extends TestBase {

    private WebDriver driver;
    private Helpers helpers;

    public ORGPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
        helpers = new Helpers();
    }

    // Search
    @FindBy(id = "searchutgov")
    public WebElement txtSearchBox;
    @FindBy(id = "searchutgovbtn")
    public WebElement btnSearch;

    // Header Links
    @FindBy(xpath = "//div[@id='utahgov_statewideheader']//a")
    private List<WebElement> headerLinks;

    // Heading Links
    @FindBy(xpath = "//div[@class='org-header']/div/div")
    private List<WebElement> headingLinks;

    // Right Side Links
    @FindBy(xpath = "//div[@class='col-md-4 col-sm-12 col-xs-12']//a/div")
    private List<WebElement> sideLinks;
    @FindBy(xpath = "//div[@class='col-md-4 col-sm-12 col-xs-12']//div/a")
    private List<WebElement> sideLinkUrls;

    // Videos
    @FindBy(xpath = "//iframe[contains(@src,'youtube')]")
    private List<WebElement> videos;

    @FindBy(xpath = "//button[@class='ytp-play-button ytp-button']")
    private WebElement videoPlayPauseBtn;

    @FindBy(xpath = "//div[@class='col-md-12 archiveVideos']")
    private WebElement archivedVideosLnk;

    // News Articles
    @FindBy(xpath = "//div[@class='col-md-12 recent-media-coverage ']/div[@class='col-md-12 ']//a")
    private List<WebElement> newsArticles;

    // Partner Links
    @FindBy(xpath = "//div[@id='footerFile']//div[@class='col-md-12']/div/a")
    private List<WebElement> partnerLinks;

    // Footer Links
    @FindBy(xpath = "//div[@class='col-md-12 text-center']/a")
    private List<WebElement> footerLinks;


    public List<WebElement> getHeaderLinks() {
        return headerLinks;
    }

    public List<WebElement> getHeadingLinks() {
        return headingLinks;
    }

    public List<WebElement> getSideLinks() {
        return sideLinks;
    }

    public List<WebElement> getSideLinkUrls() {
        return sideLinkUrls;
    }

    public List<WebElement> getVideos() {
        return videos;
    }

    public WebElement getVideoPlayPauseBtn() {
        return videoPlayPauseBtn;
    }

    public WebElement getArchivedVideosLnk() {
        return archivedVideosLnk;
    }

    public List<WebElement> getNewsArticles() {
        return newsArticles;
    }

    public List<WebElement> getPartnerLinks() {
        return partnerLinks;
    }

    public List<WebElement> getFooterLinks() {
        return footerLinks;
    }


    public void openLinksInCurrentOrNewTabAndReturn(List<WebElement> links) {
        String startingUrl = driver.getCurrentUrl();
        String newUrl;
        try {
            for (int i=0; i<links.size(); i++) {
                links.get(i).click();
                newUrl = driver.getCurrentUrl();
                List<String> tabs = new ArrayList<>(driver.getWindowHandles());
                if (!startingUrl.equals(newUrl)) {
                    driver.navigate().back();
                }
                else if (tabs.size()>1) {
                    helpers.closeNewTabAndReturn();
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to navigate back or close tab.");
            test.log(LogStatus.FAIL, "ERROR: Failed navigate back or close tab.");
            e.printStackTrace();
        }
    }

    public void getLinkResponseCode(List<WebElement> links) {
        try {
            for (int i=0; i<links.size(); i++) {
                String url = links.get(i).getAttribute("href");
                helpers.getStatusCode(url);
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve response code.");
            test.log(LogStatus.FAIL, "ERROR: Failed to retrieve response code.");
            e.printStackTrace();
        }
    }


}
