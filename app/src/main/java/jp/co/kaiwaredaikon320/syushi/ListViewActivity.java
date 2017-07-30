package jp.co.kaiwaredaikon320.syushi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;


public class ListViewActivity extends Activity {

    // メニュー識別用のID
    private static final int MENU_ADD = Menu.FIRST;
    //	private static final int MENU_DEL = ( Menu.FIRST + 1 );

    private static final int ADD_LIST_ACTIVITY = 3401;

    private SQLiteDatabase syushiDatabase = null;
    private Cursor constantsCursor = null;

    // 日付
    private String calData;
    private String titleStr;

    // 初期の入力かどうか？
    private boolean firstFlg;

    int exInvestment = 0;
    int exRecovery = 0;
    int exTotal;

    // ListView
    private ListView mListView;
    // ListViewに表示するデータ
    private ArrayList<ListViewItem> mArrayList = new ArrayList<ListViewItem>();

/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

        Trace.d( "onCreateOptionsMenu " );

		// 動的にメニューを追加
		// 追加
		MenuItem menuNew = menu.add( Menu.NONE, MENU_ADD, 600, "追加");

		menuNew.setIcon(android.R.drawable.ic_menu_add);
		menuNew.setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
*/
/*
		MenuItem menuDel = menu.add(Menu.NONE, MENU_DEL, 600, "削除");
//		menuDel.setIcon(android.R.drawable.ic_menu_delete);
		menuDel.setIcon(android.R.drawable.ic_menu_preferences);
		menuDel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
*//*


		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		// 追加
		if (id == MENU_ADD ) {
			// 新規登録時のPosは-1を代入しておく
			intentConfigCommonProcess( true, -1 );
			return true;
		}

*/
/*
		// 設定
		if (id == MENU_DEL ) {
			Toast.makeText(this, "設定選択", Toast.LENGTH_SHORT).show();
			return true;
		}
*//*


//        // アクションバーの戻るを押したときの処理
//        if( id == android.R.id.home ){
//            finish( );
//            return true;
//        }

		return super.onOptionsItemSelected( item );
	}
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Trace.d("onCreate Start ");

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        //　選択された日付を代入
        calData = intent.getStringExtra("dddd"); // データベースのサーチワード　例)19830612
        titleStr = intent.getStringExtra("dsds"); //　○年○月○日

        firstFlg = false;

        // タイトル非表示
//        getActionBar().setDisplayShowTitleEnabled(false);
        // アイコンを消す
//        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_HOME);

        // Upアイコンの表示
//        getActionBar().setDisplayHomeAsUpEnabled (true);

        setContentView(R.layout.listviewactivity);

        TextView titleName = (TextView) findViewById(R.id.header_text_list_view);

        titleName.setText(titleStr);

        // レイアウト調整
//		layout();

        // Viewを取得
        mListView = (ListView) this.findViewById(android.R.id.list);

        // コンテキストメニューを表示するViewを登録する
        //Viewに追加する場合、registerForContextMenu(View);が必要
        registerForContextMenu(mListView);

        //　引き継いできた日付をアクションバーに代入
//		setTitle( "" + titleStr );

        // タップで収支変更
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trace.d("setOnItemClickListener pos = " + position);
                firstFlg = false;
                //データの引き継ぎ
                intentConfigCommonProcess(false, position);
            }
        });


        //	項目長押しでデータの削除
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                // アラートダイアログのタイトルを設定します
                alertDialogBuilder.setTitle("削除");
                // アラートダイアログのメッセージを設定します
                alertDialogBuilder.setMessage("データを削除しますか？");
                // アラートダイアログのはいボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
                alertDialogBuilder.setPositiveButton("はい",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Trace.d("setOnItemLongClickListener pos:" + position + " id:" + mArrayList.get(position).getID());

                                // データベースから指定されたIDを削除する
                                syushiDatabase.delete("constants", "_id = " + mArrayList.get(position).getID(), null);
                                //　リストからアラームを削除する
                                mArrayList.remove(position);

                                // リストの再描画
                                mListView.invalidateViews();
                            }
                        });

                // アラートダイアログのいいえがクリックされた時に呼び出されるコールバックリスナーを登録します
                alertDialogBuilder.setNegativeButton("いいえ",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                // アラートダイアログのキャンセルが可能かどうかの設定
                alertDialogBuilder.setCancelable(true);
                AlertDialog alertDialog = alertDialogBuilder.create();
                // アラートダイアログを表示します
                alertDialog.show();

                return true;
            }
        });


/*
		// リストが空の時のビューの設定
		LayoutInflater inflater = getLayoutInflater();
		View emptyView = inflater.inflate(
				R.layout.listviewactivity,
				(ViewGroup) findViewById(android.R.id.empty));

		mListView.setEmptyView(emptyView);
*/

