package com.movie.techbulls.model;

public class MovieListDataModel {

    String imageUrl, movieName, movieYear, type;

    public MovieListDataModel(String imageUrl, String movieName, String movieYear, String type) {
        this.imageUrl = imageUrl;
        this.movieName = movieName;
        this.movieYear = movieYear;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
