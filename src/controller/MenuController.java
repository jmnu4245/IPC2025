package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import model.User;
import javafx.scene.control.DialogPane;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class MenuController implements Initializable {

    private User usuario;
    private int totalAciertos = 0;
    private int totalErrores = 0;
    @FXML
    private Label nomUsuario;
    @FXML
    private MenuButton menuButton;
    @FXML
    private MenuItem Datos;
    @FXML
    private MenuItem cerrarSesion;
    @FXML
    private ImageView avatar;
    @FXML
    private Button selectButton;
    @FXML
    private Button randomButton;
    @FXML
    private Button resultButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // inicialización
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
        nomUsuario.setText(usuario.getNickName());
        avatar.setImage(usuario.getAvatar());
        aplicarClipCircular();
    }


    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Cerrar sesión");
        alerta.setHeaderText("¿Estás seguro de que quieres cerrar sesión?");
        alerta.setContentText("Tu sesión se guardará si continúas.");
        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/resources/estilos.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane-dark");

        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");

        alerta.getButtonTypes().setAll(botonSi, botonNo);

        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == botonSi) {

                if (totalAciertos + totalErrores > 0) {
                    usuario.addSession(totalAciertos, totalErrores);
                }

                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/IniciarSesion.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.show();


                    Stage actual = (Stage) menuButton.getScene().getWindow();
                    actual.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    @FXML
    private void desplegarMenu(ActionEvent event) {
    }

    @FXML
    private void selectAction(ActionEvent event) {
    }

    @FXML
    private void randomAction(ActionEvent event) {
    }

    @FXML
    private void resultAction(ActionEvent event) {
    }

    @FXML
    private void datosAction(ActionEvent event) {
    }
    
    private void aplicarClipCircular() {
        double size = Math.min(avatar.getFitWidth(), avatar.getFitHeight());
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        avatar.setClip(clip);
    }
    
    public void setResultados(int aciertos, int errores) {
        this.totalAciertos = aciertos;
        this.totalErrores = errores;
}
}
