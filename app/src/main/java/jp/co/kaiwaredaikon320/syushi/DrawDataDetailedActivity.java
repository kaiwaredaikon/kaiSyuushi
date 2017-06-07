package jp.co.kaiwaredaikon320.syushi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DrawDataDetailedActivity extends Activity{

	private SQLiteDatabase syushiDatabase = null;
	private Cursor constantsCursor = null;

/*
	private TextView investmentText;
	private TextView recoveryText;
	private TextView totalText;
*/

    private TextView mTitleName;        //  タイトルの

	// 検索ワード
	private String seatchWord;
	private int itemptn;		// 項目;

	private int hierarchy;		// 現在の階層

	// ListView
	private ListView mListView;
	// ListViewに表示するデータ
	private ArrayList<ListViewItem> mArrayList = new ArrayList<ListViewItem>();

//	private ListViewAdapter adapter;
	private DrawDataDetailedListViewAdapter adapter;

	private String[] searchWordArray = { "", "", "", "", "" };


/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		// アクションバーの戻るを押したときの処理
		if( id == android.R.id.home ){

			hierarchy ++;

			if( itemptn < hierarchy ){
				// DataPreferenceActivityに戻る
	        	Intent intent = new android.content.Intent( this, DrawDataActivity.class );
	        	intent.setFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY );
	        	intent.putExtra( "idid", ( itemptn - 1 ) );
	        	startActivity( intent );

//				finish();
			}
			else{

				//　title変更
//				setTitleInit( seatchWordArray[ hierarchy ] );

				Trace.d( "onKeyDown = " + seatchWordArray[ hierarchy ] );

				// 日別の詳細収支
				if( hierarchy == 0 ){
					makeDayListView( seatchWordArray[ hierarchy ] );
				}

				// 月収支
				if( hierarchy == 1 ){
					makeMonthListView( seatchWordArray[ hierarchy ] );
				}

				// 月別収支
				if( hierarchy == 2 ){
					makeYearListView( seatchWordArray[ hierarchy ] );
				}

				// 年別
		        if( hierarchy == 3 ){
		        	makeTotalListView( );
		        }

		        //　リストを一番上に戻す
				mListView.setSelection(0);

		        setTitleInit( seatchWordArray[ hierarchy ] );
			}

	        return true;
	    }
		return super.onOptionsItemSelected( item );
	}*/


	@Override
    public void onCreate(Bundle savedInstanceState) {

		Trace.d( "onCreate Start " );

        super.onCreate(savedInstanceState);

        try{

	        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext() ) ).getWritableDatabase( );

	        Intent intent = getIntent( );

	        // 項目を取得
	        itemptn = intent.getIntExtra( "itemptn", 0 );
	        //
	        seatchWord = intent.getStringExtra( "search" );
	        Trace.d( "search woird" + seatchWord );

	        //　階層を代入
	        // 0→日ごとの機種データ　1→日別収支　2→月データ　3→年収支
	        hierarchy = itemptn;

	        // タイトル非表示
//      getActionBar().setDisplayShowTitleEnabled(false);
	        // アイコンを消す
//	        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);

	        // Upアイコンの表示
