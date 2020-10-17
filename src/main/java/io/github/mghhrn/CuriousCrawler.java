package io.github.mghhrn;

import io.github.mghhrn.database.DatabaseUtil;
import io.github.mghhrn.dispatcher.DocumentQueueDispatcher;
import io.github.mghhrn.dispatcher.UrlQueueDispatcher;
import org.jsoup.nodes.Document;

import java.util.Scanner;
import java.util.concurrent.*;

public class CuriousCrawler {

    private static final BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
    private static final BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(10);

    public static final ConcurrentHashMap<Integer, String> visitedUrls= new ConcurrentHashMap<>();

    public static void main( String[] args ) {

//        System.out.println("Enter the seed link:");
//        Scanner scanner = new Scanner(System.in);
//        String seedLink = scanner.nextLine();
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

        Thread urlQueueDispatcherThread = new Thread(new UrlQueueDispatcher(urlQueue, documentQueue, documentProviderExecutor));
        Thread documentQueueDispatcherThread = new Thread(new DocumentQueueDispatcher(urlQueue, documentQueue, documentConsumerExecutor));

        urlQueueDispatcherThread.start();
        documentQueueDispatcherThread.start();
    }

    private void initializeDatabase() {

    }
}
