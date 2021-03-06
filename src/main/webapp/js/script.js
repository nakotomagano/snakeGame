var game_mode = 0;
var ws;
var msgsRecieved = 0;
var message;
var changed_direction;
function connect() {
	var username = document.getElementById("PlayerOneName").value;
	var count = 3; //timer: this many seconds before game starts
	var host = document.location.host;
	var pathname = document.location.pathname;
	var thisPlayer;
	var d = "right"; //???
	var p1wins = 0;
	var p2wins = 0;
	var draws = 0;
	ws = new WebSocket("ws://" +host  + pathname + "user/" + username+ "/mode/" + game_mode); // 

	//kad primis poruku sa severa, radi sledece:	
	ws.onmessage = function(event) {
		message = JSON.parse(event.data);
		var gOver = message.matchFinished;
		var winner = message.winner;
		var matchStatus = message.status;
		var coordinates1 = message.player1.coordinates;
		var isPlayer1Ready = message.player1.isReady;
		var isPlayer2Ready = message.player2.isReady;
		var coordinates2 = message.player2.coordinates;
		var food = message.food;
		if (thisPlayer == null){
			if (game_mode === 1) thisPlayer = "player1"
			else if(isPlayer1Ready && !isPlayer2Ready) thisPlayer = "player1";
			else thisPlayer = "player2";
		}
		else player1 = true;
		if (thisPlayer == "player1") d = message.player1.moves[message.player1.moves.length-1];
		if (thisPlayer == "player2") d = message.player2.moves[message.player2.moves.length-1];
		msgsRecieved++;
		changed_direction = false;
		var canvas = $('#canvas')[0];
		var ctx = canvas.getContext("2d");
		var w = $('#canvas').width();
		var h = $('#canvas').height();
		var cw = 15;      //cell width
		var score;
		var color = "green";
		var speed = 500;
		var paused = false;
		var snake_arr;

		var player1name = document.getElementById("lblPlayerOneName");
		var player2name = document.getElementById("lblPlayerTwoName");
		player1name.innerHTML = message.player1.name;
		player2name.innerHTML = message.player2.name;

		document.getElementById('player1-score').innerHTML = '' + p1wins;
		document.getElementById('player2-score').innerHTML = '' + p2wins;
		document.getElementById('draws-score').innerHTML = '' + draws;

		//var d = new Date();
		//var n = d.getTime();
		//console.log(n);
		//Initializer
		/*	function init(){
				score = 0;
				d = "right";
				if(typeof game_loop != "undefined") clearInterval(game_loop);
				if(gOver) game_over();
				else game_loop = setInterval(paint, speed);
			}
				init();
		 */
		//setTimeout(paint(),450);

		if(gOver) game_over();
		if(matchStatus == "starting") game_starting();
		paint();
		function paint(){
			var recX1;
			var recY1;
			var recX2;
			var recY2;
			var length_min = 0;
			var whoIsLonger;
			var longerCoordinates;
			var longerColor;
			var longerRecX;
			var longerRecY;
			//changed_direction = false;
			//Paint the canvas
			ctx.fillStyle = "black";
			ctx.fillRect(0,0,w,h);
			ctx.strokeStyle = "white";
			ctx.strokeRect(0,0,w,h);

			if(coordinates1.length < coordinates2.length){
				length_min = coordinates1.length;
				whoIsLonger = 2;
				longerColor = "yellow";
			}
			else {
				length_min = coordinates2.length;
				whoIsLonger = 1;
				longerColor = "green";
			}

			for(var i=length_min-1; i>= 0; i--){
				recX1 = coordinates1[i].x;
				recY1 = coordinates1[i].y;
				recX2 = coordinates2[i].x;
				recY2 = coordinates2[i].y;

				if(recX1==recX2 && recY1==recY2)
					paint_cell(recX1, recY1, "blue");
				else {
					paint_cell(recX1, recY1, "green");
					paint_cell(recX2, recY2, "yellow");
				}
				if(whoIsLonger==1) longerCoordinates=message.player1.coordinates;
				else longerCoordinates=message.player2.coordinates;

				
			}
			/*	for(i=0; i< coordinates2.length; i++){
				recX = coordinates2[i].x;
				recY = coordinates2[i].y;
				paint_cell(recX, recY, "yellow");
			}*/
			for(var j=length_min; j<longerCoordinates.length; j++){
				console.log('x');
				longerRecX = longerCoordinates[j].x;
				longerRecY = longerCoordinates[j].y;
				paint_cell(longerRecX, longerRecY, longerColor);				
			}
			//paint food:
			paint_cell(food.coord.x,food.coord.y,"red");
			//console.log('food: ' + food.coord.x + '.' + food.coord.y);
		}

		function paint_cell(x,y, cell_color){
			ctx.fillStyle = cell_color;
			ctx.fillRect(x*cw,y*cw,cw,cw);
			ctx.strokeStyle = "white";
			ctx.strokeRect(x*cw,y*cw,cw,cw);
		}
		function pause_game() {
			window.clearTimeout(game_loop);
			paused = true;
			$('#paused').fadeIn(300);
			$('#canvas').css('visibility','hidden');
		}

		function resume_game() {
			game_loop = setInterval(paint, speed);
			paused = false;
			$('#paused').fadeOut(300);
			$('#canvas').css('visibility','visible');
		}
		function game_over() {
			if(winner == 1){
				document.getElementById('over').innerHTML = '<h1>Game Over</h1>Winner is ' + message.player1.name + '!';
				p1wins++;
				document.getElementById('player1-score').innerHTML = message.player1.name + '<br>' + p1wins;
			} else if(winner == 2){
				document.getElementById('over').innerHTML = '<h1>Game Over</h1>Winner is ' + message.player2.name + '!';
				p2wins++;
				document.getElementById('player2-score').innerHTML = message.player2.name + '<br>' + p2wins;			
			} else if(winner == 3){
				document.getElementById('over').innerHTML = '<h1>Game Over</h1>DRAW !';
				draws++;
				document.getElementById('draws-score').innerHTML = 'DRAWS<br>' + draws;			
			}
			$('#game_over').fadeIn(300);
			//	$('#play_again').fadeIn(300);
		}

		function game_starting(){
			$('#starting').fadeIn(100);
			countdown();
		}

		function countdown(){
			displayTime();
			if (count > 0) {
				setTimeout(countdown, 1000);
				count--;
			} else {
				$('#starting').fadeOut(100);
				count = 3;
			}
		}

		function displayTime() { 
			if (count > 0)
				document.getElementById('start').innerHTML = '<span class="countdown" id="starting">' + count + '</span>'; //Starting in
			else
				document.getElementById('start').innerHTML = '<span class="countdown" id="starting">' + "GO!" + '</span>';
		}

	};
	ws.onclose = function(){
		ws.close();
	};
	$("#rematch").click(function(){
		rematch();
	});

	function rematch() {
		$('#game_over').fadeOut(300);
		//	$('#play_again').fadeOut(300);
		var Playerfrom;
		if (thisPlayer == "player1") Playerfrom = "1";
		else if (thisPlayer == "player2") Playerfrom = "2";
		var to = "server";
		var from = Playerfrom;

		var msg = {
				"from" : Playerfrom,
				"to" : "server",
				"content" : "rematch",
				"mode" : game_mode 
		}
		var json = JSON.stringify(msg);
		ws.send(json);
		//console.log(msg);
	}

	$("#exit_game").click(function(){
		ws.close();
	});

	$(document).keydown(function(e){
		var Playerfrom;
		if (thisPlayer == "player1") Playerfrom = "1";
		else if (thisPlayer == "player2") Playerfrom = "2";
		var to = "server";
		var from = Playerfrom;
		var content = d;
//		var d;
		var next_d = "";
		var key = e.which;
//		if (thisPlayer == "player1") d = message.player1.moves[message.player1.moves.length-1];
//		if (thisPlayer == "player2") d = message.player2.moves[message.player2.moves.length-1];
		if(changed_direction){
			if(key == "37") {
				next_d = "left";
			}
			else if(key == "38") {
				next_d = "up";
			}
			else if(key == "39") {
				next_d = "right";
			}
			else if(key == "40"){
				next_d = "down";
			}
			else if(key == "32") {
				if(paused == false) pause_game();
				else resume_game();
			}			var msg = {
					 "from" : Playerfrom,
					 "to" : "server",
					 "content" : d,
					 "nextDirection" : next_d
			 }
			 var json = JSON.stringify(msg);
			 ws.send(json);
			// console.log(msg);
		} else {
			if(key == "37" && d != "right" && changed_direction == false) {
				d = "left";
				changed_direction = true;
			}
			else if(key == "38" && d != "down" && changed_direction == false) {
				d = "up";
				changed_direction = true;
			}
			else if(key == "39" && d != "left" && changed_direction == false) {
				d = "right";
				changed_direction = true;
			}
			else if(key == "40" && d != "up" && changed_direction == false){
				d = "down";
				changed_direction = true;
			}
			var msg = {
					"from" : Playerfrom,
					"to" : "server",
					"content" : d
			}
			var json = JSON.stringify(msg);
			ws.send(json);
			//console.log(msg);
		}
	});

}
$("#playerName").click(function(){
	connect();
	$("#insert_data").css('visibility','hidden');
});
$("#playVsBot").click(function(){
	game_mode = 1; // 0 = play vs other player; 1 = play vs computer;
	connect();
	$("#insert_data").css('visibility','hidden');
});