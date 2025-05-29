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
import javafx.scene.image.Image;



public class MenuController implements Initializable {

    private User usuario;
    private Stage menuStage;
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
        this.totalAciertos = 0;
        this.totalErrores = 0;
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
                    System.out.println("llego aqui");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLSeleccionProblema.fxml"));
            Parent root = loader.load();

            controller.FXMLSeleccionProblemaController controller = loader.getController();
            controller.setUser(usuario);
            controller.setMenuController(this);

            Stage stage = new Stage();
            
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void randomAction(ActionEvent event) {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EnunciadoCarta.fxml"));
            Parent root = loader.load();

            controller.EnunciadoCartaController controller = loader.getController();
            int numPregunta = (int) (Math.random() * 18) + 1;
            controller.setMapData(usuario,numPregunta);
            controller.setMenuController(this);
            Stage stage = new Stage();
            stage.setTitle("Problema Aleatorio");
            stage.setScene(new Scene(root));
            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void resultAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLResultados.fxml"));
            Parent root = loader.load();

            controller.FXMLResultadosController controller = loader.getController();
            controller.setUsuarioSesion(usuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void datosAction(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FXMLUserOptions.fxml"));
        Parent root = loader.load();

        
        controller.FXMLUserOptionsController controller = loader.getController();
        controller.setUsuarioSesion(usuario);
        controller.setMenuController(this);

        Stage stage = new Stage();
        stage.setTitle("Editar datos de usuario");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.initOwner(menuButton.getScene().getWindow());
        stage.initModality(javafx.stage.Modality.WINDOW_MODAL); 
        stage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    
    private void aplicarClipCircular() {
        double size = Math.min(avatar.getFitWidth(), avatar.getFitHeight());
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        avatar.setClip(clip);
    }
    
    public void setResultados(boolean bien) {
        if (bien) {
            totalAciertos++;
        } else {
            totalErrores++;
        }
}

    
    public void actualizarAvatar(Image nuevaImagen) {
        usuario.setAvatar(nuevaImagen);
        avatar.setImage(nuevaImagen);
        aplicarClipCircular();
}
}
