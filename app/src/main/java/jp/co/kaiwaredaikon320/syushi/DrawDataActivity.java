package jp.co.kaiwaredaikon320.syushi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DrawDataActivity extends Activity{

/*
	private SQLiteDatabase syushiDatabase = null;
	private Cursor constantsCursor = null;
*/

	private int itemptn = 0;
	private String searchWord;

	// ListView
	private ListView mListView;
    private TextView mTitleName;

	// ListViewに表示するデータ
/*
	private ArrayList<ListViewDrawAdapter> mArrayList = new ArrayList<ListViewDrawAdapter>();
*/
	private ArrayList<ListViewDrawAdapter> mArrayList;


/*
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		// アクションバーの戻るを押したときの処理
		if( id == android.R.id.home ){
	        finish( );
	        return true;
	    }
		return super.onOptionsItemSelected( item );
	}*/

	@Override
    public void onCreate(Bundle savedInstanceState) {

      Trace.d( "onCreate Start " );

        super.onCreate(savedInstanceState);

        Intent intent = getIntent( );

        itemptn = intent.getIntExtra( "idid", 0 );			// id
        searchWord = intent.getStringExtra( "word" );		//　searchワード(カレンダー画面から直飛びした場合のみ使用)

        // タイトル非表示
//      getActionBar().setDisplayShowTitleEnabled(false);
        // アイコンを消す
//        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_HOME);

        // Upアイコンの表示
//     getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.drawdataactivity );

		// Viewを取得
		mListView = (ListView)this.findViewById(android.R.id.list);

        // コンテキストメニューを表示するViewを登録する
        //Viewに追加する場合、registerForContextMenu(View);が必要
        registerForContextMenu( mListView );

		// タップ処理
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Trace.d( "setOnItemClickListener pos = " + position );

				// 詳細を出すのは月間、年間、累計でOk？
				if( itemptn >= 0 && itemptn <= 2 ){
					intentDetailedCommonProcess( position );
				}
			}
		} );

		// リストの取得
		mArrayList = MyAsyncTask.getList();

        // ListViewにアダプタを設定
		DrawDataAdapter adapter = new DrawDataAdapter(
			this,
			R.layout.draw_data_list,    // 1行分のレイアウトファイル
			0,                          // 上記レイアウト内のテキスト表示箇所のId(※未使用)
			mArrayList                  // 表示対象のデータ
		);

        // 月収支詳細・年収支詳細　以外は、layoutを変更する(矢印付)
		if( itemptn >= 0 && itemptn <= 2 ){
			adapter.setVisibility( 1 );
		}
		else{
			adapter.setVisibility( 0 );
		}

		mListView.setAdapter(adapter);

	//　選択された項目にタイトルを変更する
        mTitleName = (TextView) findViewById(R.id.header_title_draw_data);

		// 月間
        if( itemptn == 0 ){
//        	setTitle( "" + getText(R.string.dpre_month_syushi) );
            mTitleName.setText(getText(R.string.dpre_month_syushi));
        }
        // 年間
        if( itemptn == 1 ){
//        	setTitle( "" + getText(R.string.dpre_year_syushi) );
            mTitleName.setText(getText(R.string.dpre_year_syushi));
        }

        // 累計
        if( itemptn == 2 ){
//        	setTitle( "" + getText(R.string.dpre_total_syushi) );
            mTitleName.setText(getText(R.string.dpre_total_syushi));
        }

        // 店舗別
        if( itemptn == 3 ){
//        	setTitle( "" + getText(R.string.dpre_tenpo_syushi) );
            mTitleName.setText(getText(R.string.dpre_tenpo_syushi));
        }

        // 機種別
        if( itemptn == 4 ){
//        	setTitle( "" + getText(R.string.dpre_type_syushi) );
            mTitleName.setText(getText(R.string.dpre_type_syushi));
        }

        // 月間収支(タイトル～)
        if( itemptn == 5 ){
//        	setTitle( "月間収支" );
            mTitleName.setText(getText(R.string.month_calendar_view));
        }

        // 年間収支(タイトル～)
        if( itemptn == 6 ){
//        	setTitle( "年間収支" );
            mTitleName.setText(getText(R.string.year_calendar_view));
        }


