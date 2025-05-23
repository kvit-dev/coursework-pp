module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;
    requires org.slf4j;
    requires javafx.graphics;
    requires jakarta.mail;

    opens main to javafx.fxml;
    opens controller to javafx.fxml;
    opens model to javafx.base;

    exports main;
    exports controller;
}