module com.mycompany.timy {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.timy to javafx.fxml;
    exports com.mycompany.timy;
}
