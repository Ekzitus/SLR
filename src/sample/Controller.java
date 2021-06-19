package sample;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Controller {
    @FXML
    private TextField TextField;
    @FXML
    private TextArea myTextArea;
    @FXML
    private TextArea myTextArea2;
    @FXML
    private GridPane GridPane;
    public File file;

    @FXML
    private void fileClicked(){
        var fileChooser = new FileChooser();
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
    private void textFieldAction() {
        if(file != null) {
//            var task = new Task<>() {
//                @Override protected Boolean call() throws InterruptedException {
//                    var row0 = new RowConstraints(10, 30, Double.MAX_VALUE);
//                    row0.setVgrow(Priority.SOMETIMES);
//                    GridPane.getRowConstraints().add(row0);
//                    var newAnalizer = new Analizer(new String[]{String.valueOf(file), TextField.getText()});
//                    var flag = false;
//                    while (!flag) {
//                        if (isCancelled()) {
//                            break;
//                        }
//                        flag = newAnalizer.step(TextField.getText());
//                        var pattern = Pattern.compile(newAnalizer.symbols.getSymbols());
//                        var matcher = pattern.matcher(newAnalizer.simvoli);
//                        int count = 0;
//                        while(matcher.find()) count++;
//                        int finalCount = count;
//                        Platform.runLater(() -> {
//                            var row = new RowConstraints(10, 30, Double.MAX_VALUE);
//                            row.setVgrow(Priority.SOMETIMES);
//                            GridPane.getRowConstraints().add(row);
//                            if(GridPane.getColumnCount() < finalCount){
//                                var column = new ColumnConstraints();
//                                column.setPercentWidth(50);
//                                GridPane.getColumnConstraints().add(column);
//                            }
//                            for(int i = 0; i < GridPane.getColumnCount(); i++){
//                                for(int j = 0; j < GridPane.getRowCount(); j++){
//                                    if(getNodeByRowColumnIndex(j, i, GridPane) == null){
//                                        var label = new Label("");
//                                        GridPane.add(label, i, j);
//                                    }
//                                }
//                            }
//                            var CountSize = GridPane.getColumnCount();
//                            for(var j = 0; j < CountSize; j++){
//                                for(var i = GridPane.getRowCount() - 1; i > 0; i--){
//                                    Label label1 = (Label)GridPane.getChildren().get(CountSize*(i - 1) + j);
//                                    String label1Text = label1.getText();
//                                    Label label2 = (Label)GridPane.getChildren().get(CountSize*i + j);
//                                    String label2Text = label2.getText();
//                                    label1.setText(label2Text);
//                                    label2.setText(label1Text);
//                                }
//                            }
//                            var matcher1 = pattern.matcher(newAnalizer.simvoli);
//                            var i = 0;
//                            while (matcher1.find()) {
//                                var label = (Label)GridPane.getChildren().get(i);
//                                label.setText(newAnalizer.simvoli.substring(matcher1.start(), matcher1.end()));
//                                i++;
//                            }
//                            notify();
//                        });
//                        wait();
//                      //  sleep(100000);
//                        myTextArea.setText(myTextArea.getText() + "\n" + newAnalizer.stack);
//                        myTextArea2.setText(myTextArea2.getText() + "\n" + newAnalizer.simvoli);
//                    }
//                    return flag;
//                }
//            };
//            var th = new Thread(task);
//            th.setDaemon(true);
//            th.start();

            Platform.runLater(() -> {
                var row0 = new RowConstraints(10, 30, Double.MAX_VALUE);
                row0.setVgrow(Priority.SOMETIMES);
                GridPane.getRowConstraints().add(row0);
                var newAnalizer = new Analizer(new String[]{String.valueOf(file), TextField.getText()});
                var flag = false;
                while (!flag) {
                    flag = newAnalizer.step(TextField.getText());
                    var pattern = Pattern.compile(newAnalizer.symbols.getSymbols());
                    var matcher = pattern.matcher(newAnalizer.simvoli);
                    int count = 0;
                    while (matcher.find()) count++;
                    int finalCount = count;
                    var row = new RowConstraints(10, 30, Double.MAX_VALUE);
                    row.setVgrow(Priority.SOMETIMES);
                    GridPane.getRowConstraints().add(row);
                    if (GridPane.getColumnCount() < finalCount) {
                        var column = new ColumnConstraints();
                        column.setPercentWidth(50);
                        GridPane.getColumnConstraints().add(column);
                    }
                    for (int i = 0; i < GridPane.getColumnCount(); i++) {
                        for (int j = 0; j < GridPane.getRowCount(); j++) {
                            if (getNodeByRowColumnIndex(j, i, GridPane) == null) {
                                var label = new Label("");
                                GridPane.add(label, i, j);
                            }
                        }
                    }
//                    var CountSize = GridPane.getColumnCount();
//                    for (var j = 0; j < CountSize; j++) {
//                        for (var i = GridPane.getRowCount() - 1; i > 0; i--) {
//                            Label label1 = (Label) GridPane.getChildren().get(CountSize * (i - 1) + j);
//                            String label1Text = label1.getText();
//                            Label label2 = (Label) GridPane.getChildren().get(CountSize * i + j);
//                            String label2Text = label2.getText();
//                            label1.setText(label2Text);
//                            label2.setText(label1Text);
//                        }
//                    }

                    for(var k = 0; k < GridPane.getColumnCount(); k++){
                        for (var i = GridPane.getRowCount() - 1; i > 0; i--) {
                            Label label1 = (Label) getNodeByRowColumnIndex(i - 1, k, GridPane);
                            String label1Text = label1.getText();
                            Label label2 = (Label) getNodeByRowColumnIndex(i, k, GridPane);
                            String label2Text = label2.getText();
                            label1.setText(label2Text);
                            label2.setText(label1Text);
                        }
                    }
                    var matcher1 = pattern.matcher(newAnalizer.simvoli);
                    var i = 0;
                    while (matcher1.find()) {
                        var label = (Label) getNodeByRowColumnIndex(0, i, GridPane);
                        label.setText(newAnalizer.simvoli.substring(matcher1.start(), matcher1.end()));
                        i++;
                    }
                    myTextArea.setText(myTextArea.getText() + "\n" + newAnalizer.stack);
                    myTextArea2.setText(myTextArea2.getText() + "\n" + newAnalizer.simvoli);
                }
            });
        }
    }
}
