package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableRow;
import model.Navigation;
import model.Session;
import model.User;

public class FXMLResultadosController implements Initializable {

    @FXML
    private DatePicker fecha;
    @FXML
    private TableView<Session> tableView;
    @FXML
    private TableColumn<Session, String> colUsuario;
    @FXML
    private TableColumn<Session, String> colFecha;
    @FXML
    private TableColumn<Session, Integer> colAciertos;
    @FXML
    private TableColumn<Session, Integer> colFallos;

    private User usuario;
    private ObservableList<Session> datos;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //PARA DEBUGUEAR SOLO
//        try {
//            Navigation nav = Navigation.getInstance();
//            User debugUser = nav.authenticate("user1", "User123!"); // Cambia por tu usuario de pruebas
//            if (debugUser != null) {
//                setUsuarioSesion(debugUser);
//                tableView.setItems(FXCollections.observableArrayList(usuario.getSessions()));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        tableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
           if (newScene != null) {
            newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
                
                if (!tableView.localToScene(tableView.getBoundsInLocal()).contains(
                        event.getSceneX(), event.getSceneY())) {
                    tableView.getSelectionModel().clearSelection();
                 }
                });
            }
        });
        
        colUsuario.setReorderable(false);
        colFecha.setReorderable(false);
        colAciertos.setReorderable(false);
        colFallos.setReorderable(false);
        fecha.setDayCellFactory((DatePicker picker) -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate today = LocalDate.now();
                    LocalDate tomorrow = today.plusDays(1);
                    setDisable(empty || !date.isBefore(tomorrow));
                }
            };
        });

        
        colUsuario.setCellValueFactory(cellData ->
            new SimpleStringProperty(usuario.getNickName())
        );
        colFecha.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTimeStamp().format(FORMATTER))
        );
        colAciertos.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getHits()).asObject()
        );
        colFallos.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getFaults()).asObject()
        );
        
        
        fecha.valueProperty().addListener((obs, oldValue, newValue) -> {
        if (newValue != null && usuario != null) {
        
        ObservableList<Session> filtradas = FXCollections.observableArrayList(
            usuario.getSessions().stream()
                .filter(session -> session.getTimeStamp().toLocalDate().isAfter(newValue))
                .toList()
            );
            tableView.setItems(filtradas);
        } else if (usuario != null) {
            
            tableView.setItems(FXCollections.observableArrayList(usuario.getSessions()));
        }
        });
        
        
        tableView.setRowFactory(tv -> new TableRow<Session>() {
        @Override
        protected void updateItem(Session item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setStyle("");
        } else {
            int aciertos = item.getHits();
            int fallos = item.getFaults();
            if (aciertos > fallos) {
                setStyle("-fx-border-color: lightgreen; -fx-border-width: 0 0 0 2px;");
            } else if (fallos > aciertos) {
                setStyle("-fx-border-color: red; -fx-border-width: 0 0 0 2px;");
            } else {
                setStyle("-fx-border-color: transparent; -fx-border-width: 0 0 0 2px;");
                    }
                }
            }
        });
       
    }

    @FXML
    private void onMenu(ActionEvent event) {
        fecha.getScene().getWindow().hide();
    }

    public void setUsuarioSesion(User u) {
        usuario = u;
        if (usuario != null) {
            datos = FXCollections.observableArrayList(usuario.getSessions());
            tableView.setItems(datos);
        }
    }
}
