package jp.co.kaiwaredaikon320.syushi;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.util.Random;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    static final String DB_NAME             = "syushi.db";  // DB名
    public static final String DB_TBL_CONSTANTS    = "constants";  // DBのテーブル名(収支)
	public static final String DB_TBL_TENPO        = "tbl_tenpo";  // DBのテーブル名(店舗)
    static final String DB_TBL_KISYU        = "tbl_kisyu";  // DBのテーブル名(機種)



    static final int DB_VERSION = 1;                		// DBのVersion

    private Context context;

	public static final String DATA                = "data";			// 日付
	public static final String TENPO               = "tenpo";		    // 店舗
    public static final String EXCHANGE_BALL       = "exchange_ball";	// 交換率 玉
    public static final String EXCHANGE_MEDAL      = "exchange_medal";	// 交換率 メダル

    public static final String NUMBER              = "number";	        // 台番
    public static final String KISYU               = "kisyu";		    // 機種
    public static final String TYPE                = "type";		    // タイプ

    public static final String EVENT               = "event";		    // イベント

	public static final String INVESTMENT          = "investment";	        // 投資
    public static final String INVESTMENT_TYPE     = "investment_type";	// 投資 タイプ
	public static final String RECOVERY            = "recovery";		    // 回収
    public static final String RECOVERY_TYPE       = "recovery_type";	    // 回収 タイプ

    public static final String START_TIME          = "start_time";         // 開始時間
    public static final String END_TIME            = "time";               // 終了時間

	public static final String MEMO		        = "memo";			    // メモ

    // コンストラクタ
    // CREATE用のSQLを取得する
    public MySQLiteOpenHelper(Context mContext ){
        super( mContext, DB_NAME, null, DB_VERSION );
        context = mContext;
    }

    public MySQLiteOpenHelper(Context mContext, String name, CursorFactory factory, int version) {
        super( mContext, name, factory, version );
        context = mContext;
    }

    // DBが存在しない状態でOpenすると、onCreateがコールされる
    // 新規作成されたDBのインスタンスが付与されるので、テーブルを作成する。
	@Override
	public void onCreate(SQLiteDatabase db) {

		try{
			DataBackup backup = new DataBackup( db );

			// このメソッドはStringとして定義したSQL文をそのまま実行することができます。
	//		db.execSQL ("CREATE TABLE constants (_id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT, tenpo TEXT, kisyu TEXT, investment INT, recovery INT );" );
            String sss = "CREATE TABLE " + DB_TBL_CONSTANTS + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DATA +      " TEXT,"    +  //日付
                    TENPO +     " TEXT,"    +  //店舗
                    EXCHANGE_BALL + " REAL,"  +  // 交換率 玉
                    EXCHANGE_MEDAL + " REAL,"  +  // 交換率 メダル
                    NUMBER +    " INTEGER," +  //台番
                    KISYU +     " TEXT,"    +  //機種
                    TYPE +     " INTEGER,"  +  //タイプ

                    EVENT +     " TEXT,"    +   //イベント

                    INVESTMENT +" INTEGER," +       //収支
                    INVESTMENT_TYPE +" INTEGER," +  //収支　タイプ
                    RECOVERY +  " INTEGER," +       //回収
                    RECOVERY_TYPE +  " INTEGER," +  //回収　タイプ

                    START_TIME+       " INTEGER,"    +   //開始時間
                    END_TIME+       " INTEGER,"    +   // 終了時間
                    MEMO +      " TEXT );";     //メモ

			db.execSQL ( sss );

			ContentValues cv=new ContentValues();

			// 日付
			cv.put( DATA, ( "00000000" ) );

			// 店舗
			cv.put( TENPO, "店舗未登録");
            //  交換率
            cv.put( EXCHANGE_BALL, 4.0 );
            cv.put( EXCHANGE_MEDAL, 20.00 );

            // 台番
            cv.put( NUMBER, 0 );

            // 機種
            cv.put( KISYU, "機種未登録");
            // 機種タイプ
            cv.put( TYPE, 0);

            // イベント
            cv.put( EVENT, "なし");


			// 収支
			cv.put( INVESTMENT, 0 );
            cv.put( INVESTMENT_TYPE, 0 );   // 投資タイプ
			// 回収
			cv.put( RECOVERY, 0 );
            cv.put( RECOVERY_TYPE, 0 );   // 投資タイプ

            // 時間
            cv.put( START_TIME, 0 );
            cv.put( END_TIME, 0 );

			// メモ
			cv.put( MEMO, "メモ" );

			//第1引数はテーブル名です。
			//第2引数のnullColumnHackはデータを挿入する際にnull値が許可されていないカラムに代わりに利用される値を指定します。
			//第3引数がContentValueオブジェクトです。
			//また、戻り値は正常終了した場合はROWID、失敗した場合は-1となります。
			long ret = db.insert( "constants", null, cv );


            // 店舗用のデータベースの作成
            sss = "CREATE TABLE " + DB_TBL_TENPO + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TENPO +     " TEXT,"    +  //店舗
                    EXCHANGE_BALL + " REAL,"  +  // 交換率 玉
                    EXCHANGE_MEDAL +      " REAL );";     // 交換率 メダル

            db.execSQL ( sss );

            Trace.d( "create tbl tenpo " );


            // 店舗用のデータベースの作成
            sss = "CREATE TABLE " + DB_TBL_KISYU + " (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KISYU +     " TEXT,"    +       // 機種
                    TYPE +      " INTEGER );";      // タイプ

            db.execSQL ( sss );

            Trace.d( "create tbl kisyu " );

			// データベース保存用フォルダを作成する
			boolean bbb = backup.makeSdcardSaveFolder( );

			// folder作成に失敗した場合はスローを投げる
			// 書き込み失敗時も
			if( !bbb || ret == -1 ){
				throw new Exception( );
			}

		}catch( SQLiteDiskIOException e ){
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );
	        alertDialogBuilder.setTitle( "内部ストレージエラー" );
	        alertDialogBuilder.setMessage( "内部ストレージの容量確保に失敗しました。\n" +
					  					   "内部ストレージの容量を確保してからもう一度お試しください。" );
	        alertDialogBuilder.setPositiveButton("OK",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which){
	    	            	// ダイアログ終了
	    	            	dialog.dismiss();
	                    }
	                });
	        alertDialogBuilder.setCancelable(true);
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();
		}catch( Exception e ){
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );
	        alertDialogBuilder.setTitle("内部ストレージエラー");
	        alertDialogBuilder.setMessage( "データーベースの初期化に失敗しました。\n" );
	        alertDialogBuilder.setPositiveButton("OK",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public  void onClick(DialogInterface dialog, int which){
	    	            	// ダイアログ終了
	    	            	dialog.dismiss();
	                    }
	                });
	        alertDialogBuilder.setCancelable(true);
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();

            Trace.e( "SQLite 初期化エラー:" + e.toString() );
		}
	}

    // コンストラクタで指定したバージョンと、参照先のDBのバージョンに差異があるときにコールされる
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ
	}


	/*
	 *
	 * @param word　検索ワード(日付)
	 * @return 指定したワードでHitした投資のtotal額

	public int getDaySyushi( SQLiteDatabase database, String word, String column ){

		int retTotal = 0;

//		Trace.d( "getDaySyushi word:"+ word+" column:"+column);
//		Trace.d( "SELECT "+column+" " );

		// 取得
		Cursor totalCursor = database.rawQuery( "SELECT "+column+" "+
												"FROM constants " +
												"WHERE data like '%" + word + "%' ",
												null );

       // データを取得
       // カーソルを一番最初に戻す。
		if( totalCursor.moveToFirst( ) ){
			do{
				//　データベースからデータを取得する
				int columndata = totalCursor.getInt( totalCursor.getColumnIndex( column ) );
				retTotal += columndata;
			}while( totalCursor.moveToNext( ) );
		}
		// リストなし時の処理
		else{
			totalCursor.close();
//			Trace.d( "データなしよ" );
			return 0;
		}

		totalCursor.close();

		return retTotal;
	}
     */


    /**
     *  第二引数で引っかかったものの、トータルを返す
     *
     * @param database 参照DB
     * @param word 検索ワード
     * @return 検索で引っかかったものの、トータル収支
     */
    public int getDayInvestment( SQLiteDatabase database, String word ){

        int retTotal = 0;

        // 取得
        Cursor totalCursor = database.rawQuery( "SELECT "+
                        MySQLiteOpenHelper.INVESTMENT + "," +
                        MySQLiteOpenHelper.INVESTMENT_TYPE + "," +
                        MySQLiteOpenHelper.EXCHANGE_BALL + "," +
                        MySQLiteOpenHelper.EXCHANGE_MEDAL +
                        " FROM constants" +
                        " WHERE data like '%" + word + "%'",
                null );

        // データを取得
        // カーソルを一番最初に戻す。
        if( totalCursor.moveToFirst( ) ){
            do{

                // データの取得
                int invPtn = totalCursor.getInt(totalCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
                double exchangeBall = totalCursor.getDouble(totalCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
                double exchangeSlot = totalCursor.getDouble(totalCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));
                double calc = totalCursor.getInt( totalCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );;

                // パチ
                if( invPtn == 1 ){
                    calc = exchangeBall*totalCursor.getInt( totalCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
                }
                // スロ
                if( invPtn == 2 ){
                    calc = exchangeSlot*totalCursor.getInt( totalCursor.getColumnIndex( MySQLiteOpenHelper.INVESTMENT ) );
                }

                //　データを加算
//              retTotal += columnData;
                retTotal += AppSetting.getBigDecimalInt(calc,0, BigDecimal.ROUND_UP);

            }while( totalCursor.moveToNext( ) );
        }
        // リストなし時の処理
        else{
            totalCursor.close();
//			Trace.d( "データなしよ" );
            return 0;
        }

        totalCursor.close();
        return retTotal;
    }

    /**
     *  第二引数で引っかかったものの、トータルを返す
     *
     * @param database 参照DB
     * @param word 検索ワード
     * @return 検索で引っかかったものの、トータル収支
     */
    public int getDayRecovery( SQLiteDatabase database, String word ){

        int retTotal = 0;

        // 取得
        Cursor totalCursor = database.rawQuery( "SELECT "+
                        MySQLiteOpenHelper.RECOVERY + "," +
                        MySQLiteOpenHelper.RECOVERY_TYPE + "," +
                        MySQLiteOpenHelper.EXCHANGE_BALL + "," +
                        MySQLiteOpenHelper.EXCHANGE_MEDAL +
                        " FROM constants" +
                        " WHERE data like '%" + word + "%' ",
                null );

        // データを取得
        // カーソルを一番最初に戻す。
        if( totalCursor.moveToFirst( ) ){
            do{
                // データの取得
                int revPtn = totalCursor.getInt(totalCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));
                double exchangeBall = totalCursor.getDouble(totalCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
                double exchangeSlot = totalCursor.getDouble(totalCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));
                double calc = totalCursor.getInt( totalCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );;

                // パチ
                if( revPtn == 1 ){
                    calc = exchangeBall*totalCursor.getInt( totalCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
                }
                // スロ
                if( revPtn == 2 ){
                    calc = exchangeSlot*totalCursor.getInt( totalCursor.getColumnIndex( MySQLiteOpenHelper.RECOVERY ) );
                }

                //　データを加算
//              retTotal += columnData;
                retTotal += AppSetting.getBigDecimalInt(calc,0, BigDecimal.ROUND_UP);

            }while( totalCursor.moveToNext( ) );
        }
        // リストなし時の処理
        else{
            totalCursor.close();
//			Trace.d( "データなしよ" );
            return 0;
        }

        totalCursor.close();
        return retTotal;
    }


	/**
	 *
	 * @param word　検索ワード(日付)
	 * @return 指定したワードでHitした投資のtotal額
	 */
	public boolean getDayDrawFlg( SQLiteDatabase database, String word, String column ){

		boolean retFlg = false;

//		Trace.d( "getDaySyushi word:"+ word+" column:"+column);
//		Trace.d( "SELECT "+column+" " );

		// 取得
		Cursor totalCursor = database.rawQuery( "SELECT "+column+" "+
												"FROM constants " +
												"WHERE data like '%" + word + "%' ",
												null );

		// データがあるとき
		if( totalCursor.moveToFirst( ) ){
            retFlg =  true;
		}

		totalCursor.close();

		return (retFlg);
	}

	/**
	 *
	 * @param database　データベース
	 * @param year　デバッグでデータを入力する年
	 */
	public void setDebugSqlData( SQLiteDatabase database, String year ){

		try{
	        // Randomクラスのインスタンス化
	        Random rnd = new Random();

			ContentValues cv=new ContentValues();

			for( int m = 1; m <= 12; m++ ){

				String ms;

				if( m >= 10 ){
					ms = ""+ m;
				}
				else{
					ms = "0"+ m;
				}

				for( int d = 1; d <= 28; d++ ){

					if( d >= 10 ){
						// 日付
						cv.put( DATA, ( year + ms + d) );
					}
					else{
						// 日付
						cv.put( DATA, ( year + ms + "0" + d ) );
					}

					// 店舗
					cv.put( TENPO, "店舗" + ( rnd.nextInt( 100 ) + 1 ) );
                    //  交換率
                    cv.put( EXCHANGE_BALL, 3.57 );
                    cv.put( EXCHANGE_MEDAL, 18.9 );

                    // 台番
                    cv.put( NUMBER, rnd.nextInt( 9999 ) + 1 );

                    // 機種
                    cv.put( KISYU, "機種" + ( rnd.nextInt( 100 ) + 1 ) );
                    // 機種タイプ
                    cv.put( TYPE, ( rnd.nextInt( 3 ) ) );

                    // イベント
                    cv.put( EVENT, "イベント"+ ( rnd.nextInt( 100 ) + 1 ) );

					// 収支
					cv.put( INVESTMENT, rnd.nextInt( 999999 ) + 1 );
                    cv.put( INVESTMENT_TYPE, ( rnd.nextInt( 3 )) );   // 投資タイプ
					// 回収
					cv.put( RECOVERY, rnd.nextInt( 999999 ) + 1 );
                    cv.put( RECOVERY_TYPE, ( rnd.nextInt( 3 ) ) );   // 投資タイプ


                    int sss = 60*  ( rnd.nextInt( 10 ) ) +  rnd.nextInt( 60 );
                    int eee = sss +( 60* rnd.nextInt( 10 ) ) +  rnd.nextInt( 60 );

                    // 時間
                    cv.put( START_TIME, sss );
                    cv.put( END_TIME, eee );

					// メモ
					cv.put( MEMO,
							"メモメモメモメモメモ " + year + m + "" + d + "\n" +
							"メモメモメモメモメモ " + year + m + "" + d + "\n" +
							"メモメモメモメモメモ " + year + m + "" + d + "\n" +
							"メモメモメモメモメモ " + year + m + "" + d + "\n" +
							"メモメモメモメモメモ " + year + m + "" + d + "\n" +
							"メモメモメモメモメモ " + year + m + "" + d + "\n");

					//第1引数はテーブル名です。
					//第2引数のnullColumnHackはデータを挿入する際にnull値が許可されていないカラムに代わりに利用される値を指定します。
					//第3引数がContentValueオブジェクトです。
					//また、戻り値は正常終了した場合はROWID、失敗した場合は-1となります。
					long ret = database.insert( "constants", null, cv );
				}
			}
		}catch( Exception e ){
			// TODO 自動生成されたメソッド・スタ
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );
	        alertDialogBuilder.setTitle( "データ作成失敗");
	        alertDialogBuilder.setMessage( "データの作成に失敗しました。" );
	        alertDialogBuilder.setPositiveButton("OK",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which){
	    	            	// ダイアログ終了
	    	            	dialog.dismiss();
	                    }
	                });
	        alertDialogBuilder.setCancelable(true);
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();
		}
	}

	/**
	 *
	 * 店舗のデータを12件作成する
	 *
	 * @param database　データを入力するデータベース
	 */
	public void setDebugTenpoSqlData( SQLiteDatabase database ){

		try{
			ContentValues cv=new ContentValues();

			for( int m = 1; m <= 12; m++ ){

				// 店舗
				cv.put( TENPO, "店舗" + m );
				//  交換率
				cv.put( EXCHANGE_BALL, 3.57 );
				cv.put( EXCHANGE_MEDAL, 18.9 );

				long ret = database.insert( DB_TBL_TENPO, null, cv );
			}
		}catch( Exception e ){
			// TODO 自動生成されたメソッド・スタ
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );
			alertDialogBuilder.setTitle( "データ作成失敗");
			alertDialogBuilder.setMessage( "データの作成に失敗しました。" );
			alertDialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which){
							// ダイアログ終了
							dialog.dismiss();
						}
					});
			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}

    /**
     *
     * 機種データを12件作成する
     *
     * @param database　データを入力するデータベース
     */
    public void setDebugKisyuSqlData( SQLiteDatabase database ){

        try{
            ContentValues cv=new ContentValues();

            for( int m = 1; m <= 12; m++ ){

                // 機種
                cv.put( KISYU, "機種" + m );
                // 機種タイプ
                cv.put( TYPE, m%3);

                long ret = database.insert( DB_TBL_KISYU, null, cv );
            }
        }catch( Exception e ){
            // TODO 自動生成されたメソッド・スタ
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );
            alertDialogBuilder.setTitle( "データ作成失敗");
            alertDialogBuilder.setMessage( "データの作成に失敗しました。" );
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            // ダイアログ終了
                            dialog.dismiss();
                        }
                    });
            alertDialogBuilder.setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}