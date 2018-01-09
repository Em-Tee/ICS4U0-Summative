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

/**
 * 
 * @author Muhammad
 *
 */
public class SelectMenu extends BaseScreen
{
	LevelMenu levelMenu;
	/**
	 * 
	 * @param g
	 * @param startText
	 */
    public SelectMenu(BaseGame g)
    {
        super(g);
    }

	public void create() 
    {      
        Texture bgTex = new Texture(Gdx.files.internal("assets/tiles-1000-1000.jpg"), true);
        bgTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        game.skin.add( "bgTex", bgTex );
        uiTable.background( game.skin.getDrawable("bgTex") );

        Texture libgdxTex = new Texture(Gdx.files.internal("assets/created-libgdx.png"), true);
        libgdxTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        Image libgdxImage = new Image( libgdxTex );

        TextButton startButton = new TextButton("Continue", game.skin, "uiTextButtonStyle");
        startButton.addListener(
            new InputListener()
            {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 
                {  return true;  }  // continue processing?

                public void touchUp (InputEvent event, float x, float y, int pointer, int button) 
                {  
                	game.phase++;
                	System.out.println(game.phase);
                	
                	if (game.phase == 3)
                		levelMenu = new LevelMenu(game, "Begin Level 1");
                	else if (game.phase == 7)
                		levelMenu = new LevelMenu(game, "Begin Level 2");
                	else if (game.phase == 11)
                		levelMenu = new LevelMenu(game, "Begin Level 3");
                	else if (game.phase == 15)
                		levelMenu = new LevelMenu(game, "Begin Level 4");
                	
                    game.setScreen( levelMenu );
                }
            });

        uiTable.add(startButton);
        uiTable.row();
        uiTable.add(libgdxImage).expandX().right();
    }

    public void update(float dt) 
    {   

    }
}