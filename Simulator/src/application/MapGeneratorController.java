package application;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.ArenaMap.ArenaMapException;
import application.ArenaMap.CellState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.stage.Stage;

public class MapGeneratorController implements Initializable {
	
	private static Color UNEXPLOREDCELLCOLOR = Color.GRAY;
	private static Color EMPTYCELLCOLOR = Color.WHITE;
	private static Color OBSTACLECELLCOLOR = Color.BLACK;
	
	private ArenaMap arenaModel;
	private File mapDescriptor;
	
	@FXML 
	Rectangle demo;
	
	@FXML
	GridPane arena;
	
	@FXML
	Label rowIndexLabel;
	
	@FXML
	Label colIndexLabel;
	
	@FXML 
	Label cellState;
	
	@FXML
	Label unexploredCount;
	
	@FXML
	Label emptyCount;
	
	@FXML
	Label obstacleCount;
	
	@FXML
	Label msgBox;
	
	private  Scene scene;
    public void setScene(Scene scene) {
    		this.scene = scene; 
    	}
    
    private Stage stage;
    public void setStage(Stage stage){
    		this.stage = stage;
    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	@FXML
	private void open(ActionEvent event){

		if(GlobalUtil.DEBUG){
			System.out.println("Click to open the file...");
		}	
		
		FileChooser fileChooser = getMapDescriptorFileChooser();
		this.mapDescriptor = fileChooser.showOpenDialog(this.stage);
		StringBuilder mapDescriptors = new StringBuilder();
		if(this.mapDescriptor != null){
			
			try(BufferedReader br = new BufferedReader(
					new FileReader(this.mapDescriptor))) {
				mapDescriptors.append(br.readLine());
				mapDescriptors.append("\n");
				mapDescriptors.append(br.readLine());
				//TODO
			//	System.out.println("mapDescriptor: " + mapDescriptors.toString());
			} catch (IOException e) {
				//e.printStackTrace();
				this.msgBox.setText("Open up " + this.mapDescriptor.getName() + " failed...");

			}
			
			try{
				this.arenaModel.setArenaMapFromDescriptor(mapDescriptors.toString());
				this.msgBox.setText("Open up " + this.mapDescriptor.getName());
				this.refreshView();
			}catch(ArenaMapException e){
				this.msgBox.setText("Parse Descriptor in " + this.mapDescriptor.getName() + " failed...");

			}

		}
		
	}

	private FileChooser getMapDescriptorFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("Map Descripter", "*.txt"));
		return fileChooser;
	}
	
	@FXML
	private void save(ActionEvent event){
		if(GlobalUtil.DEBUG){
			System.out.println("Click to save the file...");

		}
		if(this.mapDescriptor == null){
			FileChooser fileChooser = getMapDescriptorFileChooser();
			this.mapDescriptor = fileChooser.showSaveDialog(this.stage);
			
		}
		if(this.mapDescriptor == null){
			return;
		}
		
		try(BufferedWriter bw = new BufferedWriter(
				new FileWriter(this.mapDescriptor))) {
			
			bw.write(this.arenaModel.retrieveArenaDescriptor());
		    this.msgBox.setText("Save to " + this.mapDescriptor.getName());
			
		} catch (IOException e) {
			//e.printStackTrace();
			this.msgBox.setText("Open up " + this.mapDescriptor.getName() + "failed...");
		}
	}
	
    @FXML
    public void exit(ActionEvent event) {
    		this.save(null);
    		System.out.println("exit");
    		System.exit(0);
    }
    
    @FXML
    private void onMouseHovered(MouseEvent me){
//    		if(GlobalUtil.DEBUG){
//        		System.out.println("Mouse hovered on " + me.getSceneX() + " " + me.getSceneY());
//    		}
    		double xCdn = me.getSceneX();
    		double yCdn = me.getSceneY();
    		
    		int rowIndex = computeArenaRowIndex(yCdn);
    		int columnIndex = computeArenaColumnIndex(xCdn);
    	
    		updateCellIndexDisplay(rowIndex, columnIndex);
    		updateCellStateDisplay(rowIndex, columnIndex);
    }

	private void updateCellStateDisplay(int rowIndex, int columnIndex) {
		if((0 <= rowIndex && rowIndex <= GlobalUtil.rowCount - 1 ) &&
			     (0 <= columnIndex && columnIndex <= GlobalUtil.columnCount - 1)){
		ArenaMap.CellState cellState = this.arenaModel.getCellState(rowIndex, columnIndex);
			if(cellState == ArenaMap.CellState.UNEXPLORED){
				this.demo.setFill(UNEXPLOREDCELLCOLOR);
				this.cellState.setText("Unexplored");
			}else if(cellState == ArenaMap.CellState.EMPTY){
				this.demo.setFill(EMPTYCELLCOLOR);
				this.cellState.setText("Empty");
	
			}else if(cellState == ArenaMap.CellState.OBSTACLE){
				this.demo.setFill(OBSTACLECELLCOLOR);
				this.cellState.setText("Obstacle");
	
			}else{
				System.err.println("Invalid cell state");
			}
		}else{
			this.demo.setFill(UNEXPLOREDCELLCOLOR);
			this.cellState.setText("N.A.");
			
		}
	}

	private void updateCellIndexDisplay(int rowIndex, int columnIndex) {
		if((0 <= rowIndex && rowIndex <= GlobalUtil.rowCount - 1 ) &&
		     (0 <= columnIndex && columnIndex <= GlobalUtil.columnCount - 1)){
			this.rowIndexLabel.setText(GlobalUtil.chars[rowIndex]);
			this.colIndexLabel.setText("" + (columnIndex + 1));

		}else{
			this.rowIndexLabel.setText("-");
			this.colIndexLabel.setText("-");

		}
	}
    
    public void onViewShowed(){
		this.setArenaMapModel(
				new ArenaMap(GlobalUtil.rowCount,GlobalUtil.columnCount,GlobalUtil.robotDiameter)
		);
    }
    
    @FXML
    private void onMousePressed(MouseEvent me){
		if(GlobalUtil.DEBUG){
    			System.out.println("Mouse pressed on " + me.getSceneX() + " " + me.getSceneY());
		}
		
		
		double xCdn = me.getSceneX();
		double yCdn = me.getSceneY();
		
		int rowIndex = computeArenaRowIndex(yCdn);
		int columnIndex = computeArenaColumnIndex(xCdn);
		
		Rectangle rec = getArenaCell(rowIndex, columnIndex);
		ArenaMap.CellState newState = null;
		//Change the cell state in a circular way: Unexplored -> Empty -> Obstacle
		if(this.arenaModel.getCellState(rowIndex, columnIndex) 
				== CellState.UNEXPLORED){
			newState = CellState.EMPTY;
		}else if(this.arenaModel.getCellState(rowIndex, columnIndex)
				== CellState.EMPTY){
			newState = CellState.OBSTACLE;
		}else{
			newState = CellState.UNEXPLORED;
		}
		this.arenaModel.setCellState(rowIndex, columnIndex, newState);
		this.updateCellStateDisplay(rowIndex, columnIndex);
		this.refreshView();
    }
    
    private static void fillRectBaseOnState(Rectangle rec,ArenaMap.CellState cellState){
	    	if(cellState == ArenaMap.CellState.UNEXPLORED){
				rec.setFill(UNEXPLOREDCELLCOLOR);
			}else if(cellState == ArenaMap.CellState.EMPTY){
				rec.setFill(EMPTYCELLCOLOR);
			}else if(cellState == ArenaMap.CellState.OBSTACLE){
				rec.setFill(OBSTACLECELLCOLOR);
			}else{
				System.err.println("Invalid arena cell...");
			}
    }

	private Rectangle getArenaCell(int rowIndex, int columnIndex) {
		Rectangle rec = (Rectangle) this.scene.lookup("#Cell" + GlobalUtil.chars[rowIndex]  + columnIndex);
		return rec;
	}
    
    private void setArenaMapModel(ArenaMap model){
    		this.arenaModel = model;
    		if(this.arenaModel != null){
        		refreshView();

    		}
    }
    
    //return a value between [0,GlobalUtil.rowCount - 1]
    private int computeArenaRowIndex(double yCdn){ //xCdn = Coordinate X on the scene
    		double arenaYCdn = this.arena.getLayoutY();
    		double cellHeight = this.arena.getHeight() / (GlobalUtil.rowCount + 1);
    		int rowIndex = (int)((yCdn - arenaYCdn) / cellHeight);
    		rowIndex--;
    		return rowIndex;
    		
    }
    
    //return a value between [0,GlobalUtil.columnCount - 1]
    private int computeArenaColumnIndex(double xCdn){ //xCdn = Coordinate X on the scene
		double arenaXCdn = this.arena.getLayoutX();
		double cellWidth = this.arena.getWidth() / (GlobalUtil.columnCount + 1);
		int columnIndex = (int)((xCdn - arenaXCdn) / cellWidth);
		columnIndex--;
		return columnIndex;
		
    }
    
    private void refreshView(){
    		//Set the arena view based on the map from the model
    		
    		CellState[][] map = this.arenaModel.getArenaMap();
    		for(int rowIndex = 0;rowIndex < GlobalUtil.rowCount;rowIndex++){
    			for(int colIndex = 0;colIndex < GlobalUtil.columnCount;colIndex++){
    			
    				Rectangle rec = getArenaCell(rowIndex, colIndex);
    				fillRectBaseOnState(rec, map[rowIndex][colIndex]);
    				
    				
    			}
    		}
    		
    		this.unexploredCount.setText("" + this.arenaModel.getCellTypeNum(ArenaMap.CellState.UNEXPLORED));
    		this.obstacleCount.setText("" + this.arenaModel.getCellTypeNum(ArenaMap.CellState.OBSTACLE));
    		this.emptyCount.setText("" + this.arenaModel.getCellTypeNum(ArenaMap.CellState.EMPTY));

    }
    
  

}
