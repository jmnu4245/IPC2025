package controller.tools;

import drawmodel.MapDrawingTool;
import drawmodel.MapStateManager;
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

    private Group mapZoomGroup;
    private SelectionMenuManager menuManager; // To get current point color
    private ScrollPane mapScrollPane;

   
    @Override
    public void setDependencies(Group mapZoomGroup, SelectionMenuManager menuManager,ScrollPane mapScrollPane) {
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane=mapScrollPane;
    }


    @Override
    public void activate() {
    }
    @Override
    public void deactivate() {
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
                    Rectangle rect = new Rectangle(mapCoords.getX() - (size), mapCoords.getY() - (size ), size*2, size*2);
                    rect.setFill(fillColor);
                    marker = rect;
                }
                case "Triángulo" -> {
                    Polygon triangle = new Polygon();
                    // Define los puntos para un triángulo equilátero centrado en (mapCoords.getX(), mapCoords.getY())
                    double h = size * Math.sqrt(3) / 2; // Altura del triángulo
                    triangle.getPoints().addAll(
                        mapCoords.getX(), mapCoords.getY() - h,         // Punto superior
                        mapCoords.getX() - size , mapCoords.getY() + h , // Punto inferior izquierdo
                        mapCoords.getX() + size , mapCoords.getY() + h   // Punto inferior derecho
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