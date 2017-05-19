package jp.co.kaiwaredaikon320.syushi;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;

public class DataBackup {

    private Context con;
    // ファイルコピー
    private Filecopy fc;
    private SQLiteDatabase db;


    // コンストラクタ
	public DataBackup( SQLiteDatabase database ){
		db = database;
	}

    // コンストラクタ
	public DataBackup( Context context, Filecopy fcopy, SQLiteDatabase database ){
		// contextの引継ぎ
		con = context;
		// ファイルコピー
		fc = fcopy;
		db = database;
	}


	/**
	 *<p>
	 * DBのディレクトリとファイル名を取得
	 *</p>
	 */
    public String getDatabasePath( ) {
		// DBのディレクトリとファイル名
		return db.getPath();
    }

	/**
	 *<p>
	 * (内部ストレージのトップディレクトリを開く)
	 *</p>
	 */
    @SuppressLint("NewApi")
	public String getStoragePath( ) {

/*
    	// Kitkat以上のみの処理
    	if( Build.VERSION.SDK_INT >= 19 ){

    		// 外部ストレージのパスを取得する
    	    File[] extDirs = con.getExternalCacheDirs( );

    	    for( int i = 0; i < extDirs.length; i ++ ){

    	    	if( ( extDirs.length - 1 - i ) >= 0 ){
		    	    // 一番後ろがSDのパス？
		    	    if( extDirs[ ( extDirs.length - 1 - i ) ] != null ){
		    	    	String extSdDirPath = extDirs[ ( extDirs.length -1 - i ) ].toString();
		    	    	Trace.d( "extSdDirPath = " + extSdDirPath );
		    	    	return ( extSdDirPath );
		    	    }
    	    	}
    	    }
    	    return ( Environment.getExternalStorageDirectory().getPath() );
//    	    return ( Environment.getExternalStorageDirectory().getPath() + Filecopy.FILE_DIR2 );
    	}
    	else{
	    	// SDカードのパス
	    	if( Filecopy.getMount_sd() != null){
//	    		return ( Filecopy.getMount_sd() + Filecopy.FILE_DIR2 );
	    		return ( Filecopy.getMount_sd() + Filecopy.FILE_DIR2 );
			}else{
		    	// 外部ストレージパス(基本的にSDカードのパスは返ってこない)
//		    	return ( Environment.getExternalStorageDirectory().getPath() + Filecopy.FILE_DIR2 );
				return ( Environment.getExternalStorageDirectory().getPath() );
			}
    	}
*/

    	// Kitkat以上のみの処理
    	if( Build.VERSION.SDK_INT >= 19 ){

    		// 外部ストレージのパスを取得する
    	    File[] extDirs = con.getExternalCacheDirs( );

	    	if( ( MyPrefSetting.getDestination( ) ) >= 0 ){
	    	    // 一番後ろがSDのパス？
	    	    if( extDirs[ MyPrefSetting.getDestination( ) ] != null ){
	    	    	String extSdDirPath = extDirs[ MyPrefSetting.getDestination( ) ].toString();
	    	    	Trace.d( "extSdDirPath = " + extSdDirPath );
	    	    	return ( extSdDirPath );
	    	    }
	    	}

    	    return ( Environment.getExternalStorageDirectory().getPath() );
//    	    return ( Environment.getExternalStorageDirectory().getPath() + Filecopy.FILE_DIR2 );
    	}
    	else{

    		if( MyPrefSetting.getDestination( ) == 1 ){
		    	// SDカードのパス
		    	if( Filecopy.getMount_sd() != null ){
		    		return ( Filecopy.getMount_sd() + Filecopy.FILE_DIR2 );
				}
    		}

		    return ( Environment.getExternalStorageDirectory().getPath() );
    	}
    }

	/**
	 *<p>
	 * 外部ストレージがマウントされているかチェックする
	 *</p>
	 */
    public boolean getStorageState( ) {

	   // SDカードのマウント先が見つからない場合の処理
    	String st = Environment.getExternalStorageState();
    	// SDカードの状態を取得
		return st.equals(Environment.MEDIA_MOUNTED);
    }

	/**
	 *<p>
	 * ファイルをコピー
	 *</p>
	 */
    public void filecopyFromSdcard() {

    	boolean flg;
//    	String txt = null;

		String db_file = getDatabasePath(); // DB
		String sd_dir  = getStoragePath();	// SD dir

		String defStr = "バックアップデータの作成に失敗しました。\n"+
				   		"内部ストレージの容量が足りない可能性があります。\n" +
				   		"容量を確保してからもう一度お試しください。";

		try{

	    	flg = getStorageState();     //SDカードの状態

			if( !flg ) {  //書込み状態でマウントされていない。
//				txt = "アクセスに失敗しました";
//				Toast.makeText( con, txt, Toast.LENGTH_LONG).show();
//				return;         //ディレクトリ作成失敗

			Trace.d( "fasfsfdsfdsdfsf " );
				throw new Exception( );
			}

			// フォルダー作成
			File file = new File( sd_dir + "/" + Filecopy.FILE_DIR );

			flg=file.exists();           // SDカードに指定されたディレクトリがあるかどうか

Trace.d( "fasfsfdsfdsdfsf " + sd_dir + "/" + Filecopy.FILE_DIR );

	        if(!flg) {          		//ディレクトリが存在しないので作成。
				flg=file.mkdirs();    	//　sdcard/testディレクトリを作ってみる。
				if( flg == false ){
//					txt ="フォルダの作成に失敗しました";
//					Toast.makeText( con, txt, Toast.LENGTH_LONG).show();
//					return;         	//ディレクトリ作成失敗

Trace.d( "fasfsfdsfdsdfsf " );

					throw new Exception( );
				}
			}

	        // ファイルコピー
	        String src = db_file;
	        String dist = sd_dir + "/"  + Filecopy.FILE_DIR + "/" + MySQLiteOpenHelper.DB_NAME;
	        fc.filecopy( src , dist, "バックアップに失敗しました", "バックアップに成功しました", 0 );

	    // バックアップに失敗した場合
		}catch( Exception e ){

		Trace.d( "Exception = " + e.toString() );

			// バックアップに失敗した場合は中途半端にバックアップファイルが作られる可能性があるので、
			// バックアップファイルを削除しておく
			File file = new File( sd_dir + "/"  + Filecopy.FILE_DIR + "/" + MySQLiteOpenHelper.DB_NAME );
			//　バックアップファイルがある場合削除する
			if( file.exists() ){
				file.delete();
			}

			// あらーとを表示させる
			// TODO 自動生成されたメソッド・スタ
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( con );
	        alertDialogBuilder.setTitle( "バックアップ失敗" );
	        alertDialogBuilder.setMessage( defStr );
	        alertDialogBuilder.setPositiveButton( "OK",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    }
	                });
	        alertDialogBuilder.setCancelable(true);
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();
		}
    }


	/**
	 *<p>
	 * データーベース作成時にバックアップデータを作成しておく
	 *</p>
	 */
    public boolean makeSdcardSaveFolder() {

		try{
	        // マウント状態にない？
			if( getStorageState() == false ) {
				return false;
			}

			// フォルダー作成
			File file = new File( getStoragePath() + "/" + Filecopy.FILE_DIR );

	        // ディレクトリが存在しない場合ディレクトリを作成する
	        if( file.exists() == false ){
				if( file.mkdirs() == false ) {
					// ディレクトリ作成失敗
					return false;
				}
			}

		}catch( Exception e ){
			Trace.d( "makeSdcardSaveFolder error:" + e.toString() );
		}
    	return true;
    }

}