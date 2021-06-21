package sample;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.css.Match;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.graphstream.ui.fx_viewer.FxViewPanel;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    @FXML private TextField TextField;
    @FXML private TextArea myTextArea;
    @FXML private TextArea myTextArea2;
    @FXML private TextArea myTextArea3;
    @FXML private GridPane GridPane;
    @FXML private VBox VBox;
    @FXML private AnchorPane anchorPane;
    FxViewPanel fxViewPanel;
    public File file;
    int id = 0;

    @FXML
    private void fileClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        file = fileChooser.showOpenDialog(TextField.getScene().getWindow());
    }

    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    @FXML
    private void initialize(){
//        Graph graph  = new MultiGraph("mg");
//        FxViewer viewer = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//        viewer.enableAutoLayout();
//        FxViewPanel v =  (FxViewPanel) viewer.addDefaultView( false );
//        v.setLayoutX(225);
//        v.setLayoutY(95);
//        v.setPrefSize(555, 500);
//        v.setId("fxViewPanel");
//        anchorPane.getChildren().add(v);
    }

    @FXML
    private void textFieldAction() {
        if (file != null) {
//            for(Node a: anchorPane.getChildren()){
//                if(Objects.equals(a.getId(), "fxViewPanel")){
//                    fxViewPanel = (FxViewPanel) a;
//                }
//            }
//            fxViewPanel.getViewer().getGraphicGraph().addNode("A");
//            fxViewPanel.getViewer().getGraphicGraph().addNode("B");
//            fxViewPanel.getViewer().getGraphicGraph().addNode("C");
//            fxViewPanel.getViewer().getGraphicGraph().addEdge("AB", "A", "B");
//            fxViewPanel.getViewer().getGraphicGraph().addEdge("BC", "B", "C");
//            fxViewPanel.getViewer().getGraphicGraph().addEdge("CA", "C", "A");
//            fxViewPanel.getViewer().getGraphicGraph().setAttribute( "ui.antialias" );
//            fxViewPanel.getViewer().getGraphicGraph().setAttribute( "ui.quality" );

//            fxViewPanel.getViewer().getGraphicGraph().getNode("A").setAttribute("xyz", -1, 0, 0 );
//            fxViewPanel.getViewer().getGraphicGraph().getNode("B").setAttribute("xyz",  1, 0, 0 );
//            fxViewPanel.getViewer().getGraphicGraph().getNode("C").setAttribute("xyz",  0, 1, 0 );



//            fxViewPanel.getViewer().getGraphicGraph().setAttribute("ui.stylesheet", """
//                node{
//                text-alignment: under;
//                text-color: white;
//                text-style: bold;
//                text-background-mode: rounded-box;
//                text-background-color: #222C;
//                text-padding: 5px, 4px;
//                text-offset: 0px, 5px;
//                size-mode: fit;
//                }
//                """);

//            fxViewPanel.getViewer().getGraphicGraph().getNode("A").setAttribute("ui.label", "A" );
//            fxViewPanel.getViewer().getGraphicGraph().getNode("B").setAttribute("ui.label",  "B" );
//            fxViewPanel.getViewer().getGraphicGraph().getNode("C").setAttribute("ui.label",  "C" );
            Platform.runLater(() -> {
                Arrays.asList(myTextArea, myTextArea2, myTextArea3).forEach(TextInputControl::clear);
                while(GridPane.getRowConstraints().size() > 0) GridPane.getRowConstraints().remove(0);
                while(GridPane.getColumnConstraints().size() > 0) GridPane.getColumnConstraints().remove(0);
                while(GridPane.getChildren().size() > 0) GridPane.getChildren().remove(0);
                RowConstraints row0 = new RowConstraints(10, 30, Double.MAX_VALUE);
                row0.setValignment(VPos.CENTER);
                row0.setVgrow(Priority.ALWAYS);
                GridPane.getRowConstraints().add(row0);
                Analizer newAnalizer = new Analizer(new String[]{String.valueOf(file), TextField.getText()});
                boolean flag = false;
                int reduceCount = 0;
                int finalReduceCount = 0;
                while (!flag) {
                    flag = newAnalizer.step(TextField.getText());
                    if(newAnalizer.state.operation.equals("Shift")){
                        Pattern pattern = Pattern.compile(newAnalizer.symbols.getSymbols());
                        Matcher matcher = pattern.matcher(newAnalizer.simvoli);
                        int count = 0;
                        while (matcher.find()) count++;
                        int finalCount = GridPane.getColumnCount() + 1;
                        /*
                        Добавляет колонку если поступающих
                        символов больше чем колонок
                        */
                        if (GridPane.getColumnCount() < finalCount) {
                            ColumnConstraints column = new ColumnConstraints();
                            column.setPercentWidth(50);
                            column.setHalignment(HPos.CENTER);
                            column.setHgrow(Priority.SOMETIMES);
                            GridPane.getColumnConstraints().add(column);
                        }
                        /*
                        Вставляет label в пустые ячейки GridPane
                        */
                        for (int i = 0; i < GridPane.getColumnCount(); i++) {
                            for (int j = 0; j < GridPane.getRowCount(); j++) {
                                if (getNodeByRowColumnIndex(j, i, GridPane) == null) {
                                    Label label = new Label("");
                                    GridPane.add(label, i, j);
                                    GridPane.setHalignment(label, HPos.CENTER);
                                }
                            }
                        }
                        matcher = pattern.matcher(newAnalizer.simvoli);
                        int i = reduceCount - 1;
                        int j = finalReduceCount - 1;
                        if(i < 0) i = 0;
                        if(j < 0) j = 0;
                        while(j != 0){
                            matcher.find();
                            j--;
                        }
                        while (matcher.find()) {
                            if(i == reduceCount - 1){
//                                var label = (Label) getNodeByRowColumnIndex(0, i - (reduceCount -1), GridPane);
//                                if(label.getText().equals("")) {
//                                    var k = reduceCount;
//                                    while(k > 0) {
//                                        var label1 = (Label)getNodeByRowColumnIndex(0, k, GridPane);
//                                        label1.setText("");
//                                        k--;
//                                    }
//                                    if(reduceCount == 0) GridPane.setColumnSpan(label, 1);
//                                    else {
//                                        GridPane.setColumnSpan(label, reduceCount);
//                                        GridPane.setHalignment(label, HPos.CENTER);
//                                    }
//                                    label.setText(newAnalizer.simvoli.substring(matcher.start(), matcher.end()));
//                                }
                                i++;
                            }else{
                                Label label = (Label) getNodeByRowColumnIndex(0, i, GridPane);
                                Label node = getNodeByRowColumnIndex(0, i, GridPane);
                                label.setScaleX(2);
                                label.setScaleY(2);
                                if(label.getText().equals("") && GridPane.getColumnCount() > 1)
                                    label.setText("—\t" + newAnalizer.simvoli.substring(matcher.start(), matcher.end()));
                                else if (label.getText().equals(""))
                                    label.setText("|\n" + newAnalizer.simvoli.substring(matcher.start(), matcher.end()));
                                i++;
                            }
                        }
                    }else{
//                        fxViewPanel.getViewer().getGraphicGraph().addNode(Integer.toString(id));
//                        fxViewPanel.getViewer().getGraphicGraph().getNode(Integer.toString(id)).setAttribute("ui.label", newAnalizer.simvoli );
                        RowConstraints row = new RowConstraints(10, 30, Double.MAX_VALUE);
                        row.setValignment(VPos.CENTER);
                        row.setVgrow(Priority.SOMETIMES);
                        GridPane.getRowConstraints().add(row);
                        Pattern pattern = Pattern.compile(newAnalizer.symbols.getSymbols());
                        Matcher matcher = pattern.matcher(newAnalizer.simvoli);
                        int count = 0;
                        while (matcher.find()) count++;
                        int finalCount = count;
                        finalReduceCount = finalCount;
                        if(newAnalizer.reduceCount > finalReduceCount)
                            reduceCount = newAnalizer.reduceCount;
                        else
                            reduceCount = finalReduceCount;

                        /*
                        Добавляет колонку если поступающих
                        символов больше чем колонок
                        */
                        if (GridPane.getColumnCount() < (finalCount -1)) {
                            ColumnConstraints column = new ColumnConstraints();
                            column.setPercentWidth(50);
                            column.setHalignment(HPos.CENTER);
                            column.setHgrow(Priority.SOMETIMES);
                            GridPane.getColumnConstraints().add(column);
                        }
                        /*
                        Вставляет label в пустые ячейки GridPane
                        */
                        for (int i = 0; i < GridPane.getColumnCount(); i++) {
                            for (int j = 0; j < GridPane.getRowCount(); j++) {
                                if (getNodeByRowColumnIndex(j, i, GridPane) == null) {
                                    Label label = new Label("");
                                    GridPane.add(label, i, j);
                                    GridPane.setHalignment(label, HPos.CENTER);
                                }
                            }
                        }
                        for(int k = 0; k < GridPane.getColumnCount(); k++){
                            for (int i = GridPane.getRowCount() - 1; i > 0; i--) {
                                Label label1 = (Label) getNodeByRowColumnIndex(i - 1, k, GridPane);
                                String label1Text = label1.getText();
                                Label label2 = (Label) getNodeByRowColumnIndex(i, k, GridPane);
                                String label2Text = label2.getText();
                                label1.setText(label2Text);
                                label2.setText(label1Text);
                            }
                        }
                        matcher = pattern.matcher(newAnalizer.simvoli);
                        int i = 0;
                        while (matcher.find()) {
                            if(i == (finalCount - 1) && newAnalizer.reduceCount != 0) {
                                Label label = (Label) getNodeByRowColumnIndex(0, GridPane.getColumnCount() - 1 - (newAnalizer.reduceCount - 1), GridPane);
//                                GridPane.setColumnSpan(label, newAnalizer.reduceCount);
                                label.setScaleX(2);
                                label.setScaleY(2);
                                label.setText(newAnalizer.simvoli.substring(matcher.start(), matcher.end()) + "\n|");
                            } else if(reduceCount == 0){
                                Label label = (Label) getNodeByRowColumnIndex(0, 0, GridPane);
//                                GridPane.setColumnSpan(label, GridPane.getColumnCount());
                                label.setScaleX(2);
                                label.setScaleY(2);
                                label.setText(newAnalizer.simvoli.substring(matcher.start(), matcher.end()));
                            }
                            i++;
                        }
                    }
                    myTextArea.setText(myTextArea.getText() + "\n" + newAnalizer.stack);
                    myTextArea2.setText(myTextArea2.getText() + "\n" + newAnalizer.simvoli);
                    myTextArea3.setText(myTextArea3.getText() + "\n" + newAnalizer.state.operation);
                }
            });
        }
    }
}
