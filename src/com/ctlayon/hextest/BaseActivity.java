package com.ctlayon.hextest;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.content.res.Resources;
import android.graphics.Typeface;



public class BaseActivity extends SimpleBaseGameActivity {
	
		//===CONSTANTS===//
		static final int CAMERA_WIDTH = 480;
		static final int CAMERA_HEIGHT = 800;
		static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0f,1f, 0f);
		
		//===PRIVATE VARIABLES===//		
		private BitmapTextureAtlas mBitmapTextureAtlas;
		
		private TiledTextureRegion mHexagonTextureRegion;
		private TiledTextureRegion mCircleFaceTextureRegion;
		private TiledTextureRegion mZoomTextureRegion;
		
		//===PUBLIC VARIABLES===//
		public Font mFont;
		public SmoothCamera mCamera;
		
		public Scene mCurrentScene;
		public static BaseActivity instance;
		
		public Resources res;
		public int levelCount = 1;
		
		//===IMPLEMENTED INTERFACE===//
		/**
		 * Creates the EngineOptions
		 * Initiates: instance to this
		 *            mCamera to have the constants CAMERA_WIDTH,CAMERA_HEIGHT
		 */
		@Override
		public EngineOptions onCreateEngineOptions() {
			instance = this;
			mCamera = new SmoothCamera(0,0,CAMERA_WIDTH,CAMERA_HEIGHT, 10, 10, 1);
			
			return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
					new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		}
		
		/**
		 * Loads the Resources for the rest of the program
		 * Assigns the resource to the variable res for
		 *    Later access to string resources
		 */

		@Override
		protected void onCreateResources() {	
			
			 this.res = getResources();
			
			// Look for textures in the /assets/gfx/ folder	
			
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			
			// create a place to load all the textures
			// store the texture into the atlas
			
			this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
			this.mHexagonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "hexagon.png", 0, 0, 1, 1); // 48x48
			this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 48, 2, 1); // 64x32
			this.mZoomTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "icon_menu_zoom.png", 0, 80, 1, 1); // 48 x 48
			// load the atlas into cache
			
			this.mBitmapTextureAtlas.load();
			
			// Setup the font
			// load the font into cache
			
			mFont = FontFactory.create(this.getFontManager(),this.getTextureManager(), 256, 256,Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
			mFont.load();	
		}
		
		/**
		 * Assigns the very first scene as well as adding
		 * a Frame Per Second counter to the debuger
		 */

		@Override
		protected Scene onCreateScene() {
			
			// Add FPS counter
			// Set the current Scene to HexScene
			
			this.mEngine.registerUpdateHandler(new FPSLogger());
			this.mCurrentScene = new MainMenuScene();
			
			// Initializes scene to mCurrentScene
			
			return this.mCurrentScene;
		}
		
		/**
		 * Whenever the user leaves this screen
		 */

		@Override
		public void onBackPressed() {	
		    this.mCurrentScene = null;
		    super.onBackPressed();
		}

		
		//===PUBLIC ACCESS FUNCTIONS===//
		
		public void setCurrentScene(Scene scene) {
			this.mCurrentScene = scene;
			mEngine.setScene(this.mCurrentScene);
		}

		public static BaseActivity getSharedInstance() {
			return instance;
		}
		
		public TiledTextureRegion getHexagon() {			
			return this.mHexagonTextureRegion;
		}
		
		public TiledTextureRegion getCircle() {
		    return this.mCircleFaceTextureRegion;
		}
		
		public TiledTextureRegion getZoomIcon() {
		    return this.mZoomTextureRegion;
		}

}