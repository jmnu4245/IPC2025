package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import model.User;
import ourModel.PreguntasHechas;


public class FXMLSeleccionProblemaController implements Initializable {

    @FXML private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18;
    @FXML private ToggleButton mostrarHechas;
    @FXML private Button siguientePagina, anteriorPagina;

    private Button[] botonesPagina1;
    private Button[] botonesPagina2;
    private MenuController menuController;
    private User usuario;
    private int paginaActual = 1; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {

       
        botonesPagina1 = new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9};
        botonesPagina2 = new Button[]{b10, b11, b12, b13, b14, b15, b16, b17, b18};

        
        for (int i = 0; i < botonesPagina1.length; i++) botonesPagina1[i].setUserData(i + 1);
        for (int i = 0; i < botonesPagina2.length; i++) botonesPagina2[i].setUserData(i + 10);

        refrescarVista();
    }

    
    private void refrescarVista() {
        actualizarVisibilidadBotones();
        actualizarVisibilidadNavegacion();
    }

    
    private void actualizarVisibilidadBotones() {
    boolean ocultarContestadas = mostrarHechas.isSelected();
    List<Integer> preguntasContestadas = PreguntasHechas.getInstance().getPreguntasHechas();

    
        for (int i = 0; i < botonesPagina1.length; i++) {
            int id = i + 1;
            Button b = botonesPagina1[i];
            boolean esPaginaActual = (paginaActual == 1);
            b.setVisible(esPaginaActual); 

            
            if (esPaginaActual && ocultarContestadas && preguntasContestadas.contains(id)) {
                b.setDisable(true);
            } else {
                b.setDisable(false);
            }
        }

       
        for (int i = 0; i < botonesPagina2.length; i++) {
            int id = i + 10;
            Button b = botonesPagina2[i];
            boolean esPaginaActual = (paginaActual == 2);
            b.setVisible(esPaginaActual);

            if (esPaginaActual && ocultarContestadas && preguntasContestadas.contains(id)) {
                b.setDisable(true);
            } else {
                b.setDisable(false);
            }
        }
    }


    
    private void actualizarVisibilidadNavegacion() {
        siguientePagina.setVisible(paginaActual == 1);
        anteriorPagina.setVisible(paginaActual == 2);
    }

    @FXML
    private void onToggleMostrarHechas(ActionEvent event) {
        refrescarVista();
    }

    @FXML
    private void onSiguientePagina(ActionEvent event) {
        paginaActual = 2;
        refrescarVista();
    }

    @FXML
    private void onAnteriorPagina(ActionEvent event) {
        paginaActual = 1;
        refrescarVista();
    }
    
    @FXML
    private void onSeleccionarPregunta(ActionEvent event) {
         try {
        Button botonPulsado = (Button) event.getSource();
        int numPregunta = (int) botonPulsado.getUserData();
          
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EnunciadoCarta.fxml"));
        Parent root = loader.load();
       
        EnunciadoCartaController controlador = loader.getController();
       
        controlador.setMapData(usuario,numPregunta);
        controlador.setMenuController(menuController);


        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Ejercicio " + numPregunta);
        stage.show();

        mostrarHechas.getScene().getWindow().hide();

        } catch (IOException e) {
        e.printStackTrace();
        }
    }
    @FXML
    private void onCancelar(ActionEvent event){
        mostrarHechas.getScene().getWindow().hide();
    }
    public void setUser(User u){
        usuario = u;
    }
    public void setMenuController(MenuController controller) {
        this.menuController = controller;
    }
}
