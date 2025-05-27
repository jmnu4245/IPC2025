package controller.tools;

import model.MapDrawingTool;
import model.MapStateManager;
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

public class LineTool implements MapDrawingTool {
 
        
    private static final double SCROLL_THRESHOLD = 50.0;
    private static final double SCROLL_SPEED = 0.02;

    private MapStateManager stateManager;
    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;
    private ScrollPane mapScrollPane;

    private Line currentLine;
    private boolean isDrawing = false;
    private Point2D pressStart;
    private boolean dragged = false;

    @Override
    public void setDependencies(MapStateManager stateManager, Group mapZoomGroup, SelectionMenuManager menuManager,ScrollPane mapScrollPane) {
        this.stateManager = stateManager;
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane=mapScrollPane;
    }

    @Override
    public void activate() {
        stateManager.resetDrawingStates();
        stateManager.setLineStart(null);
        isDrawing = false;
    }

    @Override
    public void deactivate() {
        if (currentLine != null) {
            mapZoomGroup.getChildren().remove(currentLine);
            currentLine = null;
        }
        stateManager.setLineStart(null);
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
        if(pressStart == null){
            pressStart = mapCoords;
            }
        dragged = true;
        handleViewportAutoScroll(event);

        if (!isDrawing) {
            
            System.out.println("dragged working");
            // Comienza línea si no está en progreso
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
       
    }

    private void finalizeLine() {
        currentLine = null;
        isDrawing = false;
        stateManager.setLineStart(null);
    }
private void handleViewportAutoScroll(MouseEvent event) {
    // Obtener la posición del ratón en la escena
    double mouseX = event.getSceneX();
    double mouseY = event.getSceneY();

    // Obtener los límites del ScrollPane en la escena
    Bounds scrollPaneBounds = mapZoomGroup.localToScene(mapScrollPane.getBoundsInLocal());

    // Obtener los límites del viewport (lo que es visible)
    Bounds viewportBounds = mapScrollPane.getViewportBounds();

    // Calcular las coordenadas relativas del ratón dentro del viewport
    // Ajustamos la posición del ratón a las coordenadas internas del viewport para el cálculo de los umbrales
    double mouseXInViewport = mouseX - scrollPaneBounds.getMinX();
    double mouseYInViewport = mouseY - scrollPaneBounds.getMinY();

    // Determinar la dirección y cantidad de scroll
    double deltaH = 0;
    double deltaV = 0;

    // Scroll horizontal
    if (mouseXInViewport < SCROLL_THRESHOLD) {
        deltaH = -SCROLL_SPEED; // Mover a la izquierda
    } else if (mouseXInViewport > viewportBounds.getWidth() - SCROLL_THRESHOLD) {
        deltaH = SCROLL_SPEED;  // Mover a la derecha
    }

    // Scroll vertical
    if (mouseYInViewport < SCROLL_THRESHOLD) {
        deltaV = -SCROLL_SPEED; // Mover hacia arriba
    } else if (mouseYInViewport > viewportBounds.getHeight() - SCROLL_THRESHOLD) {
        deltaV = SCROLL_SPEED;  // Mover hacia abajo
    }

    // Aplicar el scroll
    if (deltaH != 0 || deltaV != 0) {
        double newHvalue = clamp(mapScrollPane.getHvalue() + deltaH, 0.0, 1.0);
        double newVvalue = clamp(mapScrollPane.getVvalue() + deltaV, 0.0, 1.0);

        mapScrollPane.setHvalue(newHvalue);
        mapScrollPane.setVvalue(newVvalue);

        // Opcional: Actualizar lastScrollH y lastScrollV si los usas para el zoom/pan
        // Esto es importante para que el movimiento del scroll afecte también al pan manual
        // lastScrollH = newHvalue;
        // lastScrollV = newVvalue;
    }
}
    private double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
