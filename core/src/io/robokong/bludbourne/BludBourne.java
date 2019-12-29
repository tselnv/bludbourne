package io.robokong.bludbourne;

import com.badlogic.gdx.Game;
import io.robokong.bludbourne.screens.MainGameScreen;

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
