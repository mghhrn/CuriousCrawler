package io.github.mghhrn.worker;

import io.github.mghhrn.database.ProductDao;
import io.github.mghhrn.entity.Product;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
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
//        System.out.println("Starting to extract new links and product data from: " + document.title());

        List<String> newUrls = extractOtherProductUrlsFrom(document);
        for (String newUrl : newUrls) {
            try {
                urlQueue.put(newUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Elements elements = document.select("#maincontent > div.columns > div > div.product-info-main > div.page-title-wrapper.product > h1 > span");
        Element element = elements.get(0);
        String procutName = element.text();

        element = document.selectFirst("span.price-container > span[id^=product-price-] > span");
        String productPrice = element.text();

        elements = document.select("#description > div > div > p");
        StringBuilder productDescriptionSB = new StringBuilder();
        elements.forEach(e -> productDescriptionSB.append(e.text()).append(" "));

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

        Product product = new Product(procutName, productPrice, productDescriptionSB.toString(), productExtraInformationSB.toString());
        ProductDao.save(product);
        System.out.println(product);
    }

    private List<String> extractOtherProductUrlsFrom(Document document) {
        List<String> urlList = new ArrayList<>();
        Elements elements = document.select("#maincontent > div.columns > div > div.block.related > div.block-content.content > div > ol > li");
        for (Element e : elements) {
            Element linkElement = e.selectFirst("div > a");
            String newUrl = linkElement.attr("href");
            if (!StringUtil.isBlank(newUrl)) {
                urlList.add(newUrl);
            }
        }
        return urlList;
    }
}
