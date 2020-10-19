package io.github.mghhrn;

import io.github.mghhrn.database.DatabaseUtil;
import io.github.mghhrn.dispatcher.DocumentQueueDispatcher;
import io.github.mghhrn.dispatcher.UrlQueueDispatcher;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CuriousCrawler {

    public static void main( String[] args ) {

        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(10);
        ConcurrentLinkedDeque<Future<?>> workersFuture = new ConcurrentLinkedDeque<>();

        String seedLink = "http://magento-test.finology.com.my/breathe-easy-tank.html";
        try {
            urlQueue.put(seedLink);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        DatabaseUtil.initializeDatabase();

        ExecutorService documentProviderExecutor = Executors.newFixedThreadPool(2);//2 Threads
        ExecutorService documentConsumerExecutor = Executors.newFixedThreadPool(2);//2 Threads

        Thread urlDispatcherThread = new Thread(new UrlQueueDispatcher(urlQueue, documentQueue, workersFuture, documentProviderExecutor));
        Thread documentDispatcherThread = new Thread(new DocumentQueueDispatcher(urlQueue, documentQueue, workersFuture, documentConsumerExecutor));

        urlDispatcherThread.start();
        documentDispatcherThread.start();

        waitAndFinish(urlQueue, documentQueue, urlDispatcherThread, documentDispatcherThread, workersFuture);
    }


    private static void waitAndFinish(BlockingQueue<String> urlQueue,
                                      BlockingQueue<Document> documentQueue,
                                      Thread urlDispatcherThread,
                                      Thread documentDispatcherThread,
                                      ConcurrentLinkedDeque<Future<?>> workersFuture) {
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            removeFinishedFutures(workersFuture);
            if (urlQueue.isEmpty() &&
                    documentQueue.isEmpty() &&
                    urlDispatcherThread.getState().equals(Thread.State.WAITING) &&
                    documentDispatcherThread.getState().equals(Thread.State.WAITING) &&
                    workersFuture.isEmpty()) {
                System.exit(0);
            }
        }
    }

    private static void removeFinishedFutures(ConcurrentLinkedDeque<Future<?>> workersFuture) {
        List<Future<?>> finishedTasks = new ArrayList<>();
        workersFuture.forEach(f -> {
            if (f.isDone())
                finishedTasks.add(f);
        });
        finishedTasks.forEach(f -> workersFuture.remove(f));
    }


}
