package com.ctlayon.hextest;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TopBar extends HUD {
	
	public final float height = 40;
	
	public TopBar(final float pX, final float pY, final Camera pCamera, 
			final VertexBufferObjectManager pVertexBufferObjectManager) {
		this.setPosition(pX, pY);
		Text myText = new Text(0, 0, BaseActivity.getSharedInstance().mFont,
				"Menu Bar", "High Score: 999999999".length(), 
				BaseActivity.getSharedInstance().getVertexBufferObjectManager());
		
		this.attachChild(myText);
	}
}
