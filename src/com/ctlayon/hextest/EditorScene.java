package com.ctlayon.hextest;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.input.touch.TouchEvent;


public class EditorScene extends Scene {

    // ===PUBLIC VARIABLES===//
    public SmoothCamera mCamera;
    public BaseActivity activity;
    public boolean isScrolling;
    
    // ===PRIVATE VARIABLES=== //
    private Hex hex;
    private ScrollMenu hud;
    
    // ===CONSTANTS=== //
    private final static int HEX_START_X = 0;
    private final static int HEX_START_Y = 100;
    
    private final static int GRID_WIDTH = 32;
    
    // ===CONSTRUCTOR=== //
    public EditorScene() {
        this.setBackground(new Background(.26f, .3f, .45f));

        activity = BaseActivity.getSharedInstance();
        mCamera = activity.mCamera;
        
        hex = createHex(HEX_START_X,HEX_START_Y);
        
        this.setTouchAreaBindingOnActionDownEnabled(true);
        
        hud = new ScrollMenu(0, 0, mCamera, activity.getVertexBufferObjectManager());
        
        hud.attachChild(hex);
        hud.registerTouchArea(hex);
        
        for(int i = 0; i < mCamera.getWidth(); i += GRID_WIDTH*2) {
            final Rectangle vertical = new Rectangle(i, 0, 2, BaseActivity.CAMERA_HEIGHT, activity.getVertexBufferObjectManager());
            this.attachChild(vertical);
        }
        for(int i = 0; i < mCamera.getHeight(); i += GRID_WIDTH*2) {
            final Rectangle horizontal = new Rectangle(0, i, BaseActivity.CAMERA_WIDTH, 2, activity.getVertexBufferObjectManager());
            this.attachChild(horizontal);
        }
                
        activity.mCamera.setHUD(hud);
        activity.setCurrentScene(this);
        
        registerUpdateHandler(new LevelLoopUpdateHandler());
    }

    // ===PUBLIC FUNCTIONS===//
    public void hexMove() {
        if(hex.getX()!=HEX_START_X && hex.getY()!=HEX_START_Y) {
            hud.detachChild(hex);
            this.attachChild(hex);
            this.registerTouchArea(hex);
            
            hex = createHex(HEX_START_X,HEX_START_Y);
            hud.attachChild(hex);
            hud.registerTouchArea(hex);
        }
    }
    // ===PRIVATE FUNCTIONS HELPER FUNCTIONS=== //
    
    /**
     * Creates a new Hexagon on the screen at location (pX,PY) 
     * 
     * @param pX the X Coordinate the Hexagon is to be positioned
     * @param pY the Y Coordinate the Hexagon is to be positioned
     * @return The newly created Hexagon
     */
    private Hex createHex(int pX, int pY) {
        Hex pHex = new Hex(0, 0, activity.getHexagon(), activity.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
                if(pSceneTouchEvent.isActionUp()) {                    
                    int x = Math.round((this.getX() + this.getWidth() / 2) / GRID_WIDTH);
                    int y = Math.round((this.getY() + this.getHeight() / 2) / GRID_WIDTH);
                    
                    this.setPosition(GRID_WIDTH*x,
                            y * GRID_WIDTH );
                }
                return true;
            }
        };
        pHex.setPosition(pX, pY);
        pHex.setVisible(true);
        
        return pHex;
    }

}
