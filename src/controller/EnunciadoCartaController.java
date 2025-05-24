package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.transform.Scale;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Font;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.shape.Shape;

import java.util.stream.Collectors;


public class EnunciadoCartaController implements Initializable {
    @FXML private VBox selectionMenu;
    @FXML private ColorPicker lineColorPicker;
    @FXML private ColorPicker textColorPicker;
    @FXML private ColorPicker arcColorPicker;
    @FXML private ScrollPane map_scrollpane;
    @FXML private Group mapZoomGroup;
    @FXML private ImageView mapImageView;
    @FXML private ToolBar options_toolbar;
    @FXML private ToggleGroup options;
    @FXML private StackPane rootStackPane;
    @FXML private ToggleButton manoBtn;

    private Node elementoSeleccionado = null;
    private Node haloSeleccion = null;

    
    private double currentZoomLevel = 1.0;
    private static final double MIN_ZOOM_LEVEL = 1.0;
    private static final double MAX_ZOOM_LEVEL = 5.0;
    private static final double ZOOM_FACTOR = 1.1;

    private Point2D lastMousePosition;
    
    private List<Point2D> arcPoints = new ArrayList<>();

    
    private Boolean mapClicked = false;

    // Para dibujo de herramientas
    private Point2D lineStart;
    private Point2D arcStart;
    private Point2D distanceStart;

