package controller;

import service.SelectionManager;
import model.MapStateManager;
import service.MapInteractionHandler;
import model.MapDrawingTool;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;


// Herramientas de dibujo
import controller.tools.ArcTool;
import controller.tools.DistanceTool;
import controller.tools.LatitudeTool;
import controller.tools.LineTool;
import controller.tools.MarkerTooL;
import controller.tools.TextTool;

// Java y JavaFX imports
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import model.User;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import model.Problem;
import model.Navigation;
import model.NavDAOException;
import model.Answer;

public class EnunciadoCartaController implements Initializable {

    @FXML private VBox selectionMenu;
    @FXML private ScrollPane map_scrollpane;
    @FXML private TitledPane titledPane;
    @FXML private ImageView mapImageView;
    @FXML private ToolBar options_toolbar;
    @FXML private ToggleGroup options;
    @FXML private Slider zoom_slider;
    @FXML private StackPane rootStackPane;
    @FXML private ToggleButton manoBtn;

    private Group mapZoomGroup;
    private User usuario;
    private int numPregunta;

    // Máximos y mínimos del mapa
    private double maxX, maxY, minX, minY;

    // Componentes modulares
    private MapStateManager stateManager;
    private MapInteractionHandler interactionHandler;
    private SelectionManager selectionManager;
    private SelectionMenuManager menuManager;
    private Pane rulerBar;
    private MenuController menuController;


