package com.snakegame.beans;

public class Match {
	int matichId;
	Player player1;
	Player player2;
	int messageFrom = 1;
	boolean matchFinished = false;
	Food food;
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

}
