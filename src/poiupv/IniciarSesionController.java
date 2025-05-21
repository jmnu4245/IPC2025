/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package poiupv;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
/**
 * FXML Controller class
 *
 * @author kolir
 */
public class IniciarSesionController implements Initializable {

    @FXML
    private Label notRegUser;
    @FXML
    private TextField introducirUsuario;
    @FXML
    private Label noValidFormat;
    @FXML
    private PasswordField introducirContraseña;
    @FXML
    private Button verPass;
    @FXML
    private Button botonRegistrate;
    @FXML
    private Button continuarIni;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //deshabilitar boton de continuar si los campos usuario y contraseña no están rellenos
        notRegUser.setVisible(false);
        noValidFormat.setVisible(false);
        continuarIni.setDisable(true);
        continuarIni.disableProperty().bind(introducirUsuario.textProperty().isEmpty().or(introducirContraseña.textProperty().isEmpty()));

    }    

    @FXML
    private void registrateAction(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Registrarse.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
        Stage actualStage = (Stage) botonRegistrate.getScene().getWindow();
        actualStage.close();

    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    @FXML
    private void continuarIni(ActionEvent event) {
    }
    
}
