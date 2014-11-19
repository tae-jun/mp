package myandroid.testapp;

import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import java.util.Random;

class TetrisView extends SurfaceView implements SurfaceHolder.Callback
{
	class TetrisThread extends Thread {

		// Thread ��� ���� Flag
		private boolean mRun = true;
		private SurfaceHolder mSurfaceHolder;
		
		private Paint mPaint;

		// Tetris�� ����Ǵ� ũ��
		private final int PGWidth = 13;
		private final int PGHeight = 25;
		private int[][] playGround = new int[PGWidth][PGHeight];

		
		private final int BlockLength = 15;
		private final int TopMargin = 10;

		// ���� ��ϰ� ��ġ ������ ���� ����. 
		private int currentBrick;
		private int rotate;
		private int currentX;
		private int currentY;

		// �����ϰ� ������ ��� ������ ���� Random ����.
		private Random random;

		// ��� Ÿ��. 
		private final int EMPTY = 0;
		private final int BRICK1 = 1;
		private final int BRICK2 = 2;
		private final int BRICK3 = 3;
		private final int BRICK4 = 4;
		private final int BRICK5 = 5;
		private final int BRICK6 = 6;
		private final int BRICK7 = 7;
		private final int WALL = 8;
		//CX�� CY�� �̿��Ͽ� 7���� ����� ����. 
		// 4X4 Ÿ�Ͽ� 7���� ����� �����ϰ� �ȴ�. 
		// ���� ��� 0���� ���ڷ� �� ����ε� 
		// CY[0][0]�� ���η� ���� ���� ����� �ǹǷ�, ��ǥ�� ������� �ʱ� ������ {0,0,0,0}
		// CY[0][0]�� ���η� ���ִ� ���� ����� �ǹǷ�, CurrentX, CurrentX + 1, CurrentX + 2, CurrentX - 1 �� ���ڿ� �ش��ϴ� �ڵ带 �ο��ϰ� �ȴ�. 
		private final int[][][] CX = 
		{
			{
				{0, 1, 2, -1},
				{0, 0, 0, 0},
				{0, 1, 2, -1},
				{0, 0, 0, 0},
			},
			{
				{0, 1, 0, 1},
				{0, 1, 0, 1},
				{0, 1, 0, 1},
				{0, 1, 0, 1},
			},
			{
				{0, -1, 0, 1},
				{0, 0, -1, -1},
				{0, -1, 0, 1},
				{0, 0, -1, -1},
			},
			{
				{0, -1, 0, 1},
				{0, -1, -1, 0},
				{0, -1, 0, 1},
				{0, -1, -1, 0},
			},
			{
				{0, -1, 1, -1},
				{0, 0, 0, -1},
				{0, -1, 1, 1},
				{0, 0, 0, 1},
			},
			{
				{0, 1, -1, 1},
				{0, 0, 0, -1},
				{0, 1, -1, -1},
				{0, 0, 0, 1},
			},
			{
				{0, -1, 1, 0},
				{0, 0, 0, 1},
				{0, -1, 1, 0},
				{0, -1, 0, 0},
			},
		};

		private final int[][][] CY = 
		{
			{
				{0, 0, 0, 0},
				{1, 2, 0, -1},
				{0, 0, 0, 0},
				{1, 2, 0, -1},
			},
			{
				{0, 0, 1, 1},
				{0, 0, 1, 1},
				{0, 0, 1, 1},
				{0, 0, 1, 1},
			},
			{
				{0, 0, -1, -1},
				{0, 1, 0, -1},
				{0, 0, -1, -1},
				{0, 1, 0, -1},
			},
			{
				{0, -1, -1, 0},
				{0, 0, 1, -1},
				{0, -1, -1, 0},
				{0, 0, 1, -1},
			},
			{
				{0, 0, 0, -1},
				{0, -1, 1, 1},
				{0, 0, 0, 1},
				{0, -1, 1, -1},
			},
			{
				{0, 0, 0, -1},
				{0, 1, -1, -1},
				{0, 0, 0, 1},
				{0, -1, 1, 1},
			},
			{
				{0, 0, 0, 1},
				{0, -1, 1, 0},
				{0, 0, 0, -1},
				{0, 0, -1, 1},
			},
		};

