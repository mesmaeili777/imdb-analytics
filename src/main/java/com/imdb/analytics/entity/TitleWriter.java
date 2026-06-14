package com.imdb.analytics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "title_writers", indexes = {
        @Index(name = "idx_wri_tconst", columnList = "tconst"),
        @Index(name = "idx_wri_nconst", columnList = "nconst")
})
public class TitleWriter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tconst", length = 20)
    private String tconst;

    @Column(name = "nconst", length = 20)
    private String nconst;

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

    public String getNconst() {
        return nconst;
    }

    public void setNconst(String nconst) {
        this.nconst = nconst;
    }
}