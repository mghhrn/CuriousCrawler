package io.github.mghhrn;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.BlockingQueue;

public class DocumentConsumer implements Runnable {

    private final Document document;
    private final BlockingQueue<String> urlQueue;

    public DocumentConsumer(Document document, BlockingQueue<String> urlQueue) {
        this.document = document;
        this.urlQueue = urlQueue;
    }

    @Override
    public void run() {
        System.out.println("Starting to extract new links and product data from: " + document.title());

        Elements elements = document.select("#maincontent > div.columns > div > div.product-info-main > div.page-title-wrapper.product > h1 > span");
        Element element = elements.get(0);
        String elementText = element.text();
        System.out.printf("Product name: " + elementText);

    }
}
