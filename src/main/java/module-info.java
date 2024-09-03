module com.tikeysoft.vmd_sensorsimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.eclipse.paho.client.mqttv3;


    opens com.tikeysoft.vmd_sensorsimulator to javafx.fxml;
    exports com.tikeysoft.vmd_sensorsimulator;
}