package com.ctlayon.hextest;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;

public class MainMenuScene extends MenuScene implements
		IOnMenuItemClickListener {

	BaseActivity activity;
	final int MENU_START = 0;
	final int MENU_EDITOR = 1;

	public MainMenuScene() {
		super(BaseActivity.getSharedInstance().mCamera);
		activity = BaseActivity.getSharedInstance();

		setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		IMenuItem startButton = new TextMenuItem(MENU_START, activity.mFont,activity.getString(R.string.start),
				activity.getVertexBufferObjectManager());
		IMenuItem editorButton = new TextMenuItem(MENU_EDITOR, activity.mFont,"LEVEL EDITOR",
                activity.getVertexBufferObjectManager());
		
		startButton.setPosition(mCamera.getWidth() / 3 - startButton.getWidth()
				/ 2, mCamera.getHeight() / 4 - startButton.getHeight() / 2);

		editorButton.setPosition(startButton.getX(), startButton.getY() + startButton.getHeight() * 2);
		
		addMenuItem(startButton);
		addMenuItem(editorButton);
		setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1,
			float arg2, float arg3) {
		switch (arg1.getID()) {
		case MENU_START:
		    activity.setCurrentScene(new LevelOne());
		    return true;
		case MENU_EDITOR:
		    activity.setCurrentScene(new EditorScene());
		    return true;
		default:
			break;
		}
		return false;
	}

}
