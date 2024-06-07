module com.example.latcontrol {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;

    opens com.example.latcontrol to javafx.fxml;
    exports com.example.latcontrol;
}