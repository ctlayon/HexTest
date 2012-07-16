package com.ctlayon.hextest;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Hex extends Sprite {
	
	//===CONSTANTS===//	
	static final int MAX_HEALTH = 2;
	
	//===PUBLIC VARIABLES===//
	public int hp;
	
	//===CONSTRUCTOR===//
	
	/**
	 * Creates a Hexagon calling the Sprite's constructor
	 * initilizes hp to the constant MAX_HEALTH
	 * @param pX the X coordinated passed on to the Sprite's Constructor Method
	 * @param pY the Y coordinated passed on to the Sprite's Constructor Method
	 * @param pTextureRegion the Texture that is the Hexagon
	 * @param pVertexBufferObjectManager look at the name
	 */
	public Hex(final float pX, final float pY, final ITextureRegion pTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		hp = MAX_HEALTH;		
	}
	
	//===ACCESS FUNCTIONS===//
	
	/**
	 * Why is hp public if I have access functions?
	 * nobody knows
	 */
	public void setHealth(int health) {
		this.hp = health;
	}
	
	/**
	 * Clean helps with the destruction of the hexagon
	 */
	
	public void clean() {
		this.setIgnoreUpdate(true);
		this.clearEntityModifiers();
		this.clearUpdateHandlers();
	}
	
	//===PUBLIC METHODS===//
	
	/**
	 * gotHit manages the health of the this object
	 * 
	 * @param damage is how much the hp will decrease
	 * @return True if object is alive, False if it has no 
	 *     hp left
	 */
	public boolean gotHit(int damage) {
		synchronized (this) {
			hp = hp - damage;
			if (hp <= 0)
				return false;
			else
				return true;
		}
	}
	
}
