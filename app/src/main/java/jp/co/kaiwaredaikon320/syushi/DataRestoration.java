package jp.co.kaiwaredaikon320.syushi;

import java.io.File;
import java.io.FileNotFoundException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;

public class DataRestoration {

    private Context con;
    // ファイルコピー
    private Filecopy fc;
    // DB関連
    private SQLiteDatabase db;

    // コンストラクタ
    public DataRestoration( Context context, Filecopy fcopy, SQLiteDatabase database ){
		// contextの引継ぎ
		con = context;
		// ファイルコピー
		fc = fcopy;
		// データベースの引継ぎ
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
    	}else{

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
	 * SDカードの状態を取得
	 *</p>
	 */
    public boolean getStorageState( ) {

    	String st = Environment.getExternalStorageState();

    	// SDカードの状態を取得
		return st.equals(Environment.MEDIA_MOUNTED);
    }

	/**
	 *<p>
	 * ファイルをコピー
	 *</p>
	 */
    public void filecopyFromLocal( ){

    	boolean flg;

    	String txt = "バックアップファイルが見つかりませんでした。\n"+
   					 "バックアップファイルを確認してもう一度お試しください。";

		String db_file = getDatabasePath(); // DB
		String sd_dir  = getStoragePath();	// SD dir

		String defStr = "収支データの復元に失敗しました。\n"+
		   				"外部ストレージの容量が足りない可能性があります。\n" +
		   				"容量を確保してからもう一度お試しください。";

		try {

	    	flg = getStorageState();     //SDカードの状態

			if( !flg ){  //書込み状態でマウントされていない。
//				txt = "SDカードが見つかりません。";
//				Toast.makeText( con.getApplicationContext(), txt, Toast.LENGTH_LONG).show();
//				return;         //ディレクトリ作成失敗
				defStr = txt;
				throw new Exception( );
			}

			// フォルダー
			File f = new File( sd_dir + "/" + Filecopy.FILE_DIR );
			flg = f.exists( );           // ディレクトリがあるか。

			//ディレクトリが存在しない
	        if( !flg ) {
//				txt ="バックアップが見つかりません。";
//				Toast.makeText( con.getApplicationContext(), txt, Toast.LENGTH_LONG).show();
//				return;         //ディレクトリ作成失敗
	        	defStr = txt;
	        	throw new Exception( );
			}

	        // ファイルコピー
	        String src = sd_dir + "/"  + Filecopy.FILE_DIR + "/" + MySQLiteOpenHelper.DB_NAME;
	        String dist = db_file;

	        fc.filecopy( src , dist, "復元に失敗しました。", "復元に成功しました。", 1  );

		} catch ( FileNotFoundException e ){

			// あらーとを表示させる
			// TODO 自動生成されたメソッド・スタ
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( con );

	        alertDialogBuilder.setTitle( "復元失敗" );
	        alertDialogBuilder.setMessage( txt );
	        alertDialogBuilder.setPositiveButton( "OK",
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                    }
	                });
	        alertDialogBuilder.setCancelable(true);
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();

		} catch (Exception e) {

			// 転送に失敗した場合はSQLデータを削除しておく
			File file = new File( db_file );

			Trace.d( "db_file = " + db_file );
			//　バックアップファイルがある場合削除する
			if( file.exists() ){
				file.delete();

				MyPrefSetting.setDataRestorationFlg( true );
			}

			// あらーとを表示させる
			// TODO 自動生成されたメソッド・スタ
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( con );

	        alertDialogBuilder.setTitle( "復元失敗" );
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
}