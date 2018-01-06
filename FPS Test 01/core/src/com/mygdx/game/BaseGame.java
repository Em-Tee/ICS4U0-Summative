package com.mygdx.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class BaseGame extends Game
{
    // used to store resources common to multiple screens
    Skin skin;
	int phase;
    
    public BaseGame()
    {
        skin = new Skin();
        phase = 0;
    }
    
    public abstract void create();

    public void dispose()
    {
        skin.dispose();
    }
}