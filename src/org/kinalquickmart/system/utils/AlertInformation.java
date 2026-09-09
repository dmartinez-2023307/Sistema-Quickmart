/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.kinalquickmart.system.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 *
 * @author informatica
 */
public class AlertInformation {
    
    public AlertInformation(){
        
    }
    
    
     public void viewAlert(String tipo, String titulo, String mensaje, String encabezado) {
        
        AlertType tipoAlerta;

        tipoAlerta = switch (tipo.toUpperCase()) {
            case "INFORMATION" -> AlertType.INFORMATION;
            case "WARNING" -> AlertType.WARNING;
            case "ERROR" -> AlertType.ERROR;
            case "CONFIRMATION" -> AlertType.CONFIRMATION;
            case "NONE" -> AlertType.NONE;
            default -> AlertType.NONE;
        };

        Alert alert = new Alert(tipoAlerta);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        
        alert.showAndWait();
    }
}
