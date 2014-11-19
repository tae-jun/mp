package myandroid.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.View;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.content.Context;
import android.util.Log;

import myandroid.testapp.TetrisView.TetrisThread;

public class PlayGround extends Activity
{
	private TetrisView mTetrisView;

	//private TetrisThread mTetrisThread;

	private static final int MENU_START = 1;

	private static final int MENU_STOP = 2;

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Menu 버튼을 눌렀을때 호출되는 부분
		super.onCreateOptionsMenu( menu );

		menu.add( 0, MENU_START, 0, R.string.menu_start );
		menu.add( 0, MENU_STOP, 0, R.string.menu_stop );

		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId() ) {
			case MENU_START:
				return true;
			case MENU_STOP:
				return true;
		}

		return false;
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //윈도 매니져 호출
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		// XML 파일 로드. 
		setContentView( R.layout.tetris );
		// View 등록.
		mTetrisView = (TetrisView) findViewById( R.id.tetris );
		//mTetrisThread = mTetrisView.getThread();

		//mTetrisView.setTextView( (TextView) findViewById( R.id.gamemsg );
		Log.d( "[TetrisLog]", "PlayGround#onCreate()" );
    }
}
