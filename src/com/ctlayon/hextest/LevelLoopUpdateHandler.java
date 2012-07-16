package com.ctlayon.hextest;

import org.andengine.engine.handler.IUpdateHandler;

public class LevelLoopUpdateHandler implements IUpdateHandler {

	@Override
	public void onUpdate(float pSecondsElapsed) {
		((EditorScene)BaseActivity.getSharedInstance().mCurrentScene).hexMove();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
