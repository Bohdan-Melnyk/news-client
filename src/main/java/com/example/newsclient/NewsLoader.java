package com.example.newsclient;

import com.example.newsclient.model.News;
import javafx.collections.ObservableList;

public interface NewsLoader {

    /**
     * Loads and retrieves news entries from the specified URL.
     *
     * @param url the URL from which to load the news entries
     * @return an observable list of news entries retrieved from the specified URL
     */
    ObservableList<News> loadNewsByURL(String url);
}
