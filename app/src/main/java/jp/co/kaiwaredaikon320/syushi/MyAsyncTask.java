package jp.co.kaiwaredaikon320.syushi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.os.AsyncTask;
import android.util.Log;

public class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> implements OnCancelListener {

	final String TAG = "MyAsyncTask";
	ProgressDialog dialog;

	Activity activity;
	Context context;
	FragmentManager Fragment;

	// ListViewに表示するデータ
	public static ArrayList<ListViewDrawAdapter> mArrayList;
	private SQLiteDatabase syushiDatabase = null;
	private Cursor constantsCursor = null;

	public static int errflag;

	Intent intent;

	private int itemptn = 0;
	private String searchWord;

	public static ArrayList<ListViewDrawAdapter> getList() {
		return mArrayList;
	}

	//	public MyAsyncTask(Context con, Intent i, ArrayList<ListViewDrawAdapter> list ){
	//	public MyAsyncTask(Context con, FragmentManager f, Intent i, ArrayList<ListViewDrawAdapter> list ){
	public MyAsyncTask(Activity act, FragmentManager f, Intent i, ArrayList<ListViewDrawAdapter> list) {

		activity = act;
		context = act;
		Fragment = f;
		intent = i;
		mArrayList = list;

		// 値を取得
		itemptn = intent.getIntExtra("idid", 0); // id
		searchWord = intent.getStringExtra("word"); //　searchワード(カレンダー画面から直飛びした場合のみ使用)
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, "onPreExecute");
		dialog = new ProgressDialog(context);
//		dialog.setTitle("Please wait");
		dialog.setMessage("読み込み中...");
		// 		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.setOnCancelListener(this);
		// 		dialog.setMax(100);
		// 		dialog.setProgress(0);
		dialog.show();
	}

	@Override
	// 	protected Long doInBackground(Integer... params) {
	protected Integer doInBackground(Integer... params) {
		Trace.d(TAG, "doInBackground 1 - " + params[0]);

		int ret = 0;

		//　選択された項目によってデータベースを取得する
		// 月間
		if (itemptn == 0) {
			ret = getMonthSQLData();
		}
		// 年間
		if (itemptn == 1) {
			ret = getYearSQLData();
		}

		// 累計
		if (itemptn == 2) {
			ret = getTotalSQLData();
		}

		// 店舗別
		if (itemptn == 3) {
			ret = getTenpoSQLData();
		}

		// 機種別
		if (itemptn == 4) {
			ret = getTypeSQLData();
		}

		// 月収支詳細
		if (itemptn == 5) {
			ret = getDifferentMonthSQLData();
		}

		// 年収支詳細
		if (itemptn == 6) {
			ret = getDifferentMonthSQLData();
		}

		Trace.d( "ffffffffffffffffffffffffffffffffffffffffff");
		//　dataベースを閉じる
		constantsCursor.close();
		syushiDatabase.close();

		System.gc();

		return ret;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Trace.d(TAG, "onProgressUpdate - " + values[0]);
		//		dialog.setProgress(values[0]);
	}

	@Override
	protected void onCancelled() {
		Trace.d(TAG, "onCancelled");
		dialog.dismiss();
	}

	@Override
	//	protected void onPostExecute(Long result) {
	protected void onPostExecute(Integer result) {
		Trace.d(TAG, "onPostExecute - " + result);
		dialog.dismiss();

		// 成功
		if (result == 0) {
			//			context.startActivity(intent);

			if (itemptn == 5 || itemptn == 6) {
				activity.startActivityForResult(intent, MainActivity.MY_PREFERENCE_ACTIVITY);
			}
			else {
				activity.startActivity(intent);
			}
		}
		// 失敗
		else {
			// アラートダイアログの表示
			DialogFragment newFragment = new AlertDialogFragment();
			newFragment.show(Fragment, "showAlertDialog");
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		Trace.d(TAG, "Dialog onCancell... calling cancel(true)");

		MainActivity.onShowPressFlg = false;
		this.cancel(true);
	}

	/**
	 * 月収支をのSQLデータを取得する
	 */
	private int getMonthSQLData() {

		int ret = 0; // 成功

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

		// 勝敗用
		int winTmp = 0;
		int loseTmp = 0;
		int drawTmp = 0;

		List<String> monthlist = new ArrayList<String>();
		List<String> monthTmp = new ArrayList<String>();

		try {
			// 機種の数を取得する
			syushiDatabase = (new MySQLiteOpenHelper(this.context)).getWritableDatabase();
			constantsCursor = syushiDatabase.rawQuery(
					"SELECT DISTINCT data " +
							"FROM constants " +
							"ORDER BY data",
					null);

			// 日付を取得する
			if (constantsCursor.moveToFirst()) {

				do {
					//　データベースからデータを取得する
					String type = constantsCursor.getString(constantsCursor.getColumnIndex("data"));
					// 日付データ
					monthTmp.add(type.substring(0, 6));
					Trace.d(type.substring(0, 6));
				} while (constantsCursor.moveToNext());
			}

			// 重複を排除してリストを作ってくれる？
			monthlist = new ArrayList<String>(new LinkedHashSet<String>(monthTmp));

			// 逆順でソート
			Collections.reverse(monthlist);

			// 機種ごとに収支を取得する
			for (int i = 0; i < monthlist.size(); i++) {

				// 初期データの場合は処理を行わない
				if (monthlist.get(i).equals("000000")) {
					continue;
				}

				investmentTmp = 0;
				recoveryTmp = 0;
				totalTmp = 0;

				// 勝敗用
				winTmp = 0;
				loseTmp = 0;
				drawTmp = 0;

				// SQLite
				constantsCursor = syushiDatabase.rawQuery("SELECT data, investment, recovery " +
						"FROM constants " +
						"WHERE data like '%" + monthlist.get(i) + "%' " +
						"ORDER BY data",
						null);

				// データを取得
				// カーソルを一番最初に戻す。
				if (constantsCursor.moveToFirst()) {

					do {
						//　データベースからデータを取得する
						int investment = constantsCursor.getInt(constantsCursor.getColumnIndex("investment"));
						int recovery = constantsCursor.getInt(constantsCursor.getColumnIndex("recovery"));
						int total = recovery - investment;

						// 収支
						investmentTmp += investment;
						recoveryTmp += recovery;
						totalTmp += total;

						//　勝敗
						// 分け
						if (total == 0) {
							drawTmp++;
						}
						// 勝ち
						else if (total > 0) {
							winTmp++;
						}
						// 負け
						else if (total < 0) {
							loseTmp++;
						}

					} while (constantsCursor.moveToNext());
				}
				//　リストに追加
				mArrayList.add(new ListViewDrawAdapter(monthlist.get(i).substring(0, 4) + "年"
						+ monthlist.get(i).substring(4, 6) + "月", investmentTmp, recoveryTmp, totalTmp, winTmp,
						loseTmp, drawTmp, monthlist.get(i)));
			}

			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			//    		DialogFragment newFragment = new AlertDialogFragment( );
			//   		newFragment.show( getFragmentManager(), "showAlertDialog" );
			ret = 1;
		}
		return ret;
	}

	/**
	 * 年間収支をのSQLデータを取得する
	 */
	private int getYearSQLData() {

		int ret = 0; // 成功

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

		// 勝敗用
		int winTmp = 0;
		int loseTmp = 0;
		int drawTmp = 0;

		List<String> yearlist = new ArrayList<String>();
		List<String> yearTmp = new ArrayList<String>();

		try {

			// 機種の数を取得する
			syushiDatabase = (new MySQLiteOpenHelper(context)).getWritableDatabase();
			constantsCursor = syushiDatabase.rawQuery(
					"SELECT DISTINCT data " +
							"FROM constants " +
							"ORDER BY data",
					null);

			// 日付を取得する
			if (constantsCursor.moveToFirst()) {

				do {
					//　データベースからデータを取得する
					String type = constantsCursor.getString(constantsCursor.getColumnIndex("data"));
					// 日付データ
					yearTmp.add(type.substring(0, 4));

					Trace.d(type.substring(0, 4));
				} while (constantsCursor.moveToNext());
			}

			// 重複を排除してリストを作ってくれる？
			yearlist = new ArrayList<String>(new LinkedHashSet<String>(yearTmp));

			// 逆順でソート
			Collections.reverse(yearlist);

			// 機種ごとに収支を取得する
			for (int i = 0; i < yearlist.size(); i++) {

				// 初期データの場合は処理を行わない
				if (yearlist.get(i).equals("0000")) {
					continue;
				}

				investmentTmp = 0;
				recoveryTmp = 0;
				totalTmp = 0;

				// 勝敗用
				winTmp = 0;
				loseTmp = 0;
				drawTmp = 0;

				// SQLite
				constantsCursor = syushiDatabase.rawQuery("SELECT data, investment, recovery " +
						"FROM constants " +
						"WHERE data like '%" + yearlist.get(i) + "%' " +
						"ORDER BY tenpo",
						null);
				// データを取得
				// カーソルを一番最初に戻す。
				if (constantsCursor.moveToFirst()) {

					do {
						//　データベースからデータを取得する
						int investment = constantsCursor.getInt(constantsCursor.getColumnIndex("investment"));
						int recovery = constantsCursor.getInt(constantsCursor.getColumnIndex("recovery"));
						int total = recovery - investment;

						// 収支
						investmentTmp += investment;
						recoveryTmp += recovery;
						totalTmp += total;

						//　勝敗
						// 分け
						if (total == 0) {
							drawTmp++;
						}
						// 勝ち
						else if (total > 0) {
							winTmp++;
						}
						// 負け
						else if (total < 0) {
							loseTmp++;
						}

					} while (constantsCursor.moveToNext());
				}

				//　リストに追加
				mArrayList.add(new ListViewDrawAdapter(yearlist.get(i) + "年", investmentTmp, recoveryTmp, totalTmp,
						winTmp, loseTmp, drawTmp, yearlist.get(i)));
			}

			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			//    		DialogFragment newFragment = new AlertDialogFragment( );
			//    		newFragment.show( getFragmentManager(), "showAlertDialog" );
			ret = 1;
		}

		return ret;
	}

	/**
	 * 累計収支を取得する
	 */
	private int getTotalSQLData() {

		int ret = 0; // 成功

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

		// 勝敗用
		int winTmp = 0;
		int loseTmp = 0;
		int drawTmp = 0;

		try {

			// SQLite
			syushiDatabase = (new MySQLiteOpenHelper(context)).getWritableDatabase();
			constantsCursor = syushiDatabase.rawQuery("SELECT investment, recovery, _id " +
					"FROM constants ",
					null);

			// データを取得
			// カーソルを一番最初に戻す。
			if (constantsCursor.moveToFirst()) {

				do {
					//　データベースからデータを取得する
					int investment = constantsCursor.getInt(constantsCursor.getColumnIndex("investment"));
					int recovery = constantsCursor.getInt(constantsCursor.getColumnIndex("recovery"));
					int id = constantsCursor.getInt(constantsCursor.getColumnIndex("_id"));

					int total = recovery - investment;

					Trace.d("id = " + id);

					// ID→１は初期データなので、データには含まない
					if (id == 1) {
						continue;
					}

					// 収支
					investmentTmp += investment;
					recoveryTmp += recovery;
					totalTmp += total;

					//　勝敗
					// 分け
					if (total == 0) {
						drawTmp++;
					}
					// 勝ち
					else if (total > 0) {
						winTmp++;
					}
					// 負け
					else if (total < 0) {
						loseTmp++;
					}

				} while (constantsCursor.moveToNext());
			}

			//　リストに追加
			mArrayList.add(new ListViewDrawAdapter("累計収支", investmentTmp, recoveryTmp, totalTmp, winTmp, loseTmp,
					drawTmp));

			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			//    		DialogFragment newFragment = new AlertDialogFragment( );
			//    		newFragment.show( getFragmentManager(), "showAlertDialog" );

			ret = 1;
		}

		return ret;
	}

	/**
	 * 店舗別収支のSQLデータを取得する
	 */
	private int getTenpoSQLData() {

		int ret = 0; // 成功

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

		// 勝敗用
		int winTmp = 0;
		int loseTmp = 0;
		int drawTmp = 0;

		//    	int sortFlg = 0;	// ソートの有無

		List<String> tenpolist = new ArrayList<String>();

		try {
			// 機種の数を取得する
			syushiDatabase = (new MySQLiteOpenHelper(context)).getWritableDatabase();
			constantsCursor = syushiDatabase.rawQuery(
					"SELECT DISTINCT tenpo " +
							"FROM constants " +
							"ORDER BY tenpo",
					null);

			// 機種を取得
			if (constantsCursor.moveToFirst()) {

				//			int i = 0;

				do {
					//　データベースからデータを取得する
					String type = constantsCursor.getString(constantsCursor.getColumnIndex("tenpo"));

					// 機種リストに追加
					tenpolist.add(type);

					//				if( type.equals( "店舗未登録" ) ){
					//					sortFlg = i;
					//				}

					Trace.d("機種 = " + type);

					//				i ++;

				} while (constantsCursor.moveToNext());
			}

			/*
				        // 一番上に機種未登録を持ってくるソート処理
				        if( sortFlg != 0 ){

				        	// 配列のコピー
				        	List<String> tenpoSrot = new ArrayList<String>( tenpolist );
				        	int p=1;

				        	for( int i =0; i < tenpoSrot.size(); i++ ){

				        		if( sortFlg == i ){
				        			tenpolist.set( 0, tenpoSrot.get(i) );
				        		}
				        		else{
				        			tenpolist.set( p, tenpoSrot.get(i) );
				        			p ++;
				        		}
				        	}
				        }
			*/

			// 機種ごとに収支を取得する
			for (int i = 0; i < tenpolist.size(); i++) {

				investmentTmp = 0;
				recoveryTmp = 0;
				totalTmp = 0;

				// 勝敗用
				winTmp = 0;
				loseTmp = 0;
				drawTmp = 0;

				// アポストロフィ対策。'→''に置き換える
				//    		String replace = tenpolist.get(i).replace( "'", "''" );
				//    		replace = replace.replace( "%", "$%" );
				String replace = likeEscapeProtection(tenpolist.get(i));

				// SQLite
				constantsCursor = syushiDatabase.rawQuery("SELECT tenpo, investment, recovery, _id " +
						"FROM constants " +
						//	            										   "WHERE tenpo like '" + tenpolist.get(i) + "' "+
						"WHERE tenpo like '" + replace + "' " +
						"ESCAPE " + "'$' " +
						"ORDER BY tenpo",
						null);

				// データを取得
				// カーソルを一番最初に戻す。
				if (constantsCursor.moveToFirst()) {

					do {
						//　データベースからデータを取得する
						int id = constantsCursor.getInt(constantsCursor.getColumnIndex("_id"));
						int investment = constantsCursor.getInt(constantsCursor.getColumnIndex("investment"));
						int recovery = constantsCursor.getInt(constantsCursor.getColumnIndex("recovery"));
						int total = recovery - investment;

						// 収支
						investmentTmp += investment;
						recoveryTmp += recovery;
						totalTmp += total;

						// ID→１は初期データなので、データには含まない
						if (id == 1) {
							continue;
						}

						//　勝敗
						// 分け
						if (total == 0) {
							drawTmp++;
						}
						// 勝ち
						else if (total > 0) {
							winTmp++;
						}
						// 負け
						else if (total < 0) {
							loseTmp++;
						}
					} while (constantsCursor.moveToNext());
				}

				// 勝敗が全部ｾﾞﾛの機種はリストに登録しない(店舗未登録を出さないための処理)
				if (!(drawTmp == 0 && winTmp == 0 && loseTmp == 0)) {
					//　リストに追加
					mArrayList.add(new ListViewDrawAdapter(tenpolist.get(i), investmentTmp, recoveryTmp, totalTmp,
							winTmp, loseTmp, drawTmp));
					// ソート降順
					Collections.sort(mArrayList, new ComparatorListViewDrawAdapter(0));
				}
			}
			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			//    		DialogFragment newFragment = new AlertDialogFragment( );
			//    		newFragment.show( getFragmentManager(), "showAlertDialog" );
			ret = 1;
		}

		return ret;

	}

	/**
	 * 機種別収支のSQLデータを取得する
	 */
	private int getTypeSQLData() {

		int ret = 0; // 成功

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

		// 勝敗用
		int winTmp = 0;
		int loseTmp = 0;
		int drawTmp = 0;

		//    	int sortFlg = 0;	// ソートの有無

		List<String> kisyulist = new ArrayList<String>();

		try {
			// 機種の数を取得する
			syushiDatabase = (new MySQLiteOpenHelper(context)).getWritableDatabase();
			constantsCursor = syushiDatabase.rawQuery(
					"SELECT DISTINCT kisyu " +
							"FROM constants " +
							"ORDER BY kisyu",
					null);

			// 機種を取得
			if (constantsCursor.moveToFirst()) {

				//      	int i = 0;

				do {
					//　データベースからデータを取得する
					String type = constantsCursor.getString(constantsCursor.getColumnIndex("kisyu"));
					// 機種リストに追加
					kisyulist.add(type);

					//				if( type.equals( "機種未登録" ) ){
					//					sortFlg = i;
					//				}
					//				Trace.d( "機種 = " + type );
					//				i ++;

				} while (constantsCursor.moveToNext());
			}

			/*
			        // 一番上に機種未登録を持ってくるソート処理
			        if( sortFlg != 0 ){

			        	// 配列のコピー
			        	List<String> kisyuSrot = new ArrayList<String>( kisyulist );
			        	int p=1;

			        	for( int i =0; i < kisyuSrot.size(); i++ ){

			        		if( sortFlg == i ){
			        			kisyulist.set( 0, kisyuSrot.get(i) );
			        		}
			        		else{
			        			kisyulist.set( p, kisyuSrot.get(i) );
			        			p ++;
			        		}
			        	}
			        }
			*/

			// 機種ごとに収支を取得する
			for (int i = 0; i < kisyulist.size(); i++) {

				investmentTmp = 0;
				recoveryTmp = 0;
				totalTmp = 0;

				// 勝敗用
				winTmp = 0;
				loseTmp = 0;
				drawTmp = 0;

				// アポストロフィ対策。'→''に置き換える
				//    		String replace = kisyulist.get(i).replace( "'", "''" );
				String replace = likeEscapeProtection(kisyulist.get(i));

				// SQLite
				constantsCursor = syushiDatabase.rawQuery("SELECT kisyu, investment, recovery, _id " +
						"FROM constants " +
						//            											"WHERE kisyu like '" + kisyulist.get(i) + "' "+
						"WHERE kisyu like '" + replace + "' " +
						"ESCAPE " + "'$' " +
						"ORDER BY kisyu",
						null);

				// データを取得
				// カーソルを一番最初に戻す。
				if (constantsCursor.moveToFirst()) {

					do {
						//　データベースからデータを取得する
						int id = constantsCursor.getInt(constantsCursor.getColumnIndex("_id"));
						int investment = constantsCursor.getInt(constantsCursor.getColumnIndex("investment"));
						int recovery = constantsCursor.getInt(constantsCursor.getColumnIndex("recovery"));
						int total = recovery - investment;

						// ID→１は初期データなので、データには含まない
						if (id == 1) {
							continue;
						}

						// 収支
						investmentTmp += investment;
						recoveryTmp += recovery;
						totalTmp += total;

						//　勝敗
						// 分け
						if (total == 0) {
							drawTmp++;
						}
						// 勝ち
						else if (total > 0) {
							winTmp++;
						}
						// 負け
						else if (total < 0) {
							loseTmp++;
						}
					} while (constantsCursor.moveToNext());
				}

				// 勝敗が全部ｾﾞﾛの機種はリストに登録しない(機種未登録を出さないための処理)
				if (!(drawTmp == 0 && winTmp == 0 && loseTmp == 0)) {
					//　リストに追加
					mArrayList.add(new ListViewDrawAdapter(kisyulist.get(i), investmentTmp, recoveryTmp, totalTmp,
							winTmp, loseTmp, drawTmp));
					// ソート降順
					Collections.sort(mArrayList, new ComparatorListViewDrawAdapter(0));
				}
			}

			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			//    		DialogFragment newFragment = new AlertDialogFragment( );
			//    		newFragment.show( getFragmentManager(), "showAlertDialog" );

			ret = 1;
		}

		return ret;

	}

	/**
	 * 個別の月収支をのSQLデータを取得する
	 */
	private int getDifferentMonthSQLData() {

		int ret = 0; // 成功

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

		// 勝敗用
		int winTmp = 0;
		int loseTmp = 0;
		int drawTmp = 0;

		List<String> kisyulist = new ArrayList<String>();

		try {
			// 機種の数を取得する
			syushiDatabase = (new MySQLiteOpenHelper(context)).getWritableDatabase();
			constantsCursor = syushiDatabase.rawQuery(
					"SELECT DISTINCT kisyu " +
							"FROM constants " +
							"WHERE data like '%" + searchWord + "%' " +
							"ORDER BY kisyu",
					null);

			// 機種を取得
			if (constantsCursor.moveToFirst()) {

				do {
					//　データベースからデータを取得する
					String type = constantsCursor.getString(constantsCursor.getColumnIndex("kisyu"));
					// 機種リストに追加
					kisyulist.add(type);

					Trace.d("機種 = " + type);

				} while (constantsCursor.moveToNext());
			}

			// 指定されて月ごとの収支を取得
			for (int i = 0; i < kisyulist.size(); i++) {

				investmentTmp = 0;
				recoveryTmp = 0;
				totalTmp = 0;

				// 勝敗用
				winTmp = 0;
				loseTmp = 0;
				drawTmp = 0;

				// アポストロフィ対策。'→''に置き換える
				//    		String replace = kisyulist.get(i).replace( "'", "''" );
				String replace = likeEscapeProtection(kisyulist.get(i));

				// SQLite
				constantsCursor = syushiDatabase.rawQuery("SELECT kisyu, investment, recovery, _id " +
						"FROM constants " +
						"WHERE data like '%" + searchWord + "%' " +
						//            										   "and kisyu like '" + kisyulist.get(i) + "' "+
						"and kisyu like '" + replace + "' " +
						"ESCAPE " + "'$' " +
						"ORDER BY data",
						null);

				// データを取得
				// カーソルを一番最初に戻す。
				if (constantsCursor.moveToFirst()) {

					do {
						//　データベースからデータを取得する
						int id = constantsCursor.getInt(constantsCursor.getColumnIndex("_id"));
						int investment = constantsCursor.getInt(constantsCursor.getColumnIndex("investment"));
						int recovery = constantsCursor.getInt(constantsCursor.getColumnIndex("recovery"));
						int total = recovery - investment;

						// ID→１は初期データなので、データには含まない
						if (id == 1) {
							continue;
						}

						// 収支
						investmentTmp += investment;
						recoveryTmp += recovery;
						totalTmp += total;

						//　勝敗
						// 分け
						if (total == 0) {
							drawTmp++;
						}
						// 勝ち
						else if (total > 0) {
							winTmp++;
						}
						// 負け
						else if (total < 0) {
							loseTmp++;
						}

					} while (constantsCursor.moveToNext());
				}

				//　リストに追加
				mArrayList.add(new ListViewDrawAdapter(kisyulist.get(i), investmentTmp, recoveryTmp, totalTmp, winTmp,
						loseTmp, drawTmp));
				// ソート降順
				Collections.sort(mArrayList, new ComparatorListViewDrawAdapter(0));
			}

			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			//    		DialogFragment newFragment = new AlertDialogFragment( );
			//    		newFragment.show( getFragmentManager(), "showAlertDialog" );
			ret = 1;

		}
		return ret;
	}

	/**
	 * 引数で指定した文字列の中の「'」と「%」をlike句の対応する形にして返す
	 *
	 * @param str　変換する文字列
	 * @return　「'」と「%」をlile分に対応した形の文字列
	 */
	private String likeEscapeProtection(String str) {
		// アポストロフィ対策。「'」→「''」に置き換える
		String retStr = str.replace("'", "''");
		// パーセント対急く。「%」→「$%」に置き換える
		retStr = retStr.replace("%", "$%");
		return retStr;
	}

}
