package jp.co.kaiwaredaikon320.syushi;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class AppSetting
{

	// URL
	public static final String APP_HELP_URL = "http://";

	//////////////////////////////////////////////////////////////////////////////
	// 画面
	//////////////////////////////////////////////////////////////////////////////

	// 画面比率を取得する
	public static final float BASE_DENSITY = 2.0f; // xdpi
	public static float density;
	public static float myDensity;

	// dpの取得
	public static float dp_w;
	public static float dp_h;

	// 720*1280の液晶をベースにレイアウトする
	public static final int CONTENT_WIDTH  = 360; // dp
	public static final int CONTENT_HEIGHT = 567; // dp

	// add_listのレイアウト
	// 基準360*640(567)
	public static final int CONTENT_ADD_LIST_ITEM_1 = 50;
	public static final int CONTENT_ADD_LIST_ITEM_2 = 50;
	public static final int CONTENT_ADD_LIST_ITEM_3 = 100;
	public static final int CONTENT_ADD_LIST_ITEM_4 = 100;
	public static final int CONTENT_ADD_LIST_ITEM_5 = 50;
//	public static final int CONTENT_ADD_LIST_ITEM_6 = 217;
	public static final int CONTENT_ADD_LIST_ITEM_6 = 50;

	// 拡大比率
	public static float content_raito;

	/**
	 * 設定:液晶サイズの比率を計算する
	 */
	public static final void setContentWidthRatio( float dp ) {
		content_raito = ( dp / CONTENT_WIDTH );
	}

	/**
	 * 取得:液晶サイズの比率を取得する
	 */
	public static final float getContentWidthRatio( ) {
		return content_raito;
	}


	/**
	 * 設定:dpの画面比に合わせてDPを返す
	 */
	public static final float calcMyDp( float dp, float raito ) {
		return ( dp * raito );
	}

	public static final int getMypixel( float dp, float raito ) {
		// dp に変換する ( pixel ÷ density + 0.5f（四捨五入) )
		float px = ( dp * raito + 0.5f );
		return (int)px;
	}

	public static final float getMydp( float px, float raito ) {
		// dp に変換する ( pixel ÷ density + 0.5f（四捨五入) )
		float dp = ( px / raito + 0.5f );
		return dp;
	}


	/**
	 * 設定:画面比率を設定する
	 */
	public static final void setDensity( float d ) {
		density = d;
	}

	/**
	 * 取得:画面比率を取得する
	 */
	public static final float getDensity( ) {
		return density;
	}

	/**
	 * 設定:画面比率を設定する(Xhdpiを基準にした比率を取得する)
	 */
	public static final void setMyDensity( float d ) {
		// ベース比率から、対応する比率を計算する
		myDensity = ( d / BASE_DENSITY );
	}
	/**
	 * 取得:画面比率を取得する(Xhdpiを基準にした比率を取得する)
	 */
	public static final float getMyDensity( ) {
		return myDensity;
	}

	/**
	 * 設定:DP W
	 */
	public static final void setDP_W( float w ) {
		dp_w = w;
	}
	/**
	 * 取得:DP w
	 */
	public static final float getDP_W( ) {
		return dp_w;
	}

	/**
	 * 設定:DP H
	 */
	public static final void setDP_H( float h ) {
		dp_h = h;
	}
	/**
	 * 取得:DP H
	 */
	public static final float getDP_H( ) {
		return dp_h;
	}

	/**
	 * @param number　変換したい数値
	 * @return 3桁ごとにコンマを追加した文字列
	 */
	public static String separateComma(long number){
		NumberFormat nf = NumberFormat.getInstance();
		//nf.setGroupingUsed(true);
		return nf.format(number);
	}

    /**
     * 指定の桁で、指定の方法で数字を整形してreturnする
     *
     * @param tmp  処理を受ける数字
     * @param keta  少数第何位を対象にするか
     * @param type  ROUND_HALF_UP→四捨五入　ROUND_DOWN→切り捨て　ROUND_UP→切り上げ
     *
     * @return      結果
     */
    public static double getBigDecimalDouble( double tmp, int keta, int type ){

        BigDecimal bd = new BigDecimal(tmp);
        BigDecimal bd1 = bd.setScale( keta, type);

        return bd1.doubleValue();
    }

    /**
     * 指定の桁で、指定の方法で数字を整形してreturnする
     *
     * @param tmp  処理を受ける数字
     * @param keta  少数第何位を対象にするか
     * @param type  ROUND_HALF_UP→四捨五入　ROUND_DOWN→切り捨て　ROUND_UP→切り上げ
     *
     * @return      結果
     */
    public static int getBigDecimalInt( double tmp, int keta, int type ){

        BigDecimal bd = new BigDecimal(tmp);
        BigDecimal bd1 = bd.setScale( keta, type);

        return bd1.intValue();
    }
}