        // ListViewにアダプタを設定
        ListViewAdapter adapter = new ListViewAdapter(
                this,
                R.layout.input_list,    // 1行分のレイアウトファイル
                0,                    // 上記レイアウト内のテキスト表示箇所のId(※未使用)
                mArrayList              // 表示対象のデータ
        );

        mListView.setAdapter(adapter);

        // LitViewを作成
        firstFlg = initListViewItem( calData );

        Trace.d("idArrayList = " + mArrayList);
        //		Trace.d( "" + idArrayList.get( 0 ) );

        // リストの再描画
        mListView.invalidateViews();

        // 収支入力画面に飛ぶ
        if (firstFlg) {
            intentConfigCommonProcess(true, -1);
        }

        Trace.d("onCreate end");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        constantsCursor.close();
        syushiDatabase.close();
    }


    // startActivityForResult で起動させたアクティビティが
    // finish() により破棄されたときにコールされる
    // requestCode : startActivityForResult の第二引数で指定した値が渡される
    // resultCode : 起動先のActivity.setResult の第一引数が渡される
    // Intent data : 起動先Activityから送られてくる Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Trace.d("requestCode = " + requestCode);
        Trace.d("resultCode = " + resultCode);

        // 引継ぎ用
        boolean newFlg = false;

        // requestCodeがサブ画面か確認する
        if (requestCode == ADD_LIST_ACTIVITY) {
            // resultCodeがOKか確認する
            if (resultCode == RESULT_OK) {

//                int exPos = intent.getIntExtra("lvPos", 0);
//                long iid = intent.getLongExtra("search", 0);    // 保存ID;
//
//                // 結果を取得する
//                newFlg = intent.getBooleanExtra("newFlg", false);

                // データベースのdata
                calData = intent.getStringExtra("data");

                // タイトルを変更
                titleStr = intent.getStringExtra( "ymd" );
                TextView titleName = (TextView) findViewById(R.id.header_text_list_view);
                titleName.setText(titleStr);

                //　日付変更のこともあるので、listを作成しなおす
                mArrayList.clear();

                initListViewItem(calData);

                /*
                //　データベースにデータを保存する
                ContentValues cv = new ContentValues();

                //　データベースから値を取得する。
                // IDで収支データを取得する
                constantsCursor = syushiDatabase.rawQuery(
                        "SELECT _id," +
                                MySQLiteOpenHelper.TENPO + "," +
                                MySQLiteOpenHelper.EXCHANGE_BALL + "," +
                                MySQLiteOpenHelper.EXCHANGE_MEDAL + "," +
                                MySQLiteOpenHelper.NUMBER + "," +
                                MySQLiteOpenHelper.KISYU + "," +
                                MySQLiteOpenHelper.TYPE + "," +
                                MySQLiteOpenHelper.EVENT + "," +
                                MySQLiteOpenHelper.INVESTMENT + "," +
                                MySQLiteOpenHelper.INVESTMENT_TYPE + "," +
                                MySQLiteOpenHelper.RECOVERY + "," +
                                MySQLiteOpenHelper.RECOVERY_TYPE + "," +
                                MySQLiteOpenHelper.START_TIME + "," +
                                MySQLiteOpenHelper.END_TIME + "," +
                                MySQLiteOpenHelper.MEMO +

                                " FROM constants" +
                                " WHERE _id like '%" + iid + "%' " +
                                " ORDER BY _id",
                        null);

                //　データを代入する
                if (constantsCursor.moveToFirst()) {
                    exTenpo = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.TENPO));
                    exKisyu = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.KISYU));
                    exInvestment = Integer.parseInt(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT)));
                    exRecovery = Integer.parseInt(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY)));
                    exMemo = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.MEMO));
                    invPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
                    recPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));
                    exchangeBall = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
                    exchangeSlot = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));
                }

                Trace.d("newFlg = " + newFlg);
                Trace.d("exTenpo = " + exTenpo);
                Trace.d("exKisyu = " + exKisyu);
                Trace.d("exPos = " + exPos);
                Trace.d("exMemo = " + exMemo);

                //  収支を計算する
                exTotal = getCalcTotalSyushi(invPtn, recPtn, exchangeBall, exchangeSlot);

                try {
                    //　新規作成時
                    if (newFlg) {
                        //  リストviewにデータを入力したデータを登録
                        mArrayList.add(new ListViewItem(iid, exTenpo, exKisyu, exInvestment, exRecovery, exTotal, exMemo));

                        //　新規フラグのオフ
                        newFlg = false;
                    }
                    // 変更時
                    else {
                        mArrayList.set(exPos, new ListViewItem(mArrayList.get(exPos).getID(), exTenpo, exKisyu, exInvestment, exRecovery, exTotal, exMemo));
                    }

                    // 保存失敗時
                    if (iid < 0) {
                        Trace.d("iid iid iid error");
                        throw new Exception();
                    }

                } catch (Exception e) {
                    DialogFragment newFragment = new AlertDialogFragment(
                            // Title
                            "保存失敗！",
                            // mess
                            "収支データの保存に失敗しました。\n" +
                                    "内部ストレージの容量が不足してます。\n" +
                                    "容量を確保してからもう一度お試しください。");
                    newFragment.show(getFragmentManager(), "showAlertDialog");

                    Trace.d("Exception = " + e.toString());
                }
                */
            }
            // cancel時
            else if (resultCode == RESULT_CANCELED) {
                // 初期入力でデータが保存されなかったときはカレンダー画面に戻る
                if (firstFlg) {
                    finish();
                }
            }
        }

        // リストの再描画
        mListView.invalidateViews();
    }


    /**
     * <p>
     * 収支入力画面へ飛ぶ
     * </p>
     *
     * @param newFlg 新規作成かどうか
     * @param pos    ListViewの何番目の収支データか？
     */
    public void intentConfigCommonProcess(boolean newFlg, int pos) {

        Trace.d("pos:" + pos);

        // インテント作成
        Intent intent = new Intent(getApplicationContext(), AddListActivity.class);

        // 引き継ぐもの
        intent.putExtra("newFlg", newFlg);    //　新規作成かどうか
        intent.putExtra("pos", pos);        // ポジション
        intent.putExtra("ymd", titleStr);     // 日付

        // searchワード
        if (pos >= 0) {
            intent.putExtra("search", mArrayList.get(pos).getID());
        } else {
            intent.putExtra("search", -1);    // searchワード
        }

        // 収支入力画面へ
        startActivityForResult(intent, ADD_LIST_ACTIVITY);
    }

    /**
     * <p>
     * layout調整
     * </p>
     */
    public void layout() {
/*
//		int px;
        int px_x;
        int px_y;

        LinearLayout.LayoutParams ly;
		RelativeLayout.LayoutParams rl;

		// 投資
		{
			ImageView img = (ImageView)findViewById(R.id.listAct_investmentText);
			px_x = AppSetting.getMypixel(
	        		AppSetting.calcMyDp( 48f, AppSetting.getContentWidthRatio() ),
	        		AppSetting.getDensity() );
	        px_y = AppSetting.getMypixel(
	        		AppSetting.calcMyDp( 32f, AppSetting.getContentWidthRatio() ),
	        		AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
	        rl.addRule(RelativeLayout.CENTER_HORIZONTAL );
	        rl.addRule(RelativeLayout.CENTER_VERTICAL );
	        img.setLayoutParams(rl);

	        TextView txt = (TextView)findViewById(R.id.listAct_investmentNumber_cc);
	        txt.setTextSize(
					TypedValue.COMPLEX_UNIT_DIP,
					( AppSetting.calcMyDp( 16, AppSetting.getContentWidthRatio() ) ) );
		}

		// 回収
		{
			ImageView img = (ImageView)findViewById(R.id.listAct_recoveryText);
			px_x = AppSetting.getMypixel(
	        		AppSetting.calcMyDp( 48f, AppSetting.getContentWidthRatio() ),
	        		AppSetting.getDensity() );
	        px_y = AppSetting.getMypixel(
	        		AppSetting.calcMyDp( 32f, AppSetting.getContentWidthRatio() ),
	        		AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
	        rl.addRule(RelativeLayout.CENTER_HORIZONTAL );
	        rl.addRule(RelativeLayout.CENTER_VERTICAL );
	        img.setLayoutParams(rl);

	        TextView txt = (TextView)findViewById(R.id.listAct_recoveryNumber);
	        txt.setTextSize(
					TypedValue.COMPLEX_UNIT_DIP,
					( AppSetting.calcMyDp( 16, AppSetting.getContentWidthRatio() ) ) );
		}

		// 収支
		{
			ImageView img = (ImageView)findViewById(R.id.listAct_totalText);
			px_x = AppSetting.getMypixel(
	        		AppSetting.calcMyDp( 48f, AppSetting.getContentWidthRatio() ),
	        		AppSetting.getDensity() );
	        px_y = AppSetting.getMypixel(
	        		AppSetting.calcMyDp( 32f, AppSetting.getContentWidthRatio() ),
	        		AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
	        rl.addRule(RelativeLayout.CENTER_HORIZONTAL );
	        rl.addRule(RelativeLayout.CENTER_VERTICAL );
	        img.setLayoutParams(rl);

	        TextView txt = (TextView)findViewById(R.id.listAct_totalNumber);
	        txt.setTextSize(
					TypedValue.COMPLEX_UNIT_DIP,
					( AppSetting.calcMyDp( 16, AppSetting.getContentWidthRatio() ) ) );
		}

		// 空白
		{
			Space space = (Space)findViewById(R.id.space_listAct);
	        px_y = AppSetting.getMypixel(
	        		AppSetting.calcMyDp( 1f, AppSetting.getContentWidthRatio() ),
	        		AppSetting.getDensity() );
	        ly = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, px_y );
	        space.setLayoutParams(ly);
		}*/
    }

    /**
     * 前の画面に戻る
     */
    public void onClickLinearHeaderBackAdd(View view) {
        Trace.d("onClickLinearHeaderBackAdd");
        finish();
    }

    /**
     * データの追加
     */
    public void onClickLinearHeaderAdd(View view) {
        Trace.d("onClickLinearHeaderAdd");
        intentConfigCommonProcess(true, -1);
    }


    /**
     * トータル収支の計算
     *
     * @param invPtn       投資
     * @param recPtn       回収
     * @param exchangeBall 投資パターン
     * @param exchangeSlot 回収パターン
     */
    public int getCalcTotalSyushi(int invPtn, int recPtn, double exchangeBall, double exchangeSlot) {

        double calc = 0;
        BigDecimal bd;


        Trace.d("getCalcTotalSyushi");
        Trace.d("invPtn = " + invPtn);
        Trace.d("recPtn = " + recPtn);
        Trace.d("exchangeBall = " + exchangeBall);
        Trace.d("exchangeSlot = " + exchangeSlot);

        // 投資
        if (invPtn != 0) {

            // パチ
            if (invPtn == 1) {
                calc = exchangeBall * exInvestment;
            }

            // スロ
            if (invPtn == 2) {
//                calc = exchangeSlot * exInvestment;
                calc = (exInvestment / exchangeSlot) *100;
            }

            // 切り上げ前
            Trace.d("calc = " + calc);

            exInvestment = AppSetting.getBigDecimalInt(calc,0, BigDecimal.ROUND_UP);
/*
            // ROUND_HALF_UP→四捨五入　ROUND_DOWN→切り捨て　ROUND_UP→切り上げ
            bd = new BigDecimal(calc);
            BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_UP);  //小数第0位

            // 切り上げ後
            Trace.d("calc = " + bd1.doubleValue());

            exInvestment = bd1.intValue();
*/
        }

        // 回収
        if (recPtn != 0) {

            // パチ
            if (recPtn == 1) {
                calc = exchangeBall * exRecovery;
            }

            // スロ
            if (recPtn == 2) {
//                calc = exchangeSlot * exRecovery;
                calc = (exRecovery / exchangeSlot) *100;
            }

            // 切り上げ前
            Trace.d("calc = " + calc);
            exRecovery = AppSetting.getBigDecimalInt(calc,0, BigDecimal.ROUND_UP);
/*

            bd = new BigDecimal(calc);
            BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_UP);  //小数第0位

            // 切り上げ後
            Trace.d("calc = " + bd1.doubleValue());
            exRecovery = bd1.intValue();
*/

        }
        return (exRecovery - exInvestment);
    }

    /**
     * トータル収支の計算
     *
     * @param searchWord データベースの検索ワード

     */
    public boolean initListViewItem(String searchWord ) {

        try {
            // どの項目（列）のデータを検索するか」を指定する：SELECT
            //「どの表から検索するか」を指定する：FROM
            //「どのような条件で行を検索するか」を指定する：WHERE
            // 指定した要素でソート？：ORDER BY
            //
            // dataを　constantsテーブルから取得し、dataの値でソートする？
            // SQLite
            syushiDatabase = (new MySQLiteOpenHelper(getApplicationContext())).getWritableDatabase();

//          ORDER BY → ソート
//          WHERE data like→検索
            constantsCursor = syushiDatabase.rawQuery(
                    "SELECT _id," +
                            MySQLiteOpenHelper.DATA + "," +
                            MySQLiteOpenHelper.TENPO + "," +
                            MySQLiteOpenHelper.EXCHANGE_BALL + "," +
                            MySQLiteOpenHelper.EXCHANGE_MEDAL + "," +
//                            MySQLiteOpenHelper.NUMBER + "," +
                            MySQLiteOpenHelper.KISYU + "," +
//                            MySQLiteOpenHelper.TYPE + "," +
//                            MySQLiteOpenHelper.EVENT + "," +
                            MySQLiteOpenHelper.INVESTMENT + "," +
                            MySQLiteOpenHelper.INVESTMENT_TYPE + "," +
                            MySQLiteOpenHelper.RECOVERY + "," +
                            MySQLiteOpenHelper.RECOVERY_TYPE + "," +
//                            MySQLiteOpenHelper.START_TIME + "," +
//                            MySQLiteOpenHelper.END_TIME + "," +
                            MySQLiteOpenHelper.MEMO +
                            " FROM constants" +
                            " WHERE data like '%" + searchWord + "%'" +
                            " ORDER BY _id",
                    null);

            // データを取得
            // カーソルを一番最初に戻す。
            if (constantsCursor.moveToFirst()) {

                do {

                    //　データベースからデータを取得する
                    long id = constantsCursor.getLong(constantsCursor.getColumnIndex("_id"));
                    String data = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.DATA));
                    String tenpo = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.TENPO));
                    String kisyu = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.KISYU));
                    String memo = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.MEMO));

                    exInvestment = Integer.parseInt(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT)));
                    exRecovery = Integer.parseInt(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY)));

                    int invPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
                    int recPtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));

                    double exchangeBall = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
                    double exchangeSlot = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));

                    // 収支の計算
                    exTotal = getCalcTotalSyushi(invPtn, recPtn, exchangeBall, exchangeSlot);

                    //　リストに追加
                    mArrayList.add(new ListViewItem(id, tenpo, kisyu, exInvestment, exRecovery, exTotal, memo));

                    // 取得したデータをリストに保存しておく
                    //				sqlDataArrayList.add( new ListSQLData( id, recovery, recovery ) );

                    Trace.d(id + ":" + data);
                } while (constantsCursor.moveToNext());
            }
            // リストなし時の処理
            else {
                return true;
            }
            // 容量オーバー時の処理
        } catch (SQLiteDiskIOException e) {
            // アラートダイアログの表示
            DialogFragment newFragment = new AlertDialogFragment();

            newFragment.show(getFragmentManager(), "showAlertDialog");
        }

        return false;
    }
}