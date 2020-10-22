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
    public static int QUEUE_LENGTH = 30;

    public static void main( String[] args ) {
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(QUEUE_LENGTH);
        BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(QUEUE_LENGTH);
        ConcurrentLinkedDeque<Future<?>> workersFuture = new ConcurrentLinkedDeque<>();
        int threadNumber = getBestExecutorThreadNumber();
        ExecutorService documentProviderExecutor = Executors.newFixedThreadPool(threadNumber);
        ExecutorService documentConsumerExecutor = Executors.newFixedThreadPool(threadNumber);
        UrlQueueDispatcher urlQueueDispatcher = new UrlQueueDispatcher(urlQueue, documentQueue, workersFuture, documentProviderExecutor);
        DocumentQueueDispatcher documentQueueDispatcher = new DocumentQueueDispatcher(urlQueue, documentQueue, workersFuture, documentConsumerExecutor);
        Thread urlDispatcherThread = new Thread(urlQueueDispatcher);
        Thread documentDispatcherThread = new Thread(documentQueueDispatcher);

        initializeSeedLink(urlQueue);
        initializeDatabase();

        urlDispatcherThread.start();
        documentDispatcherThread.start();

        waitForFinish(urlQueue, documentQueue, urlDispatcherThread, documentDispatcherThread, workersFuture, 2000L);
        System.out.printf("Crawling has been finished!\nTotal number of unique products that are extracted is %d.\n", urlQueueDispatcher.currentNumberOfVisitedUrls());
        System.exit(0);
    }


    private static void initializeSeedLink(BlockingQueue<String> urlQueue) {
        String seedLink = "http://magento-test.finology.com.my/breathe-easy-tank.html";
        try {
            urlQueue.put(seedLink);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static int getBestExecutorThreadNumber() {
        int executorThreadsNumber = Runtime.getRuntime().availableProcessors() / 2;
        return (executorThreadsNumber == 0) ? 1 : executorThreadsNumber;
    }


    private static boolean waitForFinish(BlockingQueue<String> urlQueue,
                                      BlockingQueue<Document> documentQueue,
                                      Thread urlDispatcherThread,
                                      Thread documentDispatcherThread,
                                      ConcurrentLinkedDeque<Future<?>> workersFuture,
                                      Long iterationWaitDuration) {
        while (true) {
            try {
                Thread.sleep(iterationWaitDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            removeFinishedFutures(workersFuture);
            if (urlQueue.isEmpty() &&
                    documentQueue.isEmpty() &&
                    urlDispatcherThread.getState().equals(Thread.State.WAITING) &&
                    documentDispatcherThread.getState().equals(Thread.State.WAITING) &&
                    workersFuture.isEmpty()) {
                return true;
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
