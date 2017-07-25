package jp.co.kaiwaredaikon320.syushi;

import android.os.Handler;
import android.view.View;
import android.view.View.OnLongClickListener;


public class LongClickRepeatAdapter {

    /**
     * 連続してボタンを押す間隔のデフォルト値 (ms)
     */
 // private static final int REPEAT_INTERVAL = 100;
    private static final int REPEAT_INTERVAL = 50;
    private static boolean isContinue;

    // コンストラクタ
	public LongClickRepeatAdapter( View view ){
		bless( REPEAT_INTERVAL, view );
	}

	/**
     * リピート間隔を指定して、Viewに長押しリピート処理を付加する
     *
     * @param repeatInterval
     *            連続してボタンを押す間隔(ms)
     * @param view
     *            付加対象のView
     */
    public void bless(final int repeatInterval, final View view) {
		final Handler handler = new Handler();
		setContinueFlg( false );
		final Runnable repeatRunnable = new Runnable() {
			@Override
			public void run() {
				// 連打フラグをみて処理を続けるか判断する
				Trace.d("キー入力監視中");
				if( !getContinueFlg() ){
					return;
				}
				// クリック処理を実行する
				view.performClick();
				// 連打間隔を過ぎた後に、再び自分を呼び出す
				handler.postDelayed(this, repeatInterval);
			}
		};
		view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				setContinueFlg( true );
				// 長押しをきっかけに連打を開始する
				handler.post(repeatRunnable);
				return true;
			}
		});

// スクロールビュー対策
// スクロールビューが動作すると通知が届かないため、親のactivityで解除処理をする
/*
		// タッチイベントを乗っ取る
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	// キーから指が離されたら連打をオフにする
                if (event.getAction() == MotionEvent.ACTION_UP) {
            		setContinueFlg( false );
                }
                return false;
            }
        });
*/
    }

	public void setContinueFlg( boolean flg ){
		isContinue = flg;
	}

	public boolean getContinueFlg( ){
		return isContinue;
	}

}