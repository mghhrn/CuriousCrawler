package io.github.mghhrn.worker;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.mghhrn.CuriousCrawler.ROOT_STORAGE_PATH;

public class DocumentProvider implements Runnable {

    private final String url;
    private final BlockingQueue<Document> documentQueue;
    private final String cacheDirectoryPath;
    private final ConcurrentHashMap<Integer, String> visitedUrls;

    public DocumentProvider(String url, BlockingQueue<Document> documentQueue, ConcurrentHashMap<Integer, String> visitedUrls) {
        this.url = url;
        this.documentQueue = documentQueue;
        this.cacheDirectoryPath = ROOT_STORAGE_PATH + File.separator + "cache";
        this.visitedUrls = visitedUrls;
    }

    @Override
    public void run() {
        if (isUrlVisited(url)) {
            return;
        }
        try {
            String webPageName = url.substring(url.lastIndexOf('/') + 1);
            File probablyCachedFile = new File(cacheDirectoryPath + File.separator + webPageName);
            Document document;
            if (probablyCachedFile.exists()) {
                document = loadDocumentFromCache(probablyCachedFile);
            } else {
                document = downloadDocumentAndCacheToTheFile(url, probablyCachedFile);
            }
            documentQueue.put(document);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isUrlVisited(String url) {
        String alreadyVisitedUrl = visitedUrls.putIfAbsent(url.hashCode(), url);
        return alreadyVisitedUrl != null;
    }

    private Document loadDocumentFromCache(File cachedFile) throws IOException {
        return Jsoup.parse(cachedFile, "UTF-8", "http://magento-test.finology.com.my/");
    }

    private Document downloadDocumentAndCacheToTheFile(String documentUrl, File cachedFile) throws IOException {
        Document document;
        document = Jsoup.connect(documentUrl).get();
        String fullPageContent = document.html();
        FileUtils.writeStringToFile(cachedFile, fullPageContent, StandardCharsets.UTF_8);
        return document;
    }
}
