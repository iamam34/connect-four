package com.example.connectfour;


import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class GameActivity extends Activity {	
	private GameLogic gameLogic = new GameLogic();
	private LinearLayout boardLayout;
	private LinearLayout buttonHolder;	
	private TextView gameTextView;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		boardLayout = (LinearLayout) findViewById(R.id.boardLayout);
		buttonHolder = (LinearLayout) findViewById(R.id.buttonHolder);
		gameTextView = (TextView) findViewById(R.id.gameTextView);
//		gameTextView.setVisibility(View.INVISIBLE); //TODO
		
//		 Intent intent = getIntent();
//		 gameTextView.setText(intent.getStringExtra(MainActivity.EXTRA_MESSAGE));
		
		// Show the Up button in the action bar.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		//Draw board
		setupBoard();
		reloadBoard(); // force draw w/ colors
	}
	
	@SuppressLint("NewApi")
	private void setupBoard() {
		final OnClickListener placeBulletButtonListener = new OnClickListener() {
            @Override
			public void onClick(View view) {
					onPlaceBulletButtonClicked(view);
			}
        };
        int player = gameLogic.getActivePlayer();
		for (int col = 0; col < GameLogic.NUM_COLS; col++) {
			// button at head of column
			ImageButton ib = new ImageButton(this);
            ib.setOnClickListener(placeBulletButtonListener);
            ib.setPadding(5,5,5,5);
            ib.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ib.setTag(Integer.toString(col));            
            ib.setLayoutParams(new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT, 1));
            buttonHolder.addView(ib);
            applyPlayerBullet(player, buttonHolder, Integer.toString(col));
			//column of board
			LinearLayout columnLayout = new LinearLayout(this);
			columnLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT,1));
			columnLayout.setGravity(Gravity.CENTER);
			columnLayout.setOrientation(LinearLayout.VERTICAL);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			    LayoutTransition lt = new LayoutTransition();
			    lt.setInterpolator(LayoutTransition.APPEARING, new BounceInterpolator());
			    columnLayout.setLayoutTransition(lt);
			}
			for (int row = 0; row < GameLogic.NUM_ROWS; row++) {
				ImageView iv = new ImageView(this);
				iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
				iv.setBackgroundResource(R.drawable.gridcell);
				iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
				//iv.setAdjustViewBounds(true);
				iv.setTag(Integer.toString(col) + Integer.toString(row));
				columnLayout.addView(iv);
			}
			boardLayout.addView(columnLayout);
		}
//		float row_height = (float) boardLayout.getHeight() / (float) GameLogic.NUM_ROWS;
//		float col_width = (float) boardLayout.getWidth() / (float) GameLogic.NUM_COLS;
//		// row_height / col_width
//		if (row_height > col_width) {
//			boardLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) row_height * GameLogic.NUM_ROWS));
//		} else {
//			boardLayout.setLayoutParams(new RelativeLayout.LayoutParams((int) col_width * GameLogic.NUM_COLS, LayoutParams.MATCH_PARENT));
//		}
	}
	
	private void nextTurn() {
		gameLogic.nextTurn();
		int player = gameLogic.getActivePlayer();
		for (int col = 0; col < GameLogic.NUM_COLS; col++) {
			applyPlayerBullet(player, buttonHolder, Integer.toString(col));
		}
	}
	
	private void attemptToPlaceBullet(int col, int row) {
		if (row == -1) { // disallowed move
			Toast.makeText(this, "Illegal move, try again", Toast.LENGTH_SHORT).show();
		} else {
			applyPlayerBullet(gameLogic.getBoard()[col][row], boardLayout, Integer.toString(col) + Integer.toString(row));
			int[] win = gameLogic.checkForWin(col, row); // winner, streak
			int winner = win[0];
			int streak = win[1];
			if (streak >= 4) {
				gameTextView.setText("CONGRATULATIONS! Player " + Integer.toString(winner) + "!!!");
				// 1. Instantiate an AlertDialog.Builder with its constructor
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				// 2. Chain together various setter methods to set the dialog characteristics
				builder.setMessage("Player " + Integer.toString(winner) + " wins!!!")
				       .setTitle(R.string.dialog_win_title)
				       .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}});
				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			nextTurn();
		}
	}
	
	private void applyPlayerBullet(int player, ViewGroup parent, String tag) {
		ImageView iv = (ImageView) parent.findViewWithTag(tag);
		System.out.println(iv.getTag());
		switch (player) {
		case GameLogic.LIGHT_PLAYER:
			iv.setImageResource(R.drawable.ic_bullet_smiley); 
			iv.setColorFilter(getResources().getColor(R.color.light_player));		
//			iv.setVisibility(View.VISIBLE);
			break;
		case GameLogic.DARK_PLAYER:
			iv.setImageResource(R.drawable.ic_bullet_smiley);
			iv.setColorFilter(getResources().getColor(R.color.dark_player));
//			iv.setVisibility(View.VISIBLE);
			break;
		default:
			iv.setImageResource(R.drawable.bullet_zilch);
//			iv.setVisibility(View.INVISIBLE);
			iv.clearColorFilter();
		}
	}
	
	private void reloadBoard() {
		int[][] board = gameLogic.getBoard();
		for (int col = 0; col < GameLogic.NUM_COLS; col++) {
			for (int row = 0; row < GameLogic.NUM_ROWS; row++) {
				applyPlayerBullet(board[col][row], boardLayout, Integer.toString(col) + Integer.toString(row));
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putSerializable("BOARD", gameLogic.getBoard());
		bundle.putInt("ACTIVE_PLAYER", gameLogic.getActivePlayer());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		gameLogic.setBoard(bundle.getSerializable("BOARD"));
		gameLogic.setActivePlayer(bundle.getInt("ACTIVE_PLAYER"));
		reloadBoard();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onPlaceBulletButtonClicked(View view) {
		int col = Integer.parseInt((String) view.getTag());
		int row = gameLogic.addBullet(col);
		attemptToPlaceBullet(col, row);
	}
		
	public void onNewGameButtonClicked(View view) {
		gameLogic.createNewGame();
		gameTextView.setText(""); // TODO keep running tally of wins
		reloadBoard();
	}
}
