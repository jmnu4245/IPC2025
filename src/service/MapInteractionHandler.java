package service; 
//En esta clase se delegan los eventos al menu o herramienta correspondiente
//y se evita que el menu retractil consuma los eventos
import model.MapStateManager;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.shape.Shape;

import java.util.function.Consumer;

public class MapInteractionHandler {

    // --- Constants ---
    private static final double MIN_ZOOM_LEVEL = 0.1386;
    private static final double MAX_ZOOM_LEVEL = 10.0;
    private static final double ZOOM_FACTOR = 1.01;

    // --- Core State ---
    private final MapStateManager stateManager;
    private final Group mapZoomGroup;
    private final ScrollPane mapScrollPane;
    private final TitledPane titledPane;
    private final StackPane rootStackPane;

    // --- Tool handlers ---
    private final Consumer<MouseEvent> toolClickHandler; 
    private final Consumer<MouseEvent> toolDragHandler;
    private final Consumer<MouseEvent> toolPressedHandler;
    private final Consumer<MouseEvent> toolReleaseHandler;
    private final Consumer<Node> selectionHandler;
    

    // --- Visual Debugging ---
    private final boolean visualDebuggingEnabled = true;
    private Group debugVisualsGroup;

    // --- Zoom ---
    private double currentZoomLevel = MIN_ZOOM_LEVEL;

    // --- Scroll state for hand tool ---
    private double lastScrollH = 0;
    private double lastScrollV = 0;

    // --- Constructor ---
    public MapInteractionHandler(
        MapStateManager stateManager,
        Group mapZoomGroup,
        ScrollPane mapScrollPane,
        StackPane rootStackPane,
        TitledPane titledPane,
        Consumer<MouseEvent> toolPressedHandler,
        Consumer<MouseEvent> toolDragHandler,
        Consumer<MouseEvent> toolReleaseHandler, 
        Consumer<MouseEvent> toolClickHandler,
        Consumer<Node> selectionHandler
    ) {
        this.stateManager = stateManager;
        this.mapZoomGroup = mapZoomGroup;
        this.mapScrollPane = mapScrollPane;
        this.rootStackPane = rootStackPane;
        this.titledPane = titledPane;

        this.toolClickHandler = toolClickHandler;
        this.toolDragHandler = toolDragHandler;
        this.toolPressedHandler = toolPressedHandler;
        this.toolReleaseHandler = toolReleaseHandler;
        this.selectionHandler = selectionHandler;
        

        if (visualDebuggingEnabled) {
            this.debugVisualsGroup = new Group();
            this.mapZoomGroup.getChildren().add(debugVisualsGroup);
        }
    }

    // --- Attach Events ---
    public void attachEventHandlers() {
        rootStackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
    }

    // --- Event Handlers ---
    private void handleMousePressed(MouseEvent event) {
        if (isControlClicked(event)) return;

        switch (stateManager.getCurrentTool()) {
            case HAND -> {
                if (event.isPrimaryButtonDown()) {
                    lastScrollH = mapScrollPane.getHvalue();
                    lastScrollV = mapScrollPane.getVvalue();
                    stateManager.setLastMousePosition(new Point2D(event.getSceneX(), event.getSceneY()));
                    rootStackPane.setCursor(Cursor.CLOSED_HAND);
                }
            }
            
            default -> {
                if (event.isPrimaryButtonDown()) {
                    toolPressedHandler.accept(event);
                    event.consume();
                }
            }
        }
    }
    private void handleMouseDragged(MouseEvent event) {
    if (isControlClicked(event)) return;
//Logica de mano incorporada
    if (stateManager.getCurrentTool() == MapStateManager.Tool.HAND &&
        stateManager.getLastMousePosition() != null &&
        event.isPrimaryButtonDown()) {
        Point2D currentMouse = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D lastMouse = stateManager.getLastMousePosition();
        double deltaX = currentMouse.getX() - lastMouse.getX();
        double deltaY = currentMouse.getY() - lastMouse.getY();
        Bounds viewportBounds = mapScrollPane.getViewportBounds();
        Bounds contentBounds = mapZoomGroup.getBoundsInLocal();
        // Las variables zoomX y zoomY se han integrado directamente.
        double contentWidth = contentBounds.getWidth() * Math.max(mapZoomGroup.getScaleX(), 0.01);
        double contentHeight = contentBounds.getHeight() * Math.max(mapZoomGroup.getScaleY(), 0.01);
        double scrollableWidth = contentWidth - viewportBounds.getWidth();
        double scrollableHeight = contentHeight - viewportBounds.getHeight();
        double newHvalue;
        double newVvalue;
        if (scrollableWidth > 1e-6) {
            newHvalue = clamp(lastScrollH - deltaX / scrollableWidth, 0.0, 1.0);
        } else {
            // Si no hay ancho desplazable, mantiene el valor horizontal anterior.
            newHvalue = lastScrollH;
        }
        if (scrollableHeight > 1e-6) {
            newVvalue = clamp(lastScrollV - deltaY / scrollableHeight, 0.0, 1.0);
        } else {
            // Si no hay alto desplazable, mantiene el valor vertical anterior.
            newVvalue = lastScrollV;
        }
        mapScrollPane.setHvalue(newHvalue);
        mapScrollPane.setVvalue(newVvalue);
        stateManager.setLastMousePosition(currentMouse);
        lastScrollH = newHvalue;
        lastScrollV = newVvalue;

    } else if (event.isPrimaryButtonDown() &&
               stateManager.getCurrentTool() != MapStateManager.Tool.HAND &&
               stateManager.getCurrentTool() != MapStateManager.Tool.SELECTION) {
        toolDragHandler.accept(event);
        event.consume();
    }
}
    private void handleMouseReleased(MouseEvent event) {
        if (isControlClicked(event)) return;
        if (stateManager.getCurrentTool() == MapStateManager.Tool.HAND) {
            stateManager.setLastMousePosition(null);
            rootStackPane.setCursor(Cursor.OPEN_HAND);
            event.consume();
        } else if (stateManager.getCurrentTool() != MapStateManager.Tool.NONE_SELECTED &&
                   stateManager.getCurrentTool() != MapStateManager.Tool.SELECTION) {
            toolReleaseHandler.accept(event);
            event.consume();
        }
    }

