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

    private Point2D centerPoint;        // El centro del arco
    private Circle tempCenterCircle;    // Para visualizar el centro

    private Line previewRadiusLine;     // Para mostrar la flecha del radio
    private Text radiusValueText;       // Para mostrar el valor del radio
    private Arc previewArc;             // Para mostrar el arco que se está dibujando

    private boolean definingRadius = false; // Estado: ¿estamos definiendo el radio?
    private boolean definingAngle = false;  // Estado: ¿estamos definiendo el ángulo?

    private static final Double[] BASE_DASH_PATTERN_DOTTED = {1.0, 3.0}; // Puntos
    private static final Double[] BASE_DASH_PATTERN_DASHED = {5.0, 5.0}; // Rayas
    // Variable para almacenar el radio final (definido o introducido)
    private double currentRadius = 0;

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
        stateManager.setArcStart(null); // Reset del punto de inicio del arco

        // Limpiar todos los elementos de previsualización
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
    }
    @Override
    public void deactivate() {
        if (tempCenterCircle != null) mapZoomGroup.getChildren().remove(tempCenterCircle);
        if (previewRadiusLine != null) mapZoomGroup.getChildren().remove(previewRadiusLine); // Limpiar aquí
        if (radiusValueText != null) mapZoomGroup.getChildren().remove(radiusValueText);     // Limpiar aquí
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
    public void onMouseClick(MouseEvent event, Point2D mapCoords) {}
@Override
public void onMouseDragged(MouseEvent event, Point2D mapCoords) {
    if (event.getButton() == MouseButton.PRIMARY) {
        // En la fase de definición del radio, no hay restricción de salida del viewport.
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

            System.out.println("--- Definición de Radio (Drag) ---");
            System.out.println("  Radio propuesto: " + proposedRadius);
            System.out.println("  Centro: " + centerPoint);
            System.out.println("  Coordenadas del ratón: " + mapCoords);

            event.consume();
        } 
        // En la fase de definición del ángulo, sí aplicamos la restricción del viewport.
        else if (definingAngle) {
            double currentMouseAngle = calculateAngle(centerPoint, mapCoords);
            double initialArcStartAngle = calculateAngle(centerPoint, stateManager.getArcStart());

            double sweep = currentMouseAngle - initialArcStartAngle;
            if (sweep > 180) sweep -= 360;
            else if (sweep < -180) sweep += 360;

            // Para la comprobación de límites, usamos el radio actual
            double radius = previewArc.getRadiusX(); 
            double minX = centerPoint.getX() - radius;
            double maxX = centerPoint.getX() + radius;
            double minY = centerPoint.getY() - radius;
            double maxY = centerPoint.getY() + radius;

            // Obtener los límites visibles del ScrollPane en coordenadas de contenido
            Bounds viewportBounds = mapScrollPane.getViewportBounds();
            double contentScale = mapZoomGroup.getScaleX();

            double visibleMinX_content = mapScrollPane.getHvalue() * (mapZoomGroup.getBoundsInLocal().getWidth() - viewportBounds.getWidth() / contentScale);
            double visibleMaxX_content = visibleMinX_content + viewportBounds.getWidth() / contentScale;
            double visibleMinY_content = mapScrollPane.getVvalue() * (mapZoomGroup.getBoundsInLocal().getHeight() - viewportBounds.getHeight() / contentScale);
            double visibleMaxY_content = visibleMinY_content + viewportBounds.getHeight() / contentScale;

            System.out.println("--- Definición de Ángulo (Drag) ---");
            System.out.println("  Valores actuales del arco (minX, maxX, minY, maxY): " + minX + ", " + maxX + ", " + minY + ", " + maxY);
            System.out.println("  Límites VISIBLES del ScrollPane en coordenadas de contenido (minX, maxX, minY, maxY): " + visibleMinX_content + ", " + visibleMaxX_content + ", " + visibleMinY_content + ", " + visibleMaxY_content);
            System.out.println("  Ángulo actual del ratón: " + currentMouseAngle);
            System.out.println("  Ángulo inicial del arco: " + initialArcStartAngle);
            System.out.println("  Barrido del ángulo: " + sweep);

            // Comprobación de límites: Si el arco se sale del viewport visible, se cancela la operación de arrastre.
            if (minX < visibleMinX_content || maxX > visibleMaxX_content || minY < visibleMinY_content || maxY > visibleMaxY_content) {
                System.out.println("❌ Ángulo cancelado: el arco se saldría del viewport");
                // No se actualizan las propiedades del arco, deteniendo visualmente el arrastre
                return; 
            }

            previewArc.setStartAngle((initialArcStartAngle + 180) % 360);
            previewArc.setLength(sweep);
            previewArc.setStroke(menuManager.getColorPickerValue());
            previewArc.setStrokeWidth(menuManager.getLineThickness());

            event.consume();
        }
    }
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

                    // También mostrar la línea de radio predefinido punteada/rayada
                    previewRadiusLine = new Line(centerPoint.getX(), centerPoint.getY(), mapCoords.getX(), mapCoords.getY());
                    previewRadiusLine.setStroke(menuManager.getColorPickerValue());
                    previewRadiusLine.setStrokeWidth(menuManager.getLineThickness());
                    // ¡APLICAR EL PATRÓN ESCALADO AQUÍ!
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
                    // ¡APLICAR EL PATRÓN ESCALADO AQUÍ!
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
    public void onMouseReleased(MouseEvent event, Point2D mapCoords) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (definingRadius) {
                // Fin de la definición del radio. No se quitan los elementos de previsualización todavía.
                // Solo se transiciona a la fase de definición de ángulo.
                definingRadius = false;
                definingAngle = true;

                // El previewArc ya se inicializó en onMousePressed si el radio era predefinido.
                // Si el radio fue arrastrado, se inicializa ahora.
                if (previewArc == null) { // Solo si no se creó antes
                    previewArc = new Arc(centerPoint.getX(), centerPoint.getY(), currentRadius, currentRadius, 0, 0);
                    previewArc.setType(menuManager.getArcType());
                    previewArc.setFill(Color.TRANSPARENT);
                    previewArc.setStroke(menuManager.getColorPickerValue());
                    previewArc.setStrokeWidth(menuManager.getLineThickness());
                    mapZoomGroup.getChildren().add(previewArc);
                }

                // Este es el punto de inicio para el cálculo del ángulo de barrido
                stateManager.setArcStart(mapCoords);
                previewArc.setStartAngle(calculateAngle(centerPoint, mapCoords));
                previewArc.setLength(0); // El barrido empieza en 0

                event.consume();
            } else if (definingAngle) {
                // Fin de la definición del ángulo y del arco completo.
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

                // Limpiar todo y resetear estados
                mapZoomGroup.getChildren().remove(tempCenterCircle);
                mapZoomGroup.getChildren().remove(previewArc);
                mapZoomGroup.getChildren().remove(previewRadiusLine); // Quitar la línea de radio
                mapZoomGroup.getChildren().remove(radiusValueText);  // Quitar el texto del radio

                tempCenterCircle = null;
                previewArc = null;
                previewRadiusLine = null; // Resetear la variable
                radiusValueText = null;  // Resetear la variable
                centerPoint = null;
                definingAngle = false;
                stateManager.setArcStart(null);

                event.consume();
            }
        }
    }
   private double calculateAngle(Point2D center, Point2D p) {
    // Invertir el signo de la diferencia en Y para ajustar al sistema de coordenadas invertido
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