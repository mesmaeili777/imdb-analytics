package com.imdb.analytics.dto;

public class CommonTitleForActorsDTO {
    private String tconst;
    private String primaryTitle;
    private Integer startYear;
    private String actor1Name;
    private String actor2Name;
    private String actor1Character;
    private String actor2Character;

    public CommonTitleForActorsDTO(String tconst, String primaryTitle, Integer startYear,
                                   String actor1Name, String actor2Name,
                                   String actor1Character, String actor2Character) {
        this.tconst = tconst;
        this.primaryTitle = primaryTitle;
        this.startYear = startYear;
        this.actor1Name = actor1Name;
        this.actor2Name = actor2Name;
        this.actor1Character = actor1Character;
        this.actor2Character = actor2Character;
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

    public String getActor1Name() {
        return actor1Name;
    }

    public void setActor1Name(String actor1Name) {
        this.actor1Name = actor1Name;
    }

    public String getActor2Name() {
        return actor2Name;
    }

    public void setActor2Name(String actor2Name) {
        this.actor2Name = actor2Name;
    }

    public String getActor1Character() {
        return actor1Character;
    }

    public void setActor1Character(String actor1Character) {
        this.actor1Character = actor1Character;
    }

    public String getActor2Character() {
        return actor2Character;
    }

    public void setActor2Character(String actor2Character) {
        this.actor2Character = actor2Character;
    }
}