/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class
 *
 * @author pablo
 */
public class FXMLUserOptionsController implements Initializable {

    @FXML
    private ImageView avatarIV;
    @FXML
    private TextField userField;
    @FXML
    private HBox emailError;
    @FXML
    private TextField emailField;
    @FXML
    private Label dateError;
    @FXML
    private DatePicker dateField;
    @FXML
    private PasswordField passField;
    @FXML
    private Label passError;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
    double width = avatarIV.getFitWidth();
    double height = avatarIV.getFitHeight();
    double radius = Math.min(width, height) / 2;
   
    // Centrar el círculo en el ImageView
    Circle circle = new Circle(width / 2, height / 2, radius);
    avatarIV.setClip(circle);

        
    }    

    @FXML
    private void onActionAvatar(ActionEvent event) {
        
    }

    @FXML
    private void onActionCancell(ActionEvent event) {
        emailField.getScene().getWindow().hide();
    }

    @FXML
    private void onActionSave(ActionEvent event) {
        
    }
    
}
