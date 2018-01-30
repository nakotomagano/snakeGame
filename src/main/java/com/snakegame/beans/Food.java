package com.snakegame.beans;

public class Food {

	private Coordinate coord;
	private String type;
	
	//TODO: create Type class for this
	public Food(){
		
	}
	
	public Food(Coordinate c, String type){
		this.coord = c;
		this.type = type;
	}

	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(Coordinate coord) {
		this.coord = coord;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
