package org.kinalquickmart.system.utils;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertInformation {

    public void viewAlert(String type, String title, String content, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        
        switch (type.toUpperCase()) {
            case "WARNING":
                alert = new Alert(Alert.AlertType.WARNING);
                break;
            case "ERROR":
                alert = new Alert(Alert.AlertType.ERROR);
                break;
            case "CONFIRMATION":
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                break;
            default:
                alert = new Alert(Alert.AlertType.INFORMATION);
        }
        
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean viewConfirm(String type, String title, String content, String header) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}