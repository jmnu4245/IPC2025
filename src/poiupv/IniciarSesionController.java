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
    }

    @FXML
    private void continuarIni(ActionEvent event) {
    }
    
}
