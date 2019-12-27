package io.robokong.bludbourne;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BludBourne extends Game {
    public static final MainGameScreen mainGameScreen = new MainGameScreen();

    @Override
    public void create() {
        setScreen(mainGameScreen);
    }

    @Override
    public void dispose() {
        mainGameScreen.dispose();
    }
}
