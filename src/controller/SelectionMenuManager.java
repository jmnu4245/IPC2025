package controller;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import drawmodel.MapStateManager;
import service.SelectionManager;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.converter.NumberStringConverter;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ArcType;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.Rectangle; 
import javafx.scene.shape.Polygon;   


import java.util.stream.Collectors;

public class SelectionMenuManager {

private Consumer<Double> distanceThicknessSliderListener;

    
    private VBox selectionMenu;
    private ColorPicker sharedColorPicker;
    private MapStateManager stateManager; 

    
    private Slider lineThicknessSliderDynamic;
    private TextField lineThicknessInputFieldDynamic;

    private ComboBox<String> markerShapeComboBoxDynamic;
    private Slider markerSizeSliderDynamic;
    private TextField markerSizeInputFieldDynamic;

    private TextField textInputDynamic; 
    private ComboBox<String> fontFamilyComboBoxDynamic;
    private Slider fontSizeSliderDynamic;
    private TextField fontSizeInputFieldDynamic;

    private ToggleGroup arcTypeToggleGroupDynamic;
    private TextField arcRadiusInputDynamic;    
    
    private Slider distanceThicknessSliderDynamic;
    
    private SelectionManager selectionManager;
    
    private final Group mapZoomGroup;
    public SelectionMenuManager(MapStateManager stateManager, VBox selectionMenu, Group mapZoomGroup, SelectionManager selectionManager) {
        this.stateManager = stateManager;
        this.selectionMenu = selectionMenu;
        this.mapZoomGroup = mapZoomGroup;
        this.selectionManager = selectionManager;

        this.sharedColorPicker = new ColorPicker();
        this.sharedColorPicker.setVisible(false);
        this.sharedColorPicker.setManaged(false);
        
        selectionMenu.setSpacing(10);
    }

    public void updateMenuForTool() {
        selectionMenu.getChildren().clear();
        resetDynamicControls();

        switch (stateManager.getCurrentTool()) {
            case MARKER -> {
                sharedColorPicker.setValue(Color.BLUE);
                sharedColorPicker.setVisible(true);
                sharedColorPicker.setManaged(true);

                markerShapeComboBoxDynamic = new ComboBox<>();
                markerShapeComboBoxDynamic.getItems().addAll("Círculo", "Cuadrado", "Triángulo");
                markerShapeComboBoxDynamic.setValue("Círculo");

                markerSizeSliderDynamic = new Slider(5, 50, 10);
                markerSizeInputFieldDynamic = new TextField();
                markerSizeInputFieldDynamic.setPrefColumnCount(4);

                markerSizeInputFieldDynamic.textProperty().bindBidirectional(
                    markerSizeSliderDynamic.valueProperty(),
                    new NumberStringConverter("0.0")
                );
                markerSizeInputFieldDynamic.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        try {
                            double value = Double.parseDouble(markerSizeInputFieldDynamic.getText());
                            if (value < markerSizeSliderDynamic.getMin()) {
                                markerSizeSliderDynamic.setValue(markerSizeSliderDynamic.getMin());
                            } else if (value > markerSizeSliderDynamic.getMax()) {
                                markerSizeSliderDynamic.setValue(markerSizeSliderDynamic.getMax());
                            }
                        } catch (NumberFormatException e) {
                            markerSizeInputFieldDynamic.setText(String.format("0.0", markerSizeSliderDynamic.getValue()));
                        }
                    }
                });
                HBox markerhbox = new HBox(5, markerSizeSliderDynamic, markerSizeInputFieldDynamic);
                
