package com.snakegame.beans;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private int id;
	private String name;
	List<Coordinate>  coordinates = new ArrayList<Coordinate>(1);
	boolean isReady;
	String direction;
	String previousDirection;
	boolean hadPreviousDirection;
	boolean rematch;
	List<String> moves = new ArrayList<String>();		//????
	
	public List<String> getMoves() {
		return moves;
	}

	public void setMoves(List<String> moves) {
		this.moves = moves;
	}

	public boolean isRematch() {
		return rematch;
	}

	public void setRematch(boolean rematch) {
		this.rematch = rematch;
	}

	public boolean getHadPreviousDirection() {
		return hadPreviousDirection;
	}

	public void setHadPreviousDirection(boolean hadPreviousDirection) {
		this.hadPreviousDirection = hadPreviousDirection;
	}

	public String getPreviousDirection() {
		return previousDirection;
	}

	public void setPreviousDirection(String previousDirection) {
		this.previousDirection = previousDirection;
	}

	String nextDirection = "";
	boolean changedPlayer;
	public Player(){
		
	}
	
	public Player(int id, String name){
		this.setId(id);
		this.name = name;
		createBody(5);
		this.setCoordinates(coordinates);
		this.direction = "right";
		this.previousDirection = "right";
		this.isReady = false;
		this.changedPlayer = false;
		this.hadPreviousDirection = false;
		this.rematch = false;
		this.moves.clear();
		this.moves.add("right"); //?????
	}
	
	public void restart(){
		createBody(5);
		this.setCoordinates(coordinates);
		this.direction = "right";
		this.previousDirection = "right";
		this.isReady = true;
		this.changedPlayer = false;
		this.hadPreviousDirection = false;
		this.rematch = false;
		this.moves.clear();
		this.moves.add("right");
	}

	public void createBody(int length) {
		coordinates.clear();
		//set starting coordinates
		for(int i = 0; i<length; i++){
			if(id==1){
				//TODO: this is not cool
				Coordinate coordinate = new Coordinate(i+5,4);
				coordinates.add(coordinate);
			} else {
				Coordinate coordinate = new Coordinate(i+8,9);
				coordinates.add(coordinate);
			}
		}
	}

	public boolean isReady() {
		return isReady;
	}
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String getNextDirection() {
		return nextDirection;
	}

	public void setNextDirection(String nextDirection) {
		this.nextDirection = nextDirection;
	}

	
	public boolean isChangedPlayer() {
		return changedPlayer;
	}

	public void setChangedPlayer(boolean changedPlayer) {
		this.changedPlayer = changedPlayer;
	}

}
