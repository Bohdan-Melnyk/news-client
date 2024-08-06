package com.example.newsclient;

import com.example.newsclient.model.News;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class NewsController implements Initializable {

    RestTemplate restTemplate = new RestTemplate();

    private ObservableList<News> newsList = FXCollections.observableArrayList();

    private final ObservableList<News> commonList = FXCollections.observableArrayList();

    private int currentIndex = 0;

    private static final String MORNING = "Morning";

    private static final String DAY = "Day";

    private static final String EVENING = "Evening";

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Label label;

    @FXML
    private ListView<String> listView;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextFlow textFlow;

    @FXML
    void nextButtonClick(ActionEvent event) {
        if (currentIndex == 0) {
            listView.getSelectionModel().selectFirst();
        }
        if (currentIndex < commonList.size() - 1) {
            currentIndex++;
            listView.getSelectionModel().selectNext();
            showCurrentNews();
        }

    }

    @FXML
    void previousButtonClick(ActionEvent event) {
        if (currentIndex > 0) {
            currentIndex--;
            showCurrentNews();
            listView.getSelectionModel().selectPrevious();
        }

    }

    @FXML
    void comboBoxChoose(ActionEvent event) {
        currentIndex = 0;
        clearAllLists();
        groupCommonListByDayTime();
        showCurrentNews();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComboBox();
        newsList = loadNews();
    }

    private ObservableList<News> loadNews() {
        var newsArray = restTemplate.getForObject("http://localhost:8080/api/news", News[].class);
        newsList.clear();
        newsList.addAll(Arrays.asList(newsArray));
        return newsList;
    }

    private List<String> buildNewsView(ObservableList<News> newsList) {
        List<String> newsToWrite = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (News news : newsList) {
            sb.append(news.getPublicationTime()).append(" ").append(news.getHeadline());
            newsToWrite.add(sb.toString());
            sb.delete(0, sb.length());
        }
        return newsToWrite;
    }

    private void initializeComboBox() {
        comboBox.getItems().addAll(List.of(MORNING, DAY, EVENING));
    }

    private void showCurrentNews() {
        textFlow.getChildren().clear();
        var viewItems = listView.getItems();
        boolean condition = !viewItems.isEmpty() && currentIndex < viewItems.size();

        if (condition) {
            Text text = new Text(commonList.get(currentIndex).getDescription());
            textFlow.getChildren().add(text);

            scrollPane.setContent(textFlow);
        }
        label.setText((condition)
                ? viewItems.get(currentIndex) : "Choose the news");
    }

    private void clearAllLists() {
        commonList.clear();
        listView.getItems().clear();
    }

    private void addItemsToListView() {
        var values = buildNewsView(commonList);
        listView.getItems().addAll(values);
    }

    private void groupCommonListByDayTime() {
        LocalTime morningStart = LocalTime.of(6, 0);
        LocalTime dayStart = LocalTime.of(12, 0);
        LocalTime eveningStart = LocalTime.of(18, 0);
        String currenTimePeriod = comboBox.getValue();
        switch (currenTimePeriod) {
            case MORNING -> {
                for (News news : newsList) {
                    if (news.getPublicationTime().isAfter(morningStart)
                            && news.getPublicationTime().isBefore(dayStart)) {
                        commonList.add(news);
                    }
                }
                addItemsToListView();
            }
            case DAY -> {
                for (News news : newsList) {
                    if (news.getPublicationTime().isAfter(dayStart)
                            && news.getPublicationTime().isBefore(eveningStart)) {
                        commonList.add(news);
                    }
                }
                addItemsToListView();
            }
            case EVENING -> {
                for (News news : newsList) {
                    if (news.getPublicationTime().isAfter(eveningStart)) {
                        commonList.add(news);
                    }
                }
                addItemsToListView();
            }
        }
    }

}


