package io.github.mghhrn.dispatcher;

import io.github.mghhrn.worker.DocumentConsumer;
import org.jsoup.nodes.Document;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class DocumentQueueDispatcher implements Runnable {

    private final BlockingQueue<String> urlQueue;
    private final BlockingQueue<Document> documentQueue;
    private final ExecutorService documentConsumerExecutor;

    public DocumentQueueDispatcher(BlockingQueue<String> urlQueue, BlockingQueue<Document> documentQueue, ExecutorService documentConsumerExecutor) {
        this.urlQueue = urlQueue;
        this.documentQueue = documentQueue;
        this.documentConsumerExecutor = documentConsumerExecutor;
    }

    @Override
    public void run() {
        Document document = null;
        while (true) {
            try {
                document = documentQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            documentConsumerExecutor.submit(new DocumentConsumer(document, urlQueue));
        }
    }
}
