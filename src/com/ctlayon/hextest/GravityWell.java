package com.ctlayon.hextest;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class GravityWell extends Rectangle {
	
    public GravityWell(float pX, float pY, float pWidth, float pHeight,
            VertexBufferObjectManager pVertexBufferObjectManager,
            PhysicsWorld world, Scene pScene) {
        super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
        
        pScene.attachChild(this);
    }

}
