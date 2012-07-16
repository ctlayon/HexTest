package com.ctlayon.hextest;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Manifold;

public abstract class LevelBase extends Scene implements IOnSceneTouchListener{
	
	//===PRIVATE VARIABLES===//
    
    // @activity - Stores BaseActivity instance for easy access
    // @hud - The HUD at the top of the screen
    
	private BaseActivity activity;
	private TopBar hud;
	
	// @mPhysicsWorld - The BOX2D World This Scene is In
	// @mBall - The Ball that collides with Hexagons
	// @mWell - The 'Paddle' that stops mBall from
	//     going into the abyss
	
	private PhysicsWorld mPhysicsWorld;
	private Ball mBall;
	private GravityWell mWell;
	
	// @mHexMap - Maps unique string to a Hexagon
	// @mBodyMap - Maps unique string to a Body
	// @hashQueue - Queue of Available unique strings
	
	private HashMap<String, Hex> mHexMap;
	private HashMap<String, Body> mBodyMap;
	
	private Queue<Integer> hashQueue;
	
	// @hexCount - Hexagons on the scene
	// @runHitChecker - True if objects collided
	//     determines if hitChecker needs to run
	
	private int hexCount = 0;
	private boolean runHitChecker = false;
	
	
	public LevelBase() {
		
		// Set Background to Bluish
		
		this.setBackground(new Background(.26f, .3f, .45f));
		
		// Store BaseActivity for Easy Access
		// Create New Physics World with no Gravity
		// Create New HashMap
		
		this.activity = BaseActivity.getSharedInstance();	
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		this.mPhysicsWorld.setContactListener(createContactListener());		
		
		this.mHexMap = new HashMap<String, Hex>();
		this.mBodyMap = new HashMap<String, Body>();
		this.hashQueue = new LinkedList<Integer>();
		
		//Create Top Bar HUD
		
		hud = new TopBar(0, 0, activity.mCamera, activity.getVertexBufferObjectManager());
		
		createWalls();		
		createWell();
		createBall();
		
		level();
		
		// Ensure that the current scene is Indeed HexScene
		//
		// Add the UpdateHandlers that handles all the movements
		//		and basically everything important
		
		activity.mCamera.setHUD(hud);
		activity.setCurrentScene(this);		
		this.setOnSceneTouchListener(this);
		
		this.registerUpdateHandler(this.mPhysicsWorld);
		registerUpdateHandler(new LevelBaseLoopUpdateHandler());
	}
	
	/**
	 * This function loops through all the bodies on the scene and checks
	 * if something has collided
	 * @warning only runs if runHitChecker is set to True
	 * @warning this could be expensive call only when needed
	 */
	
	public void hitChecker() {
	    
	    // Only run if something has collided
	    
		if(runHitChecker) {
			synchronized (this) {
			    
			    // Gets an Iterator for all the bodies in the World
			    // Loop through the iterator until you've gone through them all
				Iterator<Body> bIt = this.mPhysicsWorld.getBodies();
				
				while (bIt.hasNext()) {
				    
				    // @obj - PhysicsObject that stores all the data for bodies
				    //      seems inefficient but for this small project one class
				    //      doesn't seem to kill it
				    
					PhysicsObject obj = ((PhysicsObject)bIt.next().getUserData());
					
					// If the obj of the current body has been hit
					// Update mBall so that it stops animating
					
					if (obj.gotHit) {
					    this.mBall.canAnimate(false);
					    
					    // gotHit updates the health of whatever hexagon got hit
					    //
					    // this.mHexMap.get(obj.uniqueName()) gets the hexagon
					    //     that is mapped to this obj
					    //
					    // the object stores how much damage the hit did so reset
					    //     that damage to 0 for good measures
					    //
					    // Remove the hexagon from the scene and memory along with the 
					    //     PhysicsConnector
					    //     remove this iterator and exit the current loop
					    //
					    // Doing it this way has been deemed ineffcient to do while the
					    //     the game is running but I don't see the Garbage Collector
					    //     running so I don't care

					    
						if(!this.mHexMap.get(obj.uniqueName()).gotHit(obj.hitDamage)) {
							hashQueue.add(obj.unique);
							obj.hitDamage = 0;
							
							this.mHexMap.get(obj.uniqueName()).setVisible(false);
							this.mHexMap.get(obj.uniqueName()).detachSelf();
							this.mHexMap.get(obj.uniqueName()).clean();
							
							final PhysicsConnector con = this.mPhysicsWorld.getPhysicsConnectorManager().
									findPhysicsConnectorByShape(this.mHexMap.get(obj.uniqueName()));
							if(con!=null) {
								this.mPhysicsWorld.unregisterPhysicsConnector(con);
							}
							this.mPhysicsWorld.destroyBody(this.mBodyMap.get(obj.uniqueName()));
							this.mHexMap.remove(obj.uniqueName());
							this.mBodyMap.remove(obj.uniqueName());
							
							bIt.remove();
							break;
						}	
						obj.gotHit = false;						
					}
				}
				
			}
		}
		runHitChecker = false;
	}
	/**
	 * moveBall is called by the GameLoopUpdateHandler()
	 * it Invokes the ball move function
	 * see {@link:Ball.java}
	 */
	public void moveBall() {
	    this.mBall.move(this.mWell);
	}
	
	/**
	 * Checks if the game is over via HexMap
	 * If hexmap is empty than the game is over
	 * Looking for a better way to check for gameOver
	 */
	
	public void nextLevel() {
		if(this.mHexMap.isEmpty()) {
			activity.levelCount++;
			if(activity.levelCount == 2) {
				activity.setCurrentScene(new LevelTwo());
			}
			else if(activity.levelCount == 3) {
				activity.setCurrentScene(new LevelThree());
			}
			else {
				activity.setCurrentScene(new LevelOne());
			}
			
		}
	}
	
	/**
	 * When the Scene is touched move the Gravity
	 * Well to the touch event's X Coordinate 
	 */
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		this.mWell.setPosition(pSceneTouchEvent.getX() - mWell.getWidth() / 2,
				(BaseActivity.CAMERA_HEIGHT - 150));
		
		return true;
	}
	

	
	/**
	 * level is the first level
	 * I may add a level manager class later 
	 * 
	 * Override this  to create a new level
	 *      Make sure you call createLevel
	 * 		After you Ovveride level Reason I've
	 * 		Done it like this is so the user feels
	 * 		the need to write their own createLevel
	 * 		function 
	 */
	abstract void level();
	
	protected void createLevel(int[][] levelArray, int[][] healthArray) {
		final float X_OFFSET = 100 / PIXEL_TO_METER_RATIO_DEFAULT;
		final float Y_OFFSET = 100 / PIXEL_TO_METER_RATIO_DEFAULT;
		
		for(int x=0; x < levelArray.length; x++) {
			for(int y=0; y < levelArray[x].length;y++) {
				if(levelArray[x][y] == 1) {
					final Hex hex = createHex(x * 1.1f + X_OFFSET, y * 1.1f + Y_OFFSET);
					hex.setHealth(healthArray[x][y]);
				}
			}
		}
	}
	
	/**
	 * Creates a Hexagon at the given (X,Y) Coordinates This method is
	 * Hackish and will be elimated as I create more levels
	 * 
	 * @param x represents the X Coordinate of the Hexagons Location
	 * @param y represents the Y Coordinate of the Hexagons Location
	 * @return The newly created Hexagon
	 */	
	
	//===PRIVATE METHODS===//
	
	
	private Hex createHex(float x, float y) {
		
		final Hex hexagonSprite;
		final PhysicsObject hexData;
		
		// If there is no available string in the queue create a new unique string
		// else use the queue's string
		
		if(this.hashQueue.isEmpty()) {
			this.hexCount++;
			hexData = new PhysicsObject(activity.res.getString(R.string.hex), this.hexCount);
		}
		else {
			hexData = new PhysicsObject(activity.res.getString(R.string.hex), this.hashQueue.remove());
		}
		
		// Create a Hexagon Sprite and
		// Map it in mHexMap
		
		hexagonSprite = new Hex(0, 0, activity.getHexagon(), activity.getVertexBufferObjectManager());
		hexagonSprite.setPosition(x * hexagonSprite.getWidth(), y * hexagonSprite.getHeight());
		
		this.mHexMap.put(hexData.uniqueName(), hexagonSprite);
		
		// Create a body for the hexagonSprite and
		// Map it in mBodyMap
		
        final Body body = LevelBase.createHexagonBody(this.mPhysicsWorld, hexagonSprite,
                BodyType.StaticBody, BaseActivity.FIXTURE_DEF);  
        
        body.setUserData(hexData);
        this.mBodyMap.put(hexData.uniqueName(), body);
        
        // Attach it to the scene
        // Add the physics connector
        
        this.attachChild(hexagonSprite);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(hexagonSprite, body, true, true));
        
        return hexagonSprite;
	}
	
	/**
	 * Creates a Hexagon Body
	 * @Todo: Fix the vertices so it fits the shape better
	 * @param pPhysicsWorld the world the body will be in
	 * @param pAreaShape the shape the body will attach to
	 * @param pBodyType the type of body it is
	 * @param pFixtureDef the type of fixture it is
	 * @return A Body fit for a Hexagon shape with preset vertices
	 */
	
	private static Body createHexagonBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		/* Remember that the vertices are relative to the center-coordinates of the Shape. */
		final float halfWidth = pAreaShape.getWidthScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float halfHeight = pAreaShape.getHeightScaled() * 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		/* The top and bottom vertex of the hexagon are on the bottom and top of hexagon-sprite. */
		final float top = -halfHeight + 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;
		final float bottom = halfHeight - 0.5f / PIXEL_TO_METER_RATIO_DEFAULT;

		final float centerY = 0;

		/* The left and right vertices of the heaxgon are not on the edge of the hexagon-sprite, so we need to inset them a little. */
		final float left = -halfWidth + 6f / PIXEL_TO_METER_RATIO_DEFAULT; //close not perfect
		final float right = halfWidth - 6f / PIXEL_TO_METER_RATIO_DEFAULT; //close not perfect
		final float midRight = halfWidth - 2f / PIXEL_TO_METER_RATIO_DEFAULT; //close not perfect
		final float midLeft = -halfWidth + 2f /PIXEL_TO_METER_RATIO_DEFAULT; //close not perfect

		final Vector2[] vertices = {
				new Vector2(left, top),
				new Vector2(right, top),
				new Vector2(midRight, centerY),
				new Vector2(right, bottom),
				new Vector2(left, bottom),
				new Vector2(midLeft, centerY)
		};

		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pAreaShape, vertices, pBodyType, pFixtureDef);
	}
	
	/**
	 * Creates a contactlistener for processing of collisions
	 * No Visible Updates are done here only the PhysicsObject is
	 * updated. I've heard updating items here is poor coding so 
	 * Tell this to run a method that is than passed on to the
	 * GameLoopUpdateHandler
	 * @return the created ContactListener
	 */
	private ContactListener createContactListener()
	{
		ContactListener contactListener = new ContactListener()
		{		    
			@Override
			public void beginContact(Contact contact) {
				PhysicsObject a = (PhysicsObject) contact.getFixtureA().getBody().getUserData();
				PhysicsObject b = (PhysicsObject) contact.getFixtureB().getBody().getUserData();
				
				final float MIN_SPEED = 1.2f;
				
				// Hack for preventing inelastic collisions from sticking
				// The proper solution would be to recompile the source
				// setting the minimum speed to 0
				
				if( a.name.equals(activity.res.getString(R.string.ball)) && b.name.equals(activity.res.getString(R.string.wall)) ||
						b.name.equals(activity.res.getString(R.string.ball)) && a.name.equals(activity.res.getString(R.string.wall))) {
					if(a.name.equals(activity.res.getString(R.string.ball))) {
						Vector2 velocity = contact.getFixtureA().getBody().getLinearVelocity();
						if(Math.abs(velocity.x) < 1) {
							if(velocity.x < 0)
								contact.getFixtureA().getBody().setLinearVelocity(-MIN_SPEED, velocity.y);
							else
								contact.getFixtureA().getBody().setLinearVelocity(MIN_SPEED, velocity.y);
						}
						if(Math.abs(velocity.y) < 1) {
							if(velocity.y < 0)
								contact.getFixtureA().getBody().setLinearVelocity(velocity.x, -MIN_SPEED);
							else
								contact.getFixtureA().getBody().setLinearVelocity(velocity.x, MIN_SPEED);
						}
					}
					else {
						Vector2 velocity = contact.getFixtureB().getBody().getLinearVelocity();
						if(Math.abs(velocity.x) < 1) {
							if(velocity.x < 0)
								contact.getFixtureB().getBody().setLinearVelocity(-MIN_SPEED, velocity.y);
							else
								contact.getFixtureB().getBody().setLinearVelocity(MIN_SPEED, velocity.y);
						}
						if(Math.abs(velocity.y) < 1) {
							if(velocity.y < 0)
								contact.getFixtureB().getBody().setLinearVelocity(velocity.x, -MIN_SPEED);
							else
								contact.getFixtureB().getBody().setLinearVelocity(velocity.x, MIN_SPEED);
						}
					}
				}
				
				// Set the Hit Damage here because the ball changes
				// it's velocity by the time the collision ends
				
				if(a.name.equals(activity.res.getString(R.string.hex))) {
                    a.hitDamage = mBall.power;
                }
                else if(b.name.equals(activity.res.getString(R.string.hex))) {
                    b.hitDamage = mBall.power;
                }
			}
			@Override
			public void endContact(Contact contact) {
				PhysicsObject a = (PhysicsObject) contact.getFixtureA().getBody().getUserData();
				PhysicsObject b = (PhysicsObject) contact.getFixtureB().getBody().getUserData();
				
				// Mark the object as hit, after determining which on is the hexagon
				
				if(a.name.equals(activity.res.getString(R.string.hex))) {
					a.gotHit = true;					
					runHitChecker = true;
				}
				else if(b.name.equals(activity.res.getString(R.string.hex))) {
					b.gotHit = true;
					runHitChecker = true;
				}
							
			}
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
		};
		return contactListener;
	}
		
	//===HELPER FUNCTIONS===//
	
	/**
	 * Creates the box that traps our objects in
	 */

	private void createWalls() {
		// Create Boundary Walls
		
		final VertexBufferObjectManager vertexBufferObjectManager = activity.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, BaseActivity.CAMERA_HEIGHT - 2, BaseActivity.CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, BaseActivity.CAMERA_WIDTH, this.hud.height, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, BaseActivity.CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(BaseActivity.CAMERA_WIDTH - 2, 0, 2, BaseActivity.CAMERA_HEIGHT, vertexBufferObjectManager);

		// Attach A Box Body to the Walls (for collision Detection)
		
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 1f, 0f);
		final PhysicsObject wallData = new PhysicsObject(activity.res.getString(R.string.wall));
		
		
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef).setUserData(wallData);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef).setUserData(wallData);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef).setUserData(wallData);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef).setUserData(wallData);

		// Attach the Walls to HexScene (this)
		
		this.attachChild(ground);
		this.attachChild(roof);
		this.attachChild(left);
		this.attachChild(right);
	}
	
	/**
	 * Creates the Ball
	 */
	
	private void createBall() {		
		mBall = new Ball(50, 500, activity.getCircle(),
		        activity.getVertexBufferObjectManager(),
		        this.mPhysicsWorld,this);
	}

	/**
	 * Create the Gravity Well
	 */
	
	private void createWell() {			
		mWell = new GravityWell(BaseActivity.CAMERA_WIDTH / 2 - 70,
                BaseActivity.CAMERA_HEIGHT - 150, 70,30,
                activity.getVertexBufferObjectManager(),
                this.mPhysicsWorld, this);
	}

}
