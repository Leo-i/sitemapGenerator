package downloader;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by HerrSergio on 31.07.2016.
 */
public class Task implements Runnable {

    private Data data;
    private String url;

    public Task(Data data, String url) {
        this.data = data;
        this.url = url;
    }

    @Override
    public void run() {

        try {
            Thread.sleep((long) (300 * Math.random()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        Set<String> urls = parseForUrls(url);

        synchronized (data.getLock()) {
            data.urlDone(url, urls);

            for (String url : urls) {
                if(!data.getUrlMapInternal().containsKey(url)) {
                    data.addUrl(url);
                }
            }
        }
    }

    private Set<String> parseForUrls(String url) {

        Set<String> urls = new HashSet<>();
        try {
            URL myUrl = new URL(url);
            Document document = HttpConnection.connect(myUrl).get();
            Elements elements = document.select("a[href]");
            for(Element element:elements) {
                String newUrl = cleanUrl(element.absUrl("href"));
                URL myNewUrl = new URL(newUrl);
                if(isTheSameHost(myUrl, myNewUrl)) {
                    urls.add(myNewUrl.toString());
                }
            }
        } catch (Throwable e) {
            //e.printStackTrace();
        }
        return urls;
    }

    private static boolean isTheSameHost(URL myUrl, URL myNewUrl) {
        return myUrl.getHost().equals(myNewUrl.getHost())
                && (getPort(myUrl) == getPort(myNewUrl)
                    && myUrl.getProtocol().equals(myNewUrl.getProtocol())
                    || myUrl.getPort() < 0 && myNewUrl.getPort() < 0)
                && myUrl.getProtocol().matches("https?");
    }

    private static int getPort(URL url) {
        return url.getPort() < 0 ? url.getDefaultPort() : url.getPort();
    }

    private static String cleanUrl(String url) {
        return url.replaceAll("[#?].*", "");
    }
}
