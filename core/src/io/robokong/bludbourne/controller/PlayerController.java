package io.robokong.bludbourne.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.robokong.bludbourne.Entity;
import io.robokong.bludbourne.MapManager;

import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor {
    private final static String TAG = PlayerController.class.getSimpleName();

    enum Keys {
        LEFT, RIGHT, UP, DOWN, QUIT
    }

    enum Mouse{
        SELECT, DOACTION
    }

    private static Map<Keys, Boolean> keys = new HashMap<PlayerController.Keys, Boolean>();
    private static Map<Mouse, Boolean> mouseButtons = new HashMap<PlayerController.Mouse, Boolean>();
    private Vector3 lastMouseCoordinates;

    //initialize hashmap for inputs
    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.QUIT, false);
    }

    static {
        mouseButtons.put(Mouse.SELECT, false);
        mouseButtons.put(Mouse.DOACTION, false);
    }

    private Entity _player;

    public PlayerController(Entity player) {
        this.lastMouseCoordinates = new Vector3();
        this._player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if ( keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
            this.leftPressed();
        }
        if ( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
            this.rightPressed();
        }
        if ( keycode == Input.Keys.UP || keycode == Input.Keys.W){
            this.upPressed();
        }
        if ( keycode == Input.Keys.DOWN || keycode == Input.Keys.S){
            this.downPressed();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if ( keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
            this.leftReleased();
        }
        if ( keycode == Input.Keys.RIGHT || keycode == Input.Keys.D){
            this.rightReleased();
        }
        if ( keycode == Input.Keys.UP || keycode == Input.Keys.W){
            this.upReleased();
        }
        if ( keycode == Input.Keys.DOWN || keycode == Input.Keys.S){
            this.downReleased();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if( button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT){
            this.setClickedMouseCoordinates(screenX, screenY);
        }

        //left is selection, right is context menu
        if(button == Input.Buttons.LEFT){
            this.selectMouseButtonPressed(screenX, screenY);
        }
        if(button == Input.Buttons.RIGHT){
            this.doActionMouseButtonPressed(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //left is selection, right is context menu
        if(button == Input.Buttons.LEFT){
            this.selectMouseButtonReleased(screenX, screenY);
        }
        if(button == Input.Buttons.RIGHT){
            this.doActionMouseButtonReleased(screenX, screenY);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void dispose(){
    }

    public void update(float delta) {
        processInput(delta);
    }

    private void processInput(float delta) {
        //Keyboard input
        if( keys.get(Keys.LEFT)){
            _player.calculateNextPosition(Entity.Direction.LEFT, delta);
            _player.setState(Entity.State.WALKING);
            _player.setDirection(Entity.Direction.LEFT, delta);
        } else if( keys.get(Keys.RIGHT)){
            _player.calculateNextPosition(Entity.Direction.RIGHT, delta);
            _player.setState(Entity.State.WALKING);
            _player.setDirection(Entity.Direction.RIGHT, delta);
        } else if( keys.get(Keys.UP)){
            _player.calculateNextPosition(Entity.Direction.UP, delta);
            _player.setState(Entity.State.WALKING);
            _player.setDirection(Entity.Direction.UP, delta);
        } else if( keys.get(Keys.DOWN)){
            _player.calculateNextPosition(Entity.Direction.DOWN, delta);
            _player.setState(Entity.State.WALKING);
            _player.setDirection(Entity.Direction.DOWN, delta);
        } else if( keys.get(Keys.QUIT)){
            Gdx.app.exit();
        } else {
            _player.setState(Entity.State.IDLE);
        }
        //Mouse input
        if( mouseButtons.get(Mouse.SELECT)){
            mouseButtons.put(Mouse.SELECT, false);
        }
    }

    private void leftPressed() {
        keys.put(Keys.LEFT, true);
    }

    private void rightPressed() {
        keys.put(Keys.RIGHT, true);
    }

    private void upPressed() {
        keys.put(Keys.UP, true);
    }

    private void downPressed() {
        keys.put(Keys.DOWN, true);
    }

    private void quitPressed() {
        keys.put(Keys.QUIT, true);
    }

    private void leftReleased() {
        keys.put(Keys.LEFT, false);
    }

    private void rightReleased() {
        keys.put(Keys.RIGHT, false);
    }

    private void upReleased() {
        keys.put(Keys.UP, false);
    }

    private void downReleased() {
        keys.put(Keys.DOWN, false);
    }

    private void quitReleased() {
        keys.put(Keys.QUIT, false);
    }

    private void doActionMouseButtonPressed(int screenX, int screenY) {
        mouseButtons.put(Mouse.DOACTION, true);
    }

    private void selectMouseButtonPressed(int x, int y) {
        mouseButtons.put(Mouse.SELECT, true);
    }

    private void doActionMouseButtonReleased(int screenX, int screenY) {
        mouseButtons.put(Mouse.DOACTION, false);
    }

    private void selectMouseButtonReleased(int x, int y) {
        mouseButtons.put(Mouse.SELECT, false);
    }

    private void setClickedMouseCoordinates(int x, int y) {
        lastMouseCoordinates.set(x,y,0);
    }



}
