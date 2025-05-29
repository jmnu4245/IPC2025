package controller.tools;

import model.MapDrawingTool;
import model.MapStateManager;
import controller.SelectionMenuManager;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import java.util.Arrays;
import javafx.scene.text.Text;  
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Circle;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;

public class ArcTool implements MapDrawingTool {
    private ScrollPane mapScrollPane;
    
    private MapStateManager stateManager;
    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;

    private Point2D centerPoint;        
    private Circle tempCenterCircle;    

    private Line previewRadiusLine;     
    private Text radiusValueText;       
    private Arc previewArc;             

    private boolean definingRadius = false; 
    private boolean definingAngle = false;  

    private static final Double[] BASE_DASH_PATTERN_DOTTED = {1.0, 3.0}; 
    private static final Double[] BASE_DASH_PATTERN_DASHED = {5.0, 5.0}; 
    private double currentRadius = 0;
    
    private Rectangle clip;

    @Override
    public void setDependencies(MapStateManager stateManager,   Group mapZoomGroup, SelectionMenuManager menuManager,ScrollPane mapScrollPane) {
        this.stateManager = stateManager;
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane=mapScrollPane;
        
    }

    @Override
    public void activate() {
        stateManager.resetDrawingStates();
        stateManager.setArcStart(null); 

        if (tempCenterCircle != null) mapZoomGroup.getChildren().remove(tempCenterCircle);
        if (previewRadiusLine != null) mapZoomGroup.getChildren().remove(previewRadiusLine);
        if (radiusValueText != null) mapZoomGroup.getChildren().remove(radiusValueText);
        if (previewArc != null) mapZoomGroup.getChildren().remove(previewArc);

        tempCenterCircle = null;
        previewRadiusLine = null;
        radiusValueText = null;
        previewArc = null;
        centerPoint = null;
        definingRadius = false;
        definingAngle = false;
        
        double w = mapZoomGroup.getBoundsInLocal().getWidth();
        double h = mapZoomGroup.getBoundsInLocal().getHeight();
        if (clip == null) {
            clip = new Rectangle(10, 10, w, h);
            mapZoomGroup.setClip(clip);
        } else {
            clip.setWidth(w);
            clip.setHeight(h);
        }
    }
    @Override
    public void deactivate() {
        if (tempCenterCircle != null) mapZoomGroup.getChildren().remove(tempCenterCircle);
        if (previewRadiusLine != null) mapZoomGroup.getChildren().remove(previewRadiusLine); 
        if (radiusValueText != null) mapZoomGroup.getChildren().remove(radiusValueText);     
        if (previewArc != null) mapZoomGroup.getChildren().remove(previewArc);

        tempCenterCircle = null;
        previewRadiusLine = null;
        radiusValueText = null;
        previewArc = null;
        centerPoint = null;
        definingRadius = false;
        definingAngle = false;
        stateManager.setArcStart(null);
    }

