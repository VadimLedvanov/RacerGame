package com.javarush.games.racer;

import com.javarush.engine.cell.*;
import com.javarush.games.racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2;
    public static final int ROADSIDE_WIDTH = 14;
    private RoadMarking roadMarking;
    private PlayerCar player;

    private boolean isGameStopped;
    private RoadManager roadManager;
    private FinishLine finishLine;
    private static final int RACE_GOAL_CARS_COUNT = 1;
    private ProgressBar progressBar;
    private int score;
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.DARKGOLDENROD, "SUPER GOOD", Color.LIGHTCYAN, 48);
        stopTurnTimer();
    }

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        showGrid(false);
        createGame();
    }

    private void createGame() {
        score = 3500;
        setScore(score);
        isGameStopped = false;
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        drawScene();
        setTurnTimer(40);
    }
    private void drawScene() {

        drawField();
        roadManager.draw(this);
        roadMarking.draw(this);
        player.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }

    private void drawField() {
        for (int x = 0;x < WIDTH ; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x >= ROADSIDE_WIDTH && x < (WIDTH - ROADSIDE_WIDTH))
                    setCellColor(x, y, Color.DIMGRAY);
            }
        }

        for (int y = 0; y < HEIGHT; y++) {
            setCellColor(CENTER_X, y, Color.WHITE);
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x < ROADSIDE_WIDTH || x >= WIDTH - ROADSIDE_WIDTH)
                    setCellColor(x, y, Color.ORANGE);
            }
        }
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
            return;
        super.setCellColor(x, y, color);
    }

    private void moveAll() {
        roadMarking.move(player.speed);
        roadManager.move(player.speed);
        player.move();
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    @Override
    public void onTurn(int step) {
        if (roadManager.checkCrush(player)){
            gameOver();
            drawScene();
            return;
        }
        roadManager.generateNewRoadObjects(this);
        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT)
            finishLine.show();
        if (finishLine.isCrossed(player)) {
            win();
            drawScene();
            return;
        }
        score -= 5;
        setScore(score);
        moveAll();
        drawScene();
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.RIGHT)
            player.setDirection(Direction.RIGHT);
        else if (key == Key.LEFT)
            player.setDirection(Direction.LEFT);
        else if (key == Key.SPACE && isGameStopped)
            createGame();
        else if (key == Key.UP)
            player.speed = 2;
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.RIGHT && player.getDirection() == Direction.RIGHT)
            player.setDirection(Direction.NONE);
        else if (key == Key.LEFT && player.getDirection() == Direction.LEFT)
            player.setDirection(Direction.NONE);
        else if (key == Key.UP)
            player.speed = 1;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.TOMATO, "YOU'RE BAD DRIVER",Color.LIGHTSTEELBLUE, 50);
        stopTurnTimer();
        player.stop();
    }
}
