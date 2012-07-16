package com.ctlayon.hextest;

public class LevelThree extends LevelBase {

	@Override
	void level() {
		int[][] levelArray = {
				{1,0,0,0,1,0},
				{0,1,0,0,1,0},
				{0,0,1,0,1,0},
				{0,0,0,1,1,0},
				{0,0,0,0,1,0},
				{1,1,1,1,1,1},
		};

		int[][] healthArray = {
				{1,0,0,0,1,0},
				{0,1,0,0,1,0},
				{0,0,1,0,1,0},
				{0,0,0,1,1,0},
				{0,0,0,0,1,0},
				{1,1,1,1,1,1},
		};
		
		createLevel(levelArray, healthArray);
	}

}
