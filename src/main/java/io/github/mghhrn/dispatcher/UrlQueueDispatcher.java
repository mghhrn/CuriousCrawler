package io.github.mghhrn.dispatcher;

import io.github.mghhrn.worker.DocumentProvider;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class UrlQueueDispatcher implements Runnable {

    private final BlockingQueue<String> urlQueue;
    private final BlockingQueue<Document> documentQueue;
    private final ExecutorService documentProviderExecutor;
    private final String cacheDirectoryName = "curious-crawler" + File.separator + "cache";

    public UrlQueueDispatcher(BlockingQueue<String> urlQueue, BlockingQueue<Document> documentQueue, ExecutorService documentProviderExecutor) {
        this.urlQueue = urlQueue;
        this.documentQueue = documentQueue;
        this.documentProviderExecutor = documentProviderExecutor;
    }

    @Override
    public void run() {
        while (true) {
            String url = null;
            try {
                url = urlQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (url != null) {
                Future<?> future = documentProviderExecutor.submit(new DocumentProvider(url, cacheDirectoryName, documentQueue));
            }
        }
    }
}
