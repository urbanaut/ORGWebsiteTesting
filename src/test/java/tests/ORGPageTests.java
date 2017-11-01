package tests;

import base.TestBase;
import com.aventstack.extentreports.Status;
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

    @Test
    public void verifyHeaderPresentOnAllPages() {
        test.log(Status.INFO, "TEST: Verifying that the 'Operation Rio Grande' heading is on each ORG page.");
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
            test.log(Status.PASS, "All ORG pages display the 'Operation Rio Grande' heading.");
        } catch (Exception e) {
            System.out.println("Heading not displayed on page.");
            test.log(Status.FAIL, "The 'Operation Rio Grande' heading is missing on the page: " + driver.getCurrentUrl());
            e.printStackTrace();
        }
    }

    @Test
    public void checkORGPagesForSpellingErrors() {
        test.log(Status.INFO, "TEST: Checking all ORG pages for spelling errors.");
        try {
            helpers.crawlForSpellingErrors();
            test.log(Status.PASS, "Completed spell checking pages successfully.");
        } catch (Exception e) {
            System.out.println("Spelling check failed.");
            e.printStackTrace();
            test.log(Status.FAIL, "ERROR: Spell check encountered an error.");
        }
    }

    @Test
    public void verifyAllHeaderLinksOpen() {
        test.log(Status.INFO, "TEST: Verifying that all header links open successfully.");
        List<WebElement> links = orgPage.getHeaderLinks();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(Status.PASS, "All header links opened successfully.");
        } catch (Exception e) {
            System.out.println("Footer link not opened.");
            test.log(Status.FAIL, "ERROR: Failed to open a footer link.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyAllVideosPlay() {
        test.log(Status.INFO, "TEST: Verifying that all videos on the main ORG play.");
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
            test.log(Status.PASS, "All videos played back successfully.");
        } catch (Exception e) {
            System.out.println("Video failed to play.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyArchivedVideosOpen() {
        test.log(Status.INFO, "TEST: Verifying that archived videos page opens.");
        WebElement archivedVideos = orgPage.getArchivedVideosLnk();
        try {
            String startingUrl = driver.getCurrentUrl();
            archivedVideos.click();
            String newUrl = driver.getCurrentUrl();
            Assert.assertTrue(!startingUrl.equals(newUrl), "URL did not change after clicking link.");
            driver.navigate().back();
            test.log(Status.PASS, "'Archived Videos' link opened successfully.");
        } catch (Exception e) {
            test.log(Status.FAIL, "ERROR: Failed to open 'Archived Videos' link.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyORGPhasesPdfFileLoads() {
        test.log(Status.INFO, "TEST: Verifying that the ORG Phases PDF file opens.");
        List<WebElement> sideLinks = orgPage.getSideLinks();
        try {
            sideLinks.get(0).click();
            String url = driver.getCurrentUrl();
            String ext = url.substring(url.length() - 3);
            Assert.assertTrue(ext.equals("pdf"));
            driver.navigate().back();
            test.log(Status.PASS, "ORG Phases PDF opened successfully.");
        } catch (Exception e) {
            System.out.println("PDF file not found.");
            test.log(Status.FAIL, "ORG Phases PDF failed to open.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyVolunteerOrDonatePageOpens() {
        test.log(Status.INFO, "TEST: Verifying that the 'Volunteer or Donate' page opens.");
        WebElement volunteerLink = orgPage.getSideLinks().get(3);
        try {
            volunteerLink.click();
            helpers.checkForPageLoadTimeout();
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            Assert.assertTrue(tabs.size()>1, "No new window opened.");
            helpers.closeNewTabAndReturn();
            test.log(Status.PASS, "'Volunteer or Donate' page opened successfully.");
        } catch (Exception e) {
            System.out.println("Failed to navigate to 'Volunteer Or Donate' page.");
            test.log(Status.FAIL, "ERROR: 'Volunteer or Donate' page failed to open.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyAllSideLinksOpen() {
        test.log(Status.INFO, "TEST: Verifying that all side links open.");
        List<WebElement> links = orgPage.getSideLinks();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(Status.PASS, "Opened all side links successfully.");
        }catch (Exception e) {
            System.out.println("ERROR: Failed to open all side links.");
            test.log(Status.FAIL, "ERROR: Failed to open all side links.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyNewsArticleLinksOpen() {
        test.log(Status.INFO, "TEST: Verifying that all news article links open.");
        List<WebElement> links = orgPage.getNewsArticles();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(Status.PASS, "All news article links opened successfully.");
        } catch (Exception e) {
            System.out.println("Article link not opened.");
            test.log(Status.FAIL, "ERROR: Failed to open all news article links.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyAllPartnerLinksOpen() {
        test.log(Status.INFO, "TEST: Verifying that all partner links open.");
        List<WebElement> links = orgPage.getPartnerLinks();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(Status.PASS, "All partner links opened successfully.");
        } catch (Exception e) {
            System.out.println("Partner link not opened.");
            test.log(Status.FAIL, "ERROR: Failed to open all partner links.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyAllFooterLinksOpen() {
        test.log(Status.INFO, "TEST: Verifying that all footer links open.");
        List<WebElement> links = orgPage.getFooterLinks();
        try {
            orgPage.openLinksInCurrentOrNewTabAndReturn(links);
            test.log(Status.PASS, "All footer links opened successfully.");
        } catch (Exception e) {
            System.out.println("Footer link failed to open.");
            test.log(Status.FAIL, "ERROR: Failed to open all footer links.");
            e.printStackTrace();
        }
    }

    @Test
    public void checkLinkResponseCodes() throws Exception {
        test.log(Status.INFO, "TEST: Scanning all links for response codes.");
        try {
            helpers.crawlForResponseCodes();
            test.log(Status.PASS, "All links scanned successfully.");
        } catch (Exception e) {
            System.out.println("ERROR: Failed to scan links for response codes.");
            test.log(Status.FAIL, "ERROR: Failed to scan links for response codes.");
            e.printStackTrace();
        }
    }

}
