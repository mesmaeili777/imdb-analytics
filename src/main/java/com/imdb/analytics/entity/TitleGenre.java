package com.imdb.analytics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "title_genres", indexes = {
        @Index(name = "idx_genre_tconst", columnList = "tconst"),
        @Index(name = "idx_genre_name", columnList = "genre")
})
public class TitleGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tconst", length = 20)
    private String tconst;

    @Column(name = "genre", length = 50)
    private String genre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}