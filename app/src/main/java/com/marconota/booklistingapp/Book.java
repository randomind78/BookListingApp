package com.marconota.booklistingapp;

class Book {

    private String title;
    private String authors;
    private String infoUrl;
    private double rating;
    private String thumbnail;

    public Book() {
    }

    public Book(String title, String authors, String infoUrl, double rating, String thumbnail) {

        this.title = title;
        this.authors = authors;
        this.infoUrl = infoUrl;
        this.rating = rating;
        this.thumbnail = thumbnail;
    }


    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return authors;
    }

    public String getUrl() {
        return infoUrl;
    }

    public double getRating() {
        return rating;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public String toString() {
        return "BOOK title " + title + "\nAuthor:" + authors + "\nURL " + infoUrl + "\nRating " + rating + "\nThumbnail " + thumbnail;
    }
}
