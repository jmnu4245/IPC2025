package controller.tools;

import model.MapDrawingTool;
import model.MapStateManager;
import controller.SelectionMenuManager;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.shape.Line;

public class LatitudeTool implements MapDrawingTool {
    private ScrollPane mapScrollPane;
    private MapStateManager stateManager;
    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;
    private Bounds contentBounds;
    
    @Override
    public void setDependencies(MapStateManager stateManager, Group mapZoomGroup, 
                               SelectionMenuManager menuManager, ScrollPane mapScrollPane) {
        this.stateManager = stateManager;
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane = mapScrollPane;
    }
    
    @Override
    public void activate() {
        stateManager.resetDrawingStates();
    }
    
    @Override
    public void deactivate() {
        // Nothing specific to clean up
    }
    
    @Override
    public void onMouseClick(MouseEvent event, Point2D mapCoords) {
        double lat = mapCoords.getY(); // Assuming Y is latitude
        double lon = mapCoords.getX(); // Assuming X is longitude
        
        // Obtener los límites del contenido del mapa
        contentBounds = mapZoomGroup.getBoundsInLocal();
        System.out.println(contentBounds.getMinX()+","+contentBounds.getMaxX()+","+contentBounds.getMaxY()+","+contentBounds.getMinY());
        // Crear línea horizontal (latitud) que recorre toda la imagen
        Line horizontalLine = new Line();
        horizontalLine.setStartX(contentBounds.getMinX()+10);
        horizontalLine.setStartY(mapCoords.getY());
        horizontalLine.setEndX(contentBounds.getMaxX()-10);
        horizontalLine.setEndY(mapCoords.getY());
        horizontalLine.setStroke(Color.RED);
        horizontalLine.setStrokeWidth(3);
        
        // Crear línea vertical (longitud) que recorre toda la imagen
        Line verticalLine = new Line();
        verticalLine.setStartX(mapCoords.getX());
        verticalLine.setStartY(contentBounds.getMinY()+10);
        verticalLine.setEndX(mapCoords.getX());
        verticalLine.setEndY(contentBounds.getMaxY()-10);
        verticalLine.setStroke(Color.BLUE);
        verticalLine.setStrokeWidth(3);
        
        // Crear texto con las coordenadas

        
        // Agregar las líneas y el texto al grupo
        mapZoomGroup.getChildren().addAll(horizontalLine, verticalLine);
        
        event.consume();
    }
    
    @Override 
    public void onMousePressed(MouseEvent event, Point2D mapCoords) {}
    
    @Override 
    public void onMouseDragged(MouseEvent event, Point2D mapCoords) {}
    
    @Override 
    public void onMouseReleased(MouseEvent event, Point2D mapCoords) {}
}