   @Override
    public void onMousePressed(MouseEvent event, Point2D mapCoords) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (centerPoint == null) {
                centerPoint = mapCoords;
                tempCenterCircle = new Circle(centerPoint.getX(), centerPoint.getY(), 3, Color.ORANGE);
                mapZoomGroup.getChildren().add(tempCenterCircle);
                Double predefinedRadius = menuManager.getArcRadius();
                if (predefinedRadius != null && predefinedRadius > 0) {
                    currentRadius = predefinedRadius;
                    definingAngle = true;
                    previewArc = new Arc(centerPoint.getX(), centerPoint.getY(), currentRadius, currentRadius, 0, 0);
                    previewArc.setType(menuManager.getArcType());
                    previewArc.setFill(Color.TRANSPARENT);
                    previewArc.setStroke(menuManager.getColorPickerValue());
                    previewArc.setStrokeWidth(menuManager.getLineThickness());
                    mapZoomGroup.getChildren().add(previewArc);
                    previewRadiusLine = new Line(centerPoint.getX(), centerPoint.getY(), mapCoords.getX(), mapCoords.getY());
                    previewRadiusLine.setStroke(menuManager.getColorPickerValue());
                    previewRadiusLine.setStrokeWidth(menuManager.getLineThickness());
                    previewRadiusLine.getStrokeDashArray().setAll(getScaledDashPattern(BASE_DASH_PATTERN_DASHED, menuManager.getLineThickness())); // O DOTTED
                    mapZoomGroup.getChildren().add(previewRadiusLine);

                    radiusValueText = new Text(mapCoords.getX(), mapCoords.getY(), String.format("%.1f px", currentRadius));
                    radiusValueText.setFont(new javafx.scene.text.Font(6 + menuManager.getLineThickness() * 0.8));
                    radiusValueText.setFill(menuManager.getColorPickerValue());
                    mapZoomGroup.getChildren().add(radiusValueText);

                    event.consume();
                } else {
                    definingRadius = true;
                    previewRadiusLine = new Line(centerPoint.getX(), centerPoint.getY(), mapCoords.getX(), mapCoords.getY());
                    previewRadiusLine.setStroke(menuManager.getColorPickerValue());
                    previewRadiusLine.setStrokeWidth(menuManager.getLineThickness());
                    previewRadiusLine.getStrokeDashArray().setAll(getScaledDashPattern(BASE_DASH_PATTERN_DASHED, menuManager.getLineThickness())); // O DOTTED
                    mapZoomGroup.getChildren().add(previewRadiusLine);

                    radiusValueText = new Text(mapCoords.getX(), mapCoords.getY(), "0.0");
                    radiusValueText.setFont(new javafx.scene.text.Font(6 + menuManager.getLineThickness() * 0.8));
                    radiusValueText.setFill(menuManager.getColorPickerValue());
                    mapZoomGroup.getChildren().add(radiusValueText);

                    event.consume();
                }
            } else if (definingAngle) {
                event.consume();
            }
        }
    }
    @Override
    public void onMouseDragged(MouseEvent event, Point2D mapCoords) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (definingRadius) {
                double proposedRadius = centerPoint.distance(mapCoords);
                previewRadiusLine.setEndX(mapCoords.getX());
                previewRadiusLine.setEndY(mapCoords.getY());
                currentRadius = proposedRadius;
                radiusValueText.setText(String.format("%.1f px", currentRadius));
                radiusValueText.setX(mapCoords.getX() + 5);
                radiusValueText.setY(mapCoords.getY() - 5);
                radiusValueText.setFont(new javafx.scene.text.Font(4 * menuManager.getLineThickness()));
                radiusValueText.setFill(menuManager.getColorPickerValue());
                event.consume();
            } 
            else if (definingAngle) {
    Bounds zoomBounds = mapZoomGroup.getBoundsInLocal();

    double minX = zoomBounds.getMinX() + 20;
    double maxX = zoomBounds.getMaxX() - 20;
    double minY = zoomBounds.getMinY() + 20;
    double maxY = zoomBounds.getMaxY() - 20;

    double mouseX = mapCoords.getX();
    double mouseY = mapCoords.getY();

    if (mouseX < minX || mouseX > maxX || mouseY < minY || mouseY > maxY) {
        return;
    }

    double currentMouseAngle = calculateAngle(centerPoint, mapCoords);
    double initialArcStartAngle = calculateAngle(centerPoint, stateManager.getArcStart());
    double sweep = currentMouseAngle - initialArcStartAngle;
    if (sweep > 180) sweep -= 360;
    else if (sweep < -180) sweep += 360;

    previewArc.setStartAngle((initialArcStartAngle + 180) % 360);
    previewArc.setLength(sweep);
    previewArc.setStroke(menuManager.getColorPickerValue());
    previewArc.setStrokeWidth(menuManager.getLineThickness());
    event.consume();
}
        }
    }
    @Override
    public void onMouseReleased(MouseEvent event, Point2D mapCoords) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (definingRadius) {
                definingRadius = false;
                definingAngle = true;
                if (previewArc == null) {
                    previewArc = new Arc(centerPoint.getX(), centerPoint.getY(), currentRadius, currentRadius, 0, 0);
                    previewArc.setType(menuManager.getArcType());
                    previewArc.setFill(Color.TRANSPARENT);
                    previewArc.setStroke(menuManager.getColorPickerValue());
                    previewArc.setStrokeWidth(menuManager.getLineThickness());
                    mapZoomGroup.getChildren().add(previewArc);
                }
                stateManager.setArcStart(mapCoords);
                previewArc.setStartAngle(calculateAngle(centerPoint, mapCoords));
                previewArc.setLength(0); 
                event.consume();
            } else if (definingAngle) {
                Arc finalArc = new Arc(
                    centerPoint.getX(), centerPoint.getY(),
                    currentRadius, currentRadius,
                    previewArc.getStartAngle(),
                    previewArc.getLength()
                );
                finalArc.setType(menuManager.getArcType());
                finalArc.setFill(Color.TRANSPARENT);
                finalArc.setStroke(menuManager.getColorPickerValue());
                finalArc.setStrokeWidth(menuManager.getLineThickness());
                mapZoomGroup.getChildren().add(finalArc);

                mapZoomGroup.getChildren().remove(tempCenterCircle);
                mapZoomGroup.getChildren().remove(previewArc);
                mapZoomGroup.getChildren().remove(previewRadiusLine);
                mapZoomGroup.getChildren().remove(radiusValueText);

                tempCenterCircle = null;
                previewArc = null;
                previewRadiusLine = null;
                radiusValueText = null;
                centerPoint = null;
                definingAngle = false;
                stateManager.setArcStart(null);
                event.consume();
            }
        }
    }
    @Override
   public void onMouseClick(MouseEvent event, Point2D mapCoords) {}
   private double calculateAngle(Point2D center, Point2D p) {
    double angle = Math.toDegrees(Math.atan2(-center.getY()+ p.getY(), center.getX() - p.getX() ));
    return angle;
}
    private Double[] getScaledDashPattern(Double[] basePattern, double scaleFactor) {
        if (basePattern == null) {
            return null;
        }
        Double[] scaledPattern = new Double[basePattern.length];
        for (int i = 0; i < basePattern.length; i++) {
            scaledPattern[i] = basePattern[i] * scaleFactor;
        }
        return scaledPattern;
    }
}