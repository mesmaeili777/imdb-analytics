package com.imdb.analytics.dto;

public class BestTitlePerYearDTO {
    private Integer year;
    private String tconst;
    private String primaryTitle;
    private Double averageRating;
    private Integer numVotes;
    private String genres;

    public BestTitlePerYearDTO(Integer year, String tconst, String primaryTitle,
                               Double averageRating, Integer numVotes, String genres) {
        this.year = year;
        this.tconst = tconst;
        this.primaryTitle = primaryTitle;
        this.averageRating = averageRating;
        this.numVotes = numVotes;
        this.genres = genres;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public String getPrimaryTitle() {
        return primaryTitle;
    }

    public void setPrimaryTitle(String primaryTitle) {
        this.primaryTitle = primaryTitle;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}