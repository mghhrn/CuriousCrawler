package io.github.mghhrn.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
public class Product {

    private String name;
    private String price;
    private String description;
    private String extraInformation;

    @Override
    public String toString() {
        return  "Name: " + name + "\n" +
                "Price: " + price + "\n" +
                "Description: " + description + "\n" +
                "Extra information: " + extraInformation + "\n" +
                "^----------------------------------------------------------^\n";
    }
}
