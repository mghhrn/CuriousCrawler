package io.github.mghhrn.dispatcher;

import io.github.mghhrn.worker.DocumentProvider;
import org.jsoup.nodes.Document;

import java.util.concurrent.*;

public class UrlQueueDispatcher implements Runnable {

    private final BlockingQueue<String> urlQueue;
    private final BlockingQueue<Document> documentQueue;
    private final ConcurrentLinkedDeque<Future<?>> workersFuture;
    private final ExecutorService documentProviderExecutor;
    private final ConcurrentHashMap<Integer, String> visitedUrls = new ConcurrentHashMap<>();

    public UrlQueueDispatcher(BlockingQueue<String> urlQueue, BlockingQueue<Document> documentQueue, ConcurrentLinkedDeque<Future<?>> workersFuture, ExecutorService documentProviderExecutor) {
        this.urlQueue = urlQueue;
        this.documentQueue = documentQueue;
        this.workersFuture = workersFuture;
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
                Future<?> future = documentProviderExecutor.submit(new DocumentProvider(url, documentQueue, visitedUrls));
                workersFuture.addLast(future);
            }
        }
    }
}
