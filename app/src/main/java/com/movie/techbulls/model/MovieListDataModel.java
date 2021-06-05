package com.movie.techbulls.model;

public class MovieListDataModel {

    String imageUrl, movieName, movieYear;

    public MovieListDataModel(String imageUrl, String movieName, String movieYear) {
        this.imageUrl = imageUrl;
        this.movieName = movieName;
        this.movieYear = movieYear;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(String movieYear) {
        this.movieYear = movieYear;
    }
}
