package utils;

import base.TestBase;
import com.swabunga.spell.SpellChecker;
import com.swabunga.spell.TeXWordFinder;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.tokenizer.StringWordTokenizer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
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

        System.out.println("\nCompleted Spellchecking " + driver.getCurrentUrl());
        System.out.println(misspelledWords.size() + " Possibly Misspelled Words Found: ");
        for (String word : misspelledWords) {
            System.out.println(word);
        }
    }

    @Override
    public void spellingError(SpellCheckEvent spellCheckEvent) {
        spellCheckEvent.ignoreWord(true);
        misspelledWords.add(spellCheckEvent.getInvalidWord());
    }

    public void checkForPageLoadTimeout() throws Exception {
        int reps = 0;
        do{
            Thread.sleep(500);
            reps++;
            if (reps==20) {
                System.out.println("Timed out waiting for page to load, skipping.");
                break;
            }
        } while(driver.getCurrentUrl().equals("about:blank"));
    }

    public void getResponseCode() throws Exception {
        String url = driver.getCurrentUrl();
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(url));
        int statusCode = response.getStatusLine().getStatusCode();
        String responseMessage = response.getStatusLine().getReasonPhrase();
        System.out.println(url);
        System.out.println("Response: " + statusCode + ", " + responseMessage);
    }
}
