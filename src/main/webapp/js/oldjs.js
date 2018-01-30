$(document).ready(function(){
//	alert('a');
	var canvas = $('#canvas')[0];
	var ctx = canvas.getContext("2d");
	var w = $('#canvas').width();
	var h = $('#canvas').height();
	var cw = 15;      //cell width
	var d = "right";  //direction
	var food;
	var score;
	var color = "green";
	var speed = 130;
	var paused = false;
	//Snake array
	var snake_arr;
	var changed_direction;
	var next_d = undefined;

	//Initializer
	function init(){
		create_snake();
		create_food();
		score = 0;
		d = "right";
		if(typeof game_loop != "undefined") clearInterval(game_loop);
		game_loop = setInterval(paint, speed);
	}
	init();

	//Create snake
	function create_snake(){
		var length = 5;
		snake_arr = [];
		for(var i = length-1; i >= 0; i--){
			snake_arr.push({x: i, y: 0});
		}
	}

	//Create food
	function create_food(){
		food = {
				x:Math.floor(Math.random() * (w-cw)/cw),
				y:Math.floor(Math.random() * (h-cw)/cw)
		}
	}


	function paint(){
		changed_direction = false;
		//Paint the canvas
		ctx.fillStyle = "black";
		ctx.fillRect(0,0,w,h);
		ctx.strokeStyle = "white";
		ctx.strokeRect(0,0,w,h);

		//variables of current snake position
		var nx = snake_arr[0].x;
		var ny = snake_arr[0].y;

		//direction movementelse {
		if(d == "right")      nx++;
		else if (d == "left") nx--;
		else if (d == "up")   ny--;
		else if (d == "down") ny++;

		//Check for collisions
		if( nx < 0 || nx >= w/cw || ny < 0 || ny >= h/cw || check_collision(nx,ny,snake_arr) ){
			//Final Score:
			$('#final_score').html(score);
			//show overlay:
			$('#overlay').fadeIn(300);
			//  init();
			clearInterval(game_loop);
			return;
		}

		paint_cell(food.x, food.y, "yellow");

		for(var i =0; i < snake_arr.length; i++){
			var c = snake_arr[i];
			paint_cell(c.x,c.y, "green");
		}


		if(nx == food.x && ny == food.y) {
			var tail = {x:nx, y: ny};
			score++;
			var f = snake_arr[snake_arr.length-1];
			paint_cell(f.x,f.y, "red");
			//create new food
			create_food();
		} else {
			var tail = snake_arr.pop();
			tail.x = nx;
			tail.y = ny;
		}

		snake_arr.unshift(tail);



		if (next_d != undefined){
			d = next_d;
			next_d = undefined;
		}
		//Check score
		check_score(score);
		//display score:
		$('#current-score').html('Your score: ' + score);
		$('#snake-size').html('Snake size: ' + snake_arr.length);
	}

	function paint_cell(x,y, cell_color){
		ctx.fillStyle = cell_color;
		ctx.fillRect(x*cw,y*cw,cw,cw);
		ctx.strokeStyle = "white";
		ctx.strokeRect(x*cw,y*cw,cw,cw);
	}

	function check_collision(x, y, array){
		for(var i = 0; i < array.length; i++){
			if(array[i].x == x && array[i].y == y)
				return true;
		}

		return false;
	}

	//Keyboard controller
	$(document).keydown(function(e){
		var key = e.which;
		//alert(key);   //37 = left arrow key
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
		else if(key == "37" && d != "right") {
			next_d = "left";
		}
		else if(key == "38" && d != "down") {
			next_d = "up";
		}
		else if(key == "39" && d != "left") {
			next_d = "right";
		}
		else if(key == "40" && d != "up"){
			next_d = "down";
		}
		else if(key == "32") {
			if(paused == false) pause_game();
			else resume_game();
		}
	});

	function check_score(score) {
		//Local storage variables!! (HTML5)
		if(localStorage.getItem('highScore') == null) {
			//If there is no high score:
			localStorage.setItem('highScore', score);
		} else {
			//if there is high score:
			if(score > localStorage.getItem('highScore')) {
				localStorage.setItem('highScore', score);
			}
		}
		$('#high-score').html('High Score: ' + localStorage.highScore);

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

});

function reset_score(){
	localStorage.highScore = 0;
//	highscorediv = document.getElementById('high-score');
//	highscorediv = innerHTML = 'High Score: 0';
}



$("#playerName").click(function(evt){
	var player = { 
			"id" : "1",
			"name" : $("#PlayerOneName").val()
			}
	//evt.preventDefault();
	$.ajax({
		//	headers: { 
		//		'Accept': 'application/json',
		//		'Content-Type': 'application/json' 
		//	},
				url: "playerjson",
				type: "POST",
				contentType: "application/json",
				async: true, 
				data: JSON.stringify(player),
				//dataType: "json",
				success: function(data) {
					//alert('data: ' + data);
					//alert('player: ' + data[0]);
					//alert('player name: ' + data[0].name);
						//var jsonName = data.name;
						//$("#div" + i).html(data[i].name);
					$(".divPlayer").each(function(){
						alert('come on;')
						//html(data[i].name);
						//console.log(data[i].name);
						//document.getElementById("div1").innerHTML = data[i].name;
					});
					//console.log(jsonName);
				},
				error:function(data) {
					console.log(data); 
				}
			});
});