//	        getActionBar().setDisplayHomeAsUpEnabled (true);

			setContentView( R.layout.detailed_listview );

            // タイトル用
            mTitleName = (TextView) findViewById(R.id.header_title_draw_data);

	    	// Viewを取得
			mListView = (ListView)this.findViewById(android.R.id.list);

	        // コンテキストメニューを表示するViewを登録する
	        registerForContextMenu( mListView );

	        // アクションバータイトルを初期化
	        setTitleInit( );

            // タップでさらに詳細収支へ飛ぶ
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Trace.d( "onItemClick:hierarchy " + hierarchy );
                    hierarchy --;
                    changeListViewProcess( position );
            }
            } );

	        // ListViewにアダプタを設定
	//      adapter = new ListViewAdapter(
			adapter = new DrawDataDetailedListViewAdapter(
			this,
				R.layout.detailed_input_list, 	// 1行分のレイアウトファイル
				0,          	        // 上記レイアウト内のテキスト表示箇所のId(※未使用)
				mArrayList              // 表示対象のデータ
				);
			// 階層を設定
			adapter.setHierarchy( hierarchy );

	        mListView.setAdapter(adapter);

	        //	サーチワードの保存
            searchWordArray[ itemptn ] = seatchWord;

	        // 月の日別収支表示
	        if( itemptn == 1 ){
	        	makeMonthListView( seatchWord );
	        }

	        //　月別収支
	        if( itemptn == 2 ){
	        	makeYearListView( seatchWord );
	        }

	        if( itemptn == 3 ){
	        	makeTotalListView( );
	        }
	/*
			// total表示用のテキストViewのウィジェット取得
	    	investmentText = (TextView)this.findViewById(R.id.listAct_investmentNumber_cc);
	    	recoveryText   = (TextView)this.findViewById(R.id.listAct_recoveryNumber);
	    	totalText 	   = (TextView)this.findViewById(R.id.listAct_totalNumber);

	    	setAllListViewTotal(  );
	*/

	        Trace.d( "onCreate end" );
	    // 容量オーバー時の処理
    	}catch( SQLiteDiskIOException e ){
    		// アラートダイアログの表示
    		DialogFragment newFragment = new AlertDialogFragment( );
    		newFragment.show( getFragmentManager(), "showAlertDialog" );
    	}
    }


/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if( keyCode == KeyEvent.KEYCODE_BACK ){
	        finish( );
			return true;
		}
		return false;
	}