		// Surface�� �����ϴ� Thread 
		public TetrisThread( SurfaceHolder surfaceHolder, Context context ) {
			
			
			mSurfaceHolder = surfaceHolder;
			random = new Random();

			// PlayGround��  ���ӿ����� ����.
			for( int x=0; x<PGWidth; x++ ) {
				for( int y=0; y<PGHeight; y++ ) {
					playGround[x][y] = ( y == PGHeight-1 || x == 0 || x == PGWidth-1 ) ? WALL : EMPTY;
				}
			}
			// ���� ���� ǥ��.
			mPaint = new Paint();
			mPaint.setAntiAlias( true );
			mPaint.setStyle( Paint.Style.FILL );
		}
		private void updatePlayGround( Canvas c ) {
			// Canvas Update.
			Paint paint = mPaint;

			c.drawColor( Color.BLACK );

			// ���ӿ��� ��ü�� ��ĵ�Ͽ� �� Ÿ�� Ÿ�Կ� ���� Bitmap����. 
			for( int x=0; x<PGWidth; x++ ) {
				for( int y=0; y<PGHeight; y++ ) {
					switch( playGround[x][y] ) {
						case WALL:
							paint.setColor( Color.WHITE );
							break;
						case BRICK1:
							paint.setColor( Color.YELLOW);
							break;
						case BRICK2:
							paint.setColor( Color.RED);
							break;
						case BRICK3:
							paint.setColor( Color.GREEN);
							break;
						case BRICK4:
							paint.setColor( Color.BLUE);
							break;
						case BRICK5:
							paint.setColor( Color.CYAN );
							break;
						case BRICK6:
							paint.setColor( Color.MAGENTA );
							break;
						case BRICK7:
							paint.setColor( Color.GRAY );
							break;
						case EMPTY:
							continue;
					}
					// Surface�� Bitmap ���� .
					c.drawRect( x*BlockLength+TopMargin+1, 
								y*BlockLength+TopMargin+1, 
								x*BlockLength+TopMargin+BlockLength-1, 
								y*BlockLength+TopMargin+BlockLength-1, 
								paint );
				}
			}
		}
		// ������ ���۵� �� ȣ��. 
		private void startNewBrick() {
			//����� �������� �ʱ� ��ġ
			//���ο� ��� ����. 
			currentX = 6;
			currentY = 1;
			// ������ ��ϰ� ���
			currentBrick = random.nextInt( 7 ); 
			Log.d( "[TetrisLog]", "currentBrick="+currentBrick );
			rotate = random.nextInt( 4 );
			Log.d( "[TetrisLog]", "rotate="+rotate );
		}
		// 
		private void clearBrick() {
			
			// 
			int oldX = currentX;
			int oldY = currentY;

			for( int i=0; i<4; i++ ) {
				playGround[oldX + 
				           	CX[currentBrick][rotate][i]][oldY + CY[currentBrick][rotate][i]] = EMPTY;
			}
		}
		// Ÿ���� ����ִ��� Ȯ��. 
		private boolean checkEmpty( int keyCode ) {
			//���� Ÿ���� ��ġ�� ��ǥ.
			int tempX = currentX;
			int tempY = currentY;
			int tempRotate = rotate;
			// �� Ű �̺�Ʈ�� ���� ��ǥ ����. 
			switch( keyCode ) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					--tempX;
					break;
				case KeyEvent.KEYCODE_DPAD_UP:
					++tempRotate;
					tempRotate = tempRotate%4;
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					++tempX;
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					++tempY;
					break;
			}
			// ������ �ӽ� ��ǥ�� ���� ���� �ε����ų� �ٸ� Ÿ���� ���� ��� False ����.
			for( int i=0; i<4; i++ ) {
				if( playGround[tempX+CX[currentBrick][tempRotate][i]][tempY+CY[currentBrick][tempRotate][i]] != EMPTY )
					return false;
			}
			// ������ �ӽ� ��ǥ�� Empty Ÿ���̸� ture ����
			return true;
		}

		private boolean moveBrick( int keyCode ) {
			clearBrick();
			// �����̷��� ���� Ÿ���� ������� Ȯ��. 
			if( checkEmpty( keyCode ) == false ) {
				for( int i=0; i<4; i++ ) {
					playGround[currentX+CX[currentBrick][rotate][i]][currentY+CY[currentBrick][rotate][i]] = currentBrick+1;
				}
				// �Ʒ� ��ư�� �������� ���� X ��ǥ�� ����� �����ǰ�, ������ ���������� �Ǵ�. 
				if( keyCode == KeyEvent.KEYCODE_DPAD_DOWN ) {
					deleteLine();
					// ���ο� ����� ����.
					startNewBrick();
				}

				return true;
			}
			//  Ÿ���� ������ ������. 
			switch( keyCode ) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					--currentX;
					break;
				case KeyEvent.KEYCODE_DPAD_UP:
					++rotate;
					rotate = rotate%4;
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					++currentX;
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					++currentY;
					break;
			}

			for( int i=0; i<4; i++ ) {
				playGround[currentX+CX[currentBrick][rotate][i]][currentY+CY[currentBrick][rotate][i]] = currentBrick+1;
			}

