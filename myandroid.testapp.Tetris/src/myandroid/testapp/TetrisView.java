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

		// Thread 제어를 위한 Flag
		private boolean mRun = true;
		private SurfaceHolder mSurfaceHolder;
		
		private Paint mPaint;

		// Tetris가 진행되는 크기
		private final int PGWidth = 13;
		private final int PGHeight = 25;
		private int[][] playGround = new int[PGWidth][PGHeight];

		
		private final int BlockLength = 15;
		private final int TopMargin = 10;

		// 현재 블록과 위치 저장을 위한 변수. 
		private int currentBrick;
		private int rotate;
		private int currentX;
		private int currentY;

		// 랜덤하게 나오는 블록 생성을 위한 Random 변수.
		private Random random;

		// 블록 타입. 
		private final int EMPTY = 0;
		private final int BRICK1 = 1;
		private final int BRICK2 = 2;
		private final int BRICK3 = 3;
		private final int BRICK4 = 4;
		private final int BRICK5 = 5;
		private final int BRICK6 = 6;
		private final int BRICK7 = 7;
		private final int WALL = 8;
		//CX와 CY를 이용하여 7가지 블록을 생성. 
		// 4X4 타일에 7가지 블록을 생성하게 된다. 
		// 예를 들어 0번은 일자로 긴 블록인데 
		// CY[0][0]은 가로로 누은 일자 블록이 되므로, 좌표가 변경되지 않기 때문에 {0,0,0,0}
		// CY[0][0]은 세로로 서있는 일자 블록이 되므로, CurrentX, CurrentX + 1, CurrentX + 2, CurrentX - 1 에 일자에 해당하는 코드를 부여하게 된다. 
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

		// Surface를 제어하는 Thread 
		public TetrisThread( SurfaceHolder surfaceHolder, Context context ) {
			
			
			mSurfaceHolder = surfaceHolder;
			random = new Random();

			// PlayGround에  게임영역을 설정.
			for( int x=0; x<PGWidth; x++ ) {
				for( int y=0; y<PGHeight; y++ ) {
					playGround[x][y] = ( y == PGHeight-1 || x == 0 || x == PGWidth-1 ) ? WALL : EMPTY;
				}
			}
			// 게임 영역 표시.
			mPaint = new Paint();
			mPaint.setAntiAlias( true );
			mPaint.setStyle( Paint.Style.FILL );
		}
		private void updatePlayGround( Canvas c ) {
			// Canvas Update.
			Paint paint = mPaint;

			c.drawColor( Color.BLACK );

			// 게임영역 전체를 스캔하여 각 타일 타입에 따라서 Bitmap생성. 
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
					// Surface에 Bitmap 쓰기 .
					c.drawRect( x*BlockLength+TopMargin+1, 
								y*BlockLength+TopMargin+1, 
								x*BlockLength+TopMargin+BlockLength-1, 
								y*BlockLength+TopMargin+BlockLength-1, 
								paint );
				}
			}
		}
		// 게임이 시작될 때 호출. 
		private void startNewBrick() {
			//블록이 내려오는 초기 위치
			//새로운 블록 생성. 
			currentX = 6;
			currentY = 1;
			// 랜덤한 블록과 모양
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
		// 타일이 비어있는지 확인. 
		private boolean checkEmpty( int keyCode ) {
			//현재 타일이 위치한 좌표.
			int tempX = currentX;
			int tempY = currentY;
			int tempRotate = rotate;
			// 각 키 이벤트에 따라 좌표 수정. 
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
			// 수정된 임시 좌표에 따라서 벽에 부딪히거나 다른 타일이 있을 경우 False 리턴.
			for( int i=0; i<4; i++ ) {
				if( playGround[tempX+CX[currentBrick][tempRotate][i]][tempY+CY[currentBrick][tempRotate][i]] != EMPTY )
					return false;
			}
			// 수정된 임시 좌표에 Empty 타일이면 ture 리턴
			return true;
		}

		private boolean moveBrick( int keyCode ) {
			clearBrick();
			// 움직이려는 곳의 타일이 비었는지 확인. 
			if( checkEmpty( keyCode ) == false ) {
				for( int i=0; i<4; i++ ) {
					playGround[currentX+CX[currentBrick][rotate][i]][currentY+CY[currentBrick][rotate][i]] = currentBrick+1;
				}
				// 아래 버튼이 눌러지면 현재 X 좌표에 블록이 고정되고, 라인이 지워지는지 판단. 
				if( keyCode == KeyEvent.KEYCODE_DPAD_DOWN ) {
					deleteLine();
					// 새로운 블록이 생성.
					startNewBrick();
				}

				return true;
			}
			//  타일을 실제로 움직임. 
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
		// 라인 삭제 Method
		private void deleteLine() {
			boolean isEmpty = false;
			// 벽과 맨위 라인을 제외하고 빈 칸이 있으면 true 반환
			for( int y=PGHeight-2; y>1; y-- ) {
				for( int x=1; x<PGWidth-1; x++ ) {
					if( playGround[x][y] == EMPTY ) {
						isEmpty = true;
						break;
					}
				}
				// 벽과 맨위 라인을 제외하고 빈 칸이 없으면  라인 삭제
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
			// 각 키 매치 이 부분은 실험장비로 옮길 시 반드시 Touch 인터페이스로 변경해야함. 
			if( keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
				keyCode == KeyEvent.KEYCODE_DPAD_UP ||
				keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
				keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ) {
				//키 이벤트가 들어올경우 해당하는 키 코드에 따라 블록 이동.
				moveBrick( keyCode );
			}
			// 키 이벤트가 없을 경우 대기 
			else if( keyCode == KeyEvent.KEYCODE_SPACE ) {
				while( moveBrick( KeyEvent.KEYCODE_DPAD_DOWN ) == false );
			}
			// Surface 갱신. 
			doDraw();

			return false;
		}

		private void doDraw() {

			Canvas c = null;
			// Surface에 변경된 Bitmap 그리기.
			try {
				c = mSurfaceHolder.lockCanvas( null );
				synchronized( mSurfaceHolder ) {
					updatePlayGround( c );
				}
			}
			finally {
				// Canvas가 NULL이 아니면 화면에 Surface에 작성된 BitMap 뿌리기. 
				if( c != null ) {
					mSurfaceHolder.unlockCanvasAndPost( c );
				}
			}
		}

		public void run() {
			startNewBrick();

			//mRun Flag를 이용하여 Thread를 종료하지 않은 상태에서 게임을 멈춘다.
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
		// 쓰레드를 중지하지 않고, run Method에서 Flag를 통해 게임 컨트롤. 
		public void setRunning( boolean f ) {
			mRun = f;
		}
	}

	private TetrisThread tetrisThread;

	public TetrisView( Context context, AttributeSet attrs ) {
		//TetrisView 생성자.
		super( context, attrs );

		Log.d( "[TetrisLog]", "TetrisView#Constructor" );
		// SurfaceHoler 추출. 
		SurfaceHolder surfaceHolder = getHolder();
		// CallBack 등록.
		surfaceHolder.addCallback( this );
		// Surface 제어를 위한 Thread 생성. 
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
    // Surface가 변경될 때마다 Log 출력
	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
		Log.d( "[TetrisLog]", "TetrisView#surfaceChanged(width="+width+", height="+height+")" );
	}

	public void surfaceCreated( SurfaceHolder holder ) {
		// Surface가 생성되었을 때 Thraed 시작.
		Log.d( "[TetrisLog]", "TetrisView#surfaceCreated()" );
		tetrisThread.setRunning( true );
		tetrisThread.start();
	}

	public void surfaceDestroyed( SurfaceHolder holder ) {
		//Surface가 파괴되었을 때 호출. 
		Log.d( "[TetrisLog]", "TetrisView#surfaceDestroyed()" );
		boolean retry = true;
		// Thread가 파괴되지 않고 게임이 멈춤. 
		tetrisThread.setRunning( false );

		// 재시도 요청이 들어오면
		while( retry ) {
			try {
				// Thread 다시 실행.
				tetrisThread.join();
				retry = false;
			}
			catch( InterruptedException ie ) {
			}
		}
	}
}