*/

	@Override
	public void onDestroy( ){
		super.onDestroy( );

		if( constantsCursor != null ){
			constantsCursor.close();
		}

        syushiDatabase.close();

        Trace.d( "Destroy DrawdataDetailedActivity" );
	}

    // アクションバータイトルを初期化
    private void setTitleInit( ){

	    // 年と月
	    if( itemptn == 1 ){
//	    	setTitle( seatchWord.substring(0, 4) + "年" + seatchWord.substring(4, 6) + "月");
            mTitleName.setText(seatchWord.substring(0, 4) + "年" + seatchWord.substring(4, 6) + "月");
	    }

	    // 年のみ
	    if( itemptn == 2 ){
//	    	setTitle( seatchWord.substring(0, 4) + "年" );
            mTitleName.setText(seatchWord.substring(0, 4) + "年");
	    }

	    // 累計
	    if( itemptn == 3 ){
//	    	setTitle( "年別収支" );
            mTitleName.setText("年別収支");
	    }
    }

    // アクションバータイトルを初期化
    private void setTitleInit( String seatchStr ){

		// 階層を設定
		adapter.setHierarchy( hierarchy );

	    // 年と月と日
	    if( hierarchy == 0 ){
//	    	setTitle( seatchStr.substring(0, 4) + "年" + seatchStr.substring(4, 6) + "月" +  seatchStr.substring(6, 8) + "日" );
            mTitleName.setText( seatchStr.substring(0, 4) + "年" + seatchStr.substring(4, 6) + "月" +  seatchStr.substring(6, 8) + "日" );
	    }

	    // 年と月
	    if( hierarchy == 1 ){
//	    	setTitle( seatchStr.substring(0, 4) + "年" + seatchStr.substring(4, 6) + "月" );
            mTitleName.setText( seatchStr.substring(0, 4) + "年" + seatchStr.substring(4, 6) + "月" );
	    }

	    // 年のみ
	    if( hierarchy == 2 ){
//	    	setTitle( seatchStr.substring(0, 4) + "年" );
            mTitleName.setText( seatchStr.substring(0, 4) + "年" );
	    }
	    // 累計
	    if( hierarchy == 3 ){
//	    	setTitle( "年別収支" );
            mTitleName.setText( "年別収支" );
	    }
    }


    /**
     * 詳細収支への移行処理をまとめたもの
     */
    private void changeListViewProcess( int pos ){

		// これ以上下の階層がないので何もしない
		if( hierarchy < 0 ){
			hierarchy = 0;
		}
		else{

			Trace.d( "changeListViewProcess  hierarchy = " + hierarchy );

			//　title変更
			setTitleInit( mArrayList.get( pos ).getWord() );

//			Trace.d( "changeListViewProcess = " + mArrayList.get( pos ).getWord() );

			// 検索ワードを保存しておく？
            searchWordArray[ hierarchy ] = mArrayList.get( pos ).getWord();

			// 日別の詳細収支
			if( hierarchy == 0 ){
				Trace.d( "makeDayListView:" + mArrayList.get( pos ).getWord()  );
				makeDayListView( mArrayList.get( pos ).getWord() );
				Trace.d( "makeDayListView end" );
			}
			// 日別の詳細収支へ
			if( hierarchy == 1 ){
				Trace.d( "makeMonthListView:" + mArrayList.get( pos ).getWord()  );
				makeMonthListView( mArrayList.get( pos ).getWord() );
				Trace.d( "makeMonthListView end" );
			}
			if( hierarchy == 2 ){
				Trace.d( "makeYearListView:" + mArrayList.get( pos ).getWord()  );
				makeYearListView( mArrayList.get( pos ).getWord() );
				Trace.d( "makeYearListView end" );
			}
	        if( hierarchy == 3 ){
	        	Trace.d( "makeTotalListView:" + mArrayList.get( pos ).getWord()  );
	        	makeTotalListView( );
	        	Trace.d( "makeTotalListView end" );
	        }

//	        リストを一番上に戻す
			mListView.setSelection( 0 );
		}
    }


    /**
     * 日別収支データの詳細リストを作成する
     */
    private void makeDayListView( String word ){

    	Trace.d( "word = " + word  );

    	//　リストの初期化
		mArrayList.clear();

		System.out.println("現在の項目数は " + mArrayList.size() + " です");

        // SQLite
        constantsCursor = syushiDatabase.rawQuery( "SELECT "+
													MySQLiteOpenHelper.DATA + "," +
													MySQLiteOpenHelper.KISYU + "," +
													MySQLiteOpenHelper.INVESTMENT + "," +
													MySQLiteOpenHelper.INVESTMENT_TYPE + "," +
													MySQLiteOpenHelper.RECOVERY + "," +
													MySQLiteOpenHelper.RECOVERY_TYPE + "," +
													MySQLiteOpenHelper.EXCHANGE_BALL + "," +
													MySQLiteOpenHelper.EXCHANGE_MEDAL +
        										   " FROM constants" +
        										   " WHERE data like '%" + word + "%'"+
        										   " ORDER BY data",
        										   null );

        Trace.d("現在の項目数は " + mArrayList.size() + " です");

        // データを取得
        // カーソルを一番最初に戻す。
		if( constantsCursor.moveToFirst( ) ){

			Trace.d("現在の項目数は " + mArrayList.size() + " です");
			do{
				Trace.d("現在の項目数は " + mArrayList.size() + " です");
				//　データベースからデータを取得する
				String kisyu = constantsCursor.getString( constantsCursor.getColumnIndex( "kisyu" ) );

				//　交換率
				double exchangeBall = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
				double exchangeSlot = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));

				// 投資
				int invPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
				double calcInv = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );

				// パチ
				if( invPtn == 1 ){
					calcInv = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
				}
				// スロ
				if( invPtn == 2 ){
					calcInv = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
				}

				//　回収計算
				// データの取得
				int revPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));
				double calcRev = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );;

				// パチ
				if( revPtn == 1 ){
					calcRev = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
				}
				// スロ
				if( revPtn == 2 ){
					calcRev = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
				}

				Trace.d("現在の項目数は " + mArrayList.size() + " です");

				//　リストに登録
				mArrayList.add( new ListViewItem( kisyu,
						AppSetting.getBigDecimalInt(calcInv,0, BigDecimal.ROUND_UP),
						AppSetting.getBigDecimalInt(calcRev,0, BigDecimal.ROUND_UP),
						((AppSetting.getBigDecimalInt(calcRev,0, BigDecimal.ROUND_UP))-(AppSetting.getBigDecimalInt(calcInv,0, BigDecimal.ROUND_UP))),
						word ) );

				Trace.d("現在の項目数は " + mArrayList.size() + " です");
			}while( constantsCursor.moveToNext( ) );
		}

		constantsCursor.close();
		System.gc();

		// リストの再描画
