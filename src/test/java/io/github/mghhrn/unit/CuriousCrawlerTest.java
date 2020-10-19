package io.github.mghhrn.unit;

import io.github.mghhrn.CuriousCrawler;
import io.github.mghhrn.dispatcher.DocumentQueueDispatcher;
import io.github.mghhrn.dispatcher.UrlQueueDispatcher;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CuriousCrawlerTest {

    @Test
    public void given_hardCodedLink_when_initializeSeedLink_then_linkIsAvailableInQueue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(2);
        CuriousCrawler curiousCrawler = new CuriousCrawler();
        Method method = curiousCrawler.getClass().getDeclaredMethod("initializeSeedLink", BlockingQueue.class);
        method.setAccessible(true);
        method.invoke(curiousCrawler, urlQueue);
        String seedLink = urlQueue.take();
        assertThat(seedLink).isEqualTo("http://magento-test.finology.com.my/breathe-easy-tank.html");
    }

    @Test
    public void given_aSimpleMachine_when_getBestExecutorThreadNumber_then_returnValidNumber() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        CuriousCrawler curiousCrawler = new CuriousCrawler();
        Method method = curiousCrawler.getClass().getDeclaredMethod("getBestExecutorThreadNumber", null);
        method.setAccessible(true);
        Object result = method.invoke(curiousCrawler, null);
        Integer threadNumber = (Integer) result;

        assertThat(threadNumber).isGreaterThan(0);
        assertThat(threadNumber).isLessThanOrEqualTo(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void given_providedData_when_waitAndFinish_then_systemExit0() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(10);
        ConcurrentLinkedDeque<Future<?>> workersFuture = new ConcurrentLinkedDeque<>();
        ExecutorService documentProviderExecutor = Executors.newFixedThreadPool(2);
        ExecutorService documentConsumerExecutor = Executors.newFixedThreadPool(2);
        Thread urlDispatcherThread = new Thread(new UrlQueueDispatcher(urlQueue, documentQueue, workersFuture, documentProviderExecutor));
        Thread documentDispatcherThread = new Thread(new DocumentQueueDispatcher(urlQueue, documentQueue, workersFuture, documentConsumerExecutor));
        urlDispatcherThread.start();
        documentDispatcherThread.start();
        CuriousCrawler curiousCrawler = new CuriousCrawler();
        Method method = curiousCrawler.getClass().getDeclaredMethod("waitForFinish", BlockingQueue.class, BlockingQueue.class, Thread.class, Thread.class, ConcurrentLinkedDeque.class, Long.class);
        method.setAccessible(true);
        Object result = method.invoke(curiousCrawler, urlQueue, documentQueue, urlDispatcherThread, documentDispatcherThread, workersFuture, 0L);
        Boolean returnedValue = (Boolean) result;

        assertThat(returnedValue).isTrue();
    }

    @Test
    public void given_nonEmptyConcurrentLinkedDequeWithFinishedFutures_when_removeFinishedFutures_then_emptyConcurrentLinkedDeque() throws InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ConcurrentLinkedDeque<Future<?>> workersFuture = new ConcurrentLinkedDeque<>();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<?> future1 = executorService.submit(() -> {});
        Future<?> future2 = executorService.submit(() -> {});
        workersFuture.addLast(future1);
        workersFuture.addLast(future2);
        Thread.sleep(500);
        CuriousCrawler curiousCrawler = new CuriousCrawler();
        Method method = curiousCrawler.getClass().getDeclaredMethod("removeFinishedFutures", ConcurrentLinkedDeque.class);
        method.setAccessible(true);
        method.invoke(curiousCrawler, workersFuture);

        assertThat(workersFuture.isEmpty()).isTrue();
    }
}