/*
		// 月間
        if( itemptn == 0 ){
        	setTitle( "" + getText(R.string.dpre_month_syushi) );
        	getMonthSQLData( );
        }
        // 年間
        if( itemptn == 1 ){
        	setTitle( "" + getText(R.string.dpre_year_syushi) );
        	getYearSQLData( );
        }

        // 累計
        if( itemptn == 2 ){
        	setTitle( "" + getText(R.string.dpre_total_syushi) );
        	getTotalSQLData( );
        }

        // 店舗別
        if( itemptn == 3 ){
        	setTitle( "" + getText(R.string.dpre_tenpo_syushi) );
        	getTenpoSQLData( );
        }

        // 機種別
        if( itemptn == 4 ){
        	setTitle( "" + getText(R.string.dpre_type_syushi) );
        	getTypeSQLData( );
        }

        // 月収支詳細
        if( itemptn == 5 ){
        	setTitle( "月間収支" );
        	getDifferentMonthSQLData();
        }

        // 年収支詳細
        if( itemptn == 6 ){
        	setTitle( "年間収支" );
        	getDifferentMonthSQLData( );
        }

		//　dataベースを閉じる
		constantsCursor.close();
        syushiDatabase.close();
*/

		Trace.d( "idArrayList = " + mArrayList );
//		Trace.d( "" + idArrayList.get( 0 ) );

		Trace.d( "TextSet前" );


		// total表示用のテキストViewのウィジェット取得
//    	investmentText = (TextView)this.findViewById(R.id.listAct_investmentNumber_cc);
//    	recoveryText   = (TextView)this.findViewById(R.id.listAct_recoveryNumber);
//    	totalText 	   = (TextView)this.findViewById(R.id.listAct_totalNumber);
		// リストの再描画
//		mListView.invalidateViews();

        Trace.d( "onCreate end" );
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		Trace.d( "DrawdataActivity Destroy" );
	}


    /**
    *
    * 設定画面の項目作成用
    *
    */
/*
	public class ListViewDrawAdapter{

		private String mItem;		//　項目

		private int mInvestment;	//　投資
		private int mRecovery;		//　回収
		private int mTotal;			//　収支

		private int mWin;			//　勝ち数
		private int mLose;			//　負け数
		private int mDraw;			//　引き分け

		private String mWord;		//　サーチワード

		// コンストラクタ
		public ListViewDrawAdapter( String item,
									int investment, int recovery, int total,
									int win, int lose, int draw
									){
			mItem = item;

			mInvestment = investment;
			mRecovery   = recovery;
			mTotal      = total;

			//　勝敗
			mWin  = win;
			mLose = lose;
			mDraw = draw;

			mWord = null;
		}

		// コンストラクタ
		public ListViewDrawAdapter( String item,
									int investment, int recovery, int total,
									int win, int lose, int draw,
									String word
									){
			mItem = item;

			mInvestment = investment;
			mRecovery   = recovery;
			mTotal      = total;

			//　勝敗
			mWin  = win;
			mLose = lose;
			mDraw = draw;

			mWord = word;
		}

		public ListViewDrawAdapter(Context context) {
		}

		// 項目
		public void setItem( String item ) {
			mItem = item;
		}
		public String getItem() {
			return mItem;
		}

		//　投資額
		public void setInvestment( int investment ) {
			mInvestment = investment;
		}
		public int getInvestment() {
			return mInvestment;
		}
		// 回収
		public void setRecovery( int recovery ) {
			mRecovery = recovery;
		}
		public int getRecovery(){
			return mRecovery;
		}
		//　収支
		public void setTotal( int total ) {
			mTotal = total;
		}
		public int getTotal() {
			return mTotal;
		}


		// 勝ち
		public void setWin( int win ) {
			mWin = win;
		}
		public int getWin() {
			return mWin;
		}

		// 負け
		public void setLose( int lose ) {
			mLose = lose;
		}
		public int getLose() {
			return mLose;
		}

		// 引き分け
		public void setD( int draw ) {
			mDraw = draw;
		}
		public int getDraw() {
			return mDraw;
		}

		//　サーチワード
		protected void setWord( String word ) {
			mWord = word;
		}
		protected String getWord() {
			return mWord;
		}
	}
*/

	// startActivityForResult で起動させたアクティビティが
	// finish() により破棄されたときにコールされる
	// requestCode : startActivityForResult の第二引数で指定した値が渡される
	// resultCode : 起動先のActivity.setResult の第一引数が渡される
	// Intent data : 起動先Activityから送られてくる Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }



    /**
     * 月収支をのSQLデータを取得する
     */
