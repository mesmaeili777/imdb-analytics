package com.imdb.analytics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "title_ratings", indexes = {
        @Index(name = "idx_rating_votes", columnList = "num_votes")
})
public class TitleRating {
    @Id
    @Column(name = "tconst", length = 20)
    private String tconst;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "num_votes")
    private Integer numVotes;

    public TitleRating() {}

    public TitleRating(String tconst, Double averageRating, Integer numVotes) {
        this.tconst = tconst;
        this.averageRating = averageRating;
        this.numVotes = numVotes;
    }

    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
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
}