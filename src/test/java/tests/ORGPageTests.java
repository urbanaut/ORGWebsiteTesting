package tests;

import base.TestBase;
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
        } catch (Exception e) {
            System.out.println("Heading not displayed on page.");
            e.printStackTrace();
        }
    }

    @Test
    public void checkMainPageForSpellingErrors() {
        try {
            helpers.checkPageSpelling();
        } catch (Exception e) {
            System.out.println("Spelling check failed.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyAllVideosPlay() {
        List<WebElement> videos = orgPage.getVideos();
        try {
            for (int i=0; i<videos.size(); i++) {
                videos.get(i).click();
                driver.switchTo().frame(i);
                String videoState = orgPage.videoPlayPauseBtn.getAttribute("aria-label");
                Assert.assertTrue(videoState.equals("Pause"), "Video is not playing.");
                driver.switchTo().defaultContent();
                videos.get(i).click();
                driver.navigate().refresh();
            }
        } catch (Exception e) {
            System.out.println("Video failed to play.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyPdfFileLoads() {
        List<WebElement> sideLinks = orgPage.getSideLinks();
        try {
            sideLinks.get(0).click();
            String url = driver.getCurrentUrl();
            String ext = url.substring(url.length() - 3);
            Assert.assertTrue(ext.equals("pdf"));
            driver.navigate().back();
        } catch (Exception e) {
            System.out.println("PDF file not found.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyVolunteerOrDonatePageOpens() {
        WebElement volunteerLink = orgPage.getSideLinks().get(3);
        try {
            volunteerLink.click();
            Thread.sleep(1000);
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            Assert.assertTrue(tabs.size()>1, "No new window opened.");
            helpers.closeNewTabAndReturn();
        } catch (Exception e) {
            System.out.println("Failed to navigate to 'Volunteer Or Donate' page.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyNewsArticleLinksOpen() {
        List<WebElement> links = orgPage.getNewsArticles();
        try {
            orgPage.openLinksInNewTabAndReturn(links);
        } catch (Exception e) {
            System.out.println("Article link not opened.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyAllPartnerLinksOpen() {
        List<WebElement> links = orgPage.getPartnerLinks();
        try {
            orgPage.openLinksInNewTabAndReturn(links);
        } catch (Exception e) {
            System.out.println("Partner link not opened.");
            e.printStackTrace();
        }
    }

    @Test
    public void verifyAllFooterLinksOpen() {
        List<WebElement> links = orgPage.getFooterLinks();
        try {
            orgPage.openLinksAndReturn(links);
        } catch (Exception e) {
            System.out.println("Footer link not opened.");
            e.printStackTrace();
        }
    }
}
