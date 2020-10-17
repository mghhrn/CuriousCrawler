package io.github.mghhrn.dispatcher;

import io.github.mghhrn.DocumentProvider;
import org.jsoup.nodes.Document;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class UrlQueueDispatcher implements Runnable {

    private final BlockingQueue<String> urlQueue;
    private final BlockingQueue<Document> documentQueue;
    private final ExecutorService documentProviderExecutor;
    private final String cacheDirectoryName = "curious-crawler-cache";

    public UrlQueueDispatcher(BlockingQueue<String> urlQueue, BlockingQueue<Document> documentQueue, ExecutorService documentProviderExecutor) {
        this.urlQueue = urlQueue;
        this.documentQueue = documentQueue;
        this.documentProviderExecutor = documentProviderExecutor;
    }

    @Override
    public void run() {
        String url = null;
        while (true) {
            try {
                url = urlQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            documentProviderExecutor.submit(new DocumentProvider(url, cacheDirectoryName, documentQueue));
        }
    }
}
