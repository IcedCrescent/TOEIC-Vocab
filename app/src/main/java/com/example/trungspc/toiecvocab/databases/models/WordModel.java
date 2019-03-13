package com.example.trungspc.toiecvocab.databases.models;

/**
 * Created by Trung's PC on 3/11/2018.
 */

public class WordModel {
    private int id;
    private String origin;
    private String explanation;
    private String type;
    private String pronunciation;
    private String imageUrl;
    private String example;
    private String exampleTranslation;
    private int topicID;
    private int level;

    public WordModel(int id, String origin, String explanation, String type, String pronunciation, String imageUrl, String example, String exampleTranslation, int topicID, int level) {
        this.id = id;
        this.origin = origin;
        this.explanation = explanation;
        this.type = type;
        this.pronunciation = pronunciation;
        this.imageUrl = imageUrl;
        this.example = example;
        this.exampleTranslation = exampleTranslation;
        this.topicID = topicID;
        this.level = level;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setexplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public void setExampleTranslation(String exampleTranslation) {
        this.exampleTranslation = exampleTranslation;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {

        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getType() {
        return type;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getExample() {
        return example;
    }

    public String getExampleTranslation() {
        return exampleTranslation;
    }

    public int getTopicID() {
        return topicID;
    }

    public int getLevel() {
        return level;
    }
}
