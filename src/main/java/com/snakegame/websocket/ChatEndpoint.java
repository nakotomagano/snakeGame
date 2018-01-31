package com.snakegame.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.snakegame.beans.Coordinate;
import com.snakegame.beans.Food;
import com.snakegame.beans.Match;
import com.snakegame.beans.Message;
import com.snakegame.beans.Player;


@ServerEndpoint(value = "/chat/{username}", decoders = { MatchDecoder.class, MessageDecoder.class}, encoders = {MatchEncoder.class, MessageEncoder.class})
public class ChatEndpoint {

	private Session session;
	private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
	private static HashMap<String, String> users = new HashMap<>();
	private static Map<String, Session> sessions = new HashMap<String, Session>();
	private static Match match = new Match();
	private static ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor(); //newScheduledThreadPool(0);	//newSingleThreadScheduledExecutor()
	//ScheduledExecutorService timer = Executors.newScheduledThreadPool(5);
	private final int MIN_SNAKE_SIZE = 4;
	private final int GAME_SPEED = 300; //in milliseconds
	private final int GAME_DELAY = 3000; //in milliseconds
	private final Coordinate START_FOOD_POSITION = new Coordinate(-1,-1);

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {

		this.session = session;
		chatEndpoints.add(this);
		sessions.put(session.getId(), session);
		users.put(session.getId(), username);

		if(chatEndpoints.size() == 1) {
			Player player1 = new Player(chatEndpoints.size(),username);
			match.setPlayer1(player1);
			match.getPlayer1().setReady(true);
			Player player2 = new Player(2,"waiting");
			match.setPlayer2(player2);
		} else if (chatEndpoints.size() == 2){
			Player player2 = new Player(chatEndpoints.size(),username);
			match.setPlayer2(player2);
			match.getPlayer2().setReady(true);
		}
		if (!match.getPlayer2().isReady()){
			broadcast(match);
		} else
			startTheMatch();
	}

	private void startTheMatch() {
		//TODO: change this, for now there has to be food, so at beginning it's set outside the canvas 
		Food f = new Food(START_FOOD_POSITION, "starting");
		match.setFood(f);
		//	gameOverSent = false;
		match.setMatchFinished(false);
		match.setStatus("starting");
		try {
			broadcast(match);
		} catch (IOException | EncodeException e1) {
			e1.printStackTrace();
		}
		match.setStatus("started");
		timer.scheduleAtFixedRate(
				() -> {
					if(!match.isGameOverSent()){
						try {
							calculateNextPositions();
						} catch (IOException | EncodeException e) {
							e.printStackTrace();
						}
					}
				},GAME_DELAY,GAME_SPEED,TimeUnit.MILLISECONDS);
		//add food:
		timer.scheduleAtFixedRate(
				() -> {
					if(!match.isGameOverSent()){
						Food food = new Food(new Coordinate(10,12), "addBodySize");
						match.setFood(food);
					}
				},10,15,TimeUnit.SECONDS);
	}

	private void calculateNextPositions() throws IOException, EncodeException {
		//	boolean changedPlayer1 = false;
		Player player1 = match.getPlayer1();
		Player player2 = match.getPlayer2();
		int player1Size = player1.getCoordinates().size();
		int player2Size = player2.getCoordinates().size();

		Coordinate head1 = player1.getCoordinates().get(player1Size-1);
		Coordinate head2 = player2.getCoordinates().get(player2Size-1);


		Coordinate tail1 = player1.getCoordinates().get(0);
		Coordinate tail2 = player2.getCoordinates().get(0);

		Coordinate newHead1 = new Coordinate();
		Coordinate newHead2 = new Coordinate();

		/*
		System.out.println("=====Podaci pre pomeranja:=====");
		System.out.println("Koordinate: " + player2.getCoordinates().toString());
		System.out.println("Previous direcion : " + player2.getPreviousDirection() + " Direction: " + player2.getDirection()+ " Next Direction: " + player2.getNextDirection());
		 */
		movePlayer(player1, newHead1, head1, tail1);
		movePlayer(player2, newHead2, head2, tail2);
		/*
		System.out.println("Podaci posle pomeranja:");
		System.out.println("Koordinate: " + player2.getCoordinates());
		System.out.println("Previous direcion : " + player2.getPreviousDirection()+" Direction: " + player2.getDirection()+" Next Direction: " + player2.getNextDirection());
		 */
		checkForCombat(newHead1, newHead2);
		//When someone hits the wall, remove one tile and respawn
		if(playerHitTheWall(newHead1) || playerHitHimself(player1))
			player1.createBody(player1.getCoordinates().size()-1);
		if(playerHitTheWall(newHead2) || playerHitHimself(player2))
			player2.createBody(player2.getCoordinates().size()-1);
		if(player1.getCoordinates().size() < MIN_SNAKE_SIZE || player2.getCoordinates().size() < MIN_SNAKE_SIZE)
			match.setMatchFinished(true);
		if(!match.isGameOverSent())
			broadcast(match);
		if(match.isMatchFinished()){
			match.setGameOverSent(true);
		}
	}
	//Part of calculateNextPositions()
	private void movePlayer(Player player, Coordinate newHead, Coordinate head, Coordinate tail) {
		boolean playerAteFood = false;
		//	String playerDirection = player.getDirection();
		//if(player.getMoves().isEmpty())
		//	System.out.println("GET MOVES IS EMPTY!!!");
		String playerDirection = player.getMoves().get(0);
		if("right".equals(playerDirection)) {
			newHead.setX(head.getX() + 1);
			newHead.setY(head.getY());
			player.getCoordinates().add(newHead);
		} else if ("left".equals(playerDirection)) {
			newHead.setX(head.getX() - 1);
			newHead.setY(head.getY());
			player.getCoordinates().add(newHead);
		} else if ("up".equals(playerDirection)){
			newHead.setX(head.getX());
			newHead.setY(head.getY() - 1);
			player.getCoordinates().add(newHead);
		} else if ("down".equals(playerDirection)){
			newHead.setX(head.getX());
			newHead.setY(head.getY() + 1);
			player.getCoordinates().add(newHead);
		}

		playerAteFood = isFoodEaten(newHead);
		if(!playerAteFood)
			player.getCoordinates().remove(tail);
		if(player.getMoves().size() > 1)
			player.getMoves().remove(0);

		//String zaStampu = Integer.toString(player.getId()) + ": " + player.getMoves();
		//System.out.println(zaStampu);
	}
	private boolean isFoodEaten(Coordinate head) {
		Coordinate fCoord = match.getFood().getCoord();
		if(head.getX() == fCoord.getX() && head.getY() == fCoord.getY()) {
			match.getFood().setCoord(START_FOOD_POSITION);
			return true;
		}
		return false;
	}

	private boolean playerHitTheWall(Coordinate head) {
		if(head.getX()<0 || head.getX() > 40 || head.getY()<0 || head.getY() > 26)
			return true;
		return false;
	}

	private boolean playerHitHimself(Player player) {
		Coordinate head = player.getCoordinates().get(player.getCoordinates().size()-1);
		for(int i=0; i<player.getCoordinates().size()-1; i++)
			if(head.getX() == player.getCoordinates().get(i).getX() && head.getY() == player.getCoordinates().get(i).getY())
				return true;
		return false;
	}

	private boolean someoneHitTheWall(Coordinate newHead1, Coordinate newHead2) {
		//TODO: move 40 to constants.
		if(newHead1.getX()<0 || newHead1.getX() > 40 || newHead2.getX()<0 || newHead2.getX() > 40 
				|| newHead1.getY()<0 || newHead1.getY() > 26 || newHead2.getY()<0 || newHead2.getY() > 26 )
			return true;
		return false;
	}

	private void checkForCombat(Coordinate head1, Coordinate head2) {

		for (Coordinate c: match.getPlayer2().getCoordinates()){
			if(head1.getX() == c.getX() && head1.getY() == c.getY()){
				match.getPlayer2().getCoordinates().remove(0); //l2-1
				break;
			}
		}
		for (Coordinate c: match.getPlayer1().getCoordinates()){
			if(head2.getX() == c.getX() && head2.getY() == c.getY()){
				match.getPlayer1().getCoordinates().remove(0); //l1-1
				break;
			}
		}

	}

