package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import model.User;

public class MenuController implements Initializable {

    private User usuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // inicialización
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }
}
