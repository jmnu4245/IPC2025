package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import model.Navigation;
import model.NavDAOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;

import model.User;

public class FXMLUserOptionsController implements Initializable {
    //Navigation nav = null;
    boolean passwordVisible = false;
    private static final int EDAD_MINIMA = 16;
    User usuarioSesion;
    @FXML
    private ImageView avatarIV;
    @FXML
    private TextField userField;
    @FXML
    private Label emailError;
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
    @FXML
    private TextField plainPasswordField;
    @FXML
    private Button saveButton;

    private final BooleanProperty validEmail = new SimpleBooleanProperty(true);
    private final BooleanProperty validPassword = new SimpleBooleanProperty(true);
    private final BooleanProperty validDate = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//         try {
//             nav = Navigation.getInstance();
//             User debugUser = nav.authenticate("user3", "User123!");
//             if (debugUser == null) {
//                 debugUser = nav.registerUser("user4", "prueba@prueba.com", "User123!", null, LocalDate.of(2000, 1, 1));
//             }
//             setUsuarioSesion(debugUser);
//         } catch (NavDAOException e) {
//             e.printStackTrace();
//         }

        plainPasswordField.textProperty().bindBidirectional(passField.textProperty());

        double width = avatarIV.getFitWidth();
        double height = avatarIV.getFitHeight();
        double radius = Math.min(width, height) / 2;
        Circle circle = new Circle(width / 2, height / 2, radius);
        avatarIV.setClip(circle);

        dateField.setDayCellFactory((DatePicker picker) -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    setDisable(empty || !date.isBefore(today));
                }
            };
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean esValido = User.checkEmail(newVal);
            validEmail.set(esValido);
            emailError.setVisible(!esValido);
            setFieldValidation(emailField, esValido);
        });

        dateField.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esValido = false;
            if (newVal != null) {
                LocalDate fechaLimite = LocalDate.now().minusYears(EDAD_MINIMA);
                esValido = !newVal.isAfter(fechaLimite);
            }
            validDate.set(esValido);
            dateError.setVisible(!esValido);
        });

        passField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean esValido = User.checkPassword(newVal);
            validPassword.set(esValido);
            passError.setVisible(!esValido);
            setFieldValidation(passField, esValido);
            setFieldValidation(plainPasswordField, esValido);
        });

        plainPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean esValido = User.checkPassword(newVal);
            validPassword.set(esValido);
            passError.setVisible(!esValido);
            setFieldValidation(passField, esValido);
            setFieldValidation(plainPasswordField, esValido);
        });

        BooleanBinding validFields = validEmail.and(validPassword).and(validDate);
        saveButton.disableProperty().bind(validFields.not());
    }

    @FXML
    private void onActionAvatar(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Selecciona una imagen de avatar");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        java.io.File initialDir = new java.io.File(System.getProperty("user.home"), "Desktop");
        if (initialDir.exists()) {
            fileChooser.setInitialDirectory(initialDir);
        }
        java.io.File selectedFile = fileChooser.showOpenDialog(avatarIV.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image newAvatar = new Image(selectedFile.toURI().toString());
                avatarIV.setImage(newAvatar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onActionCancell(ActionEvent event) {
        emailField.getScene().getWindow().hide();
    }

    @FXML
    private void onActionSave(ActionEvent event) {
        usuarioSesion.setAvatar(avatarIV.getImage());
        usuarioSesion.setEmail(emailField.getText());
        usuarioSesion.setBirthdate(dateField.getValue());
        usuarioSesion.setPassword(passField.getText());
        emailField.getScene().getWindow().hide();
    }

    @FXML
    private void verAction(ActionEvent event) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            plainPasswordField.setText(passField.getText());
            plainPasswordField.setVisible(true);
            plainPasswordField.setManaged(true);
            passField.setVisible(false);
            passField.setManaged(false);
            syncStyles(passField, plainPasswordField);
        } else {
            passField.setText(plainPasswordField.getText());
            passField.setVisible(true);
            passField.setManaged(true);
            plainPasswordField.setVisible(false);
            plainPasswordField.setManaged(false);
            syncStyles(plainPasswordField, passField);
        }
    }

    public void setUsuarioSesion(User u){
        usuarioSesion = u;
        if(u!=null){
            userField.setText(usuarioSesion.getNickName());
            emailField.setText(usuarioSesion.getEmail());
            LocalDate fecha = usuarioSesion.getBirthdate();
            if(fecha.getYear() > LocalDate.now().getYear()){
                fecha = fecha.minusYears(100);
            }
            dateField.setValue(fecha);
            avatarIV.setImage(usuarioSesion.getAvatar());
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

    private void setFieldValidation(TextField field, boolean esValido) {
        field.getStyleClass().removeAll("textfield-error", "textfield-success");
        if (esValido) {
            if (!field.getStyleClass().contains("textfield-success")) {
                field.getStyleClass().add("textfield-success");
            }
        } else {
            if (!field.getStyleClass().contains("textfield-error")) {
                field.getStyleClass().add("textfield-error");
            }
        }
    }
}
