(() => {
  const canvas = document.getElementById("gameCanvas");
  const board = document.getElementById("gameBoard");
  const scoreEl = document.getElementById("score");
  const bestEl = document.getElementById("best");
  const livesEl = document.getElementById("lives");
  const startBtn = document.getElementById("startBtn");
  const pauseBtn = document.getElementById("pauseBtn");
  const resetBtn = document.getElementById("resetBtn");
  const overlay = document.getElementById("overlay");
  const overlayTitle = document.getElementById("overlayTitle");
  const overlayMessage = document.getElementById("overlayMessage");

  if (!canvas || !board) {
    return;
  }

  const ctx = canvas.getContext("2d");
  const storageKey = "starRunnerBestScore";

  const state = {
    width: 0,
    height: 0,
    running: false,
    paused: false,
    gameOver: false,
    score: 0,
    best: 0,
    lives: 3,
    time: 0,
    lastTime: 0,
    asteroidTimer: 0,
    starTimer: 0,
    invulnerableUntil: 0,
    bgStars: [],
  };

  const player = {
    width: 34,
    height: 40,
    x: 0,
    y: 0,
    speed: 360,
  };

  const asteroids = [];
  const stars = [];
  const keys = new Set();

  const clamp = (value, min, max) => Math.max(min, Math.min(max, value));
  const rand = (min, max) => Math.random() * (max - min) + min;

  const loadBest = () => {
    const stored = Number.parseInt(localStorage.getItem(storageKey), 10);
    state.best = Number.isFinite(stored) ? stored : 0;
  };

  const saveBest = () => {
    localStorage.setItem(storageKey, String(state.best));
  };

  const updateUi = () => {
    scoreEl.textContent = String(Math.floor(state.score));
    bestEl.textContent = String(state.best);
    livesEl.textContent = String(state.lives);
    pauseBtn.textContent = state.paused ? "Resume" : "Pause";
    pauseBtn.disabled = !state.running;
  };

  const showOverlay = (title, message) => {
    overlayTitle.textContent = title;
    overlayMessage.textContent = message;
    overlay.classList.add("visible");
  };

  const hideOverlay = () => {
    overlay.classList.remove("visible");
  };

  const createBackgroundStars = () => {
    const count = Math.max(40, Math.floor((state.width * state.height) / 9000));
    state.bgStars = Array.from({ length: count }, () => ({
      x: Math.random() * state.width,
      y: Math.random() * state.height,
      r: rand(0.5, 1.6),
      speed: rand(8, 26),
      alpha: rand(0.35, 0.9),
    }));
  };

  const resize = () => {
    const rect = board.getBoundingClientRect();
    const scale = window.devicePixelRatio || 1;
    canvas.width = rect.width * scale;
    canvas.height = rect.height * scale;
    canvas.style.width = `${rect.width}px`;
    canvas.style.height = `${rect.height}px`;
    ctx.setTransform(scale, 0, 0, scale, 0, 0);
    state.width = rect.width;
    state.height = rect.height;
    player.y = state.height - player.height - 18;
    player.x = clamp(player.x, 12, state.width - player.width - 12);
    createBackgroundStars();
    render();
  };

  const resetGame = () => {
    state.running = false;
    state.paused = false;
    state.gameOver = false;
    state.score = 0;
    state.lives = 3;
    state.time = 0;
    state.asteroidTimer = 0;
    state.starTimer = 0;
    state.invulnerableUntil = 0;
    asteroids.length = 0;
    stars.length = 0;
    player.x = (state.width - player.width) / 2;
    player.y = state.height - player.height - 18;
    updateUi();
    showOverlay("Star Runner", "Press Start or Space to play.");
    render();
  };

  const startGame = () => {
    if (state.running && !state.paused) {
      return;
    }
    if (state.gameOver) {
      resetGame();
    }
    state.running = true;
    state.paused = false;
    state.lastTime = performance.now();
    hideOverlay();
    updateUi();
    requestAnimationFrame(loop);
  };

  const togglePause = () => {
    if (!state.running) {
      return;
    }
    state.paused = !state.paused;
    updateUi();
    if (state.paused) {
      showOverlay("Paused", "Press P or Pause to resume.");
      return;
    }
    hideOverlay();
    state.lastTime = performance.now();
    requestAnimationFrame(loop);
  };

  const endGame = () => {
    state.running = false;
    state.paused = false;
    state.gameOver = true;
    const finalScore = Math.floor(state.score);
    if (finalScore > state.best) {
      state.best = finalScore;
      saveBest();
    }
    updateUi();
    showOverlay(
      "Game Over",
      `Score ${finalScore}. Press R or Start to try again.`
    );
  };

  const circleRectCollision = (circle, rect) => {
    const closestX = clamp(circle.x, rect.x, rect.x + rect.width);
    const closestY = clamp(circle.y, rect.y, rect.y + rect.height);
    const dx = circle.x - closestX;
    const dy = circle.y - closestY;
    return dx * dx + dy * dy <= circle.r * circle.r;
  };

  const updatePlayer = (dt) => {
    let direction = 0;
    if (keys.has("ArrowLeft") || keys.has("KeyA")) {
      direction -= 1;
    }
    if (keys.has("ArrowRight") || keys.has("KeyD")) {
      direction += 1;
    }
    player.x += (direction * player.speed * dt) / 1000;
    player.x = clamp(player.x, 10, state.width - player.width - 10);
  };

  const spawnAsteroid = (difficulty) => {
    const radius = rand(12, 26);
    const minX = radius + 8;
    const maxX = Math.max(minX, state.width - radius - 8);
    asteroids.push({
      x: rand(minX, maxX),
      y: -radius,
      r: radius,
      speed: rand(140, 240) + difficulty * 45,
    });
  };

  const spawnStar = (difficulty) => {
    const radius = rand(8, 14);
    const minX = radius + 8;
    const maxX = Math.max(minX, state.width - radius - 8);
    stars.push({
      x: rand(minX, maxX),
      y: -radius,
      r: radius,
      speed: rand(120, 180) + difficulty * 20,
    });
  };

  const updateEntities = (dt) => {
    const delta = dt / 1000;
    asteroids.forEach((asteroid) => {
      asteroid.y += asteroid.speed * delta;
    });
    stars.forEach((star) => {
      star.y += star.speed * delta;
    });

    for (let i = asteroids.length - 1; i >= 0; i -= 1) {
      if (asteroids[i].y - asteroids[i].r > state.height + 20) {
        asteroids.splice(i, 1);
      }
    }

    for (let i = stars.length - 1; i >= 0; i -= 1) {
      if (stars[i].y - stars[i].r > state.height + 20) {
        stars.splice(i, 1);
      }
    }
  };

  const updateBackground = (dt) => {
    const delta = dt / 1000;
    state.bgStars.forEach((star) => {
      star.y += star.speed * delta;
      if (star.y > state.height + star.r) {
        star.y = -star.r;
        star.x = Math.random() * state.width;
      }
    });
  };

  const handleCollisions = () => {
    const playerRect = {
      x: player.x,
      y: player.y,
      width: player.width,
      height: player.height,
    };

    const now = performance.now();
    const invulnerable = now < state.invulnerableUntil;

    if (!invulnerable) {
      for (const asteroid of asteroids) {
        if (circleRectCollision(asteroid, playerRect)) {
          state.lives -= 1;
          state.invulnerableUntil = now + 1200;
          if (state.lives <= 0) {
            endGame();
          }
          break;
        }
      }
    }

    for (let i = stars.length - 1; i >= 0; i -= 1) {
      if (circleRectCollision(stars[i], playerRect)) {
        stars.splice(i, 1);
        state.score += 120;
      }
    }
  };

  const update = (dt) => {
    state.time += dt;
    const difficulty = Math.min(4, 1 + state.time / 20000);

    state.score += dt * 0.04 * difficulty;
    updateBackground(dt);
    updatePlayer(dt);

    state.asteroidTimer -= dt;
    if (state.asteroidTimer <= 0) {
      spawnAsteroid(difficulty);
      state.asteroidTimer = rand(300, 700) / difficulty;
    }

    state.starTimer -= dt;
    if (state.starTimer <= 0) {
      spawnStar(difficulty);
      state.starTimer = rand(1200, 2300);
    }

    updateEntities(dt);
    handleCollisions();
    updateUi();
  };

  const drawStarShape = (x, y, radius) => {
    const spikes = 5;
    const inner = radius * 0.45;
    let rotation = Math.PI / 2 * 3;
    let step = Math.PI / spikes;
    ctx.beginPath();
    ctx.moveTo(x, y - radius);
    for (let i = 0; i < spikes; i += 1) {
      ctx.lineTo(x + Math.cos(rotation) * radius, y + Math.sin(rotation) * radius);
      rotation += step;
      ctx.lineTo(x + Math.cos(rotation) * inner, y + Math.sin(rotation) * inner);
      rotation += step;
    }
    ctx.lineTo(x, y - radius);
    ctx.closePath();
    ctx.fill();
  };

  const drawBackground = () => {
    ctx.fillStyle = "#050814";
    ctx.fillRect(0, 0, state.width, state.height);
    ctx.fillStyle = "#0c1733";
    ctx.fillRect(0, 0, state.width, state.height);

    state.bgStars.forEach((star) => {
      ctx.globalAlpha = star.alpha;
      ctx.fillStyle = "#8fb9ff";
      ctx.beginPath();
      ctx.arc(star.x, star.y, star.r, 0, Math.PI * 2);
      ctx.fill();
    });
    ctx.globalAlpha = 1;
  };

  const drawPlayer = () => {
    const now = performance.now();
    const blinking = now < state.invulnerableUntil && Math.floor(now / 120) % 2 === 0;
    if (blinking) {
      ctx.globalAlpha = 0.5;
    }

    ctx.save();
    ctx.translate(player.x + player.width / 2, player.y + player.height / 2);
    ctx.fillStyle = "#5cc8ff";
    ctx.beginPath();
    ctx.moveTo(0, -player.height / 2);
    ctx.lineTo(player.width / 2, player.height / 2);
    ctx.lineTo(-player.width / 2, player.height / 2);
    ctx.closePath();
    ctx.fill();
    ctx.restore();
    ctx.globalAlpha = 1;
  };

  const drawAsteroids = () => {
    asteroids.forEach((asteroid) => {
      ctx.fillStyle = "#ff7a7a";
      ctx.beginPath();
      ctx.arc(asteroid.x, asteroid.y, asteroid.r, 0, Math.PI * 2);
      ctx.fill();
      ctx.strokeStyle = "rgba(0,0,0,0.2)";
      ctx.stroke();
    });
  };

  const drawStars = () => {
    stars.forEach((star) => {
      ctx.fillStyle = "#ffd36d";
      drawStarShape(star.x, star.y, star.r);
    });
  };

  const render = () => {
    drawBackground();
    drawStars();
    drawAsteroids();
    drawPlayer();
  };

  const loop = (timestamp) => {
    if (!state.running || state.paused) {
      return;
    }
    const dt = Math.min(40, timestamp - state.lastTime);
    state.lastTime = timestamp;
    update(dt);
    render();
    requestAnimationFrame(loop);
  };

  const handlePointer = (event) => {
    const rect = canvas.getBoundingClientRect();
    const x = event.clientX - rect.left;
    player.x = clamp(x - player.width / 2, 10, state.width - player.width - 10);
  };

  const setupControls = () => {
    window.addEventListener("keydown", (event) => {
      if (
        [
          "ArrowLeft",
          "ArrowRight",
          "ArrowUp",
          "ArrowDown",
          "Space",
          "KeyA",
          "KeyD",
        ].includes(event.code)
      ) {
        event.preventDefault();
      }

      if (event.code === "Space") {
        startGame();
      }
      if (event.code === "KeyP") {
        togglePause();
      }
      if (event.code === "KeyR") {
        resetGame();
      }
      keys.add(event.code);
    });

    window.addEventListener("keyup", (event) => {
      keys.delete(event.code);
    });

    board.addEventListener("pointerdown", (event) => {
      handlePointer(event);
      startGame();
    });

    board.addEventListener("pointermove", (event) => {
      if (event.buttons === 1) {
        handlePointer(event);
      }
    });

    board.addEventListener("pointerup", () => {
      keys.clear();
    });

    startBtn.addEventListener("click", startGame);
    pauseBtn.addEventListener("click", togglePause);
    resetBtn.addEventListener("click", resetGame);
    window.addEventListener("resize", resize);
  };

  const init = () => {
    loadBest();
    resize();
    setupControls();
    resetGame();
  };

  init();
})();
