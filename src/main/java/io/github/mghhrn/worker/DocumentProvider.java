package io.github.mghhrn.worker;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class DocumentProvider implements Runnable {

    private final String url;
    private final BlockingQueue<Document> documentQueue;
    private final String cacheDirectoryName;
    private final String cacheDirectoryFullPath;

    public DocumentProvider(String url, String cacheDirectoryName, BlockingQueue<Document> documentQueue) {
        this.url = url;
        this.cacheDirectoryName = cacheDirectoryName;
        this.documentQueue = documentQueue;
        this.cacheDirectoryFullPath = FileUtils.getTempDirectoryPath() + File.separator + cacheDirectoryName + File.separator;
    }

    @Override
    public void run() {
        System.out.println("Starting to provide the document of: " + url);

        try {
            String webPageName = url.substring(url.lastIndexOf('/') + 1);
            File cachedFile = new File(cacheDirectoryFullPath + webPageName);
            Document document;
            if (cachedFile.exists()) {
                document = Jsoup.parse(cachedFile, "UTF-8", "http://magento-test.finology.com.my/");
                // TODO: Remove below line for final code version. This is for endless loop prevention.
                System.out.printf("File " + webPageName + " is already cached. Ignoring further process.");
                return;
            } else {
                document = Jsoup.connect(url).get();
                String fullPageContent = document.html();
                FileUtils.writeStringToFile(cachedFile, fullPageContent, StandardCharsets.UTF_8);
            }
            documentQueue.put(document);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
