package com.example.newsclient;

import com.example.newsclient.model.News;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloController {

    RestTemplate restTemplate = new RestTemplate();

    private ObservableList<News> newsList = FXCollections.observableArrayList();
    private int currentIndex = 0;

    @FXML
    private ComboBox<String> timeFilter;
    @FXML
    private Label headlineLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;

    @FXML
    public void initialize() {
        newsList.addAll(loadNews());

        previousButton.setOnAction(event -> showPreviousNews());
        nextButton.setOnAction(event -> showNextNews());
        timeFilter.setOnAction(event -> filterNewsByTime(timeFilter.getValue()));

        showCurrentNews();
    }

    private List<News> loadNews() {
        var newsArray = restTemplate.getForObject("http://localhost:8080/api/news-test", News[].class);
        return Arrays.asList(newsArray);
    }

    private void showCurrentNews() {
        if (!newsList.isEmpty()) {
            News currentNews = newsList.get(currentIndex);
            headlineLabel.setText(currentNews.getHeadline());
            descriptionLabel.setText(currentNews.getDescription());
        }
    }

    private void showPreviousNews() {
        if (currentIndex > 0) {
            currentIndex--;
            showCurrentNews();
        }
    }

    private void showNextNews() {
        if (currentIndex < newsList.size() - 1) {
            currentIndex++;
            showCurrentNews();
        }
    }

    private void filterNewsByTime(String timePeriod) {
        List<News> filteredNews = new ArrayList<>();
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime dayStart = LocalTime.of(12, 0);
        LocalTime eveningStart = LocalTime.of(18, 0);

        for (News news : newsList) {
            LocalTime publicationTime = news.getPublicationTime();
            if (timePeriod.equals("Morning") && publicationTime.isAfter(morningStart) && publicationTime.isBefore(dayStart)) {
                filteredNews.add(news);
            } else if (timePeriod.equals("Day") && publicationTime.isAfter(dayStart) && publicationTime.isBefore(eveningStart)) {
                filteredNews.add(news);
            } else if (timePeriod.equals("Evening") && publicationTime.isAfter(eveningStart)) {
                filteredNews.add(news);
            }
        }

        if (!filteredNews.isEmpty()) {
            newsList.setAll(filteredNews);
            currentIndex = 0;
            showCurrentNews();
        }
    }
}