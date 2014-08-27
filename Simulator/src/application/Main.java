package application;
	

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.control.Label;

public class Main extends Application {
	
	private static final int arcAmount = 20;
	private static int arenaLabelFont = 16;
	
	private void addCellsForArena(GridPane arena){
		
		int recLength = (int) (arena.getPrefWidth() / (GlobalUtil.columnCount + 1) * 0.9);
		for(int colLabelIndex = 1;colLabelIndex <= GlobalUtil.columnCount;colLabelIndex++){
			Label colLabel = new Label("" + colLabelIndex);
			colLabel.setFont(new Font(arenaLabelFont));
			colLabel.setMinSize(recLength, recLength);
			colLabel.setAlignment(Pos.CENTER);
			arena.add(colLabel, colLabelIndex, 0);
			
		}

		for(int rowLabelIndex = 1;rowLabelIndex <= GlobalUtil.rowCount;rowLabelIndex++){
			
			Label colLabel = new Label("" + GlobalUtil.chars[rowLabelIndex - 1]);
			colLabel.setFont(new Font(arenaLabelFont));

			colLabel.setMinSize(recLength, recLength);
			colLabel.setAlignment(Pos.CENTER);
			arena.add(colLabel, 0, rowLabelIndex);
			
		}
		
		for(int rowIndex = 1; rowIndex <= GlobalUtil.rowCount;rowIndex++){
			for(int colIndex = 1; colIndex <= GlobalUtil.columnCount;colIndex++){
				Rectangle rec = new Rectangle(recLength,recLength);
				rec.setId("Cell" + GlobalUtil.chars[rowIndex - 1] + (colIndex - 1));
				rec.setArcHeight(arcAmount);
				rec.setArcWidth(arcAmount);
				arena.add(rec,colIndex, rowIndex);

				
			}
		}
	}
	
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MapGenerator.fxml"));
			Parent root = (Parent)loader.load();
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			stage.setScene(scene);
			stage.setTitle("MapGenerator");
		 		
			GridPane arena = (GridPane)scene.lookup("#Arena");
			addCellsForArena(arena);
			
			MapGeneratorController myController = (MapGeneratorController)loader.getController();
			myController.setScene(scene);
			myController.setStage(stage);
			stage.setOnCloseRequest(
					new EventHandler<WindowEvent>() {

						@Override
						public void handle(WindowEvent event) {
							myController.exit(null);
						}
			});
			
			stage.setOnShown(
					new EventHandler<WindowEvent>() {

						@Override
						public void handle(WindowEvent event) {
							myController.onViewShowed();
						}
			});

			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
