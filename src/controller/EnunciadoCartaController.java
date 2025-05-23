package controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class EnunciadoCartaController implements Initializable {

    @FXML private ScrollPane map_scrollpane;
    @FXML private Group mapZoomGroup;
    @FXML private ImageView mapImageView;
    @FXML private ToolBar options_toolbar;
    @FXML private ToggleGroup options;
    @FXML private StackPane rootStackPane;
    @FXML private ToggleButton manoBtn;

    private double currentZoomLevel = 1.0;
    private static final double MIN_ZOOM_LEVEL = 1.0;
    private static final double MAX_ZOOM_LEVEL = 5.0;
    private static final double ZOOM_FACTOR = 1.1;

    private Point2D lastMousePosition;
    
    private Boolean mapClicked = false;

    // Para dibujo de herramientas
    private Point2D lineStart;
    private Point2D arcStart;
    private Point2D distanceStart;

    private enum Tool { HAND, POINT, LINE, ARC, TEXT, DELETE, PROTRACTOR, DISTANCE, LATITUDE, NONE_SELECTED }
    private Tool currentTool = Tool.NONE_SELECTED;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización básica
        mapImageView.setPreserveRatio(true);
        mapImageView.setSmooth(true);
        map_scrollpane.setPannable(false);
        map_scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        map_scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mapImageView.setMouseTransparent(true);
mapZoomGroup.setPickOnBounds(true);
mapZoomGroup.setFocusTraversable(true);

// Fondo invisible clicable
Rectangle fondo = new Rectangle(mapImageView.getImage().getWidth(), mapImageView.getImage().getHeight());
fondo.setFill(Color.TRANSPARENT);
fondo.setMouseTransparent(false);
mapZoomGroup.getChildren().add(0, fondo);

        
        // Filtros de eventos de ratón
        rootStackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleClickFiltered);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressedFiltered);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDraggedFiltered);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleasedFiltered);
        

        map_scrollpane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPan);
        // Configuración de listener para el ToggleGroup
        configurarToolSelector();

        // Selección inicial
        manoBtn.setSelected(true);
    }

    private void configurarToolSelector() {
        options.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                ToggleButton btn = (ToggleButton) newToggle;
                switch (btn.getId()) {
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

    private void manoPressed() {
        currentTool = Tool.HAND;
        rootStackPane.setCursor(Cursor.OPEN_HAND);
    }

    private void puntoPressed() {
        currentTool = Tool.POINT;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
    }

    private void lineaPressed() {
        currentTool = Tool.LINE;
        lineStart = null;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
    }

    private void arcoPressed() {
        currentTool = Tool.ARC;
        arcStart = null;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
    }

    private void textoPressed() {
        currentTool = Tool.TEXT;
        rootStackPane.setCursor(Cursor.TEXT);
    }

    private void limpiarPressed() {
        currentTool = Tool.DELETE;
        mapZoomGroup.getChildren().removeIf(node -> node != mapImageView);
        rootStackPane.setCursor(Cursor.DEFAULT);
    }

    private void distanciaPressed() {
        currentTool = Tool.DISTANCE;
        distanceStart = null;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
    }

    private void latitudPressed() {
        currentTool = Tool.LATITUDE;
        rootStackPane.setCursor(Cursor.CROSSHAIR);
    }

    private void noneSelected() {
        currentTool = Tool.NONE_SELECTED;
        rootStackPane.setCursor(Cursor.DEFAULT);
    }

    // ========== EVENTOS DE MOUSE FILTRADOS ===========
    private void handleMousePressedFiltered(MouseEvent event) {
        Node target = event.getPickResult().getIntersectedNode();
    while (target != null && target != rootStackPane) {
        if (target instanceof javafx.scene.control.Control) {
            System.out.println("Control clicked in handleClickFiltered: " + target);
            
            return;
        }
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
        if (target instanceof javafx.scene.control.Control) {
            System.out.println("Control clicked in handleClickFiltered: " + target);
            
            return; 
        }
        target = target.getParent();
    }
   

    Point2D localPoint = mapZoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
    if (!mapZoomGroup.getBoundsInLocal().contains(localPoint)) return;

    // We only want to process clicks for drawing tools, not the hand tool here
    if (currentTool != Tool.HAND) {
        switch (currentTool) {
            case POINT:
                Circle c = new Circle(localPoint.getX(), localPoint.getY(), 5, Color.BLUE);
                mapZoomGroup.getChildren().add(c);
                event.consume(); // Consume MOUSE_CLICKED for drawing a point
                break;
            case LINE:
                if (lineStart == null) {
                    lineStart = localPoint;
                } else {
                    Line line = new Line(lineStart.getX(), lineStart.getY(), localPoint.getX(), localPoint.getY());
                    line.setStroke(Color.RED);
                    mapZoomGroup.getChildren().add(line);
                    lineStart = null;
                }
                event.consume(); // Consume MOUSE_CLICKED for drawing a line
                break;
            // ... (ensure other drawing tools also consume MOUSE_CLICKED)
            case ARC:
                if (arcStart == null) {
                    arcStart = localPoint;
                } else {
                    double radius = arcStart.distance(localPoint);
                    Arc arc = new Arc(arcStart.getX(), arcStart.getY(), radius, radius, 0, 90);
                    arc.setType(ArcType.OPEN);
                    arc.setStroke(Color.GREEN);
                    arc.setFill(Color.TRANSPARENT);
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
            default:

                break;
        }
    }
}
    private void handleMouseDraggedFiltered(MouseEvent event) {
        Node target = event.getPickResult().getIntersectedNode();
    while (target != null && target != rootStackPane) {
        if (target instanceof javafx.scene.control.Control) {
            System.out.println("Control clicked in handleClickFiltered: " + target);
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
            System.out.println("Control clicked in handleClickFiltered: " + target);
            
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

    @FXML private void zoomIn() { setZoomLevel(currentZoomLevel * ZOOM_FACTOR); }
    @FXML private void zoomOut() { setZoomLevel(currentZoomLevel / ZOOM_FACTOR); }

    private void setZoomLevel(double newZoomLevel) {
        newZoomLevel = clamp(newZoomLevel, MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL);
        double pivotX = (map_scrollpane.getHvalue() * (mapZoomGroup.getBoundsInLocal().getWidth() * currentZoomLevel - map_scrollpane.getViewportBounds().getWidth())) / currentZoomLevel;
        double pivotY = (map_scrollpane.getVvalue() * (mapZoomGroup.getBoundsInLocal().getHeight() * currentZoomLevel - map_scrollpane.getViewportBounds().getHeight())) / currentZoomLevel;
        double oldScale = currentZoomLevel;
        currentZoomLevel = newZoomLevel;
        mapZoomGroup.setScaleX(currentZoomLevel);
        mapZoomGroup.setScaleY(currentZoomLevel);
        double scaleFactor = newZoomLevel / oldScale;
        mapZoomGroup.setTranslateX(mapZoomGroup.getTranslateX() * scaleFactor - (pivotX * (scaleFactor - 1)));
        mapZoomGroup.setTranslateY(mapZoomGroup.getTranslateY() * scaleFactor - (pivotY * (scaleFactor - 1)));
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
}
