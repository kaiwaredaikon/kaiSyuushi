package jp.co.kaiwaredaikon320.syushi;

import java.util.regex.Pattern;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

public class Trace {

	public static final boolean DEBUG_MODE = true;

    private static final String TAG = "TRACE";

    public static final void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, getClassLine( ) + " " + msg );
        }
    }

    public static final void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d( tag, getClassLine() + " " + msg );
        }
    }

    public static final void e(String msg){
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static final void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg );
        }
    }

    public static final void i(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static final void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static final void v(String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static final void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static final void w(String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static final void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static final void heap(){
        heap(TAG);
    }

    public static final void heap(String tag) {
        if (BuildConfig.DEBUG){
            String msg = "heap : Free=" + Long.toString(Debug.getNativeHeapFreeSize() / 1024) + "kb" +
                    ", Allocated=" + Long.toString(Debug.getNativeHeapAllocatedSize() / 1024) + "kb" +
                    ", Size=" + Long.toString(Debug.getNativeHeapSize() / 1024) + "kb";

            Log.v(tag, msg);
        }
    }

    /**
     * クラス名、行数などget
     *
     * @return className#methodName:line
     */
    private static String getClassLine() {
        final StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        final String cla = trace.getClassName();
        Pattern pattern = Pattern.compile("[\\.]+");
        final String[] splitedStr = pattern.split(cla);
        final String simpleClass = splitedStr[splitedStr.length - 1];
        final String mthd = trace.getMethodName();
        final int line = trace.getLineNumber();
        final String tag = simpleClass + "#" + mthd + "(" + line +")";

        return tag;
    }


    /**
     *
     * @param con context
     * @return 画面の解像度関連をまとめてStringを返す
     */
    public static String getMetricsDP( Context con ) {

    	String str = "";

    	if (DEBUG_MODE){

	    	str += "density=" + con.getResources().getDisplayMetrics().density;

	    	if( con.getResources().getDisplayMetrics().density == 1.0f ){
	    		str += "(mdpi)\n";
	    	}
	    	else if( con.getResources().getDisplayMetrics().density == 1.5f ){
	    		str += "(hdpi)\n";
	    	}
	    	else if( con.getResources().getDisplayMetrics().density == 2.0f ){
	    		str += "(xdpi)\n";
	    	}
	    	else if( con.getResources().getDisplayMetrics().density == 3.0f ){
	    		str += "(xxdpi)\n";
	    	}
	    	else if( con.getResources().getDisplayMetrics().density == 4.0f ){
	    		str += "(xxxdpi)\n";
	    	}
	    	else{
	    		str += "(unknown)\n";
	    	}

	    	str += "densityDpi=" + con.getResources().getDisplayMetrics().densityDpi + "\n";
	    	str += "解像度:" + con.getResources().getDisplayMetrics().widthPixels + "×" + con.getResources().getDisplayMetrics().heightPixels + "\n";
	    	str += "Dpi:" + con.getResources().getDisplayMetrics().xdpi + "×" + con.getResources().getDisplayMetrics().ydpi + "\n";
	    	str += "DP:" + ( con.getResources().getDisplayMetrics().widthPixels / con.getResources().getDisplayMetrics().density ) + "×" + ( con.getResources().getDisplayMetrics().heightPixels / con.getResources().getDisplayMetrics().density ) + "\n";
    	}

    	return str;
    }

}
