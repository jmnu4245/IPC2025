package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import ourModel.PreguntasHechas;

public class FXMLSeleccionProblemaController implements Initializable {

    @FXML private Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18;
    @FXML private ToggleButton mostrarHechas;
    @FXML private Button siguientePagina, anteriorPagina;

    private Button[] botonesPagina1;
    private Button[] botonesPagina2;

    private int paginaActual = 1; // 1 o 2

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // DEBUG: marcar alguna pregunta como hecha
        PreguntasHechas.getInstance().agregarPregunta(18);
        PreguntasHechas.getInstance().agregarPregunta(6);
        PreguntasHechas.getInstance().agregarPregunta(2);

        // Agrupa los botones en arrays de página
        botonesPagina1 = new Button[]{b1, b2, b3, b4, b5, b6, b7, b8, b9};
        botonesPagina2 = new Button[]{b10, b11, b12, b13, b14, b15, b16, b17, b18};

        // Opcional: guardar el id de pregunta en cada botón (1-18)
        for (int i = 0; i < botonesPagina1.length; i++) botonesPagina1[i].setUserData(i + 1);
        for (int i = 0; i < botonesPagina2.length; i++) botonesPagina2[i].setUserData(i + 10);

        refrescarVista(); // Muestra la página inicial correctamente
    }

    /** Refresca la vista teniendo en cuenta la página y el toggle */
    private void refrescarVista() {
        actualizarVisibilidadBotones();
        actualizarVisibilidadNavegacion();
    }

    /** Muestra/oculta botones según página y toggle */
    private void actualizarVisibilidadBotones() {
    boolean ocultarContestadas = mostrarHechas.isSelected();
    List<Integer> preguntasContestadas = PreguntasHechas.getInstance().getPreguntasHechas();

    // Página 1: ids 1-9
        for (int i = 0; i < botonesPagina1.length; i++) {
            int id = i + 1;
            Button b = botonesPagina1[i];
            boolean esPaginaActual = (paginaActual == 1);
            b.setVisible(esPaginaActual); // Muestra solo los de la página actual

            // Deshabilita si el toggle está activado Y la pregunta está hecha
            if (esPaginaActual && ocultarContestadas && preguntasContestadas.contains(id)) {
                b.setDisable(true);
            } else {
                b.setDisable(false);
            }
        }

        // Página 2: ids 10-18
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


    /** Muestra/oculta los botones de navegación */
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
        Button botonPulsado = (Button) event.getSource();
        int numPregunta = (int) botonPulsado.getUserData();

        System.out.println("Has pulsado la pregunta número: " + numPregunta);
        // Cargar escena problema pasandole el número numPregunta
        
        mostrarHechas.getScene().getWindow().hide();
        
    }
    @FXML
    private void onCancelar(ActionEvent event){
        mostrarHechas.getScene().getWindow().hide();
    }
}
