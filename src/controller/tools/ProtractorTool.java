package controller.tools;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import drawmodel.MapDrawingTool;
import drawmodel.MapStateManager;
import controller.SelectionMenuManager;

public class ProtractorTool implements MapDrawingTool {

    private Group mapZoomGroup;
    private SelectionMenuManager menuManager;
    private ScrollPane mapScrollPane;

    private ImageView rulerImageView;
    private boolean isDraggingRuler = false;
    private boolean isRotatingRuler = false;
    private Point2D dragAnchor;
    private double initialLayoutX, initialLayoutY;
    private double initialAngle;
    private double initialRotateAnchorAngle;

    private Rectangle clip;

    @Override
    public void activate() {
        if (rulerImageView == null) {
            Image rulerImage = new Image(getClass().getResourceAsStream("/resources/transportador.png"));
            rulerImageView = new ImageView(rulerImage);
            rulerImageView.setPreserveRatio(true);
            rulerImageView.setFitWidth(2000);
            rulerImageView.setLayoutX(100);
            rulerImageView.setLayoutY(100);
            rulerImageView.setPickOnBounds(true);
            mapZoomGroup.getChildren().add(rulerImageView);
        } else {
            rulerImageView.setVisible(true);
        }

        double w = mapZoomGroup.getBoundsInLocal().getWidth();
        double h = mapZoomGroup.getBoundsInLocal().getHeight();
        if (clip == null) {
            clip = new Rectangle(10, 10, w, h);
            mapZoomGroup.setClip(clip);
        } else {
            clip.setWidth(w);
            clip.setHeight(h);
        }

        System.out.println("RulerTool activada. Regla visible: " + rulerImageView.isVisible());
    }

    @Override
    public void deactivate() {
        if (rulerImageView != null) {
            rulerImageView.setVisible(false);
        }
        isDraggingRuler = false;
        isRotatingRuler = false;
    }

    @Override
    public void onMouseClick(MouseEvent event, Point2D mapCoords) {
    }

    @Override
    public void onMousePressed(MouseEvent event, Point2D mapCoords) {
        if (rulerImageView != null && rulerImageView.isVisible()) {
            Point2D localCoords = rulerImageView.sceneToLocal(event.getSceneX(), event.getSceneY());
            if (rulerImageView.contains(localCoords)) {
                dragAnchor = mapCoords;
                
                if (event.getButton() == MouseButton.SECONDARY) {
                    isRotatingRuler = true;
                    // Ángulo inicial de la regla
                    initialAngle = rulerImageView.getRotate();
                    // Ángulo entre el centro de la regla y el mouse al iniciar
                    Point2D center = getRulerCenter();
                    initialRotateAnchorAngle = Math.toDegrees(Math.atan2(mapCoords.getY() - center.getY(), mapCoords.getX() - center.getX()));
                } else if (event.getButton() == MouseButton.PRIMARY) { // Botón izquierdo
                    isDraggingRuler = true;
                    initialLayoutX = rulerImageView.getLayoutX();
                    initialLayoutY = rulerImageView.getLayoutY();
                }
            }
        }
    }

    @Override
    public void onMouseDragged(MouseEvent event, Point2D mapCoords) {
        if (rulerImageView == null || !rulerImageView.isVisible()) return;

        if (isDraggingRuler) {
            double dx = mapCoords.getX() - dragAnchor.getX();
            double dy = mapCoords.getY() - dragAnchor.getY();

            double newX = initialLayoutX + dx;
            double newY = initialLayoutY + dy;

            double maxX = clip.getX() + clip.getWidth() - rulerImageView.getFitWidth();
            double maxY = clip.getY() + clip.getHeight() - rulerImageView.getFitHeight();
            double minX = clip.getX();
            double minY = clip.getY();

            rulerImageView.setLayoutX(Math.max(minX, Math.min(newX, maxX)));
            rulerImageView.setLayoutY(Math.max(minY, Math.min(newY, maxY)));
        } else if (isRotatingRuler) {
        Point2D center = getRulerCenter();
        double currentAngle = Math.toDegrees(Math.atan2(mapCoords.getY() - center.getY(), mapCoords.getX() - center.getX()));
        double angleDelta = currentAngle - initialRotateAnchorAngle;
        double newAngle = initialAngle + angleDelta;

        // Antes de aplicar la rotación, revisa si la regla se saldría del área visible
        if (isRotationWithinBounds(newAngle)) {
            rulerImageView.setRotate(newAngle);
        }
        // Si no, simplemente no aplica la rotación (o podrías limitarla al máximo permitido)
    }
    }

    @Override
    public void onMouseReleased(MouseEvent event, Point2D mapCoords) {
        isDraggingRuler = false;
        isRotatingRuler = false;
    }

    @Override
    public void setDependencies(Group mapZoomGroup,
                                SelectionMenuManager menuManager, ScrollPane mapScrollPane) {
        this.mapZoomGroup = mapZoomGroup;
        this.menuManager = menuManager;
        this.mapScrollPane = mapScrollPane;
    }

    private Point2D getRulerCenter() {
        double centerX = rulerImageView.getLayoutX() + rulerImageView.getFitWidth() / 2.0;
        double centerY = rulerImageView.getLayoutY() + rulerImageView.getFitHeight() / 2.0;
        return new Point2D(centerX, centerY);
    }
    private boolean isRotationWithinBounds(double angleDegrees) {
    double width = rulerImageView.getFitWidth();
    double height = rulerImageView.getFitHeight();
    double layoutX = rulerImageView.getLayoutX();
    double layoutY = rulerImageView.getLayoutY();

    // Centro de la regla
    double cx = layoutX + width / 2.0;
    double cy = layoutY + height / 2.0;

    // Esquinas antes de rotar (en coordenadas locales)
    Point2D[] corners = new Point2D[] {
        new Point2D(layoutX, layoutY),
        new Point2D(layoutX + width, layoutY),
        new Point2D(layoutX + width, layoutY + height),
        new Point2D(layoutX, layoutY + height)
    };

    // Aplica la rotación a cada esquina
    double angleRad = Math.toRadians(angleDegrees);
    double cos = Math.cos(angleRad);
    double sin = Math.sin(angleRad);

    for (Point2D corner : corners) {
        // Traslada la esquina para que el centro esté en (0,0)
        double tx = corner.getX() - cx;
        double ty = corner.getY() - cy;
        // Rota
        double rx = cx + (tx * cos - ty * sin);
        double ry = cy + (tx * sin + ty * cos);

        // Comprueba si está dentro del área del clip
        if (!clip.contains(rx, ry)) {
            return false;
        }
    }
    return true;
}
}
