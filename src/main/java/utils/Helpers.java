package utils;

import base.TestBase;
import com.swabunga.spell.SpellChecker;
import com.swabunga.spell.TeXWordFinder;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.tokenizer.StringWordTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Helpers extends TestBase implements SpellCheckListener{

    private List<String> misspelledWords;

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

        System.out.println(misspelledWords.size() + " Misspelled Words: ");
        for (String word : misspelledWords) {
            System.out.println(word);
        }

    }

    @Override
    public void spellingError(SpellCheckEvent spellCheckEvent) {
        spellCheckEvent.ignoreWord(true);
        misspelledWords.add(spellCheckEvent.getInvalidWord());
    }

    public boolean checkForNewPageLoad(WebElement link) {
        String pageUrl = driver.getCurrentUrl();
        link.click();
        String newUrl = driver.getCurrentUrl();
        return !pageUrl.equals(newUrl);
    }

    public boolean checkForFullPageLoad() {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        return js.executeScript("return document.readyState").toString().equalsIgnoreCase("complete");
    }

    public void checkLinkResponseCodes() throws Exception {
        String responseCode = "";
        int reps = 0;
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());

        if (tabs.size()>1)
            driver.switchTo().window(tabs.get(1));
        do{
            Thread.sleep(500);
            reps++;
            if (reps==20) {
                System.out.println("Timed out waiting for page to load.");
                break;
            }
        } while(driver.getCurrentUrl().equals("about:blank"));

        String newUrl = driver.getCurrentUrl();
        if (newUrl.equals("about:blank")){
            System.out.println("Page failed to load within 10 seconds, checking next link...\n");
            closeNewTabAndReturn();
            return;
        }

        try {
            URL url = new URL(newUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
            responseCode = String.valueOf(connection.getResponseCode());
            String responseMessage = connection.getResponseMessage();
            System.out.println(newUrl);
            System.out.println("Response: " + responseCode + " " + responseMessage + "\n");
//            connection.disconnect();
        } catch (Exception e) {
            System.out.println("Retrieving response code failed.");
            e.printStackTrace();
        }

        if (!responseCode.equals("200")) {
            System.out.println("Warning: Response code was not 200 (OK) as expected.\n");
        }

        closeNewTabAndReturn();
    }


}
