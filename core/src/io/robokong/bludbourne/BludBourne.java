package io.robokong.bludbourne;

import com.badlogic.gdx.Game;
import io.robokong.bludbourne.screens.MainGameScreen;

public class BludBourne extends Game {
    public static final MainGameScreen _mainGameScreen = new
            MainGameScreen();
    @Override
    public void create(){
        setScreen(_mainGameScreen);
    }
    @Override
    public void dispose(){
        _mainGameScreen.dispose();
    }
}
