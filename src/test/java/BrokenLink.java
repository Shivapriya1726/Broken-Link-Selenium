import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class BrokenLink {
    public static void main(String[] args) {
        String siteHost = "https://www.wikipedia.org/"; // Replace URL
        List<String> urls = new ArrayList<>(); // URLs to check
        urls.add(siteHost);

        List<String> brokenLinks = new ArrayList<>();

        int pointer = 0;

        // Set up Selenium WebDriver
        System.setProperty("webdriver.chrome.driver", "D://chromedriver-win64//chromedriver-win64//chromedriver.exe/");
        WebDriver driver = new ChromeDriver();


        while (urls.size() > pointer) {

            String url = urls.get(pointer);
            System.out.println("URL: " + url);


            boolean flagBroken = isBrokenLink(url);
            pointer += 1;
            if (flagBroken) {
                brokenLinks.add(url);
                continue;
            }


            driver.get(url);
            try {
                Thread.sleep(2000); // allow JS to load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            List<WebElement> linkElements = driver.findElements(By.tagName("a"));
            System.out.println("New Links count: " + linkElements.size());

            for (WebElement elem : linkElements) {
                String newUrl = elem.getAttribute("href");
                if (newUrl != null && newUrl.contains(siteHost) && !urls.contains(newUrl)) {
                    System.out.println("=> " + newUrl);
                    urls.add(newUrl);
                }
            }
        }


        System.out.println("Total Page Count: " + urls.size());
        System.out.println("Total Broken Links: " + brokenLinks.size());
        for (String brokenLink : brokenLinks) {
            System.out.println(brokenLink);
        }

        // Close the WebDriver
        driver.quit();
    }

    // Method to check if a link is broken
    private static boolean isBrokenLink(String url) {
        HttpURLConnection connection = null;
        try {
            URL link = new URL(url);
            connection = (HttpURLConnection) link.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            int responseCode = connection.getResponseCode();
            return responseCode != HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            // If request fails, treat it as broken
            return true;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
