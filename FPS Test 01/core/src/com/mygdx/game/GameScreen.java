package com.mygdx.game;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.math.*;

public class GameScreen extends BaseScreen
{
    private PhysicsActor mousey;
    private BaseActor cheese;
    private BaseActor floor;
    private Image winImage;
    private TextButton startButton;
    private boolean win;

    private float timeElapsed;
    private Label timeLabel;

    // game world dimensions
    final int mapWidth = 1000;
    final int mapHeight = 1000;
    
    WinMenu winMenu;

    public GameScreen(BaseGame g)
    {
        super(g);
    }

    public void create() 
    {        
        timeElapsed = 0;

        floor = new BaseActor();
        floor.setTexture( new Texture(Gdx.files.internal("assets/tiles-1000-1000.jpg")) );
        floor.setPosition( 0, 0 );
        mainStage.addActor( floor );

        cheese = new BaseActor();
        cheese.setTexture( new Texture(Gdx.files.internal("assets/cheese.png")) );
        cheese.setPosition( 400, 300 );
        cheese.setOrigin( cheese.getWidth()/2, cheese.getHeight()/2 );
        cheese.setEllipseBoundary();
        mainStage.addActor( cheese );

        mousey = new PhysicsActor();

        mousey.setPosition( 20, 20 );
        mousey.setMaxSpeed(1000);
        mousey.setDeceleration(2000);

        TextureRegion[] frames = new TextureRegion[4];
        for (int n = 0; n < 4; n++)
        {   
            String fileName = "assets/mouse" + n + ".png";
            Texture tex = new Texture(Gdx.files.internal(fileName));
            tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            frames[n] = new TextureRegion( tex );
        }
        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);

        Animation<TextureRegion> anim = new Animation<TextureRegion>(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

        mousey.storeAnimation( "walk", anim );

        Texture mouseTex = new Texture(Gdx.files.internal("assets/mouse0.png"));
        mouseTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        mousey.storeAnimation( "stop", mouseTex );

        mousey.setOrigin( mousey.getWidth()/2, mousey.getHeight()/2 );
        mousey.setAutoAngle(true);
        mousey.setEllipseBoundary();
        mainStage.addActor(mousey);

        ////////////////////
        // USER INTERFACE //
        ////////////////////

        Texture winTex = new Texture(Gdx.files.internal("assets/you-win.png"), true);
        winTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        winImage = new Image( winTex );
        winImage.setVisible(false);

        timeLabel = new Label( "Time: --", game.skin, "uiLabelStyle" );

        startButton = new TextButton("Continue", game.skin, "uiTextButtonStyle");

        uiTable.pad(10);
        uiTable.add().expandX();
        uiTable.add(timeLabel);
        uiTable.row();
        uiTable.add(winImage).colspan(2).padTop(50);
        uiTable.row();
        uiTable.add().colspan(2).expandY();
        uiTable.row();
        uiTable.add(startButton).colspan(2);

        
        startButton.addListener(
        	new InputListener()
            {
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) 
                    {  return true;  }  // continue processing?

                public void touchUp (InputEvent event, float x, float y, int pointer, int button) 
                {  
                	game.phase++;
                    	
                	System.out.println(game.phase);
                	if (game.phase == 5)
                		winMenu = new WinMenu(game, "Continue");
                	else if (game.phase == 9)
                		winMenu = new WinMenu(game, "Continue");
                	else if (game.phase == 13)
                		winMenu = new WinMenu(game, "Continue");
                	else if (game.phase == 17)
                    	winMenu = new WinMenu(game, "Continue");
                    	
                    game.setScreen( winMenu );
                }
            }
        );

        startButton.setVisible(false);
        
        
        win = false;
    }

    public void update(float dt) 
    {   
        mousey.setAccelerationXY(0,0);
        if (Gdx.input.isKeyPressed(Keys.LEFT)) 
            mousey.addAccelerationXY(-500,0);
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            mousey.addAccelerationXY(500,0);
        if (Gdx.input.isKeyPressed(Keys.UP)) 
            mousey.addAccelerationXY(0,500);
        if (Gdx.input.isKeyPressed(Keys.DOWN)) 
            mousey.addAccelerationXY(0,-500);

        // set correct animation
        if ( mousey.getSpeed() > 1 && mousey.getAnimationName().equals("stop") )
            mousey.setActiveAnimation("walk");
        if ( mousey.getSpeed() < 1 && mousey.getAnimationName().equals("walk") )
            mousey.setActiveAnimation("stop");

        // bound mousey to the rectangle defined by mapWidth, mapHeight
        mousey.setX( MathUtils.clamp( mousey.getX(), 0,  mapWidth - mousey.getWidth() ));
        mousey.setY( MathUtils.clamp( mousey.getY(), 0,  mapHeight - mousey.getHeight() ));

        // check win condition: mousey must be overlapping cheese
        if ( !win && cheese.overlaps( mousey, true ) )
        {
            win = true;

            Action spinShrinkFadeOut = Actions.parallel(
                    Actions.alpha(1),         // set transparency value
                    Actions.rotateBy(360, 1), // rotation amount, duration
                    Actions.scaleTo(0,0, 2),  // x amount, y amount, duration
                    Actions.fadeOut(1)        // duration of fade in
                );
            cheese.addAction( spinShrinkFadeOut );

            Action fadeInColorCycleForever = Actions.sequence( 
                    Actions.alpha(0),   // set transparency value
                    Actions.show(),     // set visible to true
                    Actions.fadeIn(2),  // duration of fade out
                    Actions.forever(    
                        Actions.sequence(
                            // color shade to approach, duration
                            Actions.color( new Color(1,0,0,1), 1 ),
                            Actions.color( new Color(0,0,1,1), 1 )
                        )
                    )
                );
            winImage.addAction( fadeInColorCycleForever );

            startButton.setVisible(true);
        }

        if (!win)
        {
            timeElapsed += dt;
            timeLabel.setText( "Time: " + (int)timeElapsed );
        }

        // camera adjustment
        Camera cam = mainStage.getCamera();

        // center camera on player
        cam.position.set( mousey.getX() + mousey.getOriginX(), mousey.getY() + mousey.getOriginY(), 0 );

        // bound camera to layout
        cam.position.x = MathUtils.clamp(cam.position.x, viewWidth/2,  mapWidth - viewWidth/2);
        cam.position.y = MathUtils.clamp(cam.position.y, viewHeight/2, mapHeight - viewHeight/2);
        cam.update();
    }

    // InputProcessor methods for handling discrete input
    public boolean keyDown(int keycode)
    {
        if (keycode == Keys.P)    
            togglePaused(); 

        return false;
    }
}