package com.snakegame.beans;

import com.google.gson.annotations.Expose;

public class Message {
	
	@Expose
    private String from;
	@Expose
    private String to;
	@Expose
    private String content;
	@Expose
	private String nextDirection = "";
	@Expose
	private boolean changedDirection = false;
	@Expose
	private String mode;
	
	@Override
    public String toString() {
        return super.toString();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getNextDirection() {
		return nextDirection;
	}

	public void setNextDirection(String nextDirection) {
		this.nextDirection = nextDirection;
	}

	public boolean isChangedDirection() {
		return changedDirection;
	}

	public void setChangedDirection(boolean changedDirection) {
		this.changedDirection = changedDirection;
	}

	public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}