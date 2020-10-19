package io.github.mghhrn.dispatcher;

import io.github.mghhrn.worker.DocumentConsumer;
import org.jsoup.nodes.Document;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DocumentQueueDispatcher implements Runnable {

    private final BlockingQueue<String> urlQueue;
    private final BlockingQueue<Document> documentQueue;
    private final ConcurrentLinkedDeque<Future<?>> workersFuture;
    private final ExecutorService documentConsumerExecutor;

    public DocumentQueueDispatcher(BlockingQueue<String> urlQueue, BlockingQueue<Document> documentQueue, ConcurrentLinkedDeque<Future<?>> workersFuture, ExecutorService documentConsumerExecutor) {
        this.urlQueue = urlQueue;
        this.documentQueue = documentQueue;
        this.workersFuture = workersFuture;
        this.documentConsumerExecutor = documentConsumerExecutor;
    }

    @Override
    public void run() {
        while (true) {
            Document document = null;
            try {
                document = documentQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (document != null) {
                Future<?> future = documentConsumerExecutor.submit(new DocumentConsumer(document, urlQueue));
                workersFuture.addLast(future);
            }
        }
    }
}
