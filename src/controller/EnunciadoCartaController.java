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
import controller.tools.RulerTool;
import controller.tools.LatitudeTool;
import controller.tools.LineTool;
import controller.tools.MarkerTooL;
import controller.tools.TextTool;
import controller.tools.ProtractorTool;

// Java y JavaFX imports
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
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
    @FXML private ToggleButton distanciaBtn;
    @FXML private ToggleButton transportadorBtn;

    private Group mapZoomGroup;


    // Componentes modulares
    private MapStateManager stateManager;
    private MapInteractionHandler interactionHandler;
    private SelectionManager selectionManager;
    private SelectionMenuManager menuManager;


    // Herramientas de dibujo
    private Map<MapStateManager.Tool, MapDrawingTool> drawingTools;
    private MapDrawingTool activeDrawingTool;
    private RulerTool RulerTool;
    private ProtractorTool ProtractorTool;

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
        zoom_slider.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double newValue = zoom_slider.getValue();
            if (deltaY > 0) {    newValue += 0.1;    } 
            else {               newValue -= 0.1;    }
            newValue = Math.max(zoom_slider.getMin(), Math.min(zoom_slider.getMax(), newValue));
            zoom_slider.setValue(newValue);
            event.consume();
        });
        // 1. Inicializar manejador de estado
        stateManager = new MapStateManager();

        // 2. Inicializar manejador de selección y menú
        selectionManager = new SelectionManager(mapZoomGroup);
        menuManager = new SelectionMenuManager(stateManager, selectionMenu, mapZoomGroup);

        // 3. Inicializar herramientas de dibujo
        RulerTool = new RulerTool();
        ProtractorTool = new ProtractorTool();
        //Inicializar el resto
        drawingTools = new HashMap<>();
        drawingTools.put(MapStateManager.Tool.PROTRACTOR, ProtractorTool);
        drawingTools.put(MapStateManager.Tool.MARKER, new MarkerTooL());
        drawingTools.put(MapStateManager.Tool.LINE, new LineTool());
        drawingTools.put(MapStateManager.Tool.ARC, new ArcTool());
        drawingTools.put(MapStateManager.Tool.TEXT, new TextTool());
        drawingTools.put(MapStateManager.Tool.DISTANCE, RulerTool);
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
          }
    public void setNumEj() {}
    private void handleToolClick(MouseEvent event) {
    Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
    boolean handled = false;
    if (activeDrawingTool != null) {
        activeDrawingTool.onMouseClick(event, mapCoords);
        handled = true;
        if (!stateManager.getCurrentTool().equals(MapStateManager.Tool.SELECTION)){
            selectionManager.deselectCurrentElement();
        }
    } else if (stateManager.getisRuleSel()) {
        RulerTool.onMouseClick(event, mapCoords);
        handled = true;
    } else if (stateManager.getisProtractorSel()) {
        ProtractorTool.onMouseClick(event, mapCoords);
        handled = true;
    }
    if (handled) event.consume();
}

private void handleToolDrag(MouseEvent event) {
    Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
    boolean handled = false;
    if (activeDrawingTool != null) {
        activeDrawingTool.onMouseDragged(event, mapCoords);
        handled = true;
    } else if (stateManager.getisRuleSel()) {
        RulerTool.onMouseDragged(event, mapCoords);
        handled = true;
    } else if (stateManager.getisProtractorSel()) {
        ProtractorTool.onMouseDragged(event, mapCoords);
        handled = true;
    }
    if (handled) event.consume();
}

private void handleToolRelease(MouseEvent event) {
    Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
    boolean handled = false;
    if (activeDrawingTool != null) {
        activeDrawingTool.onMouseReleased(event, mapCoords);
        handled = true;
    } else if (stateManager.getisRuleSel()) {
        RulerTool.onMouseReleased(event, mapCoords);
        handled = true;
    } else if (stateManager.getisProtractorSel()) {
        ProtractorTool.onMouseReleased(event, mapCoords);
        handled = true;
    }
    if (handled) event.consume();
}

private void handleToolPressed(MouseEvent event) {
    Point2D mapCoords = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
    boolean handled = false;
    if (activeDrawingTool != null) {
        activeDrawingTool.onMousePressed(event, mapCoords);
        handled = true;
    } else if (stateManager.getisRuleSel()) {
        RulerTool.onMousePressed(event, mapCoords);
        handled = true;
    } else if (stateManager.getisProtractorSel()) {
        ProtractorTool.onMousePressed(event, mapCoords);
        handled = true;
    }
    if (handled) event.consume();
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
    transportadorBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
    if (isSelected) {
        stateManager.setisProtractorSel(true);
        drawingTools.get(MapStateManager.Tool.PROTRACTOR).activate();
    } else {
        stateManager.setisProtractorSel(false);
        drawingTools.get(MapStateManager.Tool.PROTRACTOR).deactivate();
        
    }
}); 
    distanciaBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
    if (isSelected) {
        stateManager.setisRuleSel(true);
        drawingTools.get(MapStateManager.Tool.DISTANCE).activate();
    } else {
        stateManager.setisRuleSel(false);
        drawingTools.get(MapStateManager.Tool.DISTANCE).deactivate();
    }
});     
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
                    case "latitudBtn": newTool = MapStateManager.Tool.LATITUDE; break;
                    default: newTool = MapStateManager.Tool.NONE_SELECTED; break;
                }
                stateManager.setCurrentTool(newTool);
                updateCursorAndMenu(newTool);
                if (newTool != MapStateManager.Tool.DELETE || newTool != MapStateManager.Tool.NONE_SELECTED){
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
            case LATITUDE:
                rootStackPane.setCursor(Cursor.CROSSHAIR); break;
            case TEXT: rootStackPane.setCursor(Cursor.TEXT); break;
            case DELETE:
            case NONE_SELECTED:
            default: rootStackPane.setCursor(Cursor.DEFAULT); break;
        }
        menuManager.updateMenuForTool();
    }
    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        mapZoomGroup.setScaleX(scaleValue);
        mapZoomGroup.setScaleY(scaleValue);
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
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
}