    // Herramientas de dibujo
    private Map<MapStateManager.Tool, MapDrawingTool> drawingTools;
    private MapDrawingTool activeDrawingTool;
    @FXML
    private ToggleButton selectionBtn;
    @FXML
    private ToggleButton puntoBtn;
    @FXML
    private ToggleButton lineaBtn;
    @FXML
    private ToggleButton arcoBtn;
    @FXML
    private ToggleButton textoBtn;
    @FXML
    private ToggleButton limpiarBtn;
    @FXML
    private ToggleButton transportadorBtn;
    @FXML
    private ToggleButton distanciaBtn;
    @FXML
    private ToggleButton latitudBtn;
    @FXML
    private ColorPicker ColorPicker;
    @FXML
    private RadioButton A_answer;
    @FXML
    private RadioButton B_answer;
    @FXML
    private RadioButton C_answer;
    @FXML
    private RadioButton D_answer;
    @FXML
    private Label enunciadoCarta;
    @FXML
    private Label respuestaA;
    @FXML
    private Label respuestaB;
    @FXML
    private Label respuestaC;
    @FXML
    private Label respuestaD;
    @FXML
    private ToggleGroup answers;
    @FXML
    private Button entregarButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Group contentGroup = new Group();
        mapZoomGroup = new Group();
        contentGroup.getChildren().add(mapZoomGroup);
        mapZoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);
        zoom_slider.setMin(0.13);
        zoom_slider.setMax(1.5);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        zoom_slider.setValue(0.14);
        // 1. Inicializar manejador de estado
        stateManager = new MapStateManager();

        // 2. Inicializar manejador de selección y menú
        selectionManager = new SelectionManager(mapZoomGroup);
        menuManager = new SelectionMenuManager(stateManager, selectionMenu, mapZoomGroup);

        // 3. Inicializar herramientas de dibujo
        drawingTools = new HashMap<>();
        drawingTools.put(MapStateManager.Tool.MARKER, new MarkerTooL());
        drawingTools.put(MapStateManager.Tool.LINE, new LineTool());
        drawingTools.put(MapStateManager.Tool.ARC, new ArcTool());
        drawingTools.put(MapStateManager.Tool.TEXT, new TextTool());
        drawingTools.put(MapStateManager.Tool.DISTANCE, new DistanceTool());
        drawingTools.put(MapStateManager.Tool.LATITUDE, new LatitudeTool());

        drawingTools.values().forEach(tool -> tool.setDependencies(stateManager, mapZoomGroup, menuManager,map_scrollpane));

        // 4. Inicializar manejador de interacción
        interactionHandler = new MapInteractionHandler(
            stateManager, mapZoomGroup, map_scrollpane, rootStackPane, titledPane,
            this::handleToolPressed,
            this::handleToolDrag,
            this::handleToolRelease,
            this::handleToolClick,
            this::handleSelection
        );
        interactionHandler.attachEventHandlers();

        // 5. Configurar botones y herramientas
        configureToolSelector();
        manoBtn.setSelected(true);
        
        stateManager.currentToolProperty().addListener((obs, oldTool, newTool) -> {
    if (newTool == MapStateManager.Tool.DISTANCE) {
        double width = mapZoomGroup.getBoundsInLocal().getWidth();
        rulerBar = createRulerBar(width);

        // Posicionar la regla abajo
        rulerBar.setTranslateY(mapZoomGroup.getBoundsInLocal().getHeight() - rulerBar.getPrefHeight());

        mapZoomGroup.getChildren().add(rulerBar);
    } else {
        if (rulerBar != null) {
            mapZoomGroup.getChildren().remove(rulerBar);
            rulerBar = null;
        }
    }
});
    }
    public void setNumEj() {}
    private void handleToolClick(MouseEvent event) {
        if (activeDrawingTool != null) {
            Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
            activeDrawingTool.onMouseClick(event, mapCoords);
            if(!stateManager.getCurrentTool().equals(MapStateManager.Tool.SELECTION)){
            selectionManager.deselectCurrentElement();}
        }
    }

    private void handleToolDrag(MouseEvent event) {
        if (activeDrawingTool != null) {
            Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
            activeDrawingTool.onMouseDragged(event, mapCoords);
        }
    }

    private void handleToolRelease(MouseEvent event) {
        if (activeDrawingTool != null) {
            Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
            activeDrawingTool.onMouseReleased(event, mapCoords);
        }
    }

    private void handleToolPressed(MouseEvent event) {
        if (activeDrawingTool != null) {
            Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
            activeDrawingTool.onMousePressed(event, mapCoords);
        }
    }

    private void handleSelection(Node clickedNode) {
        selectionManager.selectElement(clickedNode);
        if (clickedNode != null && clickedNode != mapImageView) {
            menuManager.showOptionsForSelectedNode(clickedNode);
        } else {
            menuManager.updateMenuForTool();
        }
    }

    private void configureToolSelector() {
        options.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (activeDrawingTool != null) {
                activeDrawingTool.deactivate();
            }
            selectionManager.deselectCurrentElement();

            if (newToggle != null) {
                ToggleButton btn = (ToggleButton) newToggle;
                MapStateManager.Tool newTool;
                switch (btn.getId()) {
                    case "selectionBtn": newTool = MapStateManager.Tool.SELECTION; break;
                    case "manoBtn": newTool = MapStateManager.Tool.HAND; break;
                    case "puntoBtn": newTool = MapStateManager.Tool.MARKER; break;
                    case "lineaBtn": newTool = MapStateManager.Tool.LINE; break;
                    case "arcoBtn": newTool = MapStateManager.Tool.ARC; break;
                    case "textoBtn": newTool = MapStateManager.Tool.TEXT; break;
                    case "limpiarBtn": newTool = MapStateManager.Tool.DELETE; break;
                    case "transportadorBtn": newTool = MapStateManager.Tool.PROTRACTOR; break;
                    case "distanciaBtn": newTool = MapStateManager.Tool.DISTANCE; break;
                    case "latitudBtn": newTool = MapStateManager.Tool.LATITUDE; break;
                    default: newTool = MapStateManager.Tool.NONE_SELECTED; break;
                }

                stateManager.setCurrentTool(newTool);
                updateCursorAndMenu(newTool);

                if (newTool == MapStateManager.Tool.DELETE) {
                    mapZoomGroup.getChildren().removeIf(node -> node != mapImageView);
                    manoBtn.setSelected(true);
                } else {
                    activeDrawingTool = drawingTools.get(newTool);
                    if (activeDrawingTool != null) {
                        activeDrawingTool.activate();
                    }
                }
            } else {
                stateManager.setCurrentTool(MapStateManager.Tool.NONE_SELECTED);
                updateCursorAndMenu(MapStateManager.Tool.NONE_SELECTED);
                activeDrawingTool = null;
            }
        });
    }

    private void updateCursorAndMenu(MapStateManager.Tool tool) {
        switch (tool) {
            case SELECTION: rootStackPane.setCursor(Cursor.DEFAULT); break;
            case HAND: rootStackPane.setCursor(Cursor.OPEN_HAND); break;
            case MARKER:
            case LINE:
            case ARC:
            case DISTANCE:
            case LATITUDE:
                rootStackPane.setCursor(Cursor.CROSSHAIR); break;
            case TEXT: rootStackPane.setCursor(Cursor.TEXT); break;
            case DELETE:
            case NONE_SELECTED:
            default: rootStackPane.setCursor(Cursor.DEFAULT); break;
        }
        menuManager.updateMenuForTool();
    }

    @FXML
    private void zoomIn() {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + 0.1);
    }

    @FXML
    private void zoomOut() {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal - 0.1);
    }

    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        mapZoomGroup.setScaleX(scaleValue);
        mapZoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    private static double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return min;
        return Math.min(Math.max(value, min), max);
    }
    private Pane createRulerBar(double totalWidth) {
    Pane ruler = new Pane();
    ruler.setPrefHeight(80); // Más alto para mayor visibilidad
    ruler.setMaxWidth(Double.MAX_VALUE);
    ruler.setStyle("-fx-background-color: transparent;");

    double pixelsPerKm = 240.0 / 100.0; // 2.4 px/km
    double spacing = pixelsPerKm * 10; // Marcas cada 10 km

    for (double x = 0; x <= totalWidth; x += spacing) {
        double km = x / pixelsPerKm;
        double height;
        double strokeWidth;
        Color color = Color.DARKSLATEGRAY;

        // Cada 100 km: marca mayor + etiqueta
        if (km % 100 == 0) {
            height = 50;
            strokeWidth = 3;

            Text label = new Text(String.format("%.0f km", km));
            label.setFont(Font.font(16));
            label.setFill(color);
            label.setLayoutX(x - 25);
            label.setLayoutY(75);
            ruler.getChildren().add(label);
        }
        // Cada 50 km: marca media
        else if (km % 50 == 0) {
            height = 35;
            strokeWidth = 2.5;
        }
        // Cada 10 km: marca menor
        else {
            height = 20;
            strokeWidth = 2;
        }

        Line mark = new Line(x, 0, x, height);
        mark.setStroke(color);
        mark.setStrokeWidth(strokeWidth);
        ruler.getChildren().add(mark);
    }

    return ruler;
}
    
    public void setMapData(User usuario, int numPregunta) {
        this.usuario = usuario;
        this.numPregunta = numPregunta;
        
        try {
            Problem problema = Navigation.getInstance().getProblems().get(numPregunta - 1); // -1 porque el índice empieza en 0
            enunciadoCarta.setText(problema.getText());
            List<Answer> respuestas = new ArrayList<>(problema.getAnswers());
            Collections.shuffle(respuestas);
            respuestaA.setText("A. " + respuestas.get(0).getText());
            respuestaB.setText("B. " + respuestas.get(1).getText());
            respuestaC.setText("C. " + respuestas.get(2).getText());
            respuestaD.setText("D. " + respuestas.get(3).getText());
            A_answer.setText("A. " + respuestas.get(0).getText());
            B_answer.setText("B. " + respuestas.get(1).getText());
            C_answer.setText("C. " + respuestas.get(2).getText());
            D_answer.setText("D. " + respuestas.get(3).getText());
            A_answer.setUserData(respuestas.get(0));
            B_answer.setUserData(respuestas.get(1));
            C_answer.setUserData(respuestas.get(2));
            D_answer.setUserData(respuestas.get(3));
            entregarButton.setDisable(true);
            answers.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            entregarButton.setDisable(newVal == null);
        });
        } catch (NavDAOException | IndexOutOfBoundsException e) {
            enunciadoCarta.setText("Error al cargar el problema.");
            e.printStackTrace();
    }
    }

    @FXML
    private void entregarAction(ActionEvent event) {
            RadioButton selected = (RadioButton) answers.getSelectedToggle();

        if (selected != null) {
            Answer selectedAnswer = (Answer) selected.getUserData();

            // Evaluar todas las respuestas
            for (RadioButton rb : List.of(A_answer, B_answer, C_answer, D_answer)) {
                Answer ans = (Answer) rb.getUserData();
                if (ans.getValidity()) {
                    rb.setStyle("-fx-opacity:1; -fx-text-fill: #2ecc71;"); // Verde (respuesta correcta)
                } else {
                    rb.setStyle("-fx-opacity:1; -fx-text-fill: #e74c3c;"); // Rojo (incorrecta)
                }
                rb.setDisable(true);
            }

            entregarButton.setDisable(true);

            
            boolean acierto = selectedAnswer.getValidity();
            if (menuController != null) {
                menuController.setResultados(acierto);
            }


            answers.getToggles().forEach(t -> ((ToggleButton) t).setDisable(true));
            entregarButton.setDisable(true);
        }
    }
    public void setMenuController(MenuController controller) {
        this.menuController = controller;
    }
}
