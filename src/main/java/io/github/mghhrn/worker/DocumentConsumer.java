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

    private static final String productNameCssQuery = "#maincontent > div.columns > div > div.product-info-main > div.page-title-wrapper.product > h1 > span";
    private static final String productPriceCssQuery = "span.price-container > span[id^=product-price-] > span";
    private static final String productDescriptionCssQuery = "#description > div > div > p";
    private static final String productExtraInformationCssQuery = "#product-attribute-specs-table > tbody > tr";
    private static final String otherLinksCssQuery = "#maincontent > div.columns > div > div.block.related > div.block-content.content > div > ol > li";

    private final Document document;
    private final BlockingQueue<String> urlQueue;

    public DocumentConsumer(Document document, BlockingQueue<String> urlQueue) {
        this.document = document;
        this.urlQueue = urlQueue;
    }

    @Override
    public void run() {
        List<String> newUrls = extractOtherProductUrlsFrom(document);
        addUrlsToUrlQueue(newUrls, urlQueue);
        String productName = extractProductName(document);
        String productPrice = extractProductPrice(document);
        String productDescription = extractProductDescription(document);
        String productExtraInformation = extractProductExtraInformation(document);
        Product product = new Product(productName, productPrice, productDescription, productExtraInformation);
        ProductDao.save(product);
        System.out.println(product);
    }

    private List<String> extractOtherProductUrlsFrom(Document document) {
        List<String> urlList = new ArrayList<>();
        Elements elements = document.select(otherLinksCssQuery);
        for (Element e : elements) {
            Element linkElement = e.selectFirst("div > a");
            String newUrl = linkElement.attr("href");
            if (!StringUtil.isBlank(newUrl)) {
                urlList.add(newUrl);
            }
        }
        return urlList;
    }

    private void addUrlsToUrlQueue(List<String> urlList, BlockingQueue<String> urlQueue) {
        for (String url : urlList) {
            try {
                urlQueue.put(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String extractProductName(Document document) {
        return document.selectFirst(productNameCssQuery).text();
    }

    private String extractProductPrice(Document document) {
        return document.selectFirst(productPriceCssQuery).text();
    }

    private String extractProductDescription(Document document) {
        Elements elements;
        elements = document.select(productDescriptionCssQuery);
        StringBuilder productDescriptionSB = new StringBuilder();
        elements.forEach(e -> productDescriptionSB.append(e.text()).append(" "));
        return productDescriptionSB.toString();
    }

    private String extractProductExtraInformation(Document document) {
        Elements elements;
        elements = document.select(productExtraInformationCssQuery);
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
        return productExtraInformationSB.toString();
    }
}
