package myandroid.testapp;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class myandroid.testapp.TetrisTest \
 * myandroid.testapp.tests/android.test.InstrumentationTestRunner
 */
public class TetrisTest extends ActivityInstrumentationTestCase<Tetris> {

    public TetrisTest() {
        super("myandroid.testapp", Tetris.class);
    }

}
