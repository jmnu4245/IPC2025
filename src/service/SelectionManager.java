package service;

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
        if (element != null && !isMapBackground(element)) {
            selectedElement = element;
            createSelectionHalo(element);
        }
    }

    private void createSelectionHalo(Node element) {
    selectionHalo.getChildren().clear();
    javafx.geometry.Bounds bounds = element.getBoundsInParent();

    Shape halo;

    if (element instanceof javafx.scene.shape.Line) {
        javafx.scene.shape.Line line = (javafx.scene.shape.Line) element;
        // Crear una copia ligeramente más gruesa de la línea como halo
        javafx.scene.shape.Line lineHalo = new javafx.scene.shape.Line(
            line.getStartX(), line.getStartY(),
            line.getEndX(), line.getEndY()
        );
        lineHalo.setStroke(javafx.scene.paint.Color.DODGERBLUE);
        lineHalo.getStrokeDashArray().addAll(6.0, 6.0);
        lineHalo.setStrokeWidth(4);
        lineHalo.setFill(null);
        halo = lineHalo;
    } else {
        Circle circleHalo = new Circle();
        circleHalo.setRadius(Math.max(bounds.getWidth(), bounds.getHeight()) / 2 + 5);
        circleHalo.setCenterX(bounds.getMinX() + bounds.getWidth() / 2);
        circleHalo.setCenterY(bounds.getMinY() + bounds.getHeight() / 2);
        circleHalo.setStyle("-fx-stroke: dodgerblue; -fx-stroke-width: 2; -fx-stroke-dash-array: 6 6; -fx-fill: transparent;");
        halo = circleHalo;
    }

    selectionHalo.getChildren().add(halo);
}



    private boolean isMapBackground(Node element) {
        // Check if element is the map background image
        return element.getId() != null && element.getId().equals("mapImageView");
    }

    public void deselectCurrentElement() {
        if (selectedElement != null) {
            selectionHalo.getChildren().clear();
            selectedElement = null;
        }
    }
}
