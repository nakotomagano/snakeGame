<html>
<head>
  <title>Snake Game</title>
  <link rel="stylesheet" href="css/style.css" type="text/css" />
</head>
<body>
  <div class="container">
    <div id="overlay" class="hwindow">
      Your Final Score: <span id="final_score"></span>
      <br />
      <a onclick="window.location.reload()" href="#">Click to play again</a>
    </div>
    <div id="paused" class="hwindow">
      <h1>Game is paused</h1>
      <br />
      <h4>Press space to continue</h4>
    </div>
    <canvas id="canvas" width="600" height="400">
      Sorry, your browser does not support canvas element
    </canvas>
    <div id="stats">
      <div id="current-score" class="score"></div>
      <div id="high-score" class="score"></div>
      <div id="snake-size" class="score"></div>
      <button onclick="reset_score()" id="reset_score">Reset High Score</button>
    </div>
    aaa
  </div>
</body>
</html>
