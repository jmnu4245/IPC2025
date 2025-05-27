/**
 * FXML Controller class
 *
 * @author kolir
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;
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
import java.time.LocalDate;
import model.Navigation;
import model.User;
import javafx.scene.image.Image;
import model.NavDAOException;
import javafx.scene.shape.Circle;

public class RegistrarseController implements Initializable {

    @FXML private Label noValidUser;
    @FXML private TextField userField;
    @FXML private Label userFormat;
    @FXML private Label noValidMail;
    @FXML private TextField mailField;
    @FXML private Label noValidPass;
    @FXML private PasswordField passField;
    @FXML private Button verButton;
    @FXML private Label passFormat;
    @FXML private DatePicker dateField;
    @FXML private Label ageFormat;
    @FXML private Button avatarButton;
    @FXML private Button cancelarButton;
    @FXML private Button registrarButton;
    @FXML private StackPane passContainer;
    @FXML private TextField plainPasswordField;
    private Image selectedAvatar = null;

    private boolean passwordVisible = false;
    @FXML
    private ImageView avatarIm;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noValidUser.setVisible(false);
        userFormat.setVisible(false);
        noValidMail.setVisible(false);
        noValidPass.setVisible(false);
        passFormat.setVisible(false);
        ageFormat.setVisible(false);

        passField.setVisible(true);
        passField.setManaged(true);
        plainPasswordField.setVisible(false);
        plainPasswordField.setManaged(false);

        plainPasswordField.textProperty().bindBidirectional(passField.textProperty());
        plainPasswordField.getStyleClass().add("text-input");
        
        
        

        avatarButton.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> applyAvatarClip());
        avatarIm.imageProperty().addListener((obs, oldImg, newImg) -> applyAvatarClip());


        userField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean formatoValido = User.checkNickName(newVal);
            userFormat.setVisible(!formatoValido);

            if (formatoValido) {
                try {
                    Navigation nav = Navigation.getInstance();
                    boolean existe = nav.exitsNickName(newVal);
                    noValidUser.setVisible(existe);
                    if (!existe) setValid(userField);
                    else setInvalid(userField);
                } catch (NavDAOException e) {
                    e.printStackTrace();
                }
            } else {
                setInvalid(userField);
                noValidUser.setVisible(false);
            }
        });

        mailField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean esValido = User.checkEmail(newVal);
            noValidMail.setVisible(!esValido);
            if (esValido) setValid(mailField);
            else setInvalid(mailField);
        });

        passField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean esValido = User.checkPassword(newVal);
            noValidPass.setVisible(!esValido);
            passFormat.setVisible(!esValido);
            if (esValido) setValid(passField);
            else setInvalid(passField);
        });

        plainPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean esValido = User.checkPassword(newVal);
            noValidPass.setVisible(!esValido);
            passFormat.setVisible(!esValido);
            if (esValido) setValid(plainPasswordField);
            else setInvalid(plainPasswordField);
        });
        
        if (selectedAvatar == null) {
            selectedAvatar = new Image(getClass().getResource("/resources/avatar_defecto.jpg").toExternalForm());
            avatarIm.setImage(selectedAvatar);
        }
    }

    @FXML
    private void verAction(ActionEvent event) {
        passwordVisible = !passwordVisible;

        passField.setVisible(!passwordVisible);
        passField.setManaged(!passwordVisible);

        plainPasswordField.setVisible(passwordVisible);
        plainPasswordField.setManaged(passwordVisible);

        if (passwordVisible) {
            syncStyles(passField, plainPasswordField);
        } else {
            syncStyles(plainPasswordField, passField);
        }
    }

    @FXML
    private void avatarAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona una imagen de avatar");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File initialDir = new File(System.getProperty("user.home"), "Desktop");
        if (initialDir.exists()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File selectedFile = fileChooser.showOpenDialog(avatarButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                
                selectedAvatar = new Image(
                    selectedFile.toURI().toString(),
                    avatarButton.getWidth(),
                    avatarButton.getHeight(),
                    false, // NO preserve ratio
                    true   // smooth
                );
                avatarIm.setImage(selectedAvatar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void cancelarAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/IniciarSesion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            Stage actualStage = (Stage) cancelarButton.getScene().getWindow();
            actualStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registrarAction(ActionEvent event) {
        String nick = userField.getText().trim();
        String mail = mailField.getText().trim();
        String pass = passField.getText();
        LocalDate birthdate = dateField.getValue();

        noValidUser.setVisible(false);
        userFormat.setVisible(false);
        noValidMail.setVisible(false);
        noValidPass.setVisible(false);
        passFormat.setVisible(false);
        ageFormat.setVisible(false);

        boolean valido = true;

        if (!User.checkNickName(nick)) {
            noValidUser.setVisible(true);
            userFormat.setVisible(true);
            valido = false;
        }

        if (!User.checkEmail(mail)) {
            noValidMail.setVisible(true);
            valido = false;
        }

        if (!User.checkPassword(pass)) {
            noValidPass.setVisible(true);
            passFormat.setVisible(true);
            valido = false;
        }

        if (birthdate == null || birthdate.isAfter(LocalDate.now().minusYears(16))) {
            ageFormat.setVisible(true);
            valido = false;
        }

        if (!valido) return;

        try {
            Navigation nav = Navigation.getInstance();

            if (nav.exitsNickName(nick)) {
                noValidUser.setText("Nombre de usuario ya registrado");
                noValidUser.setVisible(true);
                return;
            }

            User nuevoUsuario = nav.registerUser(nick, mail, pass, selectedAvatar, birthdate);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Menu.fxml"));
            Parent root = loader.load();
            MenuController controller = loader.getController();
            controller.setUsuario(nuevoUsuario);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            Stage actualStage = (Stage) registrarButton.getScene().getWindow();
            actualStage.close();

        } catch (NavDAOException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setValid(TextField field) {
        field.getStyleClass().removeAll("textfield-error");
        if (!field.getStyleClass().contains("textfield-success")) {
            field.getStyleClass().add("textfield-success");
        }
    }

    private void setInvalid(TextField field) {
        field.getStyleClass().removeAll("textfield-success");
        if (!field.getStyleClass().contains("textfield-error")) {
            field.getStyleClass().add("textfield-error");
        }
    }

    private void syncStyles(TextField from, TextField to) {
        to.getStyleClass().removeAll("textfield-error", "textfield-success");
        if (from.getStyleClass().contains("textfield-error")) {
            to.getStyleClass().add("textfield-error");
        } else if (from.getStyleClass().contains("textfield-success")) {
            to.getStyleClass().add("textfield-success");
        }
    }
    
    private void applyAvatarClip() {
        double size = Math.min(avatarButton.getWidth(), avatarButton.getHeight());
        Circle circle = new Circle(size / 2, size / 2, size / 2);
        avatarButton.setClip(circle);
}
}
