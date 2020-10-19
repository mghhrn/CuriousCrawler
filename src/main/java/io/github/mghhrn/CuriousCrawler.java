package io.github.mghhrn;

import io.github.mghhrn.dispatcher.DocumentQueueDispatcher;
import io.github.mghhrn.dispatcher.UrlQueueDispatcher;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static io.github.mghhrn.database.DatabaseUtil.initializeDatabase;

public class CuriousCrawler {

    public static final String ROOT_STORAGE_PATH = FileUtils.getTempDirectoryPath() + File.separator + "curious-crawler";

    public static void main( String[] args ) {
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(10);
        ConcurrentLinkedDeque<Future<?>> workersFuture = new ConcurrentLinkedDeque<>();
        int threadNumber = getBestExecutorThreadNumber();
        ExecutorService documentProviderExecutor = Executors.newFixedThreadPool(threadNumber);
        ExecutorService documentConsumerExecutor = Executors.newFixedThreadPool(threadNumber);
        Thread urlDispatcherThread = new Thread(new UrlQueueDispatcher(urlQueue, documentQueue, workersFuture, documentProviderExecutor));
        Thread documentDispatcherThread = new Thread(new DocumentQueueDispatcher(urlQueue, documentQueue, workersFuture, documentConsumerExecutor));

        initializeSeedLink(urlQueue);
        initializeDatabase();

        urlDispatcherThread.start();
        documentDispatcherThread.start();

        waitAndFinish(urlQueue, documentQueue, urlDispatcherThread, documentDispatcherThread, workersFuture);
    }


    private static void initializeSeedLink(BlockingQueue<String> urlQueue) {
        String seedLink = "http://magento-test.finology.com.my/breathe-easy-tank.html";
        try {
            urlQueue.put(seedLink);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }


    private static int getBestExecutorThreadNumber() {
        int executorThreadsNumber = Runtime.getRuntime().availableProcessors() / 2;
        return (executorThreadsNumber == 0) ? 1 : executorThreadsNumber;
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
                System.out.println("Crawling has finished!");
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
        finishedTasks.forEach(workersFuture::remove);
    }
}