                markerhbox.setAlignment(Pos.CENTER);
                selectionMenu.getChildren().addAll(
                    new Label("Color de relleno:"),
                    sharedColorPicker,
                    new Label("Forma del marcador:"),
                    markerShapeComboBoxDynamic,
                    new Label("Tamaño del marcador:"),
                    markerhbox
                );
            }
           case LINE -> {
                sharedColorPicker.setValue(Color.BLUE);
                sharedColorPicker.setVisible(true);
                sharedColorPicker.setManaged(true);

                lineThicknessSliderDynamic = new Slider(1, 20, 2);
                lineThicknessInputFieldDynamic = new TextField(String.format("%.1f", lineThicknessSliderDynamic.getValue()));
                lineThicknessInputFieldDynamic.setPrefColumnCount(4);

                lineThicknessInputFieldDynamic.textProperty().bindBidirectional(
                    lineThicknessSliderDynamic.valueProperty(),
                    new NumberStringConverter("0.0")
                );
                lineThicknessInputFieldDynamic.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        try {
                            double value = Double.parseDouble(lineThicknessInputFieldDynamic.getText());
                            if (value < lineThicknessSliderDynamic.getMin()) {
                                lineThicknessSliderDynamic.setValue(lineThicknessSliderDynamic.getMin());
                            } else if (value > lineThicknessSliderDynamic.getMax()) {
                                lineThicknessSliderDynamic.setValue(lineThicknessSliderDynamic.getMax());
                            }
                        } catch (NumberFormatException e) {
                            lineThicknessInputFieldDynamic.setText(String.format("0.0", lineThicknessSliderDynamic.getValue()));
                        }
                    }
                });
                HBox lineaHbox = new HBox(5, lineThicknessSliderDynamic, lineThicknessInputFieldDynamic);
                lineaHbox.setAlignment(Pos.CENTER);
                selectionMenu.getChildren().addAll(
                    new Label("Color de línea:"),
                    sharedColorPicker,
                    new Label("Grosor:"),
                    lineaHbox
                );
            }
            case ARC -> {
                sharedColorPicker.setValue(Color.BLUE);
                sharedColorPicker.setVisible(true);
                sharedColorPicker.setManaged(true);

                RadioButton open = new RadioButton("Abierto");
                RadioButton chord = new RadioButton("Cuerda");
                RadioButton round = new RadioButton("Redondeado");
                arcTypeToggleGroupDynamic = new ToggleGroup();
                open.setToggleGroup(arcTypeToggleGroupDynamic);
                chord.setToggleGroup(arcTypeToggleGroupDynamic);
                round.setToggleGroup(arcTypeToggleGroupDynamic);
                open.setSelected(true);

                arcRadiusInputDynamic = new TextField();
                arcRadiusInputDynamic.setPromptText("Radio (opcional)");
                arcRadiusInputDynamic.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("\\d*(\\.\\d*)?")) {
                        arcRadiusInputDynamic.setText(oldVal);
                    }
                });

                lineThicknessSliderDynamic = new Slider(1, 20, 2);
                lineThicknessInputFieldDynamic = new TextField(String.format("%.1f", lineThicknessSliderDynamic.getValue()));
                lineThicknessInputFieldDynamic.setPrefColumnCount(4);

                lineThicknessInputFieldDynamic.textProperty().bindBidirectional(
                    lineThicknessSliderDynamic.valueProperty(),
                    new NumberStringConverter("0.0")
                );
                lineThicknessInputFieldDynamic.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        try {
                            double value = Double.parseDouble(lineThicknessInputFieldDynamic.getText());
                            if (value < lineThicknessSliderDynamic.getMin()) {
                                lineThicknessSliderDynamic.setValue(lineThicknessSliderDynamic.getMin());
                            } else if (value > lineThicknessSliderDynamic.getMax()) {
                                lineThicknessSliderDynamic.setValue(lineThicknessSliderDynamic.getMax());
                            }
                        } catch (NumberFormatException e) {
                            lineThicknessInputFieldDynamic.setText(String.format("0.0", lineThicknessSliderDynamic.getValue()));
                        }
                    }
                });
                HBox ArcoHbox = new HBox(5, lineThicknessSliderDynamic, lineThicknessInputFieldDynamic);
                ArcoHbox.setAlignment(Pos.CENTER);
                selectionMenu.getChildren().addAll(
                    new Label("Color del arco:"),
                    sharedColorPicker,
                    new Label("Grosor:"),
                    ArcoHbox,
                    new Label("Radio (px, opcional):"),
                    arcRadiusInputDynamic
                );
            }
             case TEXT -> {
                sharedColorPicker.setValue(Color.BLACK);
                sharedColorPicker.setVisible(true);
                sharedColorPicker.setManaged(true);
                sharedColorPicker.setOnAction(null);

                
                fontFamilyComboBoxDynamic = new ComboBox<>();
                fontFamilyComboBoxDynamic.getItems().addAll(javafx.scene.text.Font.getFamilies());
                fontFamilyComboBoxDynamic.setValue("System");
                fontFamilyComboBoxDynamic.setPrefWidth(150);

                fontSizeSliderDynamic = new Slider(8, 72, 12);
                fontSizeSliderDynamic.setBlockIncrement(1);
                fontSizeInputFieldDynamic = new TextField();
                fontSizeInputFieldDynamic.setPrefColumnCount(4);

                fontSizeInputFieldDynamic.textProperty().bindBidirectional(
                    fontSizeSliderDynamic.valueProperty(),
                    new NumberStringConverter("0.0")
                );
                fontSizeInputFieldDynamic.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        try {
                            double value = Double.parseDouble(fontSizeInputFieldDynamic.getText());
                            if (value < fontSizeSliderDynamic.getMin()) {
                                fontSizeSliderDynamic.setValue(fontSizeSliderDynamic.getMin());
                            } else if (value > fontSizeSliderDynamic.getMax()) {
                                fontSizeSliderDynamic.setValue(fontSizeSliderDynamic.getMax());
                            }
                        } catch (NumberFormatException e) {
                            fontSizeInputFieldDynamic.setText(String.format("%.1f", fontSizeSliderDynamic.getValue()));
                        }
                    }
                });

                HBox fontSizeControl = new HBox(5,fontSizeSliderDynamic, fontSizeInputFieldDynamic);
                fontSizeControl.setAlignment(Pos.CENTER);

                selectionMenu.getChildren().addAll(                   
                    new Label("Color:"),
                    sharedColorPicker,
                    new Label("Fuente:"),
                    fontFamilyComboBoxDynamic,
                     new Label("Tamaño:"),
                    fontSizeControl
                );
            }
            case DELETE -> {mapZoomGroup.getChildren().removeIf(n -> !(n instanceof ImageView));
            }                       
            case LATITUDE -> {
                selectionMenu.getChildren().add(new Label("Latitud/Longitud se muestra \n automáticamente al hacer clic"));
            }
            case SELECTION, HAND-> {
                selectionMenu.getChildren().add(new Label("Selecciona una herramientan \n para ver opciones."));
            }
            case NONE_SELECTED -> {
               selectionMenu.getChildren().add(new Label("Selecciona una herramienta \n para ver opciones."));
            }
        }
        selectionMenu.setVisible(true);
        selectionMenu.setManaged(true);
    }

    public void showOptionsForSelectedNode() {
    selectionMenu.getChildren().clear();
    resetDynamicControls();
    sharedColorPicker.setVisible(false);
    sharedColorPicker.setManaged(false);
    sharedColorPicker.setOnAction(null);

    Button deleteButton = new Button("Eliminar");
    deleteButton.setOnAction(e -> {
       selectionManager.removeSelectedElement();
       selectionMenu.getChildren().clear();
    });
    Node SelectedNode =selectionManager.getSelectedElement();
    switch (SelectedNode) {
        case Circle circle -> {
            System.out.println("Círculo (marcador) seleccionado");
            sharedColorPicker.setValue((Color) circle.getFill());
            sharedColorPicker.setOnAction(e -> circle.setFill(sharedColorPicker.getValue()));
            sharedColorPicker.setVisible(true);
            sharedColorPicker.setManaged(true);

            selectionMenu.getChildren().addAll(
                
                sharedColorPicker,
                
                deleteButton
            );
        }
        case Rectangle rect -> {
            System.out.println("Cuadrado (marcador) seleccionado");
            sharedColorPicker.setValue((Color) rect.getFill());
            sharedColorPicker.setOnAction(e -> rect.setFill(sharedColorPicker.getValue()));
            sharedColorPicker.setVisible(true);
            sharedColorPicker.setManaged(true);

            selectionMenu.getChildren().addAll(
                new Label("Color de relleno:"),
                sharedColorPicker,
                deleteButton
            );
        }
        case Polygon poly -> {
            System.out.println("Triángulo (marcador) seleccionado");
            sharedColorPicker.setValue((Color) poly.getFill());
            sharedColorPicker.setOnAction(e -> poly.setFill(sharedColorPicker.getValue()));
            sharedColorPicker.setVisible(true);
            sharedColorPicker.setManaged(true);

            selectionMenu.getChildren().addAll(
                new Label("Color de relleno:"),
                sharedColorPicker,
                deleteButton
            );
        }
        case Line line -> {
        System.out.println("Línea seleccionada");
        sharedColorPicker.setValue((Color) line.getStroke());
        sharedColorPicker.setOnAction(e -> line.setStroke(sharedColorPicker.getValue()));
        sharedColorPicker.setVisible(true);
        sharedColorPicker.setManaged(true);
        selectionMenu.getChildren().addAll(
        new Label("Color de línea:"),
        sharedColorPicker,
        deleteButton
    );
}

        case Text text -> {
            System.out.println("Texto seleccionado");
            sharedColorPicker.setValue((Color) text.getFill());
            sharedColorPicker.setOnAction(e -> text.setFill(sharedColorPicker.getValue()));
            sharedColorPicker.setVisible(true);
            sharedColorPicker.setManaged(true);

            selectionMenu.getChildren().addAll(
                new Label("Color del texto:"),
                sharedColorPicker,
                deleteButton
            );
        }
        case Arc arc -> {
            System.out.println("Arco seleccionado");
            sharedColorPicker.setValue((Color) arc.getStroke());
            sharedColorPicker.setOnAction(e -> arc.setStroke(sharedColorPicker.getValue()));
            sharedColorPicker.setVisible(true);
            sharedColorPicker.setManaged(true);

            selectionMenu.getChildren().addAll(
                new Label("Color del arco:"),
                sharedColorPicker,
                deleteButton
            );
        }
        default -> {
            System.out.println("Objeto no identificable o no editable.");
            selectionMenu.getChildren().add(new Label("Selecciona un elemento dibujado para editarlo."));
        }
    }
    selectionMenu.setVisible(true);
    selectionMenu.setManaged(true);
}

    public Color getColorPickerValue() {
        return sharedColorPicker != null ? sharedColorPicker.getValue() : Color.BLUE;
    }

    public double getLineThickness() { return lineThicknessSliderDynamic != null ? lineThicknessSliderDynamic.getValue() : 2.0; }

    public ArcType getArcType() {
        if (arcTypeToggleGroupDynamic != null && arcTypeToggleGroupDynamic.getSelectedToggle() != null) {
            RadioButton selected = (RadioButton) arcTypeToggleGroupDynamic.getSelectedToggle();
            if ("Abierto".equals(selected.getText())) return ArcType.OPEN;
            if ("Cuerda".equals(selected.getText())) return ArcType.CHORD;
            if ("Redondeado".equals(selected.getText())) return ArcType.ROUND;
        }
        return ArcType.OPEN;
    }
    public Double getArcRadius() {
        if (arcRadiusInputDynamic != null && !arcRadiusInputDynamic.getText().isEmpty()) {
            try {
                return Double.parseDouble(arcRadiusInputDynamic.getText());
            } catch (NumberFormatException e) {
                System.out.println("nonumero");
                return null;
            }
        }
        return null;
    }   
    public String getMarkerShape() {
        return markerShapeComboBoxDynamic != null ? markerShapeComboBoxDynamic.getValue() : "Círculo";
    }
    public double getMarkerSize() {
        return markerSizeSliderDynamic != null ? markerSizeSliderDynamic.getValue() : 10.0;
    }
    public String getFontFamily() {
        return fontFamilyComboBoxDynamic != null && fontFamilyComboBoxDynamic.getValue() != null
               ? fontFamilyComboBoxDynamic.getValue() : "System";
    }
    public double getFontSize() {
        return fontSizeSliderDynamic != null ? fontSizeSliderDynamic.getValue() : 12.0;
    }
    public void clearTextInputField() {
        if (textInputDynamic != null) {
            textInputDynamic.setText("");
        }
    }
    public String getTextInputValue() {
    return textInputDynamic != null ? textInputDynamic.getText() : "";
}
    public void clearMenu(){
    selectionMenu.getChildren().clear();
    }
    public double getDistanceThicknessSliderDynamic(){
    return distanceThicknessSliderDynamic.getValue();
    }
    private void resetDynamicControls() {
        lineThicknessSliderDynamic = null;
        arcTypeToggleGroupDynamic = null;
        textInputDynamic = null;
        markerShapeComboBoxDynamic = null;
        textInputDynamic = null;
        fontFamilyComboBoxDynamic = null;
        fontSizeSliderDynamic = null;
        fontSizeInputFieldDynamic = null;
        markerSizeSliderDynamic = null;
        arcRadiusInputDynamic = null;
        distanceThicknessSliderDynamic=null;
    }
}