/*
    private void getMonthSQLData( ){

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

    	// 勝敗用
    	int winTmp = 0;
    	int loseTmp = 0;
    	int drawTmp = 0;

    	List<String> monthlist = new ArrayList<String>();
    	List<String> monthTmp = new ArrayList<String>();


    	try{
	        // 機種の数を取得する
	        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext() ) ).getWritableDatabase( );
	        constantsCursor = syushiDatabase.rawQuery(
										    			"SELECT DISTINCT data "+
										    			"FROM constants "+
										    			"ORDER BY data",
										    			null );

	        // 日付を取得する
	        if( constantsCursor.moveToFirst( ) ){

	        	do{
					//　データベースからデータを取得する
					String type = constantsCursor.getString( constantsCursor.getColumnIndex( "data" ) );
					// 日付データ
					monthTmp.add( type.substring( 0, 6 ) );
					Trace.d( type.substring( 0, 6 ) );
	        	}while( constantsCursor.moveToNext( ) );
	        }

	        // 重複を排除してリストを作ってくれる？
	        monthlist = new ArrayList<String>(new LinkedHashSet<String>(monthTmp));

	        // 逆順でソート
	        Collections.reverse(monthlist);

	        // 機種ごとに収支を取得する
	        for( int i =0; i < monthlist.size(); i++ ){

	        	// 初期データの場合は処理を行わない
	        	if( monthlist.get(i).equals( "000000" ) ){
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
	            constantsCursor = syushiDatabase.rawQuery( "SELECT data, investment, recovery "+
	            										   "FROM constants " +
	            										   "WHERE data like '%" + monthlist.get(i) + "%' "+
	            										   "ORDER BY data",
	            										   null );

	            // データを取得
	            // カーソルを一番最初に戻す。
	    		if( constantsCursor.moveToFirst( ) ){

	    			do{
						//　データベースからデータを取得する
						int investment = constantsCursor.getInt( constantsCursor.getColumnIndex( "investment" ) );
						int recovery = constantsCursor.getInt( constantsCursor.getColumnIndex( "recovery" ) );
						int total = recovery - investment;

					// 収支
						investmentTmp += investment;
						recoveryTmp   += recovery;
						totalTmp      += total;

					//　勝敗
						// 分け
						if( total == 0 ){
							drawTmp ++;
						}
						// 勝ち
						else if( total > 0 ){
							winTmp ++;
						}
						// 負け
						else if( total < 0 ){
							loseTmp ++;
						}

	    			}while( constantsCursor.moveToNext( ) );
	    		}
				//　リストに追加
				mArrayList.add( new ListViewDrawAdapter( monthlist.get( i ).substring(0, 4) + "年"+monthlist.get( i ).substring(4, 6)+"月", investmentTmp, recoveryTmp, totalTmp, winTmp, loseTmp, drawTmp, monthlist.get( i )) );
	        }

    	// 容量オーバー時の処理
    	}catch( SQLiteDiskIOException e ){
    		// アラートダイアログの表示
    		DialogFragment newFragment = new AlertDialogFragment( );
    		newFragment.show( getFragmentManager(), "showAlertDialog" );
    	}
    }
*/

    /**
     * 年間収支をのSQLデータを取得する
     */