    private enum Tool {SELECTION, HAND, POINT, LINE, ARC, TEXT, DELETE, PROTRACTOR, DISTANCE, LATITUDE, NONE_SELECTED }
    private Tool currentTool = Tool.NONE_SELECTED;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización básica
        mapImageView.setPreserveRatio(true);
        mapImageView.setSmooth(true);
        //Se desactiva el paneo por defecto del scrollpane
        map_scrollpane.setPannable(false);
        //Se esconden las barras del scrollPane
        map_scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        map_scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);       
        // Filtros de eventos de ratón
        rootStackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleClickFiltered);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressedFiltered);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDraggedFiltered);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleasedFiltered);
        //Movimiento con las flechas
        map_scrollpane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPan);
        // Configuración de listener para el ToggleGroup
        configurarToolSelector();
        //Selección inicial
        manoBtn.setSelected(true);

        lineColorPicker.setValue(Color.RED);
        textColorPicker.setValue(Color.BLACK);
        arcColorPicker.setValue(Color.BLUE);
    }
 // ==========SE AÑADE LISTENER AL TOGGLEGROUP ===========
    private void configurarToolSelector() {
        options.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                ToggleButton btn = (ToggleButton) newToggle;
                switch (btn.getId()) {
                    case "selectionBtn" : selectionPressed(); break;
                    case "manoBtn":       manoPressed();        break;
                    case "puntoBtn":      puntoPressed();       break;
                    case "lineaBtn":      lineaPressed();       break;
                    case "arcoBtn":       arcoPressed();        break;
                    case "textoBtn":      textoPressed();       break;
                    case "limpiarBtn":    limpiarPressed();     break;
                    case "transportadorBtn": /*no implementado*/ break;
                    case "distanciaBtn":  distanciaPressed();   break;
                    case "latitudBtn":    latitudPressed();     break;
                    default:               noneSelected();       break;
                }
            } else {
                noneSelected();
            }
        });
    }
    // ========== IMPLEMENTACIÓN DE CADA HERRAMIENTA ===========
    private void selectionPressed(){
        currentTool = Tool.SELECTION;
        rootStackPane.setCursor(Cursor.DEFAULT);
        actualizarSelectionMenu();}
    private void manoPressed() {
        currentTool = Tool.HAND;
        rootStackPane.setCursor(Cursor.OPEN_HAND);
        actualizarSelectionMenu();
    }
    private void puntoPressed() {
        currentTool = Tool.POINT;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
        actualizarSelectionMenu();
    }
    private void lineaPressed() {
    currentTool = Tool.LINE;
    lineStart = null;
    rootStackPane.setCursor(Cursor.CROSSHAIR);
    actualizarSelectionMenu("line");
}
    private void textoPressed() {
        currentTool = Tool.TEXT;
        rootStackPane.setCursor(Cursor.TEXT);
        actualizarSelectionMenu("text");
    }
    private void arcoPressed() {
        currentTool = Tool.ARC;
        arcPoints.clear();
        rootStackPane.setCursor(Cursor.CROSSHAIR);
        actualizarSelectionMenu("arc");
    }
    private void limpiarPressed() {
        currentTool = Tool.DELETE;
        mapZoomGroup.getChildren().removeIf(node -> node != mapImageView);
        rootStackPane.setCursor(Cursor.DEFAULT);
        actualizarSelectionMenu();
    }
    private void distanciaPressed() {
        currentTool = Tool.DISTANCE;
        distanceStart = null;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
        actualizarSelectionMenu();
    }
    private void latitudPressed() {
        currentTool = Tool.LATITUDE;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
        actualizarSelectionMenu();
    }
    private void noneSelected() {
        currentTool = Tool.NONE_SELECTED;
        rootStackPane.setCursor(Cursor.DEFAULT);
        actualizarSelectionMenu();
    }
    // ========== EVENTOS DE MOUSE FILTRADOS ===========
    private void handleMousePressedFiltered(MouseEvent event) {
        Node target = event.getPickResult().getIntersectedNode();
    while (target != null && target != rootStackPane) {
        if (target instanceof javafx.scene.control.Control) {            return;    }
        target = target.getParent();
    }
     if (currentTool == Tool.HAND && event.isPrimaryButtonDown()) {
        lastMousePosition = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
        rootStackPane.setCursor(Cursor.CLOSED_HAND); 
        event.consume(); 
    }
}
    private void handleClickFiltered(MouseEvent event) {
    Node target = event.getPickResult().getIntersectedNode();
    while (target != null && target != rootStackPane) {
        if (target instanceof javafx.scene.control.Control) {            return;    }
        target = target.getParent();
    }
    Point2D localPoint = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
    if (!mapZoomGroup.getBoundsInLocal().contains(localPoint)) return;

    // We only want to process clicks for drawing tools, not the hand tool here
    if (currentTool != Tool.HAND) {
        switch (currentTool) {
            case LINE:
                if (lineStart == null) {
                    lineStart = localPoint;
                } else {
                    Line line = new Line(lineStart.getX(), lineStart.getY(), localPoint.getX(), localPoint.getY());
                    line.setStroke(lineColorPicker.getValue());
                    mapZoomGroup.getChildren().add(line);
                    lineStart = null;
                }
                break;

            
            // ... (ensure other drawing tools also consume MOUSE_CLICKED)
            case ARC:
                if (arcStart == null) {
                    arcStart = localPoint;
                } else {
                    double radius = arcStart.distance(localPoint);
                    Arc arc = new Arc(arcStart.getX(), arcStart.getY(), radius, radius, 0, 90);
                    arc.setType(ArcType.OPEN);
                    arc.setFill(Color.TRANSPARENT);
                    arc.setStroke(arcColorPicker.getValue());
                    mapZoomGroup.getChildren().add(arc);
                    arcStart = null;
                }
                event.consume();
                break;
            case TEXT:
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Añadir texto");
                dialog.setHeaderText("Texto en la carta");
                dialog.setContentText("Introduce el texto:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(txt -> {
                    Text textNode = new Text(localPoint.getX(), localPoint.getY(), txt);
                    textNode.setFill(Color.BLACK);
                    mapZoomGroup.getChildren().add(textNode);
                });
                event.consume();
                break;
            case DELETE:
                Node toRemove = null;
                double minDist = Double.MAX_VALUE;
                for (Node n : mapZoomGroup.getChildren()) {
                    if (n == mapImageView) continue;
                    Bounds b = n.getBoundsInParent();
                    double centerX = b.getMinX() + b.getWidth()/2;
                    double centerY = b.getMinY() + b.getHeight()/2;
                    double dist = localPoint.distance(new Point2D(centerX, centerY));
                    if (dist < minDist) { minDist = dist; toRemove = n; }
                }
                if (toRemove != null && minDist < 20) {
                    mapZoomGroup.getChildren().remove(toRemove);
                }
                event.consume();
                break;
            case DISTANCE:
                if (distanceStart == null) {
                    distanceStart = localPoint;
                } else {
                    Line distLine = new Line(distanceStart.getX(), distanceStart.getY(), localPoint.getX(), localPoint.getY());
                    distLine.setStroke(Color.BLUE);
                    mapZoomGroup.getChildren().add(distLine);
                    double dist = distanceStart.distance(localPoint);
                    Text distText = new Text((distanceStart.getX()+localPoint.getX())/2,
                                             (distanceStart.getY()+localPoint.getY())/2 - 5,
                                             String.format("%.2f px", dist));
                    mapZoomGroup.getChildren().add(distText);
                    distanceStart = null;
                }
                event.consume();
                break;
            case LATITUDE:
                double lat = localPoint.getY();
                double lon = localPoint.getX();
                Text latLonText = new Text(localPoint.getX(), localPoint.getY(),
                                           String.format("(lat: %.2f, lon: %.2f)", lat, lon));
                mapZoomGroup.getChildren().add(latLonText);
                event.consume();
                break;
            case SELECTION:
                Node clickedNode = getTopNodeAt(event.getX(), event.getY());
                    if (clickedNode != null) {
                        mostrarOpcionesPara(clickedNode);
                        seleccionarElemento(clickedNode);                        
                    }
                    break;
            default:
               
                break;
        }
    }
}
    private void handleMouseDraggedFiltered(MouseEvent event) {
        Node target = event.getPickResult().getIntersectedNode();
    while (target != null && target != rootStackPane) {
        if (target instanceof javafx.scene.control.Control) {
            return; 
        }
        target = target.getParent();
    }
        if (currentTool == Tool.HAND && lastMousePosition != null && event.isPrimaryButtonDown()) {
            Point2D currentLocal = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
            double dx = (currentLocal.getX() - lastMousePosition.getX()) * currentZoomLevel;
            double dy = (currentLocal.getY() - lastMousePosition.getY()) * currentZoomLevel;
            mapZoomGroup.setTranslateX(mapZoomGroup.getTranslateX() + dx);
            mapZoomGroup.setTranslateY(mapZoomGroup.getTranslateY() + dy);
            clampTranslation();
            event.consume();
        }
    }
    private void handleMouseReleasedFiltered(MouseEvent event) {
        Node target = event.getPickResult().getIntersectedNode();
    while (target != null && target != rootStackPane) {
        if (target instanceof javafx.scene.control.Control) {
            
            return; // Let the event propagate to the actual control's handlers
        }
        target = target.getParent();
    }
        if (currentTool == Tool.HAND) {
            lastMousePosition = null;
            event.consume();
            rootStackPane.setCursor(Cursor.OPEN_HAND);
        }
    }
    private void handleKeyPan(KeyEvent event) {
        double delta = 10;
        if (event.getCode() == KeyCode.LEFT) mapZoomGroup.setTranslateX(mapZoomGroup.getTranslateX() + delta);
        else if (event.getCode() == KeyCode.RIGHT) mapZoomGroup.setTranslateX(mapZoomGroup.getTranslateX() - delta);
        else if (event.getCode() == KeyCode.UP) mapZoomGroup.setTranslateY(mapZoomGroup.getTranslateY() + delta);
        else if (event.getCode() == KeyCode.DOWN) mapZoomGroup.setTranslateY(mapZoomGroup.getTranslateY() - delta);
        clampTranslation();
    }
    // ========== EVENTOS DE ZOOM CORRESPONDIENTES A LOS BOTONES + - ===========
    @FXML private void zoomIn() { setZoomLevel(currentZoomLevel * ZOOM_FACTOR); }
    @FXML private void zoomOut() { setZoomLevel(currentZoomLevel / ZOOM_FACTOR); }
    // ========== MANEJO ZOOM Y PANEO ===========
    private void setZoomLevel(double newZoomLevel) {
        newZoomLevel = clamp(newZoomLevel, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);
        double pivotX = -mapZoomGroup.getTranslateX() + (map_scrollpane.getViewportBounds().getWidth()/2) ;
        double pivotY = (map_scrollpane.getViewportBounds().getHeight()/2) - mapZoomGroup.getTranslateY();
        //System.out.println(mapZoomGroup.getTranslateX()+","+mapZoomGroup.getTranslateY());
        //System.out.println(pivotX+","+pivotY);
        //Circle c = new Circle(pivotX, pivotY, 5, Color.BLUE);
        //mapZoomGroup.getChildren().add(c);                  
        currentZoomLevel = newZoomLevel;
        Scale scale = new Scale(currentZoomLevel, currentZoomLevel, pivotX, pivotY);
        mapZoomGroup.getTransforms().setAll(scale);
        clampTranslation();
    }
    private void clampTranslation() {
        double vw = map_scrollpane.getViewportBounds().getWidth();
        double vh = map_scrollpane.getViewportBounds().getHeight();
        Bounds gb = mapZoomGroup.getBoundsInParent();
        double cw = gb.getWidth(), ch = gb.getHeight();
        double maxX = (cw - cw/currentZoomLevel)/2;
        double maxY = (ch - ch/currentZoomLevel)/2;
        double minX = -cw + vw + (cw - cw/currentZoomLevel)/2;
        double minY = -ch + vh + (ch - ch/currentZoomLevel)/2;
        mapZoomGroup.setTranslateX(clamp(mapZoomGroup.getTranslateX(), minX, maxX));
        mapZoomGroup.setTranslateY(clamp(mapZoomGroup.getTranslateY(), minY, maxY));
    }
    private static double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return min;
        return Math.min(Math.max(value, min), max);
    }
    private void actualizarSelectionMenu(String tipo) {
    selectionMenu.setVisible(true);
    selectionMenu.setManaged(true);

    lineColorPicker.setVisible("line".equals(tipo));
    lineColorPicker.setManaged("line".equals(tipo));

    textColorPicker.setVisible("text".equals(tipo));
    textColorPicker.setManaged("text".equals(tipo));

    arcColorPicker.setVisible("arc".equals(tipo));
    arcColorPicker.setManaged("arc".equals(tipo));
}
    private void actualizarSelectionMenu() {
    selectionMenu.getChildren().clear();

    switch (currentTool) {
        case POINT -> {
            selectionMenu.getChildren().addAll(
                new Label("Color del punto:"),
                new ColorPicker(Color.BLUE)
            );
        }
        case LINE -> {
            selectionMenu.getChildren().addAll(
                new Label("Color de línea:"),
                new ColorPicker(Color.RED),
                new Label("Grosor:"),
                new Slider(1, 10, 2)
            );
        }
        case ARC -> {
            RadioButton open = new RadioButton("Abierto");
            RadioButton chord = new RadioButton("Cuerda");
            RadioButton round = new RadioButton("Redondeado");
            ToggleGroup group = new ToggleGroup();
            open.setToggleGroup(group);
            chord.setToggleGroup(group);
            round.setToggleGroup(group);
            selectionMenu.getChildren().addAll(
                new Label("Tipo de arco:"),
                open, chord, round
            );
        }
        case TEXT -> {
            selectionMenu.getChildren().addAll(
                new Label("Texto:"),
                new TextField(),
                new Label("Color:"),
                new ColorPicker(Color.BLACK)
            );
        }
        case DELETE -> {
            javafx.scene.control.ListView<String> list = new javafx.scene.control.ListView<>();
            list.getItems().addAll(
                mapZoomGroup.getChildren().stream()
                    .filter(n -> n != mapImageView)
                    .map(n -> n.getClass().getSimpleName())
                    .collect(Collectors.toList())
            );
            javafx.scene.control.Button borrar = new javafx.scene.control.Button("Eliminar Seleccionado");
            borrar.setOnAction(e -> {
                int i = list.getSelectionModel().getSelectedIndex();
                if (i >= 0) mapZoomGroup.getChildren().remove(i + 1);
            });
            selectionMenu.getChildren().addAll(new Label("Elementos en el mapa:"), list, borrar);
        }
        case DISTANCE -> {
            selectionMenu.getChildren().addAll(
                new Label("Color:"),
                new ColorPicker(Color.BLUE)
            );
        }
        case LATITUDE -> {
            selectionMenu.getChildren().addAll(
                new Label("Latitud/Longitud se muestra automáticamente al hacer clic")
            );
        }
        case SELECTION ->{
            
        }
        case HAND, NONE_SELECTED -> {
            selectionMenu.getChildren().add(new Label("Selecciona una herramienta para ver opciones."));
        }
    }
}
    private void mostrarOpcionesPara(Node node) {
    if (node instanceof Line) {
        System.out.println("Línea seleccionada");
        lineColorPicker.setValue((Color) ((Line) node).getStroke());
        actualizarSelectionMenu("line");
    } else if (node instanceof Text) {
        System.out.println("Texto seleccionado");
        textColorPicker.setValue((Color) ((Text) node).getFill());
        actualizarSelectionMenu("text");
    } else if (node instanceof Arc) {
        System.out.println("Arco seleccionado");
        arcColorPicker.setValue((Color) ((Arc) node).getStroke());
        actualizarSelectionMenu("arc");
    } else {
        System.out.println("Objeto no identificado");
        selectionMenu.setVisible(false);
        selectionMenu.setManaged(false);
    }
}
    private Node getTopNodeAt(double sceneX, double sceneY) {
    // Convert scene coordinates to local coordinates of mapZoomGroup
    Point2D localPoint = mapZoomGroup.sceneToLocal(sceneX, sceneY);

    // Create a list of nodes to check, excluding mapImageView initially
    List<Node> clickableNodes = mapZoomGroup.getChildren().stream()
                                    .filter(node -> node != mapImageView) // Exclude the image
                                    .collect(Collectors.toCollection(ArrayList::new));

    // Reverse the order to check the topmost drawn elements first
    // Nodes added later to the Group are rendered on top
    java.util.Collections.reverse(clickableNodes);

    for (Node node : clickableNodes) {
        // Check if the local point (relative to mapZoomGroup) is within the node's bounds
        if (node.getBoundsInLocal().contains(localPoint)) {
            return node;
        }
    }

    // If no drawn element is found, check if the click was on the mapImageView itself
    if (mapImageView.getBoundsInLocal().contains(localPoint)) {
        return mapImageView;
    }

    return null; // No relevant node found at the clicked location
}
    private void seleccionarElemento(Node elemento) {
    // Quitar selección anterior
    if (haloSeleccion != null) {
        mapZoomGroup.getChildren().remove(haloSeleccion);
        haloSeleccion = null;
    }

    if (elementoSeleccionado != null) {
        elementoSeleccionado.setEffect(null);
        elementoSeleccionado = null;
    }

    // Aplicar nueva selección
    if (elemento != null) {
        elementoSeleccionado = elemento;
        Bounds bounds = elemento.getBoundsInParent();
        Shape halo;

        if (elemento instanceof Circle c) {
            halo = new Circle(c.getCenterX(), c.getCenterY(), c.getRadius() + 5);
        } else {
            halo = new Rectangle(bounds.getMinX() - 5, bounds.getMinY() - 5,
                                 bounds.getWidth() + 10, bounds.getHeight() + 10);
        }

        halo.setStroke(Color.DODGERBLUE);
        halo.setStrokeWidth(2);
        halo.setFill(Color.TRANSPARENT);
        halo.getStrokeDashArray().addAll(6.0, 6.0);
        haloSeleccion = halo;

        mapZoomGroup.getChildren().add(haloSeleccion);
        halo.toBack(); // Asegura que no tape al objeto
    }
}

}