//		mListView.invalidateViews();
        adapter.notifyDataSetChanged();
		Trace.d("現在の項目数は " + mArrayList.size() + " です");
    }


    /**
     * 月収支データの詳細リストを作成する
     */
    private void makeMonthListView( String word ){

		int totalInv = 0;
		int totalRev = 0;

    	//　リストの初期化
		mArrayList.clear();

    	List<String> listStr = new ArrayList<String>();
    	List<String> listStrTmp = new ArrayList<String>();

        // 機種の数を取得する
        constantsCursor = syushiDatabase.rawQuery(
									    			"SELECT DISTINCT data "+
									    			"FROM constants "+
									    			"WHERE data like '%" + word + "%' "+
									    			"ORDER BY data",
									    			null );

        // 日付を取得する
        if( constantsCursor.moveToFirst( ) ){

        	do{
				//　データベースからデータを取得する
				String type = constantsCursor.getString( constantsCursor.getColumnIndex( "data" ) );
				// 日付データ
				listStrTmp.add( type );
        	}while( constantsCursor.moveToNext( ) );
        }

        // 重複を排除してリストを作ってくれる？
        listStr = new ArrayList<String>(new LinkedHashSet<String>(listStrTmp));


        // 機種ごとに収支を取得する
        for( int i =0; i < listStr.size(); i++ ){

			totalInv = 0;
			totalRev = 0;

            // SQLite
            constantsCursor = syushiDatabase.rawQuery( "SELECT "+
														MySQLiteOpenHelper.DATA + "," +
														MySQLiteOpenHelper.INVESTMENT + "," +
														MySQLiteOpenHelper.INVESTMENT_TYPE + "," +
														MySQLiteOpenHelper.RECOVERY + "," +
														MySQLiteOpenHelper.RECOVERY_TYPE + "," +
														MySQLiteOpenHelper.EXCHANGE_BALL + "," +
														MySQLiteOpenHelper.EXCHANGE_MEDAL +
            										   " FROM constants" +
            										   " WHERE data like '%" + listStr.get(i) + "%'"+
            										   " ORDER BY data",
            										   null );

            // データを取得
            // カーソルを一番最初に戻す。
    		if( constantsCursor.moveToFirst( ) ){

    			do{
					//　交換率
					double exchangeBall = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
					double exchangeSlot = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));

					// 投資
					int invPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
					double calcInv = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );

					// パチ
					if( invPtn == 1 ){
						calcInv = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
					}
					// スロ
					if( invPtn == 2 ){
						calcInv = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
					}

					//　トータル投資
					totalInv += AppSetting.getBigDecimalInt(calcInv,0, BigDecimal.ROUND_UP);


					//　回収計算
					// データの取得
					int revPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));
					double calcRev = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );;

					// パチ
					if( revPtn == 1 ){
						calcRev = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
					}
					// スロ
					if( revPtn == 2 ){
						calcRev = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
					}

					//　トータル
					totalRev += AppSetting.getBigDecimalInt(calcRev,0, BigDecimal.ROUND_UP);

    			}while( constantsCursor.moveToNext( ) );
    		}
			//　リストに追加
			mArrayList.add( new ListViewItem( listStr.get( i ).substring(4, 6)+"月"+listStr.get( i ).substring(6, 8)+"日",
					totalInv, totalRev, (totalRev-totalInv), listStr.get(i) ) );
        }

        constantsCursor.close();
        System.gc();

		// リストの再描画
