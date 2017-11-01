package utils;

import base.TestBase;
import com.aventstack.extentreports.Status;
import com.swabunga.spell.SpellChecker;
import com.swabunga.spell.TeXWordFinder;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.tokenizer.StringWordTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Helpers extends TestBase implements SpellCheckListener {

    private List<String> misspelledWords;
    private static final String SCREEN_SHOT_PATH = "src/main/java/output/";
    private Set<String> crawledLinks = new HashSet<>();
    private Queue<String> linksToCrawl = new LinkedBlockingQueue<>(1024);


    public void checkPageSpelling() throws Exception {
        String dictFile = "src\\main\\resources\\dictionaries\\en-US.dic";
        File dict = new File(dictFile);

        SpellChecker spellChecker = new SpellChecker(new SpellDictionaryHashMap(dict));
        spellChecker.addSpellCheckListener(this);

        String body = driver.getPageSource();
        Document doc = Jsoup.parse(body);
        String bodyText = doc.body().text();
        StringWordTokenizer tokenizer = new StringWordTokenizer(bodyText, new TeXWordFinder());

        misspelledWords = new ArrayList<>();
        spellChecker.checkSpelling(tokenizer);

        if (misspelledWords.size()>1) {
            System.out.println("\nCompleted Spellchecking " + driver.getCurrentUrl());
            System.out.println(misspelledWords.size() + " Possibly Misspelled Words Found: " + String.valueOf(misspelledWords));
            test.log(Status.INFO, "Completed Spellchecking <a href='" + driver.getCurrentUrl() + "'>" + driver.getCurrentUrl() + "</a>");
            test.log(Status.WARNING, misspelledWords.size() + " Possibly Misspelled Words Found: \n <pre>" + String.valueOf(misspelledWords) + "</pre>");
        }
    }

    @Override
    public void spellingError(SpellCheckEvent spellCheckEvent) {
        spellCheckEvent.ignoreWord(true);
        misspelledWords.add(spellCheckEvent.getInvalidWord());
    }


    public void closeNewTabAndReturn() {
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());
        if (!(tabs.size() > 1)) {
            driver.navigate().back();
        } else {
            driver.switchTo().window(tabs.get(1));
            driver.close();
            driver.switchTo().window(tabs.get(0));
        }
    }

    public void checkForPageLoadTimeout() throws Exception {
        int reps = 0;
        do{
            Thread.sleep(500);
            reps++;
            if (reps==10) {
                System.out.println("Timed out waiting for page to load, skipping.");
                test.log(Status.FAIL, "ERROR: Timed out waiting for page to load.");
                break;
            }
        } while(driver.getCurrentUrl().equals("about:blank"));
    }

    private void getStatusCode(String url) throws Exception {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(new HttpGet(url));
            int statusCode = response.getStatusLine().getStatusCode();
            String responseMessage = response.getStatusLine().getReasonPhrase();

            System.out.println(url);
            System.out.println("Response: " + statusCode + ", " + responseMessage);

            // Report Logging //
            test.log(Status.INFO, "<a href='" + url + "'>" + url + "</a>");
            if (statusCode == 200) {
                test.log(Status.PASS, "<pre> Response: " + statusCode + ", " + responseMessage + "</pre>");
            } else if (statusCode == 403){
                test.log(Status.WARNING, "<pre> Response: " + statusCode + ", " + responseMessage + "</pre>");
            } else{
                test.log(Status.FAIL, "<pre> Response: " + statusCode + ", " + responseMessage + "</pre>");
//                test.addScreenCaptureFromPath(screenshotPath + getTimestamp() + ".png");
            }
        }catch (Exception e) {
            System.out.println("Retrieving response code failed from URL: <a href='" + url + "'>" + url + "</a>");
            System.out.println(e.getMessage());
            test.log(Status.FAIL, "ERROR: Failed to retrieve response code from, URL: <a href='" + url + "'>" + url + "</a>" +
                    "\n<pre>" + e.getMessage().replace("<", "&lt").replace(">","&gt") + "</pre>");
        }
    }

    public String takeScreenshot() throws Exception {
        String timestamp = new SimpleDateFormat("HH.mm.ss_MM.dd.yyy").format(new Date());
        TakesScreenshot screenshot = ((TakesScreenshot)driver);
        File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
        String snapshot = SCREEN_SHOT_PATH + timestamp +".png";
        File destFile = new File(snapshot);
        FileUtils.copyFile(srcFile, destFile);
        return snapshot;
    }


    private void crawlPages(String nextUrl, String baseUrl) throws Exception {
        driver.navigate().to(nextUrl);

        List<WebElement> anchors = driver.findElements(By.xpath("//a[@href]"));
        List<String> links = new ArrayList<>();

        for (WebElement anchor : anchors) {
            links.add(anchor.getAttribute("href"));
        }

        for (String link : links) {
            if (!linksToCrawl.contains(link)
                    && !crawledLinks.contains(link)
                    && link.contains(baseUrl)
                    && !link.contains("#")
                    && !link.contains("index")
                    && !link.contains("pdf")
                    && link.length() > 0) {
                System.out.println("URL found: " + link);
                linksToCrawl.add(link);
            }
        }

        linksToCrawl.remove(nextUrl);

        if (linksToCrawl.size() != 0) {
            for (int i=0; i<linksToCrawl.size(); i++) {
                String url = linksToCrawl.poll();
                if (url != null) {
                    crawledLinks.add(url);
                    System.out.println("Visiting link: " + url);
                    crawlPages(url, baseUrl);
                }else {
                    break;
                }
            }
        }
    }

    public void crawlForResponseCodes() throws Exception {
        String startingUrl = driver.getCurrentUrl();
        List<String> checkedLinks = new ArrayList<>();
        List<WebElement> anchors;

        crawlPages(startingUrl, startingUrl);

        for (String link : crawledLinks) {
            driver.navigate().to(link);
            test.log(Status.INFO, "<font color='orange'>SCANNING LINKS ON:&nbsp;&nbsp;</font><a href='" +
                    driver.getCurrentUrl() + "'>" + driver.getCurrentUrl().toUpperCase() + "</a>");
            anchors = driver.findElements(By.xpath("//a[@href]"));
            for (WebElement anchor : anchors) {
                String href = anchor.getAttribute("href");
                if (!checkedLinks.contains(href)) {
                    getStatusCode(href);
                    checkedLinks.add(href);
                }
            }
        }
    }

    public void crawlForSpellingErrors() throws Exception {
        String startingUrl = driver.getCurrentUrl();

        crawlPages(startingUrl, startingUrl);

        for (String link : crawledLinks) {
            driver.navigate().to(link);
            checkPageSpelling();
        }
    }

    public String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ), " ");
    }

    public String getTimestamp() {
        return new SimpleDateFormat("HH.mm.ss_MM.dd.yyy").format(new Date());
    }

    private String convertToHttps(String url) {
        if (!url.contains("https")) {
            return new StringBuilder(url).insert(4, "s").toString();
        }else {
            return url;
        }
    }

}
