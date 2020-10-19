package io.github.mghhrn.unit;

import io.github.mghhrn.database.DatabaseUtil;
import io.github.mghhrn.worker.DocumentProvider;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static io.github.mghhrn.CuriousCrawler.ROOT_STORAGE_PATH;
import static org.assertj.core.api.Assertions.*;

public class DocumentProviderTest {

    @Test
    public void given_aVisitedUrl_when_run_then_emptyDocumentQueue() {
        String url = "www.random.com/index.html";
        BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(2);
        ConcurrentHashMap<Integer, String> visitedUrls = new ConcurrentHashMap<>();
        visitedUrls.putIfAbsent(url.hashCode(), url);
        DocumentProvider documentProvider = new DocumentProvider(url, documentQueue, visitedUrls);
        ExecutorService documentProviderExecutor = Executors.newFixedThreadPool(1);
        documentProviderExecutor.submit(documentProvider);

        assertThat(documentQueue.isEmpty()).isTrue();
    }

    @Test
    public void given_nonVisitedUrl_when_run_then_nonEmptyDocumentQueue() throws InterruptedException {
        String url = "http://magento-test.finology.com.my/breathe-easy-tank.html";
        BlockingQueue<Document> documentQueue = new ArrayBlockingQueue<>(2);
        ConcurrentHashMap<Integer, String> visitedUrls = new ConcurrentHashMap<>();
        DocumentProvider documentProvider = new DocumentProvider(url, documentQueue, visitedUrls);
        ExecutorService documentProviderExecutor = Executors.newFixedThreadPool(1);
        documentProviderExecutor.submit(documentProvider);
        Thread.sleep(300);

        assertThat(documentQueue.isEmpty()).isFalse();
    }

    @Test
    public void given_nonCachedLinkAndEmptyCache_when_downloadDocumentAndCacheToTheFile_then_saveFileInCacheAndReturnNonNullDocument() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String url = "http://magento-test.finology.com.my/breathe-easy-tank.html";
        FileUtils.deleteDirectory(new File(ROOT_STORAGE_PATH));
        DatabaseUtil.createDirectoryIfNotExisted(ROOT_STORAGE_PATH);
        String cacheDirectory = ROOT_STORAGE_PATH + File.separator + "cache";
        File cachedFile = new File(cacheDirectory + File.separator + "breathe-easy-tank.html");

        assertThat(cachedFile.exists()).isFalse();

        DocumentProvider documentProvider = new DocumentProvider(null, null, null);
        Method method = documentProvider.getClass().getDeclaredMethod("downloadDocumentAndCacheToTheFile", String.class, File.class);
        method.setAccessible(true);
        Object result = method.invoke(documentProvider, url, cachedFile);

        assertThat(result).isNotNull();

        Document document = (Document) result;

        assertThat(document.title()).isEqualTo("Breathe-Easy Tank");
        assertThat(cachedFile.exists()).isTrue();
    }
}