/*
    private void getYearSQLData( ){

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

    	// 勝敗用
    	int winTmp = 0;
    	int loseTmp = 0;
    	int drawTmp = 0;

    	List<String> yearlist = new ArrayList<String>();
    	List<String> yearTmp = new ArrayList<String>();

    	try{

	        // 機種の数を取得する
	        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext() ) ).getWritableDatabase( );
	        constantsCursor = syushiDatabase.rawQuery(
										    			"SELECT DISTINCT data "+
										    			"FROM constants "+
										    			"ORDER BY data",
										    			null );

	        // 日付を取得する
	        if( constantsCursor.moveToFirst( ) ){

	        	do{
					//　データベースからデータを取得する
					String type = constantsCursor.getString( constantsCursor.getColumnIndex( "data" ) );
					// 日付データ
					yearTmp.add( type.substring( 0, 4 ) );

					Trace.d( type.substring( 0, 4 ) );
	        	}while( constantsCursor.moveToNext( ) );
	        }

	        // 重複を排除してリストを作ってくれる？
	        yearlist = new ArrayList<String>(new LinkedHashSet<String>(yearTmp));

	        // 逆順でソート
	        Collections.reverse(yearlist);

	        // 機種ごとに収支を取得する
	        for( int i =0; i < yearlist.size(); i++ ){

	        	// 初期データの場合は処理を行わない
	        	if( yearlist.get(i).equals( "0000" ) ){
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
	            constantsCursor = syushiDatabase.rawQuery( "SELECT data, investment, recovery "+
	            										   "FROM constants " +
	            										   "WHERE data like '%" + yearlist.get(i) + "%' "+
	            										   "ORDER BY tenpo",
	            										   null );
	            // データを取得
	            // カーソルを一番最初に戻す。
	    		if( constantsCursor.moveToFirst( ) ){

	    			do{
						//　データベースからデータを取得する
						int investment = constantsCursor.getInt( constantsCursor.getColumnIndex( "investment" ) );
						int recovery = constantsCursor.getInt( constantsCursor.getColumnIndex( "recovery" ) );
						int total = recovery - investment;

					// 収支
						investmentTmp += investment;
						recoveryTmp   += recovery;
						totalTmp      += total;

					//　勝敗
						// 分け
						if( total == 0 ){
							drawTmp ++;
						}
						// 勝ち
						else if( total > 0 ){
							winTmp ++;
						}
						// 負け
						else if( total < 0 ){
							loseTmp ++;
						}

	    			}while( constantsCursor.moveToNext( ) );
	    		}

				//　リストに追加
				mArrayList.add( new ListViewDrawAdapter( yearlist.get(i)+"年", investmentTmp, recoveryTmp, totalTmp, winTmp, loseTmp, drawTmp, yearlist.get(i) ) );
	        }


    	// 容量オーバー時の処理
    	}catch( SQLiteDiskIOException e ){
    		// アラートダイアログの表示
    		DialogFragment newFragment = new AlertDialogFragment( );
    		newFragment.show( getFragmentManager(), "showAlertDialog" );
    	}
    }
*/

    /**
     * 累計収支を取得する
     */
