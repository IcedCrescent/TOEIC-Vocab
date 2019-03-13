package com.example.trungspc.toiecvocab.databases.models;

/**
 * Created by Trung's PC on 3/4/2018.
 */

public class CategoryModel {

    private String name;
    private String color;

    public CategoryModel(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
