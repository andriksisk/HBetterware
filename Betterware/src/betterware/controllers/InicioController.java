/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package betterware.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author manac
 */
public class InicioController implements Initializable {
    
    @FXML
    private Button inicioBoton;

    @FXML
    private void cargaTablaP(){
        try {
            // Cargar el otro FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/betterware/tablaPrincipal.fxml"));
            Parent root = loader.load();
            
            // Crear la nueva ventana
            Stage newStage = new Stage();
            newStage.setTitle("Tabla Principal");
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) inicioBoton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
