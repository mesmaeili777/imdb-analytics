package com.imdb.analytics.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "title_basics")
public class TitleBasics {
    @Id
    @Column(name = "tconst", length = 20)
    private String tconst;

    @Column(name = "title_type", length = 50)
    private String titleType;

    @Column(name = "primary_title", length = 500)
    private String primaryTitle;

    @Column(name = "original_title", length = 500)
    private String originalTitle;

    @Column(name = "is_adult")
    private Boolean isAdult;  // Changed from Integer to Boolean

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "runtime_minutes")
    private Integer runtimeMinutes;

    public String getTconst() { return tconst; }
    public void setTconst(String tconst) { this.tconst = tconst; }

    public String getTitleType() { return titleType; }
    public void setTitleType(String titleType) { this.titleType = titleType; }

    public String getPrimaryTitle() { return primaryTitle; }
    public void setPrimaryTitle(String primaryTitle) { this.primaryTitle = primaryTitle; }

    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }

    public Boolean getIsAdult() { return isAdult; }
    public void setIsAdult(Boolean isAdult) { this.isAdult = isAdult; }

    public Integer getStartYear() { return startYear; }
    public void setStartYear(Integer startYear) { this.startYear = startYear; }

    public Integer getEndYear() { return endYear; }
    public void setEndYear(Integer endYear) { this.endYear = endYear; }

    public Integer getRuntimeMinutes() { return runtimeMinutes; }
    public void setRuntimeMinutes(Integer runtimeMinutes) { this.runtimeMinutes = runtimeMinutes; }
}