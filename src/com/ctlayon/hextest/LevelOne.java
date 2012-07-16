package com.ctlayon.hextest;

public class LevelOne extends LevelBase {

	@Override
	void level() {
		final int NUM_ROWS = 4;
		final int NUM_COLUMNS = 8;
		
		int[][] levelArray = new int[NUM_COLUMNS][NUM_ROWS];
		int[][] healthArray = new int[NUM_COLUMNS][NUM_ROWS];
		
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for(int y = 0; y<NUM_ROWS; y++) {
				if(y% 2 == 1) {
					levelArray[x][y] = 1;
					healthArray[x][y] = 2;
				}
				else {
					levelArray[x][y] = 0;
					healthArray[x][y] = 0;
				}
			}			
		}
		
		createLevel(levelArray, healthArray);		
	}

}
