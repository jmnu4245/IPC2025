package model; 

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;


public class MapStateManager {

    public enum Tool {SELECTION, HAND, MARKER, LINE, ARC, TEXT, DELETE, PROTRACTOR, DISTANCE, LATITUDE, NONE_SELECTED }

    private final ObjectProperty<Tool> currentTool = new SimpleObjectProperty<>(Tool.NONE_SELECTED);
    private boolean isProtractorSel;
    private boolean isRuleSel;
    private double zoom;
    // Para el paneo
    private Point2D lastMousePosition; 
    //Map limit
    private double maxX , maxY ,minX, minY;
    private Node selectedElement = null; // Elemento Seleccionado
    private Node selectionHalo = null; // Halo para el elemento seleccionado

    // Tool-specific temporary states
    private Point2D lineStart;
    private Point2D arcStart;
    private Point2D distanceStart;
    

    public ObjectProperty<Tool> currentToolProperty() {
        return currentTool;
    }
    public Tool getCurrentTool() {
        return currentTool.get();
    }
    public void setCurrentTool(Tool tool) {
        this.currentTool.set(tool);
    }
    public void setCurrentToolNone(){
        this.currentTool.set(Tool.NONE_SELECTED);
    }
    public void setisProtractorSel(Boolean isProtractorSel){ this.isProtractorSel= isProtractorSel;}
    public boolean getisProtractorSel(){return isProtractorSel ;}
     public void setisRuleSel(Boolean isRuleSel){ this.isRuleSel= isRuleSel;}
    public boolean getisRuleSel(){return isRuleSel ;}
    public Point2D getLastMousePosition() {
        return lastMousePosition;
    }

    public void setLastMousePosition(Point2D lastMousePosition) {
        this.lastMousePosition = lastMousePosition;
    }

    public Node getSelectedElement() {
        return selectedElement;
    }

    public void setSelectedElement(Node selectedElement) {
        this.selectedElement = selectedElement;
    }

    public Node getSelectionHalo() {
        return selectionHalo;
    }

    public void setSelectionHalo(Node selectionHalo) {
        this.selectionHalo = selectionHalo;
    }

    public Point2D getLineStart() {
        return lineStart;
    }

    public void setLineStart(Point2D lineStart) {
        this.lineStart = lineStart;
    }

    public Point2D getArcStart() {
        return arcStart;
    }

    public void setArcStart(Point2D arcStart) {
        this.arcStart = arcStart;
    }

    public Point2D getDistanceStart() {
        return distanceStart;
    }

    public void setDistanceStart(Point2D distanceStart) {
        this.distanceStart = distanceStart;
    }

    public void resetDrawingStates() {
        setLineStart(null);
        setArcStart(null);
        setDistanceStart(null);
    }
    public double getMaxX() {
    return maxX;
}

public void setMaxX(double maxX) {
    this.maxX = maxX;
}

public double getMaxY() {
    return maxY;
}

public void setMaxY(double maxY) {
    this.maxY = maxY;
}

public double getMinX() {
    return minX;
}

public void setMinX(double minX) {
    this.minX = minX;
}

public double getMinY() {
    return minY;
}

public void setMinY(double minY) {
    this.minY = minY;
}
public void setMinZoom(double zoom){this.zoom = zoom;}
public double getMinZoom(){return zoom;}



}