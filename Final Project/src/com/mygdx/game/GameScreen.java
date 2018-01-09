package com.mygdx.game;
import java.util.ArrayList;

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
    private PhysicsActor mousey;;
    private Car turtle;
    private Car cop;
    private BaseActor cheese;
    private ArrayList<BaseActor> blocks;
    private BaseActor floor;
    private Image winImage;
    private TextButton startButton;
    private boolean win;

    private float timeElapsed;
    private Label timeLabel;
    
    private int level;

    // game world dimensions
    final int mapWidth = 3000;
    final int mapHeight = 2000;
    
    WinMenu winMenu;

    public GameScreen(BaseGame g, int l)
    {
        super(g);
        level = l;
    }

    public void create() 
    {        
        timeElapsed = 0;

        floor = new BaseActor();
        floor.setTexture( new Texture(Gdx.files.internal("assets/tiles-1000-1000.jpg")) );

        int[] floorCoords = { 0,0, 1000,0, 2000,0, 0,1000, 1000,1000, 2000,1000 };
        for (int i = 0; i < 6; i++) {
        	BaseActor f = floor.clone();
        	f.setPosition( floorCoords[2*i], floorCoords[2*i+1] );
            mainStage.addActor( f );
        }
        
        BaseActor rock = new BaseActor();
        rock.setTexture( new Texture(Gdx.files.internal("assets/ninepatch-1.png")) );
        rock.setHeight(100);
        rock.setEllipseBoundary();

        blocks= new ArrayList<BaseActor>();
        int[] rockCoords = {400,0, 400,100, 400,200, 400,300, 400,400, 400,600, 400,700, 400,800
        		, 800,0, 800,100, 800,200, 800,300, 800,400, 800,500, 800,600, 800,700, 800,800
        		, 300,400, 200,400, 100,400, 0,400, 300,600, 200,600, 100,600, 0,600,};
        for (int i = 0; i < 25; i++)
        {
            BaseActor r = rock.clone();
            r.setPosition( rockCoords[2*i], rockCoords[2*i+1] );
            //mainStage.addActor( r );
            blocks.add( r );
        }
        

        cheese = new BaseActor();
        cheese.setTexture( new Texture(Gdx.files.internal("assets/cheese.png")) );
        cheese.setPosition( 600, 900 );
        cheese.setOrigin( cheese.getWidth()/2, cheese.getHeight()/2 );
        cheese.setEllipseBoundary();
        mainStage.addActor( cheese );

        /*
        mousey = new PhysicsActor();

        mousey.setPosition( 600, 20 );
        mousey.setMaxSpeed(1000);
        mousey.setDeceleration(2000);
        */
        
        cop = new Car();

        cop.setPosition( 0, 500 );
        cop.setMaxSpeed(500);
        cop.setDeceleration(8000);
        cop.setScale(0.5f);
        

        TextureRegion[] frames = new TextureRegion[4];
        for (int n = 0; n < 4; n++)
        {   
            String fileName = "assets/mouse" + n + ".png";
            Texture tex = new Texture(Gdx.files.internal(fileName));
            tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            frames[n] = new TextureRegion( tex );
        }
        Array<TextureRegion> framesArray = new Array<TextureRegion>(frames);

        Animation anim = new Animation(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);
        
        Texture mouseTex = new Texture(Gdx.files.internal("assets/mouse0.png"));
        mouseTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        
        /*
        mousey.storeAnimation( "walk", anim );
        mousey.storeAnimation( "stop", mouseTex );
        mousey.setOrigin( mousey.getWidth()/2, mousey.getHeight()/2 );
        mousey.setAutoAngle(true);
        mousey.setEllipseBoundary();
        mainStage.addActor(mousey);
        */
       

        cop.storeAnimation("walk", anim);;
        cop.storeAnimation( "stop", mouseTex );
        
        cop.setOrigin( cop.getWidth()/2, cop.getHeight()/2 );
        cop.setAutoAngle(true);
        cop.setEllipseBoundary();
        
        //mainStage.addActor(cop);

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
        
        turtle = new Car();
        TextureRegion[] turtFrames = new TextureRegion[6];
        for (int n = 1; n <= 6; n++)
        {   
            String fileName = "assets/turtle-" + n + ".png";
            Texture tex = new Texture(Gdx.files.internal(fileName));
            tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            turtFrames[n-1] = new TextureRegion( tex );
        }
        Array<TextureRegion> turtFramesArray = new Array<TextureRegion>(turtFrames);

        Animation turtAnim = new Animation(0.1f, turtFramesArray, Animation.PlayMode.LOOP);
        //turtle.storeAnimation( "swim", turtAnim );

        //Texture frame1 = new Texture(Gdx.files.internal("assets/turtle-1.png"));
        Texture frame1 = new Texture(Gdx.files.internal("assets/Audi.png"));
        turtle.storeAnimation( "rest", frame1 );

        turtle.setOrigin( turtle.getWidth()/2, turtle.getHeight()/2 );
        turtle.setPosition( 300, 600 );
        turtle.scaleBy(0.01f);
        turtle.setRotation( 90 );
        turtle.setEllipseBoundary();
        turtle.setMaxSpeed(500);
        turtle.setDeceleration(750);
        mainStage.addActor(turtle);
        
        win = false;
    }

    public void update(float dt) 
    {   
        // process input
        turtle.setAccelerationXY(0,0);

        if (Gdx.input.isKeyPressed(Keys.LEFT) && turtle.getSpeed() != 0)  {
            turtle.rotateBy(90 * dt * turtle.getSpeed() * 0.002f);
            turtle.accelerateForward(-500);
        } if (Gdx.input.isKeyPressed(Keys.RIGHT) && turtle.getSpeed() != 0) {
            turtle.rotateBy(-90 * dt * turtle.getSpeed() * 0.002f);
            turtle.accelerateForward(-500);
        } if (Gdx.input.isKeyPressed(Keys.UP)) 
            turtle.accelerateForward(1000);
        if (Gdx.input.isKeyPressed(Keys.DOWN)) 
            turtle.accelerateForward(-800);

        // set correct animation
        if ( turtle.getSpeed() > 1 && turtle.getAnimationName().equals("rest") )
            turtle.setActiveAnimation("swim");
        if ( turtle.getSpeed() < 1 && turtle.getAnimationName().equals("swim") )
            turtle.setActiveAnimation("rest");

        // bound turtle to the screen
        turtle.setX( MathUtils.clamp( turtle.getX(), 0,  mapWidth - turtle.getWidth() ));
        turtle.setY( MathUtils.clamp( turtle.getY(), 0,  mapHeight - turtle.getHeight() ));

        //COP MOVEMENT//
        cop.setAccelerationXY(0,0);
        
        float deltaX = Math.abs(cop.getX()-turtle.getX());
        float deltaY = Math.abs(cop.getY()-turtle.getY());
        double targetAngle =  Math.atan(deltaY/deltaX);
        double targetAngleDeg = 0;
        
        if(cop.getX()>turtle.getX())
        {	
        	if(cop.getY()>turtle.getY())
        	{targetAngleDeg = 180 + Math.toDegrees(targetAngle);}
        	if(cop.getY()<turtle.getY())
        	{targetAngleDeg = 180 - Math.toDegrees(targetAngle);}
        }
        if(cop.getX()<turtle.getX())
        {
        	if(cop.getY()>turtle.getY())
        	{targetAngleDeg = 360 - Math.toDegrees(targetAngle);}
        	if(cop.getY()<turtle.getY())
        	{targetAngleDeg = 0 + Math.toDegrees(targetAngle);}
        }
        
        double difference = Math.abs(targetAngleDeg-cop.getRotation());
        double closestAngle = targetAngleDeg;
       
        for(int i=-15;i<15;i++)
        {
        	double circle = 360*i;
        	if(Math.abs(targetAngleDeg+circle-cop.getRotation())<difference)
        	{difference = Math.abs(targetAngleDeg+circle-cop.getRotation());
        	closestAngle = targetAngleDeg+circle; }
        }

        if (cop.getRotation()>closestAngle)
    		{cop.rotateBy(-200 * dt);}
    	if (cop.getRotation()<closestAngle)
    		{cop.rotateBy(200 * dt);}
    	
    	if(difference<10)
        cop.accelerateForward(500);
        
    	
        // set correct animation
        
        if ( cop.getSpeed() > 1 && cop.getAnimationName().equals("stop") )
            cop.setActiveAnimation("walk");
        if ( cop.getSpeed() < 1 && cop.getAnimationName().equals("walk") )
            cop.setActiveAnimation("stop");
        
        
        // bound mousey to the rectangle defined by mapWidth, mapHeight
        cop.setX( MathUtils.clamp( cop.getX(), 0,  mapWidth - cop.getWidth() ));
        cop.setY( MathUtils.clamp( cop.getY(), 0,  mapHeight - cop.getHeight() ));
        
        
        for (BaseActor r : blocks)
        {
        	//turtle.overlaps(r, true);
            cop.overlaps(r, true);
        }
        
        // check win condition: mousey must be overlapping cheese
        if ( !win && cheese.overlaps( turtle, true ) )
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
        cam.position.set( turtle.getX() + turtle.getOriginX(), turtle.getY() + turtle.getOriginY(), 0 );

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