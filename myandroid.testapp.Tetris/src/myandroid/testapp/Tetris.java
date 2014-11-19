package myandroid.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.widget.Button;
import android.content.Intent;
import android.util.Log;

public class Tetris extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// ���� ��Ƽ��Ƽ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d("[TetrisLog]", "Tetris#onCreate()");

		// Start Button ���
		Button startButton = (Button) findViewById(R.id.startGame);
		startButton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {

				// Intent ����
				Intent intent = new Intent(Tetris.this, PlayGround.class);
				// Activity ����
				startActivity(intent);
			}
		});
	}
}