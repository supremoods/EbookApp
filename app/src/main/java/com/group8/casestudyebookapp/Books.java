package com.group8.casestudyebookapp;

public class Books {

    public String title, author, dateAdded, category, fileurl, filename;



    public Books(String title, String author, String dateAdded, String category, String fileurl, String filename) {
        this.title = title;
        this.author = author;
        this.dateAdded = dateAdded;
        this.category = category;
        this.fileurl = fileurl;
        this.filename = filename;
    }

    public Books() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
