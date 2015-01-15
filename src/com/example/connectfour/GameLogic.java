package com.example.connectfour;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;

public class GameLogic {
	public static final int NO_PLAYER = 0;
	public static final int LIGHT_PLAYER = 1;
	public static final int DARK_PLAYER = 2;
	public static final int NUM_ROWS = 6;
	public static final int NUM_COLS = 7;
	private int whoseTurn;
	private int[][] board; // col row
	
	public GameLogic() {
		whoseTurn = LIGHT_PLAYER;
		board = new int[NUM_COLS][NUM_ROWS];
	}
	
	public void createNewGame() {
		for (int col = 0; col < NUM_COLS; col++) {
			for (int row = 0; row < NUM_ROWS; row++) {
				board[col][row] = NO_PLAYER;
			}
		}
	}
	
	public int[][] getBoard() {
		return board;
	}
	public void setBoard(Serializable serializable) {
		//assert serializable is an int[][]; //TODO
		board = (int[][]) serializable;		
	}

	public void setActivePlayer(int player) {
		whoseTurn = player;
	}

	public int addBullet(final int col) {
		int row = getHighestFilledRowIn(col);
		if (row > 0) { //not filled to top
			board[col][row - 1] = whoseTurn;
			return row - 1; // > 0 
		} else {
			return -1;
		}
	}	
	
	public void nextTurn() {
		switch (whoseTurn) {
		case LIGHT_PLAYER:
			whoseTurn = DARK_PLAYER;
			break;
		case DARK_PLAYER:
			whoseTurn = LIGHT_PLAYER;
			break;
		default:
			System.err.println("Whose turn?" + Integer.toString(whoseTurn));
		}
	}
	public int getActivePlayer() {
		return whoseTurn;
	}
	
	public int[] checkForWin(final int col, final int row) { // player, longestStreak
		int player = board[col][row];
		final int[][] directions = { 
			new int[] {-1,-1}, //nw
			new int[] {-1,0}, //w
			new int[] {-1,1}, //sw
			new int[] {0,1}, //s
			};
		HashMap<int[], Integer> streaks = new HashMap<int[], Integer>();
		for (int[] direction: directions) {
			int streak = 0;
			// pos dir
			int owner = player;
			while (owner == player && streak < 5) { // TODO hardcoded
				streak++; // not first time
				int colPlusDir = col + streak * direction[0];
				int rowPlusDir = row + streak * direction[1];
				try {
					owner = board[colPlusDir][rowPlusDir];
				} catch (IndexOutOfBoundsException e) {
					owner = NO_PLAYER;
				}
			}
			// neg dir
			owner = player;
			streak--; //to offset incr in while-loop // TODO ugly
			int nStreak = 0;
			while (owner == player && streak < 5) { // TODO hardcoded
				nStreak--; 
				streak++;
				int colPlusDir = col + nStreak * direction[0];
				int rowPlusDir = row + nStreak * direction[1];				
				try {
					owner = board[colPlusDir][rowPlusDir];
				} catch (IndexOutOfBoundsException e) {
					owner = NO_PLAYER;
				}
			}
			streaks.put(direction, streak);
		}
		return new int[] {player, Collections.max(streaks.values())};
	}
	
	private int getHighestFilledRowIn(int col) {
		/*
		 * Returns the index (where top of board = 0) of the highest 
		 * filled (claimed) cell in this column.
		 */
		int row = 0;
		while (row < NUM_ROWS && board[col][row] == NO_PLAYER) {
			row++;
		}
		return row; // will be > NUM_ROWS if col is empty
	}
	
	@SuppressWarnings("unused")
	private void printBoard() {
		System.out.println("Board:");
		for (int row = 0; row < NUM_ROWS; row ++) {
			for (int col = 0; col < NUM_COLS; col++) {
				System.out.print(Integer.toString(board[col][row]));
			}
			System.out.println();
		}
	}


	
}
