package io.github.mghhrn.unit;

import io.github.mghhrn.database.DatabaseUtil;
import io.github.mghhrn.dispatcher.DocumentQueueDispatcher;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

import static io.github.mghhrn.CuriousCrawler.ROOT_STORAGE_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class DocumentQueueDispatcherTest {

    @Test
    public void given_aDocumentInQueue_when_run_workersFutureNotEmpty() throws IOException, InterruptedException {
        BlockingQueue<String> urlQueue = null;
        BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(2);
        ConcurrentLinkedDeque<Future<?>> workersFuture = new ConcurrentLinkedDeque<>();
        ExecutorService documentConsumerExecutor = Executors.newFixedThreadPool(1);
        String cacheDirectory = ROOT_STORAGE_PATH + File.separator + "cache";
        FileUtils.deleteDirectory(new File(ROOT_STORAGE_PATH));
        DatabaseUtil.createDirectoryIfNotExisted(ROOT_STORAGE_PATH);
        DatabaseUtil.createDirectoryIfNotExisted(cacheDirectory);
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("cache/breathe-easy-tank.html").getFile());
        Document document = Jsoup.parse(resourceFile, "UTF-8", "http://magento-test.finology.com.my/");
        documentQueue.put(document);
        DocumentQueueDispatcher documentQueueDispatcher = new DocumentQueueDispatcher(urlQueue, documentQueue, workersFuture, documentConsumerExecutor);
        Thread dispatcherThread = new Thread(documentQueueDispatcher);

        assertThat(workersFuture.isEmpty()).isTrue();

        dispatcherThread.start();
        Thread.sleep(777);

        assertThat(workersFuture.size()).isEqualTo(1);
    }
}
