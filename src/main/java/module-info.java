module com.example.newsclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires spring.web;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;


    opens com.example.newsclient to javafx.fxml;
    exports com.example.newsclient;
    exports com.example.newsclient.model;
}