package controller.tools;

import drawmodel.MapDrawingTool;
import drawmodel.MapStateManager;
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
    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;
    private Bounds contentBounds;
    
    @Override
    public void setDependencies(Group mapZoomGroup, 
                               SelectionMenuManager menuManager, ScrollPane mapScrollPane) {
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane = mapScrollPane;
    }
    
    @Override
    public void activate() {
    }
    
    @Override
    public void deactivate() {
        // Nothing specific to clean up
    }
    
    @Override
    public void onMouseClick(MouseEvent event, Point2D mapCoords) {
        double lat = mapCoords.getY();
        double lon = mapCoords.getX();
        
        // Obtener los límites del contenido del mapa
        contentBounds = mapZoomGroup.getBoundsInLocal();
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