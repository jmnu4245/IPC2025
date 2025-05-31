package service;

import utils.utils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import java.util.HashMap;
import java.util.Map;

public class SelectionManager {
    private final Group mapZoomGroup;
    private Node selectedElement;
    private Map<Node, Object[]> originalStyles = new HashMap<>();
    private Group selectionHalo;

    public SelectionManager(Group mapZoomGroup) {
        this.mapZoomGroup = mapZoomGroup;
        this.selectionHalo = new Group();
        this.mapZoomGroup.getChildren().add(this.selectionHalo);
    }

    public void selectElement(Node element) {
        // Remove previous selection halo if it exists
        if (selectedElement != null) {
            deselectCurrentElement();
        }

        // Apply new selection
        if (element != null && !utils.isMapBackground(element)) {
            selectedElement = element;
            createSelectionHalo(element);
        }
    }

    private void createSelectionHalo(Node element) {
    selectionHalo.getChildren().clear();

    if (!(element instanceof Shape)) {
        return; // Solo creamos halos para Shapes
    }

    switch (element) {
        case javafx.scene.shape.Line line -> {
            javafx.scene.shape.Line haloLine = new javafx.scene.shape.Line(
                line.getStartX(), line.getStartY(),
                line.getEndX(), line.getEndY()
            );
            haloLine.getTransforms().addAll(line.getTransforms());

            double originalStrokeWidth = line.getStrokeWidth();
            haloLine.setStroke(javafx.scene.paint.Color.YELLOW);
            haloLine.getStrokeDashArray().addAll(1.0, 6.0);
            haloLine.setStrokeWidth(Math.min(Math.max(originalStrokeWidth * 1.4, 5), originalStrokeWidth + 20));
            haloLine.setFill(null);
            haloLine.setMouseTransparent(true);

            selectionHalo.getChildren().add(haloLine);
        }

        case javafx.scene.shape.Arc arc -> {
            javafx.scene.shape.Arc haloArc = new javafx.scene.shape.Arc(
                arc.getCenterX(), arc.getCenterY(),
                arc.getRadiusX(), arc.getRadiusY(),
                arc.getStartAngle(), arc.getLength()
            );
            haloArc.setType(arc.getType());
            haloArc.getTransforms().addAll(arc.getTransforms());

            double originalStrokeWidth = arc.getStrokeWidth();
            haloArc.setStroke(javafx.scene.paint.Color.YELLOW);
            haloArc.getStrokeDashArray().addAll(1.0, 6.0);
            haloArc.setStrokeWidth(Math.min(Math.max(originalStrokeWidth * 1.4, 5), originalStrokeWidth + 20));
            haloArc.setFill(null);
            haloArc.setMouseTransparent(true);

            selectionHalo.getChildren().add(haloArc);
        }

        case javafx.scene.text.Text text -> {
            javafx.geometry.Bounds bounds = text.getBoundsInParent();

            javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(
                bounds.getMinX() - 4, bounds.getMinY() - 2,
                bounds.getWidth() + 8, bounds.getHeight() + 4
            );
            rect.setArcWidth(6);
            rect.setArcHeight(6);
            rect.setStroke(javafx.scene.paint.Color.YELLOW);
            rect.getStrokeDashArray().addAll(6.0, 6.0);
            rect.setStrokeWidth(2);
            rect.setFill(null);
            rect.setMouseTransparent(true);

            selectionHalo.getChildren().add(rect);
        }

        default -> {
            javafx.geometry.Bounds bounds = element.getBoundsInParent();

            Circle circleHalo = new Circle();
            circleHalo.setRadius(Math.max(bounds.getWidth(), bounds.getHeight()) / 2 + 5);
            circleHalo.setCenterX(bounds.getMinX() + bounds.getWidth() / 2);
            circleHalo.setCenterY(bounds.getMinY() + bounds.getHeight() / 2);
            circleHalo.setStyle("-fx-stroke: yellow; -fx-stroke-width: 2; -fx-stroke-dash-array: 6 6; -fx-fill: transparent;");
            circleHalo.setMouseTransparent(true);

            selectionHalo.getChildren().add(circleHalo);
        }
    }
}
    public void deselectCurrentElement() {
        if (selectedElement != null) {
            selectionHalo.getChildren().clear();
            selectedElement = null;
        }
        
    }
    public Node getSelectedElement() {
    return selectedElement;
}
    public void removeSelectedElement() {
        if (selectedElement != null) {
            // Elimina el elemento del grupo principal
            mapZoomGroup.getChildren().remove(selectedElement);
            // Limpia el halo de selección
            selectionHalo.getChildren().clear();
            // Limpia la referencia del elemento seleccionado
            selectedElement = null;
        }
    }
    
}
