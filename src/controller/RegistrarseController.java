/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import poiupv.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author kolir
 */
public class RegistrarseController implements Initializable {

    @FXML
    private Label noValidUser;
    @FXML
    private TextField userField;
    @FXML
    private Label userFormat;
    @FXML
    private Label noValidMail;
    @FXML
    private TextField mailField;
    @FXML
    private Label noValidPass;
    @FXML
    private PasswordField passField;
    @FXML
    private Button verButton;
    @FXML
    private Label passFormat;
    @FXML
    private DatePicker dateField;
    @FXML
    private Label ageFormat;
    @FXML
    private Button avatarButton;
    @FXML
    private Button cancelarButton;
    @FXML
    private Button registrarButton;
    @FXML
    private StackPane passContainer;
    @FXML
    private TextField plainPasswordField;
    private boolean passwordVisible = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noValidUser.setVisible(false);
        userFormat.setVisible(false);
        noValidMail.setVisible(false);
        noValidPass.setVisible(false);
        passFormat.setVisible(false);
        ageFormat.setVisible(false);

        if (plainPasswordField != null && passField != null) {
        plainPasswordField.managedProperty().bind(plainPasswordField.visibleProperty());
        plainPasswordField.visibleProperty().bind(passField.visibleProperty().not());
        plainPasswordField.textProperty().bindBidirectional(passField.textProperty());
        plainPasswordField.styleProperty().bind(passField.styleProperty());
        plainPasswordField.getStyleClass().add("text-input");
        } 
    }

    @FXML
    private void verAction(ActionEvent event) {
        passwordVisible = !passwordVisible;
        passField.setVisible(passwordVisible);
        // plainPasswordField.setVisible(!passwordVisible); // Eliminamos esta línea
    }

    @FXML
    private void avatarAction(ActionEvent event) {
    }

    @FXML
    private void cancelarAction(ActionEvent event) {
    }

    @FXML
    private void registrarAction(ActionEvent event) {
    }

}