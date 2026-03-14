package com.example.carracinggame2d;

import com.carracinggame.MainApp;
import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }
}