package jp.co.kaiwaredaikon320.syushi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MyDialogPreference extends DialogPreference {

	// レイアウト
	LayoutInflater inflater;
	// ファイルコピー
	private Filecopy fc;
	// DB
	private SQLiteDatabase db;

	// バックアップ
	DataBackup backup;
	DataRestoration restoration;

	// 機能を割り当てる
	private FuncID func_id;

	private enum FuncID {
		FUNC_ID_BACKUP,
		FUNC_ID_RESTORATION,
	}

	private String mes;

	/**
	 * XMLより呼び出す際のコンストラクタ
	 * @param context, 2
	 */
	public MyDialogPreference(Context context, AttributeSet attrs) {

		super(context, attrs);

		// Layout
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// ファイルコピー
		fc = new Filecopy(context);

		try {
			// DB関連
			db = new MySQLiteOpenHelper(context).getWritableDatabase();

			func_id = getFuncID();

			// バックアップ処理
			if (func_id == FuncID.FUNC_ID_BACKUP) {
				backup = new DataBackup(context, fc, db);
			}

			// 復元処理
			if (func_id == FuncID.FUNC_ID_RESTORATION) {
				restoration = new DataRestoration(context, fc, db);
			}

			// 容量オーバー時の処理
		} catch (SQLiteDiskIOException e) {
			// アラートダイアログの表示
			//			DialogFragment newFragment = new AlertDialogFragment( );
			//			newFragment.show( get.getFragmentManager(), "showAlertDialog" );
		}
	}

	@Override
	protected View onCreateDialogView() {

		View v = inflater.inflate(R.layout.pref_dialog, null);
		// TextViewに文字を代入
		TextView stringTextView = (TextView) v.findViewById(R.id.string_item);

		// 機能を振り分ける
		// バックアップ処理
		if (func_id == FuncID.FUNC_ID_BACKUP) {

			// 文言を設定する
			mes = "収支データのバックアップファイルを作成します。\n" +
					//    			  "前回のバックアップは上書きされますが、よろしいですか？\n"+
					"バックアップファイルが既に存在する場合は上書きされますがよろしいですか？\n" +
					"\n" +
					"保存先:" + backup.getStoragePath() +
					"/" + Filecopy.FILE_DIR + "/" + MySQLiteOpenHelper.DB_NAME;
		}

		// 復元処理
		if (func_id == FuncID.FUNC_ID_RESTORATION) {

			// 文言を設定する
			mes = "バックアップファイルから収支データの復元を行います。\n" +
					"現在の収支データは上書きされますがよろしいですか？" +
					"\n\n" +
					"読込先:" + restoration.getStoragePath() +
					"/" + Filecopy.FILE_DIR + "/" + MySQLiteOpenHelper.DB_NAME;
		}

		stringTextView.setText(mes);

		return v;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

		if (positiveResult) {

			// バックアップ
			if (func_id == FuncID.FUNC_ID_BACKUP) {
				backup.filecopyFromSdcard();
			}
			// 復元
			if (func_id == FuncID.FUNC_ID_RESTORATION) {
				restoration.filecopyFromLocal();
			}
		}

		// DBを閉じておく
		if (db != null) {
			db.close();
			db = null;
			System.gc();
		}

		super.onDialogClosed(positiveResult);
	}

	/*
		// 機能別にＩＤを設定する
		private int getFuncID( FuncID id ){
			int ret = 0;

			switch( id ){
				// バックアップの場合
				case FUNC_ID_BACKUP:
					ret = 0;
					break;
				// 復元の場合
				case FUNC_ID_RESTORATION:
					ret = 1;
					break;
			}
			return ret;
		}
	*/

	// 機能別にＩＤを設定する
	private FuncID getFuncID() {

		// バックアップ処理
		if (getKey().equals("pref_key_backup")) {
			return (FuncID.FUNC_ID_BACKUP);
		}

		// 復元処理
		if (getKey().equals("pref_key_restoration")) {
			return (FuncID.FUNC_ID_RESTORATION);
		}

		return null;
	}

}
