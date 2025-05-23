/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class
 *
 * @author pablo
 */
public class FXMLUserOptionsController implements Initializable {

    @FXML
    private ImageView avatarIV;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        double radius = avatarIV.getFitWidth() / 2;
        Circle circle = new Circle(radius, radius, radius);
        avatarIV.setClip(circle);
    }    
    
}
