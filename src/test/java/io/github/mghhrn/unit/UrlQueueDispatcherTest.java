package io.github.mghhrn.unit;

import io.github.mghhrn.dispatcher.UrlQueueDispatcher;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

public class UrlQueueDispatcherTest {

    @Test
    public void given_aUrlInQueue_when_run_workersFutureNotEmpty() throws InterruptedException {
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(2);
        BlockingQueue<Document> documentQueue = null;
        ConcurrentLinkedDeque<Future<?>> workersFuture = new ConcurrentLinkedDeque<>();
        ExecutorService documentProviderExecutor = Executors.newFixedThreadPool(1);
        urlQueue.put("http://magento-test.finology.com.my/breathe-easy-tank.html");
        UrlQueueDispatcher urlQueueDispatcher = new UrlQueueDispatcher(urlQueue, documentQueue, workersFuture, documentProviderExecutor);
        Thread dispatcherThread = new Thread(urlQueueDispatcher);

        assertThat(workersFuture.isEmpty()).isTrue();

        dispatcherThread.start();
        Thread.sleep(777);

        assertThat(workersFuture.size()).isEqualTo(1);
    }
}
