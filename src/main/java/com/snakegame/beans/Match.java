package com.snakegame.beans;

import com.google.gson.annotations.Expose;

public class Match {
	@Expose
	int matichId;
	@Expose
	Player player1;
	@Expose
	Player player2;
	@Expose
	int messageFrom = 0;
	@Expose
	boolean matchFinished = false;
	@Expose
	Food food;
	@Expose
	boolean gameOverSent = false;
	@Expose
	String status = "";

	public Match(){
		
	}

	public int getMatichId() {
		return matichId;
	}
	public void setMatichId(int matichId) {
		this.matichId = matichId;
	}
	public Player getPlayer1() {
		return player1;
	}
	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}
	public Player getPlayer2() {
		return player2;
	}
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public int getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(int messageFrom) {
		this.messageFrom = messageFrom;
	}

	
	public boolean isMatchFinished() {
		return matchFinished;
	}

	public void setMatchFinished(boolean matchFinished) {
		this.matchFinished = matchFinished;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isGameOverSent() {
		return gameOverSent;
	}

	public void setGameOverSent(boolean gameOverSent) {
		this.gameOverSent = gameOverSent;
	}
	public void restart() {
		this.getPlayer1().restart();
		this.getPlayer2().restart();
		this.setMatchFinished(false);
		this.setGameOverSent(false);
		this.setStatus("starting");
	}

}
