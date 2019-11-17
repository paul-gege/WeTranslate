package com.example.wetranslate;

import java.io.Serializable;

public class Favorites implements Serializable {
    private String input;
    private String result;
    private String language;
    private int id;

    public Favorites(String input, String result, String language){
        super();
        this.input = input;
        this.result = result;
        this.language = language;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