			return false;
		}
		// ���� ���� Method
		private void deleteLine() {
			boolean isEmpty = false;
			// ���� ���� ������ �����ϰ� �� ĭ�� ������ true ��ȯ
			for( int y=PGHeight-2; y>1; y-- ) {
				for( int x=1; x<PGWidth-1; x++ ) {
					if( playGround[x][y] == EMPTY ) {
						isEmpty = true;
						break;
					}
				}
				// ���� ���� ������ �����ϰ� �� ĭ�� ������  ���� ����
				if( isEmpty == false ) {
					for( int i=y; i>1; i-- ) {
						for( int x=1; x<PGWidth-1; x++ ) {
							playGround[x][i] = playGround[x][i-1];
						}
					}

					++y;
				}

				isEmpty = false;
			}
		}

		boolean doKeyDown( int keyCode, KeyEvent msg ) {
			Log.d( "[TetrisLog]", "keyCode="+keyCode );
			// �� Ű ��ġ �� �κ��� �������� �ű� �� �ݵ�� Touch �������̽��� �����ؾ���. 
			if( keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
				keyCode == KeyEvent.KEYCODE_DPAD_UP ||
				keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
				keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ) {
				//Ű �̺�Ʈ�� ���ð�� �ش��ϴ� Ű �ڵ忡 ���� ��� �̵�.
				moveBrick( keyCode );
			}
			// Ű �̺�Ʈ�� ���� ��� ��� 
			else if( keyCode == KeyEvent.KEYCODE_SPACE ) {
				while( moveBrick( KeyEvent.KEYCODE_DPAD_DOWN ) == false );
			}
			// Surface ����. 
			doDraw();

			return false;
		}

		private void doDraw() {

			Canvas c = null;
			// Surface�� ����� Bitmap �׸���.
			try {
				c = mSurfaceHolder.lockCanvas( null );
				synchronized( mSurfaceHolder ) {
					updatePlayGround( c );
				}
			}
			finally {
				// Canvas�� NULL�� �ƴϸ� ȭ�鿡 Surface�� �ۼ��� BitMap �Ѹ���. 
				if( c != null ) {
					mSurfaceHolder.unlockCanvasAndPost( c );
				}
			}
		}

		public void run() {
			startNewBrick();

			//mRun Flag�� �̿��Ͽ� Thread�� �������� ���� ���¿��� ������ �����.
			while( mRun ) {
				doDraw();
				moveBrick( KeyEvent.KEYCODE_DPAD_DOWN );
				

				try {
							
					Thread.sleep( 1000 );
				}
				catch( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		// �����带 �������� �ʰ�, run Method���� Flag�� ���� ���� ��Ʈ��. 
		public void setRunning( boolean f ) {
			mRun = f;
		}
	}

	private TetrisThread tetrisThread;

	public TetrisView( Context context, AttributeSet attrs ) {
		//TetrisView ������.
		super( context, attrs );

		Log.d( "[TetrisLog]", "TetrisView#Constructor" );
		// SurfaceHoler ����. 
		SurfaceHolder surfaceHolder = getHolder();
		// CallBack ���.
		surfaceHolder.addCallback( this );
		// Surface ��� ���� Thread ����. 
		tetrisThread = new TetrisThread( surfaceHolder, context );

		setFocusable( true );
	}


	@Override
	public boolean onKeyDown( int keyCode, KeyEvent msg ) {
		return tetrisThread.doKeyDown( keyCode, msg );
	}

    @Override
	public void onWindowFocusChanged( boolean hasWindowFocus ) {
		Log.d( "[TetrisLog]", "TetrisView#onWindowFocusChanged()" );
	}
    // Surface�� ����� ������ Log ���
	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
		Log.d( "[TetrisLog]", "TetrisView#surfaceChanged(width="+width+", height="+height+")" );
	}

	public void surfaceCreated( SurfaceHolder holder ) {
		// Surface�� �����Ǿ��� �� Thraed ����.
		Log.d( "[TetrisLog]", "TetrisView#surfaceCreated()" );
		tetrisThread.setRunning( true );
		tetrisThread.start();
	}

	public void surfaceDestroyed( SurfaceHolder holder ) {
		//Surface�� �ı��Ǿ��� �� ȣ��. 
		Log.d( "[TetrisLog]", "TetrisView#surfaceDestroyed()" );
		boolean retry = true;
		// Thread�� �ı����� �ʰ� ������ ����. 
		tetrisThread.setRunning( false );

		// ��õ� ��û�� ������
		while( retry ) {
			try {
				// Thread �ٽ� ����.
				tetrisThread.join();
				retry = false;
			}
			catch( InterruptedException ie ) {
			}
		}
	}
}

