module com.carracinggame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires java.desktop;
    requires java.prefs;

    opens com.carracinggame to javafx.fxml;
    opens com.carracinggame.scene to javafx.fxml;
    opens com.carracinggame.shop to javafx.fxml;

    exports com.carracinggame;
    exports com.carracinggame.scene;
    exports com.carracinggame.shop;
    exports com.carracinggame.database;
    exports com.carracinggame.core;
}