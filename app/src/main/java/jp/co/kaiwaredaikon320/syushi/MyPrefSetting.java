package jp.co.kaiwaredaikon320.syushi;

public class MyPrefSetting {

	// 定数
	///////////////////////////

	// 入力の最大値
	public static final int INPUTMAX = 999999;

	// 変数
	///////////////////////////

	// シークバー　保存key
	public static String[] key = {
		"pref_save_key_tani",
		"pref_save_key_seekbar",
		"pref_save_key_seekbar_max",
		"pref_key_destination"
	};

	// シークバー　初期値
	public static String[] defaultVals = {
		"1000",		// 1千
		"1000",		// 1千
		"50000",	// 5万
	};

	// シークバー
	public static int tani;
	public static int seekbar;
	public static int seekbar_max;

	// データバックアップ
	public static boolean restoration;

	public static int mDestination;


	// 関数
	//////////////////////////

	/**
	 * 設定：＋－単位
	 */
	public static final void setRate( int atai ) {
		tani = atai;
	}
	/**
	 * 設定：シークバーの単位
	 */
	public static final void setSeekBarRate( int atai ) {
		seekbar = atai;
	}
	/**
	 * 設定：シークバーの最大値
	 */
	public static final void setSeekBarMax( int atai ) {
		seekbar_max = atai;
	}
	/**
	 * 取得：＋－単位
	 */
	public static final int getRate( ) {
		return tani;
	}
	/**
	 * 取得：シークバーの単位
	 */
	public static final int getSeekBarRate( ) {
		return seekbar;
	}
	/**
	 * 取得：シークバーの最大値
	 */
	public static final int getSeekBarMax( ) {
		return seekbar_max;
	}
	/**
	 * 設定:データ復元時に、カレンダーを更新するためのフラグ
	 */
	public static final void setDataRestorationFlg( boolean flg ) {
		restoration = flg;
	}
	/**
	 * 取得:データ復元時に、カレンダーを更新するためのフラグ
	 */
	public static final boolean getDataRestorationFlg( ) {
		return restoration;
	}


	/**
	 * 設定：保存先
	 */
	public static final void setDestination( int destination ) {
		mDestination = destination;

		Trace.d( "mDestination = "+ mDestination );
	}

	/**
	 * 取得:保存先
	 */
	public static final int getDestination( ) {
		Trace.d( "mDestination = "+ mDestination );
		return mDestination;
	}
}

