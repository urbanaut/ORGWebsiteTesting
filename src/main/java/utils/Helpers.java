package utils;

import base.TestBase;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
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

public class Helpers extends TestBase implements SpellCheckListener{

    private List<String> misspelledWords;
    private String screenshotPath = "src/main/java/output/";
    private Queue<String> linksToCrawl = new LinkedBlockingQueue<>(1024);
    private Set<String> crawledLinks = new HashSet<>();
    private String startingUrl = "https://operationriogrande.utah.gov";
    private List<String> checkedLinks = new ArrayList<>();

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
            test.log(Status.INFO, "Completed Spellchecking " + driver.getCurrentUrl());
            test.log(Status.INFO, misspelledWords.size() + " Possibly Misspelled Words Found: \n <pre>" + String.valueOf(misspelledWords) + "</pre>");
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

    public void getStatusCode(String url) throws Exception {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(new HttpGet(url));
            int statusCode = response.getStatusLine().getStatusCode();
            String responseMessage = response.getStatusLine().getReasonPhrase();

            System.out.println(url);
            System.out.println("Response: " + statusCode + ", " + responseMessage);

            // Report Logging //
            test.log(Status.INFO, url);
            if (statusCode == 200) {
                test.log(Status.PASS, "<pre> Response: " + statusCode + ", " + responseMessage + "</pre>");
            } else {
                test.log(Status.FAIL, "<pre> Response: " + statusCode + ", " + responseMessage + "</pre>");
//                String timestamp = new SimpleDateFormat("HH.mm.ss_MM.dd.yyy").format(new Date());
//                test.addScreenCaptureFromPath(screenshotPath + timestamp + ".png");
            }
        }catch (Exception e) {
            System.out.println("Retrieving response code failed from URL: " + url);
            System.out.println(e.getMessage());
            test.log(Status.INFO, url);
            test.log(Status.FAIL, "ERROR: Failed to retrieve response code from : " + url +
                    "\n<pre>" + e.getMessage().replace("<", "&lt").replace(">","&gt") + "</pre>");
        }
    }

    public String takeScreenshot() throws Exception {
        String timestamp = new SimpleDateFormat("HH.mm.ss_MM.dd.yyy").format(new Date());
        TakesScreenshot screenshot = ((TakesScreenshot)driver);
        File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
        String snapshot = screenshotPath + timestamp +".png";
        File destFile = new File(snapshot);
        FileUtils.copyFile(srcFile, destFile);
        return snapshot;
    }


    public void crawl() throws Exception {
        crawlPages(startingUrl);
    }

    public void crawlPages(String baseUrl) throws Exception {
        driver.navigate().to(baseUrl);

        List<WebElement> anchors = driver.findElements(By.xpath("//a[@href]"));
        List<String> links = new ArrayList<>();

        for (WebElement anchor : anchors) {
            links.add(anchor.getAttribute("href"));
        }

        for (String link : links) {
            if (!checkedLinks.contains(link)) {
                getStatusCode(link);
                checkedLinks.add(link);
            }
        }

        for (String link : links) {
            if (!linksToCrawl.contains(link)
                    && !crawledLinks.contains(link)
                    && link.contains(startingUrl)
                    && !link.contains("#")
                    && !link.contains("index")
                    && link.length() > 0) {
                System.out.println("URL found: " + link);
                linksToCrawl.add(link);
            }
        }

        linksToCrawl.remove(baseUrl);

        if (linksToCrawl.size() != 0) {
            for (int i=0; i<linksToCrawl.size(); i++) {
                String url = linksToCrawl.poll();
                if (url != null) {
                    crawledLinks.add(url);
                    System.out.println("Visiting link: " + url);
                    crawlPages(url);
                }else {
                    break;
                }
            }
        }
//        crawlUrlList(linksToCrawl);
    }

    private void crawlUrlList(Queue<String> queue) throws Exception {
        int qCount = queue.size();
        System.out.println("URLs to visit: " + qCount);
        System.out.println("Visited Urls: " + crawledLinks.size());

        if (qCount != 0) {
            for (int i = 0; i < qCount; i++) {
                String url = queue.poll();
                if (url!=null) {
                    crawledLinks.add(url);
                    System.out.println("Visiting link: " + url);
                    crawlPages(url);
                } else {
                    break;
                }
            }
        }
    }


}
