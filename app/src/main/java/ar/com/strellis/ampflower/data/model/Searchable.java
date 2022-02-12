package ar.com.strellis.ampflower.data.model;

public interface Searchable<T> {
    T getId();

    /**
     * Gets the search page where this item is expected to be found. This is supposed
     * to be a transient value, generated when the search is performed by the Pager
     * @return page where this item is contained
     */
    int getPage();

    /**
     * Sets the search page where this item can be found
     * @param page the page where the item can be found
     */
    void setPage(int page);
}