//		mListView.invalidateViews();
        adapter.notifyDataSetChanged();

		Trace.d("現在の項目数は " + mArrayList.size() + " です");
    }


    /**
     * 年収支データの詳細リストを作成する
     */
    private void makeYearListView( String word ){

		int totalInv = 0;
		int totalRev = 0;

    	//　リストの初期化
		mArrayList.clear();

    	List<String> listStr = new ArrayList<String>();
    	List<String> listStrTmp = new ArrayList<String>();

        // 機種の数を取得する
        constantsCursor = syushiDatabase.rawQuery(
									    			"SELECT DISTINCT data "+
									    			"FROM constants "+
									    			"WHERE data like '%" + word + "%' "+
									    			"ORDER BY data",
									    			null );

        // 日付を取得する
        if( constantsCursor.moveToFirst( ) ){

        	do{
				//　データベースからデータを取得する
				String type = constantsCursor.getString( constantsCursor.getColumnIndex( "data" ) );
				// 日付データ
				listStrTmp.add( type.substring( 0, 6 ) );

//				Trace.d( "typetypetypetypetype = " + type );

        	}while( constantsCursor.moveToNext( ) );
        }

        // 重複を排除してリストを作ってくれる？
        listStr = new ArrayList<String>(new LinkedHashSet<String>(listStrTmp));

        // 機種ごとに収支を取得する
        for( int i =0; i < listStr.size(); i++ ){

			totalInv = 0;
			totalRev = 0;

            // SQLite
            constantsCursor = syushiDatabase.rawQuery( "SELECT "+
														MySQLiteOpenHelper.DATA + "," +
														MySQLiteOpenHelper.INVESTMENT + "," +
														MySQLiteOpenHelper.INVESTMENT_TYPE + "," +
														MySQLiteOpenHelper.RECOVERY + "," +
														MySQLiteOpenHelper.RECOVERY_TYPE + "," +
														MySQLiteOpenHelper.EXCHANGE_BALL + "," +
														MySQLiteOpenHelper.EXCHANGE_MEDAL +
            										   " FROM constants" +
            										   " WHERE data like '%" + listStr.get(i) + "%'"+
            										   " ORDER BY data",
            										   null );

            // データを取得
            // カーソルを一番最初に戻す。
    		if( constantsCursor.moveToFirst( ) ){

    			do{
					//　交換率
					double exchangeBall = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
					double exchangeSlot = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));

					// 投資
					int invPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
					double calcInv = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );

					// パチ
					if( invPtn == 1 ){
						calcInv = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
					}
					// スロ
					if( invPtn == 2 ){
						calcInv = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
					}

					//　トータル投資
					totalInv += AppSetting.getBigDecimalInt(calcInv,0, BigDecimal.ROUND_UP);


					//　回収計算
					// データの取得
					int revPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));
					double calcRev = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );;

					// パチ
					if( revPtn == 1 ){
						calcRev = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
					}
					// スロ
					if( revPtn == 2 ){
						calcRev = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
					}

					//　トータル
					totalRev += AppSetting.getBigDecimalInt(calcRev,0, BigDecimal.ROUND_UP);

    			}while( constantsCursor.moveToNext( ) );
    		}
			//　リストに追加
			mArrayList.add( new ListViewItem( listStr.get( i ).substring(4, 6)+"月",
							totalInv, totalRev,(totalRev-totalInv), listStr.get(i) ) );
        }
		// リストの再描画
