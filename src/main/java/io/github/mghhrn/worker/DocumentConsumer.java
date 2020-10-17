package io.github.mghhrn.worker;

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
        System.out.println("Product name: " + elementText);

        element = document.selectFirst("span.price-container > span[id^=product-price-] > span");
        String productPrice = element.text();
        System.out.println("Product price: " + productPrice);

        elements = document.select("#description > div > div > p");
        StringBuilder productDescriptionSB = new StringBuilder();
        elements.forEach(e -> productDescriptionSB.append(e.text()).append(" "));
        System.out.println("Product description: " + productDescriptionSB.toString());

        elements = document.select("#product-attribute-specs-table > tbody > tr");
        StringBuilder productExtraInformationSB = new StringBuilder();
        int lastElementCounter = elements.size();
        for(Element e : elements) {
            productExtraInformationSB
                    .append(e.selectFirst("th").text())
                    .append(": ")
                    .append(e.selectFirst("td").text());
            lastElementCounter--;
            if (lastElementCounter != 0) {
                productExtraInformationSB.append(" | ");
            }
        }
        System.out.println("Product extra information: " + productExtraInformationSB.toString());
    }
}
