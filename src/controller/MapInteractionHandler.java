package controller; // Considera cambiar a 'service' si aplica mejor

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
            case SELECTION, NONE_SELECTED -> {
                if (event.isPrimaryButtonDown()) {
                    toolPressedHandler.accept(event);
                    event.consume();
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

        if (stateManager.getCurrentTool() == MapStateManager.Tool.HAND &&
            stateManager.getLastMousePosition() != null &&
            event.isPrimaryButtonDown()) {

            Point2D currentMouse = new Point2D(event.getSceneX(), event.getSceneY());
            Point2D lastMouse = stateManager.getLastMousePosition();

            double deltaX = currentMouse.getX() - lastMouse.getX();
            double deltaY = currentMouse.getY() - lastMouse.getY();

            Bounds viewportBounds = mapScrollPane.getViewportBounds();
            Bounds contentBounds = mapZoomGroup.getBoundsInLocal();

            double zoomX = Math.max(mapZoomGroup.getScaleX(), 0.01);
            double zoomY = Math.max(mapZoomGroup.getScaleY(), 0.01);

            double contentWidth = contentBounds.getWidth() * zoomX;
            double contentHeight = contentBounds.getHeight() * zoomY;

            double scrollableWidth = contentWidth - viewportBounds.getWidth();
            double scrollableHeight = contentHeight - viewportBounds.getHeight();

            double newHvalue = lastScrollH;
            double newVvalue = lastScrollV;

            if (scrollableWidth > 1e-6) {
                newHvalue = clamp(lastScrollH - deltaX / scrollableWidth, 0.0, 1.0);
            }
            if (scrollableHeight > 1e-6) {
                newVvalue = clamp(lastScrollV - deltaY / scrollableHeight, 0.0, 1.0);
            }

            mapScrollPane.setHvalue(newHvalue);
            mapScrollPane.setVvalue(newVvalue);

            stateManager.setLastMousePosition(currentMouse);
            lastScrollH = newHvalue;
            lastScrollV = newVvalue;

            System.out.println("ScrollH: " + newHvalue + ", ScrollV: " + newVvalue);
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
            Node clickedNode = getTopNodeAt(event.getSceneX(), event.getSceneY());
            selectionHandler.accept(clickedNode);
            event.consume();
        } else if (stateManager.getCurrentTool() != MapStateManager.Tool.HAND) {
            // toolClickHandler.accept(event);
            event.consume();
        }
    }

    // --- Utility Methods ---
    private Node getTopNodeAt(double sceneX, double sceneY) {
        Point2D localPoint = mapZoomGroup.sceneToLocal(sceneX, sceneY);
        Node mapImageViewNode = mapZoomGroup.lookup("#mapImageView");

        var clickableNodes = mapZoomGroup.getChildren().stream()
            .filter(node -> node != mapImageViewNode)
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        java.util.Collections.reverse(clickableNodes);

        for (Node node : clickableNodes) {
            if (node.getBoundsInLocal().contains(localPoint)) {
                System.out.println(node);
                return node;
            }
        }

        if (mapImageViewNode != null && mapImageViewNode.getBoundsInLocal().contains(localPoint)) {
            return mapImageViewNode;
        }

        return null;
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
