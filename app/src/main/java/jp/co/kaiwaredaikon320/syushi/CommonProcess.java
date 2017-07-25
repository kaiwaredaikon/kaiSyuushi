package jp.co.kaiwaredaikon320.syushi;

import android.graphics.Color;
import android.widget.TextView;

public class CommonProcess {

	/*
	 * 収支によって、viewの色を変更するメソッド
	 */
	public static void changeTotalTextColor1( TextView _textview, int total ){

		// ±0
		if( total == 0 ){
			_textview.setTextColor(Color.BLACK);
		}
		// ＋収支
		else if( total > 0 ){
			_textview.setTextColor(Color.BLUE);
		}
		// －収支
		else{
			_textview.setTextColor(Color.RED);
		}
	}

	/*
	 * 収支によって、viewの色を変更するメソッド
	 */
	public static void changeTotalTextColor2( TextView _textview, int total ){

		// ±0
		if( total == 0 ){
			_textview.setTextColor(Color.WHITE);
		}
		// ＋収支
		else if( total > 0 ){
			_textview.setTextColor(Color.GREEN);
		}
		// －収支
		else{
			_textview.setTextColor(Color.RED);
		}
	}
}