/*
    private void getTotalSQLData( ){

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

    	// 勝敗用
    	int winTmp = 0;
    	int loseTmp = 0;
    	int drawTmp = 0;

    	try{

	        // SQLite
	        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext() ) ).getWritableDatabase( );
	        constantsCursor = syushiDatabase.rawQuery( "SELECT investment, recovery, _id "+
	        										   "FROM constants ",
	        										   null );

	        // データを取得
	        // カーソルを一番最初に戻す。
			if( constantsCursor.moveToFirst( ) ){

				do{
					//　データベースからデータを取得する
					int investment = constantsCursor.getInt( constantsCursor.getColumnIndex( "investment" ) );
					int recovery = constantsCursor.getInt( constantsCursor.getColumnIndex( "recovery" ) );
					int id = constantsCursor.getInt( constantsCursor.getColumnIndex( "_id" ) );

					int total = recovery - investment;

					Trace.d( "id = " + id  );

					// ID→１は初期データなので、データには含まない
					if( id == 1 ){
						continue;
					}

				// 収支
					investmentTmp += investment;
					recoveryTmp   += recovery;
					totalTmp      += total;

				//　勝敗
					// 分け
					if( total == 0 ){
						drawTmp ++;
					}
					// 勝ち
					else if( total > 0 ){
						winTmp ++;
					}
					// 負け
					else if( total < 0 ){
						loseTmp ++;
					}

				}while( constantsCursor.moveToNext( ) );
			}

			//　リストに追加
			mArrayList.add( new ListViewDrawAdapter( "累計収支", investmentTmp, recoveryTmp, totalTmp, winTmp, loseTmp, drawTmp ) );

		// 容量オーバー時の処理
    	}catch( SQLiteDiskIOException e ){
    		// アラートダイアログの表示
    		DialogFragment newFragment = new AlertDialogFragment( );
    		newFragment.show( getFragmentManager(), "showAlertDialog" );
    	}
    }
*/
    /**
     * 店舗別収支のSQLデータを取得する
     */
 /*
    private void getTenpoSQLData( ){

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

    	// 勝敗用
    	int winTmp = 0;
    	int loseTmp = 0;
    	int drawTmp = 0;

//    	int sortFlg = 0;	// ソートの有無

    	List<String> tenpolist = new ArrayList<String>();


    	try{
	        // 機種の数を取得する
	        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext() ) ).getWritableDatabase( );
	        constantsCursor = syushiDatabase.rawQuery(
										    			"SELECT DISTINCT tenpo "+
										    			"FROM constants "+
										    			"ORDER BY tenpo",
										    			null );

	        // 機種を取得
	        if( constantsCursor.moveToFirst( ) ){

	//			int i = 0;

	        	do{
					//　データベースからデータを取得する
					String type = constantsCursor.getString( constantsCursor.getColumnIndex( "tenpo" ) );

					// 機種リストに追加
					tenpolist.add(type);

	//				if( type.equals( "店舗未登録" ) ){
	//					sortFlg = i;
	//				}

					Trace.d( "機種 = " + type );

	//				i ++;

	        	}while( constantsCursor.moveToNext( ) );
	        }

//	        // 一番上に機種未登録を持ってくるソート処理
//	        if( sortFlg != 0 ){
//
//	        	// 配列のコピー
//	        	List<String> tenpoSrot = new ArrayList<String>( tenpolist );
//	        	int p=1;
//
//	        	for( int i =0; i < tenpoSrot.size(); i++ ){
//
//	        		if( sortFlg == i ){
//	        			tenpolist.set( 0, tenpoSrot.get(i) );
//	        		}
//	        		else{
//	        			tenpolist.set( p, tenpoSrot.get(i) );
//	        			p ++;
//	        		}
//	        	}
//	        }

	        // 機種ごとに収支を取得する
	        for( int i =0; i < tenpolist.size(); i++ ){

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
	        	String replace = likeEscapeProtection( tenpolist.get( i ) );

	            // SQLite
	            constantsCursor = syushiDatabase.rawQuery( "SELECT tenpo, investment, recovery, _id "+
	            										   "FROM constants " +
	//	            										   "WHERE tenpo like '" + tenpolist.get(i) + "' "+
														   "WHERE tenpo like '" + replace + "' "+
														   "ESCAPE "+ "'$' " +
	            										   "ORDER BY tenpo",
	            										   null );

	            // データを取得
	            // カーソルを一番最初に戻す。
	    		if( constantsCursor.moveToFirst( ) ){

	    			do{
						//　データベースからデータを取得する
	    				int id = constantsCursor.getInt( constantsCursor.getColumnIndex( "_id" ) );
						int investment = constantsCursor.getInt( constantsCursor.getColumnIndex( "investment" ) );
						int recovery = constantsCursor.getInt( constantsCursor.getColumnIndex( "recovery" ) );
						int total = recovery - investment;

					// 収支
						investmentTmp += investment;
						recoveryTmp   += recovery;
						totalTmp      += total;

						// ID→１は初期データなので、データには含まない
						if( id == 1 ){
							continue;
						}

					//　勝敗
						// 分け
						if( total == 0 ){
							drawTmp ++;
						}
						// 勝ち
						else if( total > 0 ){
							winTmp ++;
						}
						// 負け
						else if( total < 0 ){
							loseTmp ++;
						}
	    			}while( constantsCursor.moveToNext( ) );
	    		}

	    		// 勝敗が全部ｾﾞﾛの機種はリストに登録しない(店舗未登録を出さないための処理)
	    		if( !(drawTmp == 0 && winTmp == 0 && loseTmp == 0 ) ){
					//　リストに追加
					mArrayList.add( new ListViewDrawAdapter( tenpolist.get(i), investmentTmp, recoveryTmp, totalTmp, winTmp, loseTmp, drawTmp ) );
					// ソート降順
					Collections.sort( mArrayList,  new ComparatorListViewDrawAdapter( 0 ) );
	    		}
	        }
    	// 容量オーバー時の処理
    	}catch( SQLiteDiskIOException e ){
    		// アラートダイアログの表示
    		DialogFragment newFragment = new AlertDialogFragment( );
    		newFragment.show( getFragmentManager(), "showAlertDialog" );
    	}



    }
*/
    /**
     * 機種別収支のSQLデータを取得する
     */
 /*
    private void getTypeSQLData( ){

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

    	// 勝敗用
    	int winTmp = 0;
    	int loseTmp = 0;
    	int drawTmp = 0;

//    	int sortFlg = 0;	// ソートの有無

    	List<String> kisyulist = new ArrayList<String>( );


    	try{
	        // 機種の数を取得する
	        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext( ) ) ).getWritableDatabase( );
	        constantsCursor = syushiDatabase.rawQuery(
										    			"SELECT DISTINCT kisyu "+
										    			"FROM constants "+
										    			"ORDER BY kisyu",
										    			null );

	        // 機種を取得
	        if( constantsCursor.moveToFirst( ) ){

	  //      	int i = 0;

	        	do{
					//　データベースからデータを取得する
					String type = constantsCursor.getString( constantsCursor.getColumnIndex( "kisyu" ) );
					// 機種リストに追加
					kisyulist.add(type);

	//				if( type.equals( "機種未登録" ) ){
	//					sortFlg = i;
	//				}
	//				Trace.d( "機種 = " + type );
	//				i ++;

	        	}while( constantsCursor.moveToNext( ) );
	        }

//	        // 一番上に機種未登録を持ってくるソート処理
//	        if( sortFlg != 0 ){
//
//	        	// 配列のコピー
//	        	List<String> kisyuSrot = new ArrayList<String>( kisyulist );
//	        	int p=1;
//
//	        	for( int i =0; i < kisyuSrot.size(); i++ ){
//
//	        		if( sortFlg == i ){
//	        			kisyulist.set( 0, kisyuSrot.get(i) );
//	        		}
//	        		else{
//	        			kisyulist.set( p, kisyuSrot.get(i) );
//	        			p ++;
//	        		}
//	        	}
//	        }

	        // 機種ごとに収支を取得する
	        for( int i =0; i < kisyulist.size(); i++ ){

	    		investmentTmp = 0;
	    		recoveryTmp = 0;
	    		totalTmp = 0;

	        	// 勝敗用
	        	winTmp = 0;
	        	loseTmp = 0;
	        	drawTmp = 0;

	        	// アポストロフィ対策。'→''に置き換える
	//    		String replace = kisyulist.get(i).replace( "'", "''" );
	        	String replace = likeEscapeProtection( kisyulist.get( i ) );

	            // SQLite
	            constantsCursor = syushiDatabase.rawQuery(	"SELECT kisyu, investment, recovery, _id "+
	            											"FROM constants " +
	//            											"WHERE kisyu like '" + kisyulist.get(i) + "' "+
															"WHERE kisyu like '" + replace + "' "+
															"ESCAPE "+ "'$' " +
															"ORDER BY kisyu",
															null );

	            // データを取得
	            // カーソルを一番最初に戻す。
	    		if( constantsCursor.moveToFirst( ) ){

	    			do{
						//　データベースからデータを取得する
	    				int id = constantsCursor.getInt( constantsCursor.getColumnIndex( "_id" ) );
						int investment = constantsCursor.getInt( constantsCursor.getColumnIndex( "investment" ) );
						int recovery = constantsCursor.getInt( constantsCursor.getColumnIndex( "recovery" ) );
						int total = recovery - investment;

						// ID→１は初期データなので、データには含まない
						if( id == 1 ){
							continue;
						}

					// 収支
						investmentTmp += investment;
						recoveryTmp   += recovery;
						totalTmp      += total;

					//　勝敗
						// 分け
						if( total == 0 ){
							drawTmp ++;
						}
						// 勝ち
						else if( total > 0 ){
							winTmp ++;
						}
						// 負け
						else if( total < 0 ){
							loseTmp ++;
						}
	    			}while( constantsCursor.moveToNext( ) );
	    		}


	    		// 勝敗が全部ｾﾞﾛの機種はリストに登録しない(機種未登録を出さないための処理)
	    		if( !(drawTmp == 0 && winTmp == 0 && loseTmp == 0 ) ){
					//　リストに追加
					mArrayList.add( new ListViewDrawAdapter( kisyulist.get(i), investmentTmp, recoveryTmp, totalTmp, winTmp, loseTmp, drawTmp ) );
					// ソート降順
					Collections.sort(mArrayList,  new ComparatorListViewDrawAdapter( 0 ) );
	    		}
	        }

    	// 容量オーバー時の処理
    	}catch( SQLiteDiskIOException e ){
    		// アラートダイアログの表示
    		DialogFragment newFragment = new AlertDialogFragment( );
    		newFragment.show( getFragmentManager(), "showAlertDialog" );
    	}
    }
*/
    /**
     * 個別の月収支をのSQLデータを取得する
     */
