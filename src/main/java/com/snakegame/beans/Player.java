package com.snakegame.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Player {
	private static final Coordinate POS1 = new Coordinate(5,4);
	private static final Coordinate POS2 = new Coordinate(34,5);
	private static final Coordinate POS3 = new Coordinate(33,21);
	private static final Coordinate POS4 = new Coordinate(6,22);

	private int id;
	private String name;
	private int startingPosition;
	List<Coordinate>  coordinates = new ArrayList<Coordinate>(1);
	boolean isReady;

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

	public Player(){

	}

	public Player(int id, String name, int startingPosition){
		this.setId(id);
		this.name = name;
		this.startingPosition = startingPosition;
		createBody(5);
		this.setCoordinates(coordinates);
		this.isReady = false;
		this.rematch = false;
	}

	public void restart(){
		createBody(5);
		this.setCoordinates(coordinates);
		this.isReady = true;
		this.rematch = false;
	}

	public void createBody(int length) {
		this.coordinates.clear();
		this.moves.clear();
		addTilesAndDirection(length);
	}
	private void addTilesAndDirection(int length){
		for(int i = 0; i<length; i++){
			if(this.startingPosition == 1) {
				Coordinate coordinate = new Coordinate(i+POS1.getX(),POS1.getY());
				this.coordinates.add(coordinate);
			} else if(this.startingPosition == 2) {
				Coordinate coordinate = new Coordinate(POS2.getX()-i,POS2.getY());
				this.coordinates.add(coordinate);
			} else if(this.startingPosition == 3) {
				Coordinate coordinate = new Coordinate(POS3.getX()-i,POS3.getY());
				this.coordinates.add(coordinate);
			} else if(this.startingPosition == 4) {
				Coordinate coordinate = new Coordinate(POS4.getX()+i,POS4.getY());
				this.coordinates.add(coordinate);
			}
			if(this.startingPosition == 1 || this.startingPosition == 4) this.moves.add("right");
			else this.moves.add("left");
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

	public int getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(int startingPosition) {
		this.startingPosition = startingPosition;
	}
}
