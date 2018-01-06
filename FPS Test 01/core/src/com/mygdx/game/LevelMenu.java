package com.mygdx.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;

public class LevelMenu extends BaseScreen
{
	GameScreen gameScreen;
	String startText;
	
    public LevelMenu(BaseGame g, String startText)
    {
        super(g);
        this.startText = startText;
    }

    public void create() 
    {      
        Texture bgTex = new Texture(Gdx.files.internal("assets/tiles-1000-1000.jpg"), true);
        bgTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        game.skin.add( "bgTex", bgTex );
        uiTable.background( game.skin.getDrawable("bgTex") );

        TextButton startButton = new TextButton(startText, game.skin, "uiTextButtonStyle");
        startButton.addListener(
            new InputListener()
            {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 
                {  return true;  }  // continue processing?

                public void touchUp (InputEvent event, float x, float y, int pointer, int button) 
                {  
                	game.phase++;
                	
                	System.out.println(game.phase);
                	if (game.phase == 4)
                		gameScreen = new GameScreen(game);
                	else if (game.phase == 8)
                		gameScreen = new GameScreen(game);
                	else if (game.phase == 12)
                		gameScreen = new GameScreen(game);
                	else if (game.phase == 16)
                		gameScreen = new GameScreen(game);
                	
                    game.setScreen( gameScreen );
                }
            });

        uiTable.add(startButton);
    }

    public void update(float dt) 
    {   

    }
}