package drawmodel; 
//Esta clase guarda que herramienta(s en caso de que se use regla o tranportador) se esta usando
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

}