/*
    private void getDifferentMonthSQLData( ){

		int investmentTmp = 0;
		int recoveryTmp = 0;
		int totalTmp = 0;

    	// 勝敗用
    	int winTmp = 0;
    	int loseTmp = 0;
    	int drawTmp = 0;

    	List<String> kisyulist = new ArrayList<String>();


    	try{
	        // 機種の数を取得する
	        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext() ) ).getWritableDatabase( );
	        constantsCursor = syushiDatabase.rawQuery(
										    			"SELECT DISTINCT kisyu "+
										    			"FROM constants "+
										    			"WHERE data like '%" + searchWord + "%' "+
										    			"ORDER BY kisyu",
										    			null );

	        // 機種を取得
	        if( constantsCursor.moveToFirst( ) ){

	        	do{
					//　データベースからデータを取得する
					String type = constantsCursor.getString( constantsCursor.getColumnIndex( "kisyu" ) );
					// 機種リストに追加
					kisyulist.add(type);

					Trace.d( "機種 = " + type );

	        	}while( constantsCursor.moveToNext( ) );
	        }


	        // 指定されて月ごとの収支を取得
	        for( int i =0; i < kisyulist.size( ); i++ ){

	    		investmentTmp = 0;
	    		recoveryTmp = 0;
	    		totalTmp = 0;

	        	// 勝敗用
	        	winTmp = 0;
	        	loseTmp = 0;
	        	drawTmp = 0;


	        	// アポストロフィ対策。'→''に置き換える
	//    		String replace = kisyulist.get(i).replace( "'", "''" );
	        	String replace = likeEscapeProtection( kisyulist.get( i ) );

	            // SQLite
	            constantsCursor = syushiDatabase.rawQuery( "SELECT kisyu, investment, recovery, _id "+
	            										   "FROM constants " +
	            										   "WHERE data like '%" + searchWord + "%' "+
	//            										   "and kisyu like '" + kisyulist.get(i) + "' "+
														   "and kisyu like '" + replace + "' "+
														   "ESCAPE "+ "'$' " +
	            										   "ORDER BY data",
	            										   null );

	            // データを取得
	            // カーソルを一番最初に戻す。
	    		if( constantsCursor.moveToFirst( ) ){

	    			do{
						//　データベースからデータを取得する
	    				int id = constantsCursor.getInt( constantsCursor.getColumnIndex( "_id" ) );
						int investment = constantsCursor.getInt( constantsCursor.getColumnIndex( "investment" ) );
						int recovery = constantsCursor.getInt( constantsCursor.getColumnIndex( "recovery" ) );
						int total = recovery - investment;

						// ID→１は初期データなので、データには含まない
						if( id == 1 ){
							continue;
						}


					// 収支
						investmentTmp += investment;
						recoveryTmp   += recovery;
						totalTmp      += total;

					//　勝敗
						// 分け
						if( total == 0 ){
							drawTmp ++;
						}
						// 勝ち
						else if( total > 0 ){
							winTmp ++;
						}
						// 負け
						else if( total < 0 ){
							loseTmp ++;
						}

	    			}while( constantsCursor.moveToNext( ) );
	    		}

				//　リストに追加
				mArrayList.add( new ListViewDrawAdapter( kisyulist.get(i), investmentTmp, recoveryTmp, totalTmp, winTmp, loseTmp, drawTmp ) );
				// ソート降順
				Collections.sort(mArrayList,  new ComparatorListViewDrawAdapter( 0 ) );
	        }

	    // 容量オーバー時の処理
    	}catch( SQLiteDiskIOException e ){
    		// アラートダイアログの表示
    		DialogFragment newFragment = new AlertDialogFragment( );
    		newFragment.show( getFragmentManager(), "showAlertDialog" );
    	}
    }
*/

	/**
	 *<p>
	 * data詳細へ移行処理
	 *</p>
     *
	 * @param pos   アドレス
	 */
	public void intentDetailedCommonProcess( int pos ){

         // インテント作成
        Intent intent = new Intent( getApplicationContext(), DrawDataDetailedActivity.class );

        // 引き継ぐもの
        intent.putExtra( "itemptn", ( itemptn + 1 ) );	// 項目
        intent.putExtra( "search",  mArrayList.get( pos ).getWord() );	// サーチワード
		// トランジションアニメーションを行わずに起動
		intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION );

        // alarmの設定画面ヘ
        startActivity( intent );
//      finish();
	}

	/**
	 * 引数で指定した文字列の中の「'」と「%」をlike句の対応する形にして返す
	 *
	 * @param str　変換する文字列
	 * @return　「'」と「%」をlile分に対応した形の文字列
	 */
/*
	private String likeEscapeProtection( String str ){
    	// アポストロフィ対策。「'」→「''」に置き換える
		String retStr = str.replace( "'", "''" );
    	// パーセント対急く。「%」→「$%」に置き換える
		retStr = retStr.replace( "%", "$%" );
		return retStr;
	}
*/
    /**
     *<p>
     * 戻るキーを押したときの呼ばれる
     *</p>
     */
    public void onClickDrawDataBack( View view ) {
        Trace.d( "itemptn = " + itemptn );
        finish( );
    }

    /**
     *<p>
     * メニューキーを押したときに呼ばれる
     *</p>
     */
    public void onClickDrawDataMenu( View view ) {
        Trace.d( "itemptn = " + itemptn );
    }
}

