package controller.tools;

import model.MapDrawingTool;
import model.MapStateManager;
import controller.SelectionMenuManager;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle; 
import javafx.scene.shape.Polygon;   
import javafx.scene.shape.Circle;   
import javafx.scene.shape.Shape;   
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseButton;


public class MarkerTooL implements MapDrawingTool {

    private MapStateManager stateManager;
    private Group mapZoomGroup;
    private SelectionMenuManager menuManager; // To get current point color
    private ScrollPane mapScrollPane;

   
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
        // You might set a specific cursor here if it wasn't already set by MapInteractionHandler
    }

    @Override
    public void deactivate() {
        // Nothing specific to clean up for point tool on deactivate
    }

    @Override
    public void onMouseClick(MouseEvent event, Point2D mapCoords) {
       
    }

    @Override
    public void onMousePressed(MouseEvent event, Point2D mapCoords) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Shape marker;
            String selectedShape = menuManager.getMarkerShape();
            Color fillColor = menuManager.getColorPickerValue();
            double size = menuManager.getMarkerSize();
            // Eliminamos la variable strokeWidth aquí, ya no la necesitamos para la creación.

            switch (selectedShape) {
                case "Círculo" -> {
                    Circle circle = new Circle(mapCoords.getX(), mapCoords.getY(), size);
                    circle.setFill(fillColor);
                    marker = circle;
                }
                case "Cuadrado" -> {
                    // Resta la mitad del tamaño para centrar el cuadrado en las coordenadas del clic
                    Rectangle rect = new Rectangle(mapCoords.getX() - size / 2, mapCoords.getY() - size / 2, size, size);
                    rect.setFill(fillColor);
                    marker = rect;
                }
                case "Triángulo" -> {
                    Polygon triangle = new Polygon();
                    // Define los puntos para un triángulo equilátero centrado en (mapCoords.getX(), mapCoords.getY())
                    double h = size * Math.sqrt(3) / 2; // Altura del triángulo
                    triangle.getPoints().addAll(
                        mapCoords.getX(), mapCoords.getY() - h / 2,         // Punto superior
                        mapCoords.getX() - size / 2, mapCoords.getY() + h / 2, // Punto inferior izquierdo
                        mapCoords.getX() + size / 2, mapCoords.getY() + h / 2  // Punto inferior derecho
                    );
                    triangle.setFill(fillColor);
                    marker = triangle;
                }
                default -> {
                    // Por si acaso, si la forma no es reconocida, crea un círculo por defecto
                    Circle circle = new Circle(mapCoords.getX(), mapCoords.getY(), 10);
                    circle.setFill(Color.BLUE);
                    marker = circle;
                }
            }

            mapZoomGroup.getChildren().add(marker);
            event.consume();
        }
    }
    @Override public void onMouseDragged(MouseEvent event, Point2D mapCoords) {}
    @Override public void onMouseReleased(MouseEvent event, Point2D mapCoords) {}
}