package Map;

import java.util.ArrayList;


public class Cell {
	
	public boolean isExplored;
	public boolean isObstacle;
	public boolean isPath;
	protected int cellX; //array index, NOT coordinates //i wrt to top left
	protected int cellY; //array index, NOT coordinates //j wrt to top left
	protected double hVal;
	protected double gVal;
	protected double fVal;
	protected Cell parentCell;
	protected boolean isVisited;
	protected boolean isSetBySR = false;
	
	public Cell(boolean isExp, boolean isObs, int i, int j){
		isExplored = isExp;
		isObstacle = isObs;
		cellX = i;
		cellY = j;
	}
	
	public int getX(){
		return cellX;
	}

	public int getY(){
		return cellY;
	}
	
	public void setX(int x){
		this.cellX = x;
	}
	
	public void setY(int y){
		this.cellY = y;
	}
	
	public boolean getIsExplored(){
		return isExplored;
	}
	
	public boolean getIsObstacle(){
		return isObstacle;
	}
	
	public void setIsExplored(boolean explored, boolean isSR){
		//this.isExplored = explored;
		isExplored = explored;
		setBySR(isSR);
	}
	
	public void setIsObstacle(boolean obstacle){
		this.isObstacle = obstacle;
		
		
	}
	
	public boolean getIsPath(){
		return isPath;
	}
	
	public void setIsPath(boolean path){
		isPath = path;
	}
	
	public void printCell(){
		System.out.print("(" + cellX + ", " + cellY + ")");
	}
	public void printExplored(){
		System.out.print(isObstacle);
	}
	
	public void printObstacle(){
		System.out.print("(" + isObstacle + " , "+cellX+", "+ cellY+")");
	}
	
	public void setH(double h){
		hVal = h;
	}
	
	public void setG(double g){
		gVal = g;
	}
	
	public void setF(double f){
		fVal = f;
	}
	
	public double getH(){
		return hVal;
	}
	
	public double getG(){
		return gVal;
	}
	
	public double getF(){
		return fVal;
	}
	
	public void setParent(Cell parent){
		parentCell = parent;
	}
	
	public Cell getParent(){
		return parentCell;
	}
	
	public void setIsPhysicallyVisited(boolean visited){
		isVisited = visited;
	}
	
	public boolean getIsPhysicallyVisited(){
		return isVisited;
	}
	
	public void setBySR(boolean isSet){
		isSetBySR = isSet;
	}
	
	public boolean isSetBySR(){
		return isSetBySR;
	}

}