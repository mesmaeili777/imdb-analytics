package com.imdb.analytics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "title_principals", indexes = {
        @Index(name = "idx_princ_tconst", columnList = "tconst"),
        @Index(name = "idx_princ_nconst", columnList = "nconst"),
        @Index(name = "idx_princ_nconst_category", columnList = "nconst, category")
})
public class TitlePrincipals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tconst", length = 20)
    private String tconst;

    @Column(name = "ordering")
    private Integer ordering;

    @Column(name = "nconst", length = 20)
    private String nconst;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "job", length = 500)
    private String job;

    @Column(name = "characters", length = 1000)
    private String characters;

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

    public Integer getOrdering() {
        return ordering;
    }

    public void setOrdering(Integer ordering) {
        this.ordering = ordering;
    }

    public String getNconst() {
        return nconst;
    }

    public void setNconst(String nconst) {
        this.nconst = nconst;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }
}