    private void handleMouseClick(MouseEvent event) {
        if (isControlClicked(event)) return;

        if (stateManager.getCurrentTool() == MapStateManager.Tool.SELECTION) {
            toolClickHandler.accept(event);
            Node clickedNode = getTopNodeAt(event.getSceneX(), event.getSceneY());
            
            selectionHandler.accept(clickedNode);
            event.consume();
            
        } 
        else if(stateManager.getCurrentTool() == MapStateManager.Tool.TEXT){ toolClickHandler.accept(event); event.consume();}
        else if(stateManager.getCurrentTool() == MapStateManager.Tool.DISTANCE){ toolClickHandler.accept(event); event.consume();}
        else if(stateManager.getCurrentTool() ==MapStateManager.Tool.LATITUDE ){toolClickHandler.accept(event); event.consume();}
        else if (stateManager.getCurrentTool() != MapStateManager.Tool.HAND) {
            // toolClickHandler.accept(event);
                event.consume();
        }
        
    }
    // --- Utility Methods ---
    private Node getTopNodeAt(double sceneX, double sceneY) {
        Point2D localPoint = mapZoomGroup.sceneToLocal(sceneX, sceneY);
        Node mapImageViewNode = mapZoomGroup.lookup("#mapImageView");

        // Collect all clickable nodes except the map background
        var clickableNodes = mapZoomGroup.getChildren().stream()
            .filter(node -> node != mapImageViewNode)
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        // Reverse the list to check from top to bottom
        java.util.Collections.reverse(clickableNodes);

        for (Node node : clickableNodes) {
            if (isNodeAt(node, localPoint)) {
                return node;
            }
        }

        // Check if the map background is clicked
        if (mapImageViewNode != null && mapImageViewNode.getBoundsInLocal().contains(localPoint)) {
            return mapImageViewNode;
        }

        return null;
    }

    private boolean isNodeAt(Node node, Point2D point) {
        Bounds bounds = node.getBoundsInLocal();

        if (node instanceof Line) {
            // For lines, check if the point is close to the line
            Line line = (Line) node;
            double lineLength = Math.sqrt(Math.pow(line.getEndX() - line.getStartX(), 2) +
                                         Math.pow(line.getEndY() - line.getStartY(), 2));
            double distance = Math.abs((line.getEndX() - line.getStartX()) * (line.getStartY() - point.getY()) -
                                      (line.getStartX() - point.getX()) * (line.getEndY() - line.getStartY())) / lineLength;
            return distance <= 2; // Adjust the threshold as needed
        } else if (node instanceof Text) {
            // For text, check if the point is within the text bounds
            return bounds.contains(point);
        } else if (node instanceof Shape) {
            // For shapes like Circle, Rectangle, Arc, etc., check if the point is within the shape bounds
            return bounds.contains(point);
        }

        return false;
    }

    private boolean isControlClicked(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        Node currentNode = clickedNode;

        while (currentNode != null && currentNode != titledPane && currentNode != rootStackPane) {
            if (currentNode.getStyleClass().contains("title")) return true;
            currentNode = currentNode.getParent();
        }

        return currentNode == titledPane && titledPane.isExpanded();
    }

    private static double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return min;
        return Math.min(Math.max(value, min), max);
    }
    
}
