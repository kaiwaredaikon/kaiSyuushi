package jp.co.kaiwaredaikon320.syushi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.kaiwaredaikon320.syushi.view.calendar.CalendarSelectionEvent;
import jp.co.kaiwaredaikon320.syushi.view.calendar.CalendarView;
import jp.co.kaiwaredaikon320.syushi.view.calendar.DateInfo;
import jp.co.kaiwaredaikon320.syushi.view.calendar.OnCalendarSelectionListener;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener, OnCalendarSelectionListener {

	public static final int LIST_VIEW_ACTIVITY = 3434;
	public static final int MY_PREFERENCE_ACTIVITY = 2222;
	public static final int MY_PREFERENCE_OPTION = 3333;
	public static final int WEB_VIEW_ACTIVITY = 4444;

	protected GestureDetector detector;
	private CalendarView calView;

    private int yearBak; // 年
	private int monthBak; // 月

	private String forIntentMonth; // インテント引継ぎ用

	// 月収支用
	private TextView viewMonth2;
	private TextView viewMonth3;
	private TextView viewMonth4;

	// 年収支用
	private TextView viewYear2;
	private TextView viewYear3;
	private TextView viewYear4;

	// 総合収支
	private MySQLiteOpenHelper mySqlite;
	private SQLiteDatabase calSyushiDatabase;

    private Menu mainMenu;
    private boolean menuDrawFlg;

	public static boolean onShowPressFlg;

	// プリファレンス
	private SharedPreferences pref;

	// データベース作成
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Trace.d( "onCreate 1 ");

		setContentView(R.layout.activity_main);

        Trace.d( "onCreate 2 = " + findViewById(R.id.header_text) );

		// 画面設定
		DisplayMetrics metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		Trace.d("metrics.density = " + metrics.density);

		float d = (metrics.density);
		float w = (metrics.widthPixels / metrics.density);
		float h = (metrics.heightPixels / metrics.density);

		Trace.d("kkkkkkkkk d = " + d);
		Trace.d("kkkkkkkkk w = " + w);
		Trace.d("kkkkkkkkk h = " + h);

		AppSetting.setDensity(d);
		AppSetting.setMyDensity(AppSetting.getDensity());
		AppSetting.setDP_W(w);
		AppSetting.setDP_H(h);

		// 液晶の横幅に合わせて倍率を取得する
		AppSetting.setContentWidthRatio(AppSetting.getMydp(metrics.widthPixels, AppSetting.getDensity()));

		onShowPressFlg = false;

		// ウィジェット取得
		Trace.d("" + findViewById(R.id.Calendar));
		calView = (CalendarView) findViewById(R.id.Calendar);

		calView.setView(this);
		calView.addOnCalendarSelectionListener(this);

		// 現在の月と日を代入するg
		DateInfo dateInfo = calView.getDateInfo();

		yearBak = dateInfo.getYear();
		monthBak = dateInfo.getMonth();
		forIntentMonth = dateInfo.getYM();

		// ウィジェットのIDを取得する
		viewMonth2 = (TextView) this.findViewById(R.id.textViewMonth2_2);
		// 回収(月)
		viewMonth3 = (TextView) this.findViewById(R.id.textViewMonth3_2);
		// 収支(月)
		viewMonth4 = (TextView) this.findViewById(R.id.textViewMonth4_2);

		// 投資(年)
		viewYear2 = (TextView) this.findViewById(R.id.textViewYear2_2);
		// 回収(年)
		viewYear3 = (TextView) this.findViewById(R.id.textViewYear3_2);
		// 収支(年)
		viewYear4 = (TextView) this.findViewById(R.id.textViewYear4_2);

		// 月間収支の描画
		drawMonthTotal();

		// 年間収支
		drawYearTotal(yearBak);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		// プリファレンスの情報を取得する
		loadPreference();

		// GestureDetectorインスタンス作成
		this.detector = new GestureDetector(this, this);

        openOptionsMenu();
	}


	/**
	 * コンテンツ領域のサイズを取得する
	 */
	/*
	 * @Override public void onWindowFocusChanged(boolean hasFocus) {
	 * super.onWindowFocusChanged(hasFocus); // Viewサイズを取得する LinearLayout r =
	 * (LinearLayout) findViewById(R.id.LinearLayout03); }
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        Trace.d( "onCreateOptionsMenuonCreateOptionsMenuonCreateOptionsMenu" );
		// デバッグなし
		if (!Trace.DEBUG_MODE) {
			getMenuInflater().inflate(R.menu.main, menu);
		} else {
			getMenuInflater().inflate(R.menu.main_d, menu);
		}

        mainMenu = menu;
		return true;
	}


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        Trace.d( "onPrepareOptionsMenuonPrepareOptionsMenu" );

        if( menuDrawFlg ){
            return true;
        }
        else{
            return false;
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// 総合データ
		if (id == R.id.action_settings1) {
			Intent intent1 = new android.content.Intent(this, DataPreferenceActivity.class);
			// トランジションアニメーションを行わずに起動
			// intent1.setFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
			startActivityForResult(intent1, MY_PREFERENCE_ACTIVITY);
			return true;
		}
		// 日時移動
		if (id == R.id.action_settings2) {
			calView.setDataPickerDialog();
			return true;
		}

		// オプション
		if (id == R.id.action_settings3) {
			Intent intent3 = new android.content.Intent(this, MyPreferenceActivity.class);
			startActivityForResult(intent3, MY_PREFERENCE_OPTION);
			return true;
		}

		// ヘルプ
		if (id == R.id.action_settings4) {
			Intent intent4 = new android.content.Intent(this, WebviewActivity.class);
			startActivityForResult(intent4, WEB_VIEW_ACTIVITY);
			return true;
		}

		// デバッグ--------------------------------------------------------------------------
		// DP表示
		if (id == R.id.action_settingsDebugDp) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("DPI");
			alertDialogBuilder.setMessage(Trace.getMetricsDP(this));

			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return true;
		}


		// データ入力デバッグ
		if (id == R.id.action_settingsDebugSql) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("わくいデバッグ１");
			alertDialogBuilder.setMessage("１年の収支データの入力");

			alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					// SQLデータを強制的に代入する
                    mySqlite = new MySQLiteOpenHelper(getApplicationContext());
					calSyushiDatabase = (mySqlite.getWritableDatabase());

					// データ強制入力
                    mySqlite.setDebugSqlData(calSyushiDatabase, "" + yearBak);

					// 店舗データの強制入力
                    mySqlite.setDebugTenpoSqlData( calSyushiDatabase );

                    // 機種データの強制入力
                    mySqlite.setDebugKisyuSqlData( calSyushiDatabase );

					calSyushiDatabase.close();
                    mySqlite.close();

					// 月間収支更新
					calView.renewalMonthTotal();
					// 年間収支更新
					drawYearTotal(yearBak);
					// 月間収支更新
					drawMonthTotal();

					Trace.d("forIntentMonth = " + forIntentMonth);
				}
			});
			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return true;
		}
		// デバッグ--------------------------------------------------------------------------
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Trace.d("onDestroy");
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// false 親にイベントを戻す true ここでイベント止める
		if (this.detector.onTouchEvent(event))
			return true;
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Trace.d("onDown");
		// calView.onDown( e );
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("確認");
			alertDialogBuilder.setMessage("収太郎を終了しますか？");

			alertDialogBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			alertDialogBuilder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});

			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			return true;
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// 画面を押したときに呼び出される。ただし、画面を押してすぐにドラックしたり画面を離した場合は呼ぶ出されない。
		Trace.d("onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Trace.d("onSingleTapUp");
		// calView.onSingleTapUp( e );
		// 画面をシングルタップしたときに呼び出される。なお、ダブルタップの時にも呼ぶ出される。
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distnceX, float distanceY) {
		// 画面をドラックした時に呼ばれる
		Trace.d("onScroll");
		// calView.onScroll( e1, e2, distnceX, distanceY );
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// 長押しイベント
		Trace.d("onLongPress");
		// calView.onLongPress(e);
	}

	/**
	 * @param e1
	 *            ダウン時のタッチイベント
	 * @param e2
	 *            スクロール時のタッチイベント
	 * @param velocityX
	 *            x軸のスクロール時の距離
	 * @param velocityY
	 *            y軸のスクロール時の距離
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Trace.d("onFling");

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		// Bundle bundle = intent.getExtras();

		switch (requestCode) {
		case MY_PREFERENCE_OPTION:
			Trace.d("MY_PREFERENCE_OPTION");
			// データの更新
			// updata( );
			// 復元した場合は再描画要求
			if (MyPrefSetting.getDataRestorationFlg()) {
				// off
				MyPrefSetting.setDataRestorationFlg(false);
				// 月間収支更新
				calView.renewalMonthTotal();
				// 年間収支更新
				drawYearTotal(yearBak);
				// 月間収支更新
				drawMonthTotal();
			}
			break;

		case MY_PREFERENCE_ACTIVITY:
			Trace.d("MY_PREFERENCE_ACTIVITY");
			break;

		case LIST_VIEW_ACTIVITY:
			Trace.d("LIST_VIEW_ACTIVITY");
			// 月間収支更新
			calView.renewalMonthTotal();

			// 年間収支更新
			drawYearTotal(yearBak);
			// 月間収支更新
			drawMonthTotal();
			break;

		default:
			break;

		}
		onShowPressFlg = false;
		Trace.d("onActivityResult onActivityResult onActivityResult ");
	}

	/**
	 * <p>
	 * 収支設定画面に飛ぶ
	 * </p>
	 *
	 * @param dateInfo 選択された日付のdateInfo
	 *
	 */
	public void intentConfigCommonProcess(DateInfo dateInfo) {

		// インテント作成
		Intent intent = new Intent(this, ListViewActivity.class);
		// 日付を引き継ぐ(SQL検索データ)
		intent.putExtra("dddd", dateInfo.getYMD());

		String dataStr = dateInfo.getYear() + "年" + dateInfo.getMonth() + "月" + dateInfo.getDay() + "日";
		intent.putExtra("dsds", dataStr);

		Trace.d(dataStr);
		// 収支入力画面へ
		startActivityForResult(intent, LIST_VIEW_ACTIVITY);
	}

	@Override
	public void onCalendarSelection(CalendarSelectionEvent event) {

		DateInfo dateInfo = event.getDateInfo();
		int year = dateInfo.getYear();
		int month = dateInfo.getMonth();

		Trace.d("new:" + year + "/" + month + "old:" + yearBak + "/" + monthBak);

		// 月間収支の更新
		if (year != yearBak || month != monthBak) {
			drawMonthTotal();
			monthBak = month;
			forIntentMonth = dateInfo.getYM();
			Trace.d("月収支の更新");
		}

		// 年収支の更新
		if (year != yearBak) {
			drawYearTotal(year);
			yearBak = year;
			Trace.d("年収支の更新");
		}
	}

	/*
	 * 月間収支の描画
	 */
	private void drawMonthTotal() {
		// 月収支の計算
		int ret_i = calView.getCalMonthInvariment();
		int ret_r = calView.getCalMonthRecovery();

		// 投資(月)
		viewMonth2.setText(AppSetting.separateComma(ret_i));
		viewMonth2.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
				(AppSetting.calcMyDp(16f, AppSetting.getContentWidthRatio())));

		// 回収(月)
		viewMonth3.setText(AppSetting.separateComma(ret_r));
		viewMonth3.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
				(AppSetting.calcMyDp(16f, AppSetting.getContentWidthRatio())));

		// 収支(月)
		viewMonth4.setText(AppSetting.separateComma((ret_r - ret_i)));
		viewMonth4.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
				(AppSetting.calcMyDp(16f, AppSetting.getContentWidthRatio())));

		CommonProcess.changeTotalTextColor1(viewMonth4, (ret_r - ret_i));
	}

	/*
	 * 年間収支の描画
	 */
	private void drawYearTotal(int _year) {

		try {
			// 年間収支
            mySqlite = new MySQLiteOpenHelper(this);
			calSyushiDatabase = (mySqlite.getWritableDatabase());

//			int retYearI = mySqlite.getDaySyushi(calSyushiDatabase, _year + "", "investment");
//			int retYearR = mySqlite.getDaySyushi(calSyushiDatabase, _year + "", "recovery");

			int retYearI = mySqlite.getDayInvestment(calSyushiDatabase, _year + "");
			int retYearR = mySqlite.getDayRecovery(calSyushiDatabase, _year + "");

			// 投資(年)
			viewYear2 = (TextView) this.findViewById(R.id.textViewYear2_2);
			viewYear2.setText(AppSetting.separateComma(retYearI));
			viewYear2.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
					(AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));

			// 回収(年)
			viewYear3 = (TextView) this.findViewById(R.id.textViewYear3_2);
			viewYear3.setText(AppSetting.separateComma(retYearR));
			viewYear3.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
					(AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));

			// 収支(年)
			viewYear4 = (TextView) this.findViewById(R.id.textViewYear4_2);
			viewYear4.setText(AppSetting.separateComma((retYearR - retYearI)));
			viewYear4.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
					(AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));

			CommonProcess.changeTotalTextColor1(viewYear4, (retYearR - retYearI));

			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			DialogFragment newFragment = new AlertDialogFragment();
			newFragment.show(getFragmentManager(), "showAlertDialog");
		}

		// 終了
		calSyushiDatabase.close();
        mySqlite.close();
	}

	/**
	 * 月別収支へ飛ぶ
	 *
	 * @param view
	 */
	public void onClickLinearLayoutMonth(View view) {

		if (!onShowPressFlg) {
			// 投資(月)
			Trace.d("onClickLinearLayoutMonth2 = " + viewMonth2.getText());
			// 回収(月)
			Trace.d("onClickLinearLayoutMonth3 = " + viewMonth3.getText());

			Intent intent = new Intent(this, DrawDataActivity.class);

			intent.putExtra("idid", 5);
			intent.putExtra("word", forIntentMonth);

			Trace.d("onClickLinearLayoutMonth month:" + forIntentMonth);

			// 収支が入力されていない場合は移行させない
			if (!(viewMonth2.getText().equals("0") && viewMonth3.getText().equals("0"))) {
				// startActivity(intent);

				MyAsyncTask task = new MyAsyncTask(this, getFragmentManager(), intent,
						new ArrayList<ListViewDrawAdapter>());
				task.execute(0);
				// startActivityForResult( intent, MY_PREFERENCE_ACTIVITY );
				///////////////////////////////////

				// onShowPressFlg = true;
			}
		}
	}

	/**
	 * 年間収支へ飛ぶ
	 *
	 * @param view
	 */
	public void onClickLinearLayoutYear(View view) {

		if (onShowPressFlg == false) {

			Intent intent = new Intent(this, DrawDataActivity.class);

			intent.putExtra("idid", 6);
			intent.putExtra("word", "" + yearBak);

			Trace.d("onClickLinearLayoutYear year:" + yearBak);

			// 収支が入力されていない場合は移行させない
			if (!(viewYear2.getText().equals("0") && viewYear3.getText().equals("0"))) {
				// startActivity(intent);

				MyAsyncTask task = new MyAsyncTask(this, getFragmentManager(), intent,
						new ArrayList<ListViewDrawAdapter>());
				task.execute(0);
				// startActivityForResult( intent, MY_PREFERENCE_ACTIVITY );

				// onShowPressFlg = true;
			}
		}
	}


	/*
	 * プリファレンスの設定を読み込む
	 */
	private void loadPreference() {

		Trace.d(" アプリケーション起動時 プリファレンスの情報を設定する loadPreference ");
		int data = 0;

		// データの参照
		String data_rate = pref.getString(MyPrefSetting.key[0], MyPrefSetting.defaultVals[0]);
		String data_seekbar = pref.getString(MyPrefSetting.key[1], MyPrefSetting.defaultVals[1]);
		String data_seekbar_max = pref.getString(MyPrefSetting.key[2], MyPrefSetting.defaultVals[2]);

		data = Integer.parseInt(data_rate);
		MyPrefSetting.setRate(data);

		data = Integer.parseInt(data_seekbar);
		MyPrefSetting.setSeekBarRate(data);

		data = Integer.parseInt(data_seekbar_max);
		MyPrefSetting.setSeekBarMax(data);

		// データ復元 アプリ起動時に必ず初期化しておく
		MyPrefSetting.setDataRestorationFlg(false);

		Trace.d("aaaaaaaaaaaaaaaaaaaaa 単位 = " + MyPrefSetting.getRate());
		Trace.d("aaaaaaaaaaaaaaaaaaaaa seekbar = " + MyPrefSetting.getSeekBarRate());
		Trace.d("aaaaaaaaaaaaaaaaaaaaa max = " + MyPrefSetting.getSeekBarMax());
		Trace.d("aaaaaaaaaaaaaaaaaaaaa RestorationFlg = " + MyPrefSetting.getDataRestorationFlg());
	}


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        // ホームボタン等とタブを同時押しで、
        // 「onSaveInstanceState後にこのアクションを実行することはできません」
        // というエラーが発生するのでonSaveInstanceStateを握りつぶし。
    }

	/**
	 * 先月に移動
	 *
	 * @param view
	 */
	public void onClickLinearHeaderBack( View view )
    {
        Trace.d( "onClickLinearHeaderBack" );
        calView.onClickPrevMonth(  );
	}


    /**
     * 月日移動ダイアログを表示させる
     * @param view
     */
    public void onClickLinearHeaderYM( View view ){
        Trace.d( "onClickLinearHeaderYM" );
        calView.setDataPickerDialog();
    }



    /**
     * 来月に移動
     *
     * @param view
     */
	public void onClickLinearHeaderForward( View view ){
        Trace.d( "onClickLinearHeaderForward" );
        calView.onClickNextMonth(  );
	}

    /**
     * menuを開く
     *
     * @param view
     */
    public void onClickLinearHeaderMenu( View view ){
        Trace.d( "onClickLinearHeaderMenu1" );

        if (mainMenu != null) {
            Trace.d( "onClickLinearHeaderMenu2" );
            menuDrawFlg = true;
            openOptionsMenu();
            mainMenu.performIdentifierAction(R.id.overflow_options, 0);
            menuDrawFlg = false;
        }
    }
}
