package com.ctlayon.hextest;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ball extends AnimatedSprite {

    // ===PUBLIC VARIABLES=== //    
    public int power;
    
    // ===MEMBER VARIABLES=== //
    private Body body;
    private boolean isAnimating;
    private boolean canAnimate;
    // ===CONSTANTS=== //
    final static float Y_MAX_SPEED = -10f;
    final static float X_MAX_SPEED = 8f;
    final static float Y_MIN_SPEED = 4.2f;
    final static float Y_ACCELERATION = .20f;
    final static float X_ACCELERATION = .25f;
    final static float GRAV_DISTANCE = 3f;
    
    public Ball(float pX, float pY, ITiledTextureRegion pTextureRegion,
            VertexBufferObjectManager pVertexBufferObjectManager, 
            PhysicsWorld world, Scene pScene) {
    	
    	super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    	
    	this.isAnimating = false;
    	this.canAnimate = true;
    	this.power = 1;
    	
        final FixtureDef ballFixtureDef = PhysicsFactory.createFixtureDef(0, 1f, 0f);
        final PhysicsObject ballData = new PhysicsObject(BaseActivity.getSharedInstance().res.getString(R.string.ball));
        
        body = PhysicsFactory.createCircleBody(world, this, BodyType.DynamicBody, ballFixtureDef);
        body.setUserData(ballData);
        body.setLinearVelocity(3, 3);
        
        pScene.attachChild(this);
        world.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
    }
    
    public Body getBody() {
        return body;
    }
 
    public void move(GravityWell well) {
        
        // Get Position and Calculate Distances from Well to Ball
        float bY = this.getY() / PIXEL_TO_METER_RATIO_DEFAULT;
        float bX = this.getX() / PIXEL_TO_METER_RATIO_DEFAULT;
        float wY = well.getY() / PIXEL_TO_METER_RATIO_DEFAULT;
        float wX = (well.getX() + well.getWidth()*0.5f) / PIXEL_TO_METER_RATIO_DEFAULT;
        
        float xDist = wX - bX;
        float yDist = wY - bY;
        
        
        // If Ball is moving up and its moving really slow set it to a default speed (-1)
        
        if(this.body.getLinearVelocity().y < 0 && this.body.getLinearVelocity().y > -1f && 
        		Math.abs(yDist) >= GRAV_DISTANCE)
            this.body.setLinearVelocity(this.body.getLinearVelocity().x, -1f);
        
        // If Ball is moving down and its moving really slow set it to a default speed (1)
        
        else if(this.body.getLinearVelocity().y > 0 && this.body.getLinearVelocity().y < 1f &&
        		Math.abs(yDist) >= GRAV_DISTANCE)   
            this.body.setLinearVelocity(this.body.getLinearVelocity().x, 1f);
        
        // If it's moving slow and away from the Gravity Well's Pull
        // Makes a smoother transition
        
        if(yDist > GRAV_DISTANCE - 1f && yDist < GRAV_DISTANCE && 
                this.body.getLinearVelocity().y < 0 && this.body.getLinearVelocity().y > -Y_MIN_SPEED) {
            if(Math.abs(this.body.getLinearVelocity().y) < Y_MIN_SPEED) {
                this.body.setLinearVelocity(this.body.getLinearVelocity().x, -Y_MIN_SPEED);
            }            
        }
        // Gravity Well's Logic for pull and push
        // Constantly Pushes Up
        // Constantly Pulls In
        
        if(Math.abs(yDist) < GRAV_DISTANCE && Math.abs(xDist) < GRAV_DISTANCE) {
            this.canAnimate = true;
        	if(yDist >= 0) {
	            if(this.body.getLinearVelocity().y >= Y_MAX_SPEED) {
	                this.body.setLinearVelocity(this.body.getLinearVelocity().x, this.body.getLinearVelocity().y - Y_ACCELERATION);
	            }
        	} else if(yDist < 0) {
        		if(this.body.getLinearVelocity().y >= Y_MAX_SPEED) {
	                this.body.setLinearVelocity(this.body.getLinearVelocity().x, this.body.getLinearVelocity().y - 2*Y_ACCELERATION);
	            }
        	}
        	
            if(xDist < 0) {
                if (this.body.getLinearVelocity().x >= -X_MAX_SPEED)
                    this.body.setLinearVelocity(this.body.getLinearVelocity().x - X_ACCELERATION, this.body.getLinearVelocity().y);
            } else if (xDist >= 0)
                if (this.body.getLinearVelocity().x <= X_MAX_SPEED)
                    this.body.setLinearVelocity(this.body.getLinearVelocity().x + X_ACCELERATION, this.body.getLinearVelocity().y);
        }
        
        // Handles how much damage the ball causes
        // Determines if the ball should be
        // 'screaming' (animating)
        
        if(isAnimating) {
            if(Math.abs(this.body.getLinearVelocity().y) < 10 || !canAnimate) {
                this.stopAnimation(0);
                this.power = 1;
                isAnimating = false;
            }
            else if(Math.abs(this.body.getLinearVelocity().y)>= 10) {
                this.power = 2;
            }
        }
        else {
            if(Math.abs(this.body.getLinearVelocity().y) >= 10 && canAnimate) {
                this.animate(200);
                this.power = 2;
                isAnimating = true;
            }
            else if(Math.abs(this.body.getLinearVelocity().y) < 10) {
                this.power = 1;
            }
        }        
    }
    
    public void canAnimate(boolean canAnimate) {
        this.canAnimate = canAnimate;
    }
}
