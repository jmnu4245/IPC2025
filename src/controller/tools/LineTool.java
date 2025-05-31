package controller.tools;

import drawmodel.MapDrawingTool;
import drawmodel.MapStateManager;
import utils.utils;
import controller.SelectionMenuManager;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class LineTool implements MapDrawingTool {

    private static final double SCROLL_THRESHOLD = 50.0;
    private static final double SCROLL_SPEED = 0.005; // Más lento

    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;
    private ScrollPane mapScrollPane;

    private Line currentLine;
    private boolean isDrawing = false;
    private Circle startPointCircle;
    private Circle endPointCircle;

    private Point2D pressStart;
    private boolean dragged = false;

    // Clip para el recorte visual
    private Rectangle mapClip = null;

    @Override
    public void setDependencies(Group mapZoomGroup, SelectionMenuManager menuManager, ScrollPane mapScrollPane) {
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane = mapScrollPane;

        // Añadir clip del tamaño del zoomGroup si no existe
        if (mapClip == null) {
            mapClip = new Rectangle();
            mapZoomGroup.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                mapClip.setWidth(newBounds.getWidth());
                mapClip.setHeight(newBounds.getHeight());
            });
            Bounds b = mapZoomGroup.getLayoutBounds();
            mapClip.setWidth(b.getWidth());
            mapClip.setHeight(b.getHeight());
            mapZoomGroup.setClip(mapClip);
        }
    }

    @Override
    public void activate() {
        isDrawing = false;
    }

    @Override
    public void deactivate() {
        if (currentLine != null) {
            mapZoomGroup.getChildren().remove(currentLine);
            currentLine = null;
        }
        isDrawing = false;
    }

    @Override
    public void onMousePressed(MouseEvent event, Point2D mapCoords) {
        System.out.println("press recibido");
        if (event.getButton() == MouseButton.PRIMARY) {
            activate();
            pressStart = mapCoords;
            dragged = false;
        }
    }

    @Override
    public void onMouseDragged(MouseEvent event, Point2D mapCoords) {
        if (pressStart == null) {
            pressStart = mapCoords;
        }
        dragged = true;

        handleViewportAutoScroll(event);

        // Comprobar que el cursor está dentro del área del zoomGroup
        Bounds zoomBounds = mapZoomGroup.getBoundsInLocal();
        double margin = 0; // Cambia a 20 si quieres margen interno
        double mouseX = mapCoords.getX();
        double mouseY = mapCoords.getY();

        if (mouseX < zoomBounds.getMinX() + margin || mouseX > zoomBounds.getMaxX() - margin ||
            mouseY < zoomBounds.getMinY() + margin || mouseY > zoomBounds.getMaxY() - margin) {
            return; // No actualiza la línea si el cursor está fuera del zoomGroup
        }

        if (!isDrawing) {
            System.out.println("dragged working");
            currentLine = new Line();
            currentLine.setStartX(mapCoords.getX());
            currentLine.setStartY(mapCoords.getY());
            currentLine.setStroke(menuManager.getColorPickerValue());
            currentLine.setStrokeWidth(menuManager.getLineThickness());
            mapZoomGroup.getChildren().add(currentLine);
            isDrawing = true;
        }

        currentLine.setEndX(mapCoords.getX());
        currentLine.setEndY(mapCoords.getY());
    }

    @Override
    public void onMouseReleased(MouseEvent event, Point2D mapCoords) {
        System.out.println("released");
        System.out.println(isDrawing);
        if (dragged && isDrawing && currentLine != null) {
            System.out.println("released working");
            currentLine.setEndX(mapCoords.getX());
            currentLine.setEndY(mapCoords.getY());
            finalizeLine();
            deactivate();
        }
        event.consume();
    }

    @Override
    public void onMouseClick(MouseEvent event, Point2D mapCoords) {
        // No usado
    }

    private void finalizeLine() {
        currentLine = null;
        isDrawing = false;
    }

    private void handleViewportAutoScroll(MouseEvent event) {
        // Obtener la posición del ratón en la escena
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();

        // Obtener los límites del ScrollPane en la escena
        Bounds scrollPaneBounds = mapScrollPane.localToScene(mapScrollPane.getBoundsInLocal());

        // Obtener los límites del viewport (lo que es visible)
        Bounds viewportBounds = mapScrollPane.getViewportBounds();

        // Calcular las coordenadas relativas del ratón dentro del viewport
        double mouseXInViewport = mouseX - scrollPaneBounds.getMinX();
        double mouseYInViewport = mouseY - scrollPaneBounds.getMinY();

        double deltaH = 0;
        double deltaV = 0;

        // Scroll horizontal
        if (mouseXInViewport < SCROLL_THRESHOLD) {
            deltaH = -SCROLL_SPEED;
        } else if (mouseXInViewport > viewportBounds.getWidth() - SCROLL_THRESHOLD) {
            deltaH = SCROLL_SPEED;
        }

        // Scroll vertical
        if (mouseYInViewport < SCROLL_THRESHOLD) {
            deltaV = -SCROLL_SPEED;
        } else if (mouseYInViewport > viewportBounds.getHeight() - SCROLL_THRESHOLD) {
            deltaV = SCROLL_SPEED;
        }

        // Aplicar el scroll, pero solo si todavía hay espacio para mover el viewport en esa dirección 
        double newHvalue = utils.clamp(mapScrollPane.getHvalue() + deltaH, 0.0, 1.0);
        double newVvalue = utils.clamp(mapScrollPane.getVvalue() + deltaV, 0.0, 1.0);
        if (newHvalue != mapScrollPane.getHvalue()) {
            mapScrollPane.setHvalue(newHvalue);
        }
        if (newVvalue != mapScrollPane.getVvalue()) {
            mapScrollPane.setVvalue(newVvalue);
        }
    }

}