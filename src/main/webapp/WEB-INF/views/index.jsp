<html>
<head>
<title>Snake Game</title>
<link rel="stylesheet" href="css/style.css" type="text/css" />
</head>
<body>
	<div class="container">
		<div id="overlay" class="hwindow">
			Your Final Score: <span id="final_score"></span> <br /> <a
				onclick="window.location.reload()" href="#">Click to play again</a>
		</div>
		<div id="paused" class="hwindow">
			<h1>Game is paused</h1>
			<br />
			<h4>Press space to continue</h4>
		</div>
		<div id="over" class="hwindow">
			<h1>Game Over</h1>
			<h2>Play again?</h2>
			<button id="rematch">Yes</button>
			<button id="exit_game">No</button>
		</div>
		<canvas id="canvas" width="600" height="400">
      Sorry, your browser does not support canvas element
    </canvas>
    <div id="starting" class="hwindow">
    </div>
		<div id="stats">
			<div id="current-score" class="score"></div>
			<div id="high-score" class="score"></div>
			<div id="snake-size" class="score"></div>
		</div>
		<div class="test">
			<div id="insert_data">
			Enter your name: <input type="text" id="PlayerOneName"
				name="PlayerOneName">
			<button id="playerName">Ready</button>
			</div>
			<div id="div1" class="test" class="divPlayer">
				Player 1: <label id="lblPlayerOneName"> </label>
			</div>
			<div id="div2" class="test" class="divPlayer">
				Player 2: <label id="lblPlayerTwoName"> </label>
			</div>
		</div>
	</div>
	<script
		src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"
		type="text/javascript"></script>
	<script src="js/script.js"></script>
</body>
</html>
