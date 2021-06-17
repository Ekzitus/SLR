package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

    @FXML
    private void textFieldAction() {
        if(file != null) {
            for (TextArea textArea : Arrays.asList(myTextArea, myTextArea2)) {
                textArea.clear();
            }
            while(GridPane.getRowConstraints().size() > 1) GridPane.getRowConstraints().remove(0);
            while(GridPane.getColumnConstraints().size() > 1) GridPane.getColumnConstraints().remove(0);
            while (GridPane.getChildren().size() > 0) {
                int size = GridPane.getChildren().size() - 1;
                GridPane.getChildren().remove(GridPane.getChildren().get(size));
            }

            var task = new Task<>() {
                @Override protected Boolean call() throws InterruptedException {
                    var newAnalizer = new Analizer(new String[]{String.valueOf(file), TextField.getText()});
                    var flag = false;
                    while (!flag) {
                        if (isCancelled()) {
                            break;
                        }
                        flag = newAnalizer.step(TextField.getText());
                        Platform.runLater(() -> {
                            if(GridPane.getColumnCount() < 2){
                                var column = new ColumnConstraints();
                                column.setPercentWidth(50);
                                GridPane.getColumnConstraints().add(column);
                                for(int i = 0; i < GridPane.getColumnCount(); i++){
                                    var label = new Label("--");
                                    GridPane.add(label, i, GridPane.getRowCount() - 1);
                                }
                            }
                            var row = new RowConstraints(10, 30, Double.MAX_VALUE);
                            row.setVgrow(Priority.SOMETIMES);
                            GridPane.getRowConstraints().add(row);
                            for(var i = 0; i < GridPane.getColumnCount(); i++){
                                var label = new Label("--");
                                GridPane.add(label, i, GridPane.getRowCount() - 1);
                            }
                            var CountSize = GridPane.getColumnCount();
                            for(var i = GridPane.getRowCount() - 1; i > 0; i--){
                                var label1 = (Label)GridPane.getChildren().get(CountSize*(i - 1));
                                var label1Text = label1.getText();
                                var label2 = (Label)GridPane.getChildren().get(CountSize*i);
                                var label2Text = label2.getText();
                                label1.setText(label2Text);
                                label2.setText(label1Text);
                            }
                            var label = (Label)GridPane.getChildren().get(0);
                            var pattern = Pattern.compile(newAnalizer.symbols.symbols);
                            var matcher = pattern.matcher(newAnalizer.simvoli);
                            var i = 0;
                            while (matcher.find()) if (label.getText().equals("--")) {
                                label.setText(newAnalizer.simvoli.substring(matcher.start(), matcher.end()));
                                i = 1;
                            } else if(i == 1){
                                label = (Label) GridPane.getChildren().get(i);
                                label.setText(newAnalizer.simvoli.substring(matcher.start(), matcher.end()));
                            }
                        });
                        sleep(10);
                        myTextArea.setText(myTextArea.getText() + "\n" + newAnalizer.stack);
                        myTextArea2.setText(myTextArea2.getText() + "\n" + newAnalizer.simvoli);
                    }
                    return flag;
                }
            };
            var th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }
}
