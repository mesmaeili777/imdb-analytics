package com.imdb.analytics.dto;

public class TitleDirectorWriterDTO {
    private String tconst;
    private String primaryTitle;
    private Integer startYear;
    private String directorName;
    private Integer directorBirthYear;

    public TitleDirectorWriterDTO(String tconst, String primaryTitle, Integer startYear,
                                  String directorName, Integer directorBirthYear) {
        this.tconst = tconst;
        this.primaryTitle = primaryTitle;
        this.startYear = startYear;
        this.directorName = directorName;
        this.directorBirthYear = directorBirthYear;
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

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public Integer getDirectorBirthYear() {
        return directorBirthYear;
    }

    public void setDirectorBirthYear(Integer directorBirthYear) {
        this.directorBirthYear = directorBirthYear;
    }
}