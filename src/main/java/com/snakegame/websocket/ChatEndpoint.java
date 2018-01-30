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
	//FutureTask<Match>[] future = new FutureTask[1];
	private final int MIN_SNAKE_SIZE = 5;
	private final Coordinate START_FOOD_POSITION = new Coordinate(-1,-1);
	boolean gameOverSent = false;

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
		gameOverSent = false;
		match.setMatchFinished(false);
		try {
			broadcast(match);
		} catch (IOException | EncodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		timer.scheduleAtFixedRate(
			() -> {
				if(!gameOverSent){
					try {
						calculateNextPositions();
						//String sdf = getCurrentTimeStamp();
						//	System.out.println("Match time: " + sdf);
					} catch (IOException | EncodeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},400,400,TimeUnit.MILLISECONDS);
		//add food:
		timer.scheduleAtFixedRate(
			() -> {
				if(!gameOverSent){
					Food food = new Food(new Coordinate(10,12), "addBodySize");
					match.setFood(food);
				}
			},10,15,TimeUnit.SECONDS);
	}

	private void calculateNextPositions() throws IOException, EncodeException {
		//	boolean changedPlayer1 = false;
		Player player1 = match.getPlayer1();
		Player player2 = match.getPlayer2();
		int player1Size = match.getPlayer1().getCoordinates().size();
		int player2Size = match.getPlayer2().getCoordinates().size();

		Coordinate head1 = match.getPlayer1().getCoordinates().get(player1Size-1);
		Coordinate head2 = match.getPlayer2().getCoordinates().get(player2Size-1);


		Coordinate tail1 = match.getPlayer1().getCoordinates().get(0);
		Coordinate tail2 = match.getPlayer2().getCoordinates().get(0);

		Coordinate newHead1 = new Coordinate();
		Coordinate newHead2 = new Coordinate();

/*		if (player1.getHadPreviousDirection()){
			player1.setNextDirection(player1.getDirection());
			player1.setDirection(player1.getPreviousDirection());		
		}
		if (player2.getHadPreviousDirection()){
			player2.setNextDirection(player2.getDirection());
			player2.setDirection(player2.getPreviousDirection());		
		}*/
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
		if(playerHitTheWall(newHead1))
			match.getPlayer1().createBody(match.getPlayer1().getCoordinates().size()-1);
		if(playerHitTheWall(newHead2))
			match.getPlayer2().createBody(match.getPlayer2().getCoordinates().size()-1);
		if(match.getPlayer1().getCoordinates().size() < MIN_SNAKE_SIZE || match.getPlayer2().getCoordinates().size() < MIN_SNAKE_SIZE)
			match.setMatchFinished(true);
		if(!gameOverSent)
			broadcast(match);
		if(match.isMatchFinished()){
			gameOverSent = true;
		}
		player1.setChangedPlayer(false);
		player2.setChangedPlayer(false);
	}
	//Part of calculateNextPositions()
	private void movePlayer(Player player, Coordinate newHead, Coordinate head, Coordinate tail) {
		boolean playerAteFood = false;
	//	String playerDirection = player.getDirection();
		if(player.getMoves().isEmpty())
			System.out.println("GET MOVES IS EMPTY!!!");
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
				int l2 = match.getPlayer2().getCoordinates().size();
				match.getPlayer2().getCoordinates().remove(l2-1);
				break;
			}
		}
		for (Coordinate c: match.getPlayer1().getCoordinates()){
			if(head2.getX() == c.getX() && head2.getY() == c.getY()){
				int l1 = match.getPlayer1().getCoordinates().size();
				match.getPlayer1().getCoordinates().remove(l1-1);
				break;
			}
		}

	}

	@OnMessage
	public void onMessage(Session session, Message msg) throws IOException, EncodeException {
		//	System.out.println(msg.toString());
		if ("rematch".equals(msg.getContent())) {
			if("1".equals(msg.getFrom()))
				match.getPlayer1().setRematch(true);
			else match.getPlayer2().setRematch(true);
		}
		if (match.getPlayer1().isRematch() && match.getPlayer2().isRematch()){
			//REMATCH!
			match.getPlayer1().restart();
			match.setPlayer1(match.getPlayer1());

			match.getPlayer2().restart();
			match.setPlayer2(match.getPlayer2());
			
			//startTheMatch();
			gameOverSent = false;
			match.setMatchFinished(false);
		}
		else if(!match.isMatchFinished()){
			match.setMessageFrom(Integer.parseInt(msg.getFrom()));
			if (match.getMessageFrom() == match.getPlayer1().getId())
				//match.getPlayer1().setDirection(msg.getContent());
				checkPlayerDirection(match.getPlayer1(), msg);
			if (match.getMessageFrom() == match.getPlayer2().getId())
				//match.getPlayer2().setDirection(msg.getContent());
				checkPlayerDirection(match.getPlayer2(), msg);
		}
	}

	//Part of onMessage():
	private void checkPlayerDirection(Player player, Message msg) {
		String direction = msg.getContent();
		String nextDirection = msg.getNextDirection();
		int movesSize = 0;
		if (player.getMoves().size() > 1) movesSize = player.getMoves().size()-1;
			if(!direction.isEmpty()){
				if(!direction.equals(player.getMoves().get(movesSize))){
					if(("right".equals(direction) && !"left".equals(player.getMoves().get(movesSize)))
							|| ("left".equals(direction) && !"right".equals(player.getMoves().get(movesSize)))
							|| ("down".equals(direction) && !"up".equals(player.getMoves().get(movesSize)))
							|| ("up".equals(direction) && !"down".equals(player.getMoves().get(movesSize))))
					player.getMoves().add(direction);
				}
			/*	if(!nextDirection.isEmpty()){
					if(!nextDirection.equals(player.getMoves().get(movesSize)) && ){
						if(("right".equals(nextDirection) && !"left".equals(player.getMoves().get(movesSize)))
								|| ("left".equals(nextDirection) && !"right".equals(player.getMoves().get(movesSize)))
								|| ("down".equals(nextDirection) && !"up".equals(player.getMoves().get(movesSize)))
								|| ("up".equals(nextDirection) && !"down".equals(player.getMoves().get(movesSize))))
						player.getMoves().add(nextDirection);
					}
				}*/
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
/*
private void movePlayer1(Coordinate newHead1, Coordinate head1, Coordinate tail1) {
	boolean player1AteFood = false;
	boolean changedPlayer1 = match.getPlayer1().isChangedPlayer();
	if(!changedPlayer1) {
		if(match.getPlayer1().getNextDirection().isEmpty()){
			if("right".equals(match.getPlayer1().getDirection())) {
				newHead1.setX(head1.getX() + 1);
				newHead1.setY(head1.getY());
				match.getPlayer1().getCoordinates().add(newHead1);
			} else if ("left".equals(match.getPlayer1().getDirection())) {
				newHead1.setX(head1.getX() - 1);
				newHead1.setY(head1.getY());
				match.getPlayer1().getCoordinates().add(newHead1);
			} else if ("up".equals(match.getPlayer1().getDirection())){
				newHead1.setX(head1.getX());
				newHead1.setY(head1.getY() - 1);
				match.getPlayer1().getCoordinates().add(newHead1);
			} else if ("down".equals(match.getPlayer1().getDirection())){
				newHead1.setX(head1.getX());
				newHead1.setY(head1.getY() + 1);
				match.getPlayer1().getCoordinates().add(newHead1);
			}
			match.getPlayer1().setChangedPlayer(true);
		}
	} else {
		if(!changedPlayer1) {
			if("right".equals(match.getPlayer1().getNextDirection()) && !match.getPlayer1().getDirection().equals("left")) {
				newHead1.setX(head1.getX() + 1);
				newHead1.setY(head1.getY());
				match.getPlayer1().getCoordinates().add(newHead1);
			} else if ("left".equals(match.getPlayer1().getNextDirection()) && !match.getPlayer1().getDirection().equals("right")) {
				newHead1.setX(head1.getX() - 1);
				newHead1.setY(head1.getY());
				match.getPlayer1().getCoordinates().add(newHead1);
			} else if ("up".equals(match.getPlayer1().getNextDirection()) && !match.getPlayer1().getDirection().equals("down")){
				newHead1.setX(head1.getX());
				newHead1.setY(head1.getY() - 1);
				match.getPlayer1().getCoordinates().add(newHead1);
			} else if ("down".equals(match.getPlayer1().getNextDirection()) && !match.getPlayer1().getDirection().equals("up")){
				newHead1.setX(head1.getX());
				newHead1.setY(head1.getY() + 1);
				match.getPlayer1().getCoordinates().add(newHead1);
			}
			match.getPlayer1().setChangedPlayer(true);
		}
	}
	player1AteFood = isFoodEaten(newHead1);

	//TODO: instead of just adding +1 tile, based on food type
	//		add speed, untouchable mode or something like that
	if(!player1AteFood)
		match.getPlayer1().getCoordinates().remove(tail1);
}
 */