	@OnMessage
	public void onMessage(Session session, Message msg) throws IOException, EncodeException {
		//	System.out.println(msg.toString());
		if ("rematch".equals(msg.getContent())) {

			if("1".equals(msg.getFrom())){
				match.getPlayer1().setRematch(true);
				System.out.println("Player 1 wants a rematch!");
			}
			else if("2".equals(msg.getFrom())){
				match.getPlayer2().setRematch(true);
				System.out.println("Player 2 wants a rematch!");
			}
		}
		if (match.getPlayer1().isRematch() && match.getPlayer2().isRematch()){
			//REMATCH!
			System.out.println("Rematch starting!");
			match.restart();
			try {
				broadcast(match);
			} catch (IOException | EncodeException e1) {
				e1.printStackTrace();
			}
			//TODO: This is terrible, must find better way of delaying rematch
			match.setMatchFinished(true);
			match.setGameOverSent(true);
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			match.setMatchFinished(false);
			match.setGameOverSent(false);
			//TODO: Terribleness ends here. o.O
			match.setStatus("started");
		}
		else if(!match.isMatchFinished()){
			match.setMessageFrom(Integer.parseInt(msg.getFrom()));
			if (match.getMessageFrom() == match.getPlayer1().getId())
				checkPlayerDirection(match.getPlayer1(), msg);
			if (match.getMessageFrom() == match.getPlayer2().getId())
				checkPlayerDirection(match.getPlayer2(), msg);
		}
	}

	//Part of onMessage():
	private void checkPlayerDirection(Player player, Message msg) {
		String direction = msg.getContent();
		String nextDirection = msg.getNextDirection();
	//	System.out.println("checking player direction");
		if(!direction.isEmpty()){

			if(player.getMoves().size() <= 2) {
				if(!direction.equals(player.getMoves().get(player.getMoves().size()-1))){
					if(("right".equals(direction) && !"left".equals(player.getMoves().get(player.getMoves().size()-1)))
							|| ("left".equals(direction) && !"right".equals(player.getMoves().get(player.getMoves().size()-1)))
							|| ("down".equals(direction) && !"up".equals(player.getMoves().get(player.getMoves().size()-1)))
							|| ("up".equals(direction) && !"down".equals(player.getMoves().get(player.getMoves().size()-1)))) {
						player.getMoves().add(direction);
					}
				}
				if(!nextDirection.isEmpty()){
					if(!nextDirection.equals(player.getMoves().get(player.getMoves().size()-1)) ){
						if(("right".equals(nextDirection) && !"left".equals(player.getMoves().get(player.getMoves().size()-1)))
								|| ("left".equals(nextDirection) && !"right".equals(player.getMoves().get(player.getMoves().size()-1)))
								|| ("down".equals(nextDirection) && !"up".equals(player.getMoves().get(player.getMoves().size()-1)))
								|| ("up".equals(nextDirection) && !"down".equals(player.getMoves().get(player.getMoves().size()-1)))){
							player.getMoves().add(nextDirection);
						}
					}
				}
			} else {
				if(nextDirection.isEmpty()){
					if(!direction.equals(player.getMoves().get(player.getMoves().size()-2))){
						if(("right".equals(direction) && !"left".equals(player.getMoves().get(player.getMoves().size()-2)))
								|| ("left".equals(direction) && !"right".equals(player.getMoves().get(player.getMoves().size()-2)))
								|| ("down".equals(direction) && !"up".equals(player.getMoves().get(player.getMoves().size()-2)))
								|| ("up".equals(direction) && !"down".equals(player.getMoves().get(player.getMoves().size()-2)))) {
							player.getMoves().remove(player.getMoves().size()-1);
							player.getMoves().add(direction);
						}
					}

				} else {
					if(!nextDirection.equals(player.getMoves().get(player.getMoves().size()-2))){
						if(("right".equals(nextDirection) && !"left".equals(player.getMoves().get(player.getMoves().size()-2)))
								|| ("left".equals(nextDirection) && !"right".equals(player.getMoves().get(player.getMoves().size()-2)))
								|| ("down".equals(nextDirection) && !"up".equals(player.getMoves().get(player.getMoves().size()-2)))
								|| ("up".equals(nextDirection) && !"down".equals(player.getMoves().get(player.getMoves().size()-2)))) {
							player.getMoves().remove(player.getMoves().size()-1);
							player.getMoves().add(nextDirection);
						}
					}

				}

			}
		}
	}

	@OnClose
	public void onClose(Session session) throws IOException, EncodeException {
		chatEndpoints.remove(this);
		System.out.println("Web socket closed");
		sessions.remove(session.getId());
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
	}

	//private static void broadcast(final Player message) throws IOException, EncodeException {
	private static void broadcast(final Match match) throws IOException, EncodeException {
		chatEndpoints.forEach(endpoint -> {
			synchronized (endpoint) {
				try {
					endpoint.session.getBasicRemote().sendObject(match);
				} catch (IOException | EncodeException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public String getCurrentTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
	}
}