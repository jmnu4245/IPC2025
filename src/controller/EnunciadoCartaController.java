package poiupv;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class EnunciadoCartaController implements Initializable {

    @FXML private ScrollPane map_scrollpane;
    @FXML private ImageView  mapImageView;
    @FXML private Button     zoomIn;
    @FXML private Button     zoomOut;
    @FXML private ToolBar    options_toolbar;

    private Group zoomGroup;

    private static final double MIN_SCALE  = 1.0;
    private static final double MAX_SCALE  = 5.0;
    private static final double ZOOM_DELTA = 1.1;
    private double scale = 1.0;

    private enum Tool { NONE, POINT, LINE, ARC, TEXT, DELETE }
    private Tool currentTool = Tool.NONE;

    // almacena primer clic para líneas y arcos
    private Point2D firstPoint;

    // configuración de dibujo
    private Color currentColor = Color.RED;
    private double currentWidth = 2.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        map_scrollpane.setPannable(true);
        zoomGroup = new Group(mapImageView);
        map_scrollpane.setContent(zoomGroup);
        map_scrollpane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);
        map_scrollpane.setOnMouseClicked(this::handleMouseClick);
    }

    private void handleScroll(ScrollEvent event) {
        if (event.getDeltaY() == 0) return;
        double factor = (event.getDeltaY() > 0) ? ZOOM_DELTA : 1/ZOOM_DELTA;
        Point2D contentMouse = zoomGroup.parentToLocal(
            map_scrollpane.sceneToLocal(event.getSceneX(), event.getSceneY()));
        zoom(factor, contentMouse);
        event.consume();
    }

    @FXML private void zoomIn()  { zoom(ZOOM_DELTA, getViewportCenter()); }
    @FXML private void zoomOut() { zoom(1/ZOOM_DELTA, getViewportCenter()); }

    @FXML private void puntoPressed()    { currentTool = Tool.POINT; }
    @FXML private void lineaPressed()    { currentTool = Tool.LINE; firstPoint = null; }
    @FXML private void arcoPressed()     { currentTool = Tool.ARC;  firstPoint = null; }
    @FXML private void textoPressed()    { currentTool = Tool.TEXT; }
    @FXML private void limpiarPressed()  { // borra todo menos la imagen
        zoomGroup.getChildren().removeIf(n -> n != mapImageView);
        currentTool = Tool.NONE;
    }
    @FXML private void transportadorPressed(){} // no implementado
    @FXML private void distanciapressed(){}    // no implementado
    @FXML private void latitudPressed(){}      // no implementado
    @FXML private void manoPressed() { currentTool = Tool.NONE; }

    private void handleMouseClick(MouseEvent event) {
        Point2D sceneP = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D localP = zoomGroup.parentToLocal(map_scrollpane.sceneToLocal(sceneP));
        switch (currentTool) {
            case POINT:
                Circle c = new Circle(localP.getX(), localP.getY(), currentWidth, currentColor);
                zoomGroup.getChildren().add(c);
                break;
            case LINE:
                if (firstPoint == null) {
                    firstPoint = localP;
                } else {
                    Line l = new Line(firstPoint.getX(), firstPoint.getY(), localP.getX(), localP.getY());
                    l.setStroke(currentColor);
                    l.setStrokeWidth(currentWidth);
                    zoomGroup.getChildren().add(l);
                    firstPoint = null;
                }
                break;
            case ARC:
                if (firstPoint == null) {
                    firstPoint = localP;
                } else {
                    double radius = firstPoint.distance(localP);
                    Arc arc = new Arc(firstPoint.getX(), firstPoint.getY(), radius, radius, 0, 90);
                    arc.setType(ArcType.OPEN);
                    arc.setStroke(currentColor);
                    arc.setStrokeWidth(currentWidth);
                    arc.setFill(Color.TRANSPARENT);
                    zoomGroup.getChildren().add(arc);
                    firstPoint = null;
                }
                break;
            case TEXT:
                Text t = new Text(localP.getX(), localP.getY(), "Texto");
                t.setFill(currentColor);
                t.setStyle("-fx-font-size: " + (currentWidth*5) + "px;");
                zoomGroup.getChildren().add(t);
                break;
            case DELETE:
                // elimina el elemento más cercano
                zoomGroup.getChildren().stream()
                    .filter(n -> n instanceof Circle || n instanceof Line || n instanceof Arc || n instanceof Text)
                    .min((a,b) -> Double.compare(
                        a.localToParent(a.getBoundsInLocal()).getCenterX() - localP.getX(),
                        b.localToParent(b.getBoundsInLocal()).getCenterX() - localP.getX()))
                    .ifPresent(n -> zoomGroup.getChildren().remove(n));
                break;
            default: break;
        }
    }

    private void zoom(double factor, Point2D pivot) {
        double old = scale;
        scale = clamp(scale*factor, MIN_SCALE, MAX_SCALE);
        factor = scale/old;
        Bounds b = zoomGroup.getLayoutBounds();
        double dx = pivot.getX()/b.getWidth(), dy = pivot.getY()/b.getHeight();
        zoomGroup.setScaleX(scale); zoomGroup.setScaleY(scale);
        map_scrollpane.setHvalue(clamp((map_scrollpane.getHvalue()+dx)*factor - dx,0,1));
        map_scrollpane.setVvalue(clamp((map_scrollpane.getVvalue()+dy)*factor - dy,0,1));
    }

    private Point2D getViewportCenter() {
        Bounds vp = map_scrollpane.getViewportBounds();
        return zoomGroup.parentToLocal(map_scrollpane.localToScene(vp.getWidth()/2, vp.getHeight()/2));
    }

    private static double clamp(double v,double min,double max){return v<min?min:Math.min(v,max);}
}
