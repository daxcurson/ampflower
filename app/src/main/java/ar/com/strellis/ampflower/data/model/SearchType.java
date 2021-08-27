package ar.com.strellis.ampflower.data.model;

public enum SearchType {
    NEWEST("newest"),
    HIGHEST("highest"),
    FREQUENT("frequent"),
    RECENT("recent"),
    FORGOTTEN("forgotten"),
    FLAGGED("flagged"),
    RANDOM("random");

    private final String searchType;
    SearchType(String searchType) {
        this.searchType=searchType;
    }
    public String getSearchType()
    {
        return searchType;
    }
}
