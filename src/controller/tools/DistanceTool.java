package controller.tools;

import model.MapDrawingTool;
import model.MapStateManager;
import controller.SelectionMenuManager;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.paint.Color; // Import Color

public class DistanceTool implements MapDrawingTool {
    
    private static final double REFERENCE_WIDTH_PX = 240.0;
    private static final double REFERENCE_HEIGHT_PX = 160.0;
    private static final double REAL_WORLD_METERS = 100_000.0; // 100 km

    private ScrollPane mapScrollPane;

    private MapStateManager stateManager;
    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;

    private Line previewLine; // To show line being drawn

    private Bounds contentBounds;

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
        stateManager.setDistanceStart(null);
    }

    @Override
    public void deactivate() {
        if (previewLine != null) {
            mapZoomGroup.getChildren().remove(previewLine);
            previewLine = null;
        }
        stateManager.setDistanceStart(null);
    }

@Override
public void onMousePressed(MouseEvent event, Point2D mapCoords) {
    if (event.getButton() == MouseButton.PRIMARY) {
        if (stateManager.getDistanceStart() == null) {
            // Inicializar medición de distancia
            stateManager.setDistanceStart(mapCoords);
            
            // Crear línea de vista previa con grosor dinámico
            previewLine = new Line(
                stateManager.getDistanceStart().getX(), 
                stateManager.getDistanceStart().getY(), 
                mapCoords.getX(), 
                mapCoords.getY()
            );
            previewLine.setStroke(menuManager.getColorPickerValue());
            previewLine.setStrokeWidth(menuManager.getDistanceThicknessSliderDynamic()); // Usar grosor dinámico
            previewLine.getStrokeDashArray().addAll(5d, 5d); // Línea punteada para preview
            mapZoomGroup.getChildren().add(previewLine);
        }
    }
}

@Override
public void onMouseDragged(MouseEvent event, Point2D mapCoords) {
    if (event.getButton() == MouseButton.PRIMARY && 
        stateManager.getDistanceStart() != null && 
        previewLine != null) {
        
        // Actualizar posición final de la línea de vista previa
        previewLine.setEndX(mapCoords.getX());
        previewLine.setEndY(mapCoords.getY());
        
        // Actualizar grosor dinámicamente durante el arrastre
        previewLine.setStrokeWidth(menuManager.getDistanceThicknessSliderDynamic());
        previewLine.setStroke(menuManager.getColorPickerValue());
    }
}

@Override
public void onMouseReleased(MouseEvent event, Point2D mapCoords) {
    if (event.getButton() == MouseButton.PRIMARY && 
        stateManager.getDistanceStart() != null && 
        previewLine != null) {
        
        mapZoomGroup.getChildren().remove(previewLine);

        Line distLine = new Line(
            stateManager.getDistanceStart().getX(), 
            stateManager.getDistanceStart().getY(), 
            mapCoords.getX(), 
            mapCoords.getY()
        );
        distLine.setStroke(menuManager.getColorPickerValue());
        distLine.setStrokeWidth(menuManager.getDistanceThicknessSliderDynamic());
        mapZoomGroup.getChildren().add(distLine);

        double dx = mapCoords.getX() - stateManager.getDistanceStart().getX();
        double dy = mapCoords.getY() - stateManager.getDistanceStart().getY();

        double realDx = dx * (REAL_WORLD_METERS / REFERENCE_WIDTH_PX);
        double realDy = dy * (REAL_WORLD_METERS / REFERENCE_HEIGHT_PX);

        double dist = Math.sqrt(realDx * realDx + realDy * realDy);

        Text distText = new Text(
            (stateManager.getDistanceStart().getX() + mapCoords.getX()) / 2 -30,
            (stateManager.getDistanceStart().getY() + mapCoords.getY()) / 2 - 30,
            String.format("%.2f m", dist)
        );

        if (((Color)menuManager.getColorPickerValue()).equals(Color.BLACK)){
            distText.setFill(Color.GRAY);
        } else {
            distText.setFill(Color.BLACK);
        }

        double fontSize = Math.max(10, menuManager.getDistanceThicknessSliderDynamic() * 10);
        distText.setStyle(String.format("-fx-font-weight: bold; -fx-font-size: %.1fpx;", fontSize));

        mapZoomGroup.getChildren().add(distText);

        previewLine = null;
        stateManager.setDistanceStart(null);
    }}

@Override
public void onMouseClick(MouseEvent event, Point2D mapCoords) {

}
}