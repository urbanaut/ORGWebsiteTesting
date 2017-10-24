package tests;

import base.TestBase;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ORGPage;
import utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class ORGPageTests extends TestBase {

    private ORGPage orgPage;
    private Helpers helpers;

    @BeforeMethod
    public void setup() {
        orgPage = new ORGPage(driver);
        helpers = new Helpers();
    }

    @Test(priority=1)
    public void verifyHeaderPresentOnAllPages() {
        test.log(LogStatus.INFO, "TEST: Verifying that the 'Operation Rio Grande' heading is on each ORG page.");
        List<WebElement> sideLinks = orgPage.getSideLinks();
        List<WebElement> headingLinks = orgPage.getHeadingLinks();
        try {
            for (int i=1; i<sideLinks.size(); i++) {
                if (i==3)
                    continue;
                sideLinks.get(i).click();
                Assert.assertTrue(headingLinks.get(0).isDisplayed());
                driver.navigate().back();
            }
            test.log(LogStatus.PASS, "All ORG pages display the 'Operation Rio Grande' heading.");
        } catch (Exception e) {
            System.out.println("Heading not displayed on page.");
            test.log(LogStatus.FAIL, "The 'Operation Rio Grande' heading is missing on the page: " + driver.getCurrentUrl());
            e.printStackTrace();
        }
    }

    @Test(priority=2)
    public void checkORGPagesForSpellingErrors() {
        test.log(LogStatus.INFO, "TEST: Checking all ORG pages for spelling errors.");
        List<WebElement> sideLinks = orgPage.getSideLinks();
        try {
            helpers.checkPageSpelling();
            for (int i=1; i<sideLinks.size(); i++) {
                if (i==3)
                    continue;
                sideLinks.get(i).click();
                helpers.checkPageSpelling();
                driver.navigate().back();
            }
        } catch (Exception e) {
            System.out.println("Spelling check failed.");
            test.log(LogStatus.FAIL, "ERROR: Spell check encountered an error.");
            e.printStackTrace();
        }
    }

    @Test(priority=3)
    public void verifyAllHeaderLinksOpen() {
        test.log(LogStatus.INFO, "TEST: Verifying that all header links open successfully.");
        List<WebElement> links = orgPage.getHeaderLinks();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(LogStatus.PASS, "All header links opened successfully.");
        } catch (Exception e) {
            System.out.println("Footer link not opened.");
            test.log(LogStatus.FAIL, "ERROR: Failed to open a footer link.");
            e.printStackTrace();
        }
    }

    @Test(priority=4)
    public void verifyAllVideosPlay() {
        test.log(LogStatus.INFO, "TEST: Verifying that all videos on the main ORG play.");
        List<WebElement> videos = orgPage.getVideos();
        try {
            for (int i=0; i<videos.size(); i++) {
                videos.get(i).click();
                driver.switchTo().frame(i);
                String videoState = orgPage.getVideoPlayPauseBtn().getAttribute("aria-label");
                Assert.assertTrue(videoState.equals("Pause"), "Video is not playing.");
                driver.switchTo().defaultContent();
                videos.get(i).click();
                driver.navigate().refresh();
            }
            test.log(LogStatus.PASS, "All videos played back successfully.");
        } catch (Exception e) {
            System.out.println("Video failed to play.");
            e.printStackTrace();
        }
    }

    @Test(priority=5)
    public void verifyArchivedVideosOpen() {
        test.log(LogStatus.INFO, "TEST: Verifying that archived videos page opens.");
        WebElement archivedVideos = orgPage.getArchivedVideosLnk();
        try {
            String startingUrl = driver.getCurrentUrl();
            archivedVideos.click();
            String newUrl = driver.getCurrentUrl();
            Assert.assertTrue(!startingUrl.equals(newUrl), "URL did not change after clicking link.");
            driver.navigate().back();
            test.log(LogStatus.PASS, "'Archived Videos' link opened successfully.");
        } catch (Exception e) {
            test.log(LogStatus.FAIL, "ERROR: Failed to open 'Archived Videos' link.");
            e.printStackTrace();
        }
    }

    @Test(priority=6)
    public void verifyORGPhasesPdfFileLoads() {
        test.log(LogStatus.INFO, "TEST: Verifying that the ORG Phases PDF file opens.");
        List<WebElement> sideLinks = orgPage.getSideLinks();
        try {
            sideLinks.get(0).click();
            String url = driver.getCurrentUrl();
            String ext = url.substring(url.length() - 3);
            Assert.assertTrue(ext.equals("pdf"));
            driver.navigate().back();
            test.log(LogStatus.PASS, "ORG Phases PDF opened successfully.");
        } catch (Exception e) {
            System.out.println("PDF file not found.");
            test.log(LogStatus.FAIL, "ORG Phases PDF failed to open.");
            e.printStackTrace();
        }
    }

    @Test(priority=7)
    public void verifyVolunteerOrDonatePageOpens() {
        test.log(LogStatus.INFO, "TEST: Verifying that the 'Volunteer or Donate' page opens.");
        WebElement volunteerLink = orgPage.getSideLinks().get(3);
        try {
            volunteerLink.click();
            helpers.checkForPageLoadTimeout();
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            Assert.assertTrue(tabs.size()>1, "No new window opened.");
            helpers.closeNewTabAndReturn();
            test.log(LogStatus.PASS, "'Volunteer or Donate' page opened successfully.");
        } catch (Exception e) {
            System.out.println("Failed to navigate to 'Volunteer Or Donate' page.");
            test.log(LogStatus.FAIL, "ERROR: 'Volunteer or Donate' page failed to open.");
            e.printStackTrace();
        }
    }

    @Test(priority=8)
    public void verifyNewsArticleLinksOpen() {
        test.log(LogStatus.INFO, "TEST: Verifying that all news article links open.");
        List<WebElement> links = orgPage.getNewsArticles();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(LogStatus.PASS, "All news article links opened successfully.");
        } catch (Exception e) {
            System.out.println("Article link not opened.");
            test.log(LogStatus.FAIL, "ERROR: Failed to open all news article links.");
            e.printStackTrace();
        }
    }

    @Test(priority=9)
    public void verifyAllPartnerLinksOpen() {
        test.log(LogStatus.INFO, "TEST: Verifying that all partner links open.");
        List<WebElement> links = orgPage.getPartnerLinks();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(LogStatus.PASS, "All partner links opened successfully.");
        } catch (Exception e) {
            System.out.println("Partner link not opened.");
            test.log(LogStatus.FAIL, "ERROR: Failed to open all partner links.");
            e.printStackTrace();
        }
    }

    @Test(priority=10)
    public void verifyAllFooterLinksOpen() {
        test.log(LogStatus.INFO, "TEST: Verifying that all footer links open.");
        List<WebElement> links = orgPage.getFooterLinks();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(LogStatus.PASS, "All footer links opened successfully.");
        } catch (Exception e) {
            System.out.println("Footer link failed to open.");
            test.log(LogStatus.FAIL, "ERROR: Failed to open all footer links.");
            e.printStackTrace();
        }
    }

    @Test(priority=11)
    public void checkLinkResponseCodes() throws Exception {
        test.log(LogStatus.INFO, "TEST: Checking that all link response codes return 200.");
        List<List<WebElement>> allLinks = new ArrayList<>();
        try {
            allLinks.add(orgPage.getHeaderLinks());
            allLinks.add(orgPage.getSideLinks());
            allLinks.add(orgPage.getNewsArticles());
            allLinks.add(orgPage.getPartnerLinks());
            allLinks.add(orgPage.getFooterLinks());

            for (List<WebElement> link : allLinks) {
                orgPage.followLinkAndGetResponse(link);
            }
        } catch (Exception e) {
            System.out.println("ERROR: Failed to get response code.");
        }
    }
}
