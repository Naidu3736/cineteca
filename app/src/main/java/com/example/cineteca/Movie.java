package com.example.cineteca;

public class Movie {
    private long id;
    private String title;
    private double rating;
    private String posterUrl;

    public Movie(String title, double rating, String posterUrl) {
        this(0, title, rating, posterUrl);
    }

    public Movie(long id, String title, double rating, String posterUrl) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.posterUrl = posterUrl;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public double getRating() { return rating; }
    public String getPosterUrl() { return posterUrl; }
}