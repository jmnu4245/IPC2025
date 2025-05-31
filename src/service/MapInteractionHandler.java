package service;

// En esta clase se delegan los eventos al menu o herramienta correspondiente
// y se evita que el menu retractil consuma los eventos
import drawmodel.MapStateManager;
import utils.utils;
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

    // --- Tool handlers definidos en Enunciado Controller ---
    private final Consumer<MouseEvent> toolClickHandler; 
    private final Consumer<MouseEvent> toolDragHandler;
    private final Consumer<MouseEvent> toolPressedHandler;
    private final Consumer<MouseEvent> toolReleaseHandler;
    private final Consumer<Node> selectionHandler;
    
    // --- Zoom ---
    private double currentZoomLevel = MIN_ZOOM_LEVEL;

    // --- Scroll state for hand tool ---
    private double lastScrollH = 0;
    private double lastScrollV = 0;

    // --- Mouse state local (sustituye a stateManager.setLastMousePosition) ---
    private Point2D lastMousePosition = null;

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
    }

    // --- Los eventos de Enunciado controler pasan por esta clase primero ---
    public void attachEventHandlers() {
        rootStackPane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        rootStackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
    }

    // --- Event Handlers maestros---
      
    private void handleMousePressed(MouseEvent event) {
        if (isControlClicked(event)) return;
        lastMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
        boolean handled = false;
        if (stateManager.getCurrentTool() !=  MapStateManager.Tool.NONE_SELECTED) {
            toolPressedHandler.accept(event);
            handled = true;
        } else if (stateManager.getisRuleSel()) {
            toolPressedHandler.accept(event);
            handled = true;
        } else if (stateManager.getisProtractorSel()) {
            toolPressedHandler.accept(event);
            handled = true;
        }
        if (handled) {
            event.consume();
            return;
        }
        if (stateManager.getCurrentTool() == MapStateManager.Tool.HAND && event.isPrimaryButtonDown()) {
            lastScrollH = mapScrollPane.getHvalue();
            lastScrollV = mapScrollPane.getVvalue();
            rootStackPane.setCursor(Cursor.CLOSED_HAND);
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isControlClicked(event)) return;
        boolean handled = false;
        if ((stateManager.getCurrentTool() == MapStateManager.Tool.HAND && event.isPrimaryButtonDown()) || event.isMiddleButtonDown()) {
            Point2D currentMouse = new Point2D(event.getSceneX(), event.getSceneY());
            if (lastMousePosition == null) {
                lastMousePosition = currentMouse;
            }
            Point2D lastMouse = lastMousePosition;
            double deltaX = currentMouse.getX() - lastMouse.getX();
            double deltaY = currentMouse.getY() - lastMouse.getY();
            Bounds viewportBounds = mapScrollPane.getViewportBounds();
            Bounds contentBounds = mapZoomGroup.getBoundsInLocal();
            double contentWidth = contentBounds.getWidth() * Math.max(mapZoomGroup.getScaleX(), 0.01);
            double contentHeight = contentBounds.getHeight() * Math.max(mapZoomGroup.getScaleY(), 0.01);
            double scrollableWidth = contentWidth - viewportBounds.getWidth();
            double scrollableHeight = contentHeight - viewportBounds.getHeight();
            double newHvalue;
            double newVvalue;
            if (scrollableWidth > 1e-6) {
                newHvalue = utils.clamp(lastScrollH - deltaX / scrollableWidth, 0.0, 1.0);
            } else {
                newHvalue = lastScrollH;
            }
            if (scrollableHeight > 1e-6) {
                newVvalue = utils.clamp(lastScrollV - deltaY / scrollableHeight, 0.0, 1.0);
            } else {
                newVvalue = lastScrollV;
            }
            mapScrollPane.setHvalue(newHvalue);
            mapScrollPane.setVvalue(newVvalue);
            lastMousePosition = currentMouse;
            lastScrollH = newHvalue;
            lastScrollV = newVvalue;
        }
        else if (stateManager.getCurrentTool() !=  MapStateManager.Tool.NONE_SELECTED) {
            toolDragHandler.accept(event);
            handled = true;
        } else if (stateManager.getisRuleSel()) {
            toolDragHandler.accept(event);
            handled = true;
        } else if (stateManager.getisProtractorSel()) {
            toolDragHandler.accept(event);
            handled = true;
        }
        else if(stateManager.getCurrentTool() != MapStateManager.Tool.HAND &&
            stateManager.getCurrentTool() != MapStateManager.Tool.SELECTION) {
            toolDragHandler.accept(event);
            handled = true;
        }
        if (handled) {
            event.consume();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (isControlClicked(event)) return;
        boolean handled = false;
        if (stateManager.getCurrentTool() !=  MapStateManager.Tool.NONE_SELECTED) {
            toolReleaseHandler.accept(event);
            handled = true;
        } else if (stateManager.getisRuleSel()) {
            toolReleaseHandler.accept(event);
            handled = true;
        } else if (stateManager.getisProtractorSel()) {
            toolReleaseHandler.accept(event);
            handled = true;
        }
        if (handled) {
            event.consume();
            return;
        }
        if (stateManager.getCurrentTool() == MapStateManager.Tool.HAND) {
            lastMousePosition = null;
            rootStackPane.setCursor(Cursor.OPEN_HAND);
            event.consume();
        }
    }

    private void handleMouseClick(MouseEvent event) {
        if (isControlClicked(event)) return;
        boolean handled = false;
        if (stateManager.getCurrentTool() !=  MapStateManager.Tool.NONE_SELECTED) {
            toolClickHandler.accept(event);
            handled = true;
        } else if (stateManager.getisRuleSel()) {
            toolClickHandler.accept(event);
            handled = true;
        } else if (stateManager.getisProtractorSel()) {
            toolClickHandler.accept(event);
            handled = true;
        }
        if (stateManager.getCurrentTool() == MapStateManager.Tool.SELECTION) {
            toolClickHandler.accept(event);
            Node clickedNode = getTopNodeAt(event.getSceneX(), event.getSceneY());
            selectionHandler.accept(clickedNode);
            handled = true;
        }
        if (handled) {
            event.consume();
            return;
        }
    }
    
    // --- Metodos de utilidad de clase ---
    private Node getTopNodeAt(double sceneX, double sceneY) {
        Point2D localPoint = mapZoomGroup.sceneToLocal(sceneX, sceneY);
        Node mapImageViewNode = mapZoomGroup.lookup("#mapImageView");
        var clickableNodes = mapZoomGroup.getChildren().stream()
            .filter(node -> node != mapImageViewNode)
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        java.util.Collections.reverse(clickableNodes);

        for (Node node : clickableNodes) {
            if (utils.isNodeAt(node, localPoint)) {
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

}