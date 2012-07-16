package com.ctlayon.hextest;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScrollMenu extends HUD implements IOnSceneTouchListener {	
	// ===CONSTANTS=== //
	private final static int WIDTH = 200;
	
	// ===MEMBER VARIABLES=== //
	private float previous;
	public boolean isScrolling = false;
	
	// ===CONSTRUCTOR=== //
	public ScrollMenu(final float pX, final float pY, final Camera pCamera, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this.setPosition(pX, pY);
		
		final Rectangle vertical = new Rectangle(0, 0, WIDTH, BaseActivity.CAMERA_HEIGHT, pVertexBufferObjectManager);
        this.attachChild(vertical);
        
		Text myText = new Text(0, 0, BaseActivity.getSharedInstance().mFont, "Scroll UP", "High Score: 999999999".length(), BaseActivity.getSharedInstance().getVertexBufferObjectManager());	
		previous = 0;
		this.attachChild(myText);
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
	}

	// ===IMPLEMENTED INTERFACE=== //
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {		
		if(pSceneTouchEvent.getX()<WIDTH) {
			if(pSceneTouchEvent.isActionDown()) {
				previous = pSceneTouchEvent.getY();
			}
			if(pSceneTouchEvent.isActionMove()) {
				pScene.setPosition( pScene.getX(), pScene.getY() + ( pSceneTouchEvent.getY() - previous) );
				previous = pSceneTouchEvent.getY();
				isScrolling = true;
			}
			if(pSceneTouchEvent.isActionUp()) {
			    isScrolling = false;
			}
		}
		return false;
	}	
}