//		mListView.invalidateViews();
        adapter.notifyDataSetChanged();

        constantsCursor.close();
        System.gc();

		Trace.d("現在の項目数は " + mArrayList.size() + " です");
    }


    /**
     * 年収支データの詳細リストを作成する
     */
    private void makeTotalListView( ){

		int totalInv = 0;
		int totalRev = 0;

		// リストの初期化
		mArrayList.clear();

    	List<String> listStr = new ArrayList<String>();
    	List<String> listStrTmp = new ArrayList<String>();

        // 機種の数を取得する
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
				listStrTmp.add( type.substring( 0, 4 ) );
				Trace.d( "typetypetypetypetype = " + type );

        	}while( constantsCursor.moveToNext( ) );
        }

        // 重複を排除してリストを作ってくれる？
        listStr = new ArrayList<String>(new LinkedHashSet<String>(listStrTmp));

        // 機種ごとに収支を取得する
        for( int i =0; i < listStr.size(); i++ ){

			totalInv = 0;
			totalRev = 0;

            // SQLite
            constantsCursor = syushiDatabase.rawQuery( "SELECT "+
														MySQLiteOpenHelper.DATA + "," +
														MySQLiteOpenHelper.INVESTMENT + "," +
														MySQLiteOpenHelper.INVESTMENT_TYPE + "," +
														MySQLiteOpenHelper.RECOVERY + "," +
														MySQLiteOpenHelper.RECOVERY_TYPE + "," +
														MySQLiteOpenHelper.EXCHANGE_BALL + "," +
														MySQLiteOpenHelper.EXCHANGE_MEDAL +
            										   " FROM constants" +
            										   " WHERE data like '%" + listStr.get(i) + "%'"+
            										   " ORDER BY data",
            										   null );

            // データを取得
            // カーソルを一番最初に戻す。
    		if( constantsCursor.moveToFirst( ) ){

                if( constantsCursor.getString( constantsCursor.getColumnIndex( "data" ) ).equals( "00000000" ) ){
                	continue;
                }

    			do{
					//　交換率
					double exchangeBall = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
					double exchangeSlot = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));

					// 投資
					int invPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
					double calcInv = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );

					// パチ
					if( invPtn == 1 ){
						calcInv = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
					}
					// スロ
					if( invPtn == 2 ){
						calcInv = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
					}

					//　トータル投資
					totalInv += AppSetting.getBigDecimalInt(calcInv,0, BigDecimal.ROUND_UP);


					//　回収計算
					// データの取得
					int revPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));
					double calcRev = constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );;

					// パチ
					if( revPtn == 1 ){
						calcRev = exchangeBall*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
					}
					// スロ
					if( revPtn == 2 ){
						calcRev = exchangeSlot*constantsCursor.getInt( constantsCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
					}

					//　トータル
					totalRev += AppSetting.getBigDecimalInt(calcRev,0, BigDecimal.ROUND_UP);

    			}while( constantsCursor.moveToNext( ) );
    		}
			//　リストに追加
			mArrayList.add( new ListViewItem( listStr.get( i ).substring(0, 4)+"年",
					totalInv, totalRev, (totalRev-totalInv), listStr.get(i) ) );
        }
		// リストの再描画
//		mListView.invalidateViews();

        constantsCursor.close();
        System.gc();

        adapter.notifyDataSetChanged();

		Trace.d("現在の項目数は " + mArrayList.size() + " です");
    }

    /**
     *<p>
     * 戻るキーを押したときの呼ばれる
     *</p>
     */
    public void onClickDrawDataBack( View view ) {
        Trace.d( "hierarchy = " + hierarchy );

        hierarchy ++;

        if( itemptn < hierarchy ){
            // DataPreferenceActivityに戻る
            Intent intent = new android.content.Intent( this, DrawDataActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY );
            intent.putExtra( "idid", ( itemptn - 1 ) );
            startActivity( intent );

//			finish();
        }
        else{

            // 日別の詳細収支
            if( hierarchy == 0 ){
                makeDayListView( searchWordArray[ hierarchy ] );
            }

            // 月収支
            if( hierarchy == 1 ){
                makeMonthListView( searchWordArray[ hierarchy ] );
            }

            // 月別収支
            if( hierarchy == 2 ){
                makeYearListView( searchWordArray[ hierarchy ] );
            }

            // 年別
            if( hierarchy == 3 ){
                makeTotalListView( );
            }

            //　リストを一番上に戻す
            mListView.setSelection(0);

            // title変更
            setTitleInit( searchWordArray[ hierarchy ] );
        }
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