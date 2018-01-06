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

public class WinMenu extends BaseScreen
{
	SelectMenu selectMenu;
	String startText;
	
    public WinMenu(BaseGame g, String startText)
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
					if (game.phase == 6)
                		selectMenu = new SelectMenu(game);
                	else if (game.phase == 10)
                		selectMenu = new SelectMenu(game);
                	else if (game.phase == 14)
                		selectMenu = new SelectMenu(game);
                	else if (game.phase == 18)
                		selectMenu = new SelectMenu(game);
                	
                    game.setScreen( selectMenu );
                }
            });

        uiTable.add(startButton);
    }

    public void update(float dt) 
    {   

    }
}