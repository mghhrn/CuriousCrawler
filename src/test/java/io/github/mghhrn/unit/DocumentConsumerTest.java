package io.github.mghhrn.unit;

import io.github.mghhrn.worker.DocumentConsumer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.*;

public class DocumentConsumerTest {

    @Test
    public void given_aDocument_when_extractOtherProductUrlsFrom_then_return4Urls() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("cache/breathe-easy-tank.html").getFile());
        Document document = Jsoup.parse(resourceFile, "UTF-8", "http://magento-test.finology.com.my/");
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        DocumentConsumer documentConsumer = new DocumentConsumer(document, urlQueue);
        Method method = documentConsumer.getClass().getDeclaredMethod("extractOtherProductUrlsFrom", Document.class);
        method.setAccessible(true);
        Object result = method.invoke(documentConsumer, document);
        List<String> urlList = (List<String>) result;

        assertThat(urlList.size()).isEqualTo(4);
    }

    @Test
    public void given_aUrlListAndUrlQueue_when_addUrlsToUrlQueue_then_nonEmptyUrlQueue() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("cache/breathe-easy-tank.html").getFile());
        Document document = Jsoup.parse(resourceFile, "UTF-8", "http://magento-test.finology.com.my/");
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        List<String> urlList = new ArrayList<>();
        urlList.add("https://magento-test.finology.com.my/mimi-all-purpose-short.html");
        urlList.add("https://magento-test.finology.com.my/ana-running-short.html");
        DocumentConsumer documentConsumer = new DocumentConsumer(document, urlQueue);
        Method method = documentConsumer.getClass().getDeclaredMethod("addUrlsToUrlQueue", List.class, BlockingQueue.class);
        method.setAccessible(true);

        assertThat(urlQueue.isEmpty()).isTrue();

        method.invoke(documentConsumer, urlList, urlQueue);

        assertThat(urlQueue.isEmpty()).isFalse();
        assertThat(urlQueue.size()).isEqualTo(2);
    }

    @Test
    public void given_aDocument_when_extractProductName_then_theProductName() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("cache/breathe-easy-tank.html").getFile());
        Document document = Jsoup.parse(resourceFile, "UTF-8", "http://magento-test.finology.com.my/");
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        DocumentConsumer documentConsumer = new DocumentConsumer(document, urlQueue);
        Method method = documentConsumer.getClass().getDeclaredMethod("extractProductName", Document.class);
        method.setAccessible(true);
        Object result = method.invoke(documentConsumer, document);
        String productName = (String) result;

        assertThat(productName).isEqualTo("Breathe-Easy Tank");
    }

    @Test
    public void given_aDocument_when_extractProductPrice_then_theProductPrice() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("cache/breathe-easy-tank.html").getFile());
        Document document = Jsoup.parse(resourceFile, "UTF-8", "http://magento-test.finology.com.my/");
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        DocumentConsumer documentConsumer = new DocumentConsumer(document, urlQueue);
        Method method = documentConsumer.getClass().getDeclaredMethod("extractProductPrice", Document.class);
        method.setAccessible(true);
        Object result = method.invoke(documentConsumer, document);
        String productPrice = (String) result;

        assertThat(productPrice).isEqualTo("$34.00");
    }

    @Test
    public void given_aDocument_when_extractProductDescription_then_theProductPrice() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("cache/breathe-easy-tank.html").getFile());
        Document document = Jsoup.parse(resourceFile, "UTF-8", "http://magento-test.finology.com.my/");
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        DocumentConsumer documentConsumer = new DocumentConsumer(document, urlQueue);
        Method method = documentConsumer.getClass().getDeclaredMethod("extractProductDescription", Document.class);
        method.setAccessible(true);
        Object result = method.invoke(documentConsumer, document);
        String productDescription = (String) result;

        String expectedProductDescription = "The Breathe Easy Tank is so soft, lightweight, and comfortable, you won't even know it's there -- until its high-tech Cocona® fabric starts wicking sweat away from your body to help you stay dry and focused. Layer it over your favorite sports bra and get moving. • Machine wash/dry. • Cocona® fabric. ";
        assertThat(productDescription).isEqualTo(expectedProductDescription);
    }

    @Test
    public void given_aDocument_when_extractProductExtraInformation_then_theProductPrice() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("cache/breathe-easy-tank.html").getFile());
        Document document = Jsoup.parse(resourceFile, "UTF-8", "http://magento-test.finology.com.my/");
        BlockingQueue<String> urlQueue = new ArrayBlockingQueue<>(10);
        DocumentConsumer documentConsumer = new DocumentConsumer(document, urlQueue);
        Method method = documentConsumer.getClass().getDeclaredMethod("extractProductExtraInformation", Document.class);
        method.setAccessible(true);
        Object result = method.invoke(documentConsumer, document);
        String productExtraInformation = (String) result;

        String expectedProductExtraInformation = "Style: Tank | Material: Cocona® performance fabric, Cotton | Pattern: Solid | Climate: Indoor, Warm";
        assertThat(productExtraInformation).isEqualTo(expectedProductExtraInformation);
    }
}
