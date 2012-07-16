package com.ctlayon.hextest;

import org.andengine.engine.handler.IUpdateHandler;

public class LevelBaseLoopUpdateHandler implements IUpdateHandler {

	@Override
	public void onUpdate(float pSecondsElapsed) {
		((LevelBase)BaseActivity.getSharedInstance().mCurrentScene).hitChecker();
		((LevelBase)BaseActivity.getSharedInstance().mCurrentScene).moveBall();
		((LevelBase)BaseActivity.getSharedInstance().mCurrentScene).nextLevel();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
