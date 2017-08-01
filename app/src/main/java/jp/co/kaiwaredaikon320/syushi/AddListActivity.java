package jp.co.kaiwaredaikon320.syushi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;

import static jp.co.kaiwaredaikon320.syushi.MySQLiteOpenHelper.DB_TBL_KISYU;
import static jp.co.kaiwaredaikon320.syushi.MySQLiteOpenHelper.DB_TBL_TENPO;


public class AddListActivity extends Activity implements TextWatcher{

    // 定数
    private final int TYPE_NONE     = 0;    //  未設定
    private final int TYPE_PACHINKO = 1;    //  パチンコ
    private final int TYPE_SLOT     = 2;    //  スロット

    // メニュー識別用のID
    private static final int MENU_ADD = Menu.FIRST;

    // ソフトキーボード用
    InputMethodManager imm;

    // 画面サイズ用
    private int windowWidth;
    private int windowHeight;

    // 引継ぎデータ系
    private static boolean exNewFlg;        //  新規作成フラグ
    private static Long exSearch;           //  データベスID
    private static String exToday;          //  データの日付
    private static int exPos;               //  ポジション(ListViewItem)

    // 日付
    private Button buttonTodayId;           //  ID
    private int currentYear;                //  年
    private int currentMonth;               //  月
    private int currentDay;                 //  日

    // 店舗
    private Button buttonTenpoId;           //  ID
    private static double exchangeBall;     // 玉
    private static double exchangeSlot;     // スロット

    // 台番
    private Button buttonNumber;           //  ID

    //  交換率
    private TextView textViewExchangeId;   //  ID


    // 機種名
    private Button buttonKisyuId;          //  ID
    private static int pachiType;          // パチかスロか(0⇒未設定 1⇒パチ、2⇒スロ、？)

    //  イベント
    private Button buttonEventId;          //    ID

    // 投資
    private Button buttonInvestmentId;      //  収支入力用ID
    private TextView textViewInvestmentId;  //  投資描画用ID
    private int investmentExchangePtn;      //  投資タイプ
    private int objInvestment;              //  投資計算後の値？？

    // 回収
    private Button buttonRecoveryId;
    private TextView textViewRecoveryId;      //
    private int recoveryExchangePtn;        //
    private int objRecovery;

    // 総投資
    private TextView textViewTotalId;

    // 時給関連
    private TextView textViewTimeId;    // 稼働時間
    private TextView textViewJikyuId;   // 時給

    private double calcStartTime;     // スタート時間
    private double calcEndTime;       // 終了タイム
//    private double operationTime;     // 稼働時間

    // メモ
    private EditText editTextMemoId;

    // 入力が変更フラグ
    private boolean dateChangeFlg;   // 日付用
    private boolean inputChangeFlg;  // 日付とメモ以外全般用
    private String  memoBak;        // メモ用

    // 店舗名表示用のダイアログ
    ListViewDialog lvd;

    //  SQL関連
    private SQLiteDatabase syushiDatabase = null;
    private Cursor constantsCursor = null;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient mClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int tmpPachiType = TYPE_NONE;

        // レイアウトSet
        setContentView(R.layout.add_list_activity);

        // ソフトキーボードを表示させないようにするよう。
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        windowWidth = p.x;
        windowHeight = p.y;

        //  titleをセット
//      setTitle("収支入力");

        // インテントを取得
        Intent intent = getIntent();

/*
        // レイアウト構築する
        layout();

        // プリファレンスの情報を取得する
        pref = PreferenceManager.getDefaultSharedPreferences(this);
*/

        //　引継ぎはこれだけでOk後はデータベースから読み込む
        exNewFlg = intent.getBooleanExtra("newFlg", true);  //  新規入力フラグ
        exSearch = intent.getLongExtra("search", -1 );      // searchID
        exToday = intent.getStringExtra("ymd");            // 日付

        Trace.d( "newFlg = " + exNewFlg);
        Trace.d( "exSearch = " + exSearch);
        Trace.d( "exToday = " + exToday);

        // ポジションが必要ないようなら消す
        exPos = intent.getIntExtra("pos", 0);              //  ポジション
        Trace.d("exPos = " + exPos);

 //  初期化_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/

    // 日付
        buttonTodayId = (Button) findViewById(R.id.add_btn_today_01);
        // 引き継いできた日付を代入する
        buttonTodayId.setText(exToday);

        // DatePickerDialog用に年、月、日を別々に保存しておく
        currentYear  = Integer.parseInt( exToday.substring( 0,exToday.indexOf("年") ) );
        currentMonth = Integer.parseInt(exToday.substring( exToday.indexOf("年")+1,exToday.indexOf("月") ))-1;
        currentDay   = Integer.parseInt(exToday.substring( exToday.indexOf("月")+1,exToday.indexOf("日") ));

        Trace.d( "year:"+currentYear );
        Trace.d( "month:"+currentMonth );
        Trace.d( "day:"+currentDay );

        //  店舗
        buttonTenpoId = (Button) findViewById(R.id.add_btn_tenpo);

        // 交換率
        exchangeBall = 4.00;     // 玉
        exchangeSlot = 5.0;    // スロット

        // 台番
        buttonNumber = (Button) findViewById(R.id.add_edit_number_01);

        //交換率描画用
        textViewExchangeId = (TextView) findViewById(R.id.add_text_exchange);

        //  機種
        buttonKisyuId = (Button) findViewById(R.id.add_btn_kisyu);
        // 機種タイプ
        pachiType = TYPE_NONE;

        // イベント
        buttonEventId = (Button) findViewById(R.id.add_btn_event);

        // 投資
        buttonInvestmentId = (Button)findViewById( R.id.add_btn_investment );
        textViewInvestmentId = (TextView) findViewById( R.id.add_text_view_investment_01 );

        // 初期値代入
        buttonInvestmentId.setText("0", BufferType.NORMAL);
        investmentExchangePtn = 0;

        //  回収
        buttonRecoveryId = (Button)findViewById( R.id.add_btn_recovery );
        textViewRecoveryId = (TextView) findViewById( R.id.add_text_view_recovery_01 );

        // 初期値代入
        buttonRecoveryId.setText("0", BufferType.NORMAL);
        recoveryExchangePtn = 0;

        //  収支
        textViewTotalId = (TextView)findViewById( R.id.add_text_view_total_02 );

        // 時間関連
        textViewTimeId = (TextView)findViewById( R.id.add_text_view_jikyu_02 );   //　時間
        textViewJikyuId = (TextView)findViewById( R.id.add_text_view_jikyu_03 );  //　時給

        calcStartTime = 0;  //  開始時間
        calcEndTime   = 0;  //  終了時間

        // メモ
        editTextMemoId = (EditText) findViewById(R.id.add_edit_memo);

//  初期化_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/


        // DBを開く
        syushiDatabase = ( new MySQLiteOpenHelper( getApplicationContext() ) ).getWritableDatabase( );

        // 変更の場合はデータベースから値を代入する
        if (!exNewFlg ) {

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
                            " WHERE _id like '%" + exSearch + "%' " +
                            " ORDER BY _id",
                    null);

            // データの
            if (constantsCursor.moveToFirst()) {

                // 店舗
                buttonTenpoId.setText(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.TENPO)), BufferType.NORMAL);
                // 交換率(玉)
                exchangeBall = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL));
                // 交換率(スロ)
                exchangeSlot = constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL));

                Trace.d( "exchangeBall = " + exchangeBall );
                Trace.d( "exchangeSlot = " + exchangeSlot );

                // 台番
                buttonNumber.setText(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.NUMBER)), BufferType.NORMAL);

                // 機種名
                buttonKisyuId.setText(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.KISYU)), BufferType.NORMAL);
                // 機種タイプ
                tmpPachiType = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.TYPE));
                Trace.d( "patiType = " + tmpPachiType );

                // イベント
                buttonEventId.setText(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EVENT)), BufferType.NORMAL);

                // 投資
                buttonInvestmentId.setText(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT)), BufferType.NORMAL);
                investmentExchangePtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.INVESTMENT_TYPE));
                Trace.d( "investmentExchangePtn = " + investmentExchangePtn );

                // 回収
                buttonRecoveryId.setText(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY)), BufferType.NORMAL);
                recoveryExchangePtn = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.RECOVERY_TYPE));
                Trace.d( "recoveryExchangePtn = " + recoveryExchangePtn );

                // 時事時間
                calcStartTime = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.START_TIME));
                calcEndTime = constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.END_TIME));

                Trace.d( "calcStartTime = " + calcStartTime );
                Trace.d( "calcEndTime = " + calcEndTime  );

                String tmpStart = ""+
                        ( (calcStartTime/60)<10?"0"+(int)(calcStartTime/60):(int)(calcStartTime/60) )
                        + ":"+
                        ( (calcStartTime%60)<10?"0"+(int)(calcStartTime%60):(int)(calcStartTime%60) );

                Trace.d( "Start = " + tmpStart );

                // 開始時間を代入
                Button buttonTime = (Button)findViewById( R.id.add_btn_jikyu_01 );
                buttonTime.setText(tmpStart);

                tmpStart = ""+
                        ( (calcEndTime/60)<10?"0"+(int)(calcEndTime/60):(int)(calcEndTime/60) )
                        + ":"+
                        ( (calcEndTime%60)<10?"0"+(int)(calcEndTime%60):(int)(calcEndTime%60) );

                Trace.d( "Start = " + tmpStart );

                // 終了時間を代入
                buttonTime = (Button)findViewById( R.id.add_btn_jikyu_02 );
                buttonTime.setText(tmpStart);

                // 稼働時間を代入
                // 稼働時間の計算
                double hoge = (calcEndTime - calcStartTime) / 60;

                // 何も入っていない時は０ｈを代入しておく。
                if( calcStartTime != 0 && calcEndTime != 0 && hoge >= 0 ){
                    // 小数点第２で切り捨てる
                    hoge = AppSetting.getBigDecimalDouble( hoge, 2, BigDecimal.ROUND_DOWN );
                    textViewTimeId.setText(hoge + "h");
                    changeTotalTextColor( );
                }
                else{
                    textViewTimeId.setText("");
                }

                // メモ
                editTextMemoId.setText(constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.MEMO)), BufferType.NORMAL);
            }
        }

        // 交換率の反映
        textViewExchangeId.setText(exchangeBall+"円/"+exchangeSlot+"枚");

        // ボタンのテキストを変更する
        investmentExchangePtn --;
        onClickYenPatiSlot( findViewById(R.id.add_investment_btn_01) );

        recoveryExchangePtn --;
        onClickYenPatiSlot( findViewById(R.id.add_recovery_btn_01) );

        //  トータル収支を描画する
        changeTotalTextColor( );

        // 代入
        pachiType = tmpPachiType;

        // データ変更フラグ
        dateChangeFlg  = false;
        inputChangeFlg = false;

        // メモのバックアップ
        memoBak = editTextMemoId.getText().toString();

        //  リスナー登録( 収支入力監視用 )
//      buttonInvestmentId.addTextChangedListener(this);    //投資
//      buttonRecoveryId.addTextChangedListener(this);      //回収
//      editTextMemoId.addTextChangedListener(this);        //メモ

        // ListViewDialogの準備
        lvd = new ListViewDialog(this);

        try {

/*
            // 店舗名を取得する
            constantsCursor = syushiDatabase.rawQuery(
                    "SELECT DISTINCT tenpo " +
                            "FROM constants " +
                            "ORDER BY tenpo",
                    null
            );
*/
            // 店舗名を取得する
            constantsCursor = syushiDatabase.rawQuery(
                    "SELECT " +
                            MySQLiteOpenHelper.TENPO + "," +
                            MySQLiteOpenHelper.EXCHANGE_BALL + "," +
                            MySQLiteOpenHelper.EXCHANGE_MEDAL +

                            " FROM " + DB_TBL_TENPO+
                            " ORDER BY tenpo",
                    null
            );


            // データを取得
            // カーソルを一番最初に戻す。
            if (constantsCursor.moveToFirst()) {
                do {
                    //　データベースからデータを取得する
                    String tenpo = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.TENPO));

                    // item登録
                    if (!tenpo.equals("店舗未登録")) {
                        lvd.setItemTenpo( tenpo,
                                constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_BALL)),
                                constantsCursor.getDouble(constantsCursor.getColumnIndex(MySQLiteOpenHelper.EXCHANGE_MEDAL)));
                    }
                } while (constantsCursor.moveToNext());
            }


/*
            // 機種名
            constantsCursor = syushiDatabase.rawQuery(
                    "SELECT DISTINCT kisyu " +
                            "FROM constants " +
                            "ORDER BY kisyu",
                    null
            );
*/

            // 機種名
            constantsCursor = syushiDatabase.rawQuery(
                    "SELECT " +
                            MySQLiteOpenHelper.KISYU + "," +
                            MySQLiteOpenHelper.TYPE +
                            " FROM " + DB_TBL_KISYU+
                            " ORDER BY " + MySQLiteOpenHelper.KISYU,
                    null
            );

            // データを取得
            // カーソルを一番最初に戻す。
            if (constantsCursor.moveToFirst()) {
                do {
                    //　データベースからデータを取得する
                    String kisyu = constantsCursor.getString(constantsCursor.getColumnIndex(MySQLiteOpenHelper.KISYU));

                    // item登録
                    if (!kisyu.equals("機種未登録")) {
                        lvd.setItemKisyu(kisyu,constantsCursor.getInt(constantsCursor.getColumnIndex(MySQLiteOpenHelper.TYPE)));
                    }
                } while (constantsCursor.moveToNext());
            }

            // イベント
            constantsCursor = syushiDatabase.rawQuery(
                    "SELECT DISTINCT event " +
                            "FROM constants " +
                            "ORDER BY event",
                    null
            );

            // データを取得
            // カーソルを一番最初に戻す。
            if (constantsCursor.moveToFirst()) {
                do {
                    //　データベースからデータを取得する
                    String event = constantsCursor.getString(constantsCursor.getColumnIndex("event"));
                    // item登録
                    if (!event.equals("なし")) {
                        lvd.setItem(2, event);
                    }
                } while (constantsCursor.moveToNext());
            }


            // 容量オーバー時の処理
        } catch (SQLiteDiskIOException e) {
            // アラートダイアログの表示
            DialogFragment newFragment = new AlertDialogFragment();
            DialogFragment newFragment2 = new AlertDialogFragment( "ffffff", "ffffff" );

            newFragment.show(getFragmentManager(), "showAlertDialog");
        } catch (Exception e) {
            Trace.e( "SQLiteエラー "+e.toString());
        }

        // 後処理→onDestroyに移動
//        syushiDatabase.close();
//        constantsCursor.close();
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // 動的にメニューを追加
        // 追加
        MenuItem menuNew = menu.add(Menu.NONE, MENU_ADD, 600, "保存");

        menuNew.setIcon(android.R.drawable.ic_menu_save);
        menuNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

*/
/*
        MenuItem menuDel = menu.add(Menu.NONE, MENU_DEL, 600, "削除");
		menuDel.setIcon(android.R.drawable.ic_menu_delete);
		menuDel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
*//*

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // 保存選択
        if (id == MENU_ADD) {
            // 収支が入力されている場合は前の画面に戻る
            if ( endAddListActivity() ) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
            }
            // 収支が入力されていない場合はtoastを表示させる
            else {
                Toast.makeText(this, "収支が入力されていません。", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

*//*

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // 保存選択
        if (id == MENU_ADD) {
            // 収支が入力されている場合は前の画面に戻る
            if ( endAddListActivity() ) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
            }
            // 収支が入力されていない場合はtoastを表示させる
            else {
                Toast.makeText(this, "収支が入力されていません。", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

/*      // 未使用
        // アクションバーの戻るを押したときの処理
        if (id == android.R.id.home) {
            endReturnListActivity();
            return true;
        }
*//*


        return super.onOptionsItemSelected(item);
    }
*/



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            switch (event.getKeyCode()) {
                // backキー
                case KeyEvent.KEYCODE_BACK:
                    endReturnListActivity();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 収支保存時の終了の処理
     */
    private boolean endAddListActivity() {

        String saveTenpo = buttonTenpoId.getText().toString();
        String saveKisyu = buttonKisyuId.getText().toString();
        String saveEvent = buttonEventId.getText().toString();
        String saveMemo = editTextMemoId.getText().toString();

        int saveNumber;
        int saveInv;
        int saveRec;

        // 台番
        try {
            saveNumber = Integer.parseInt(buttonNumber.getText().toString());
        } catch (Exception e) {
            saveNumber = 0;
        }

        // 投資
        try {
            saveInv = Integer.parseInt(buttonInvestmentId.getText().toString());
        } catch (Exception e) {
            saveInv = 0;
        }

        // 回収
        try {
            saveRec = Integer.parseInt(buttonRecoveryId.getText().toString());
        } catch (Exception e) {
            saveRec = 0;
            buttonRecoveryId.setText("" + saveRec);
        }

        // 投資と回収が何も保存されていない場合は収支が入力されていない
        if ( saveInv == 0 && saveRec == 0 && exNewFlg ) {
            return (false);
        }

        Trace.d("endAddListActivity 店舗 = " + saveTenpo);
        Trace.d("saveKisyu = " + saveKisyu);
        Trace.d("saveNumber = " + saveNumber);
        Trace.d("saveRec = " + saveRec);
        Trace.d("saveMemo = " + saveMemo);

        // 空白時は店舗未登録を代入する
        if (buttonTenpoId.getText().toString().equals("")) {
            saveTenpo = "店舗未登録";
        }

        // 機種　空白時は機種未登録を代入する
        if (buttonKisyuId.getText().toString().equals("")) {
            saveKisyu = "機種未登録";
        }

        // イベント　空白時はなしを代入する
        if (buttonEventId.getText().toString().equals("")) {
            saveEvent = "なし";
        }

        //
        ContentValues cv = new ContentValues();

        // 保存データ
        cv.put( MySQLiteOpenHelper.DATA, toTwoDigits(currentYear)+toTwoDigits(currentMonth+1)+toTwoDigits(currentDay) );     // 日付
        cv.put( MySQLiteOpenHelper.TENPO, saveTenpo );  // 店舗
        cv.put( MySQLiteOpenHelper.EXCHANGE_BALL, exchangeBall );  // 交換率(玉)
        cv.put( MySQLiteOpenHelper.EXCHANGE_MEDAL, exchangeSlot ); // 交換率(メダル)
        cv.put( MySQLiteOpenHelper.NUMBER, saveNumber );           // 台番
        cv.put( MySQLiteOpenHelper.KISYU, saveKisyu );             // 機種
        cv.put( MySQLiteOpenHelper.TYPE, pachiType );              // 機種タイプ
        cv.put( MySQLiteOpenHelper.EVENT, saveEvent );             // イベント
        cv.put( MySQLiteOpenHelper.INVESTMENT, saveInv );          // 投資
        cv.put( MySQLiteOpenHelper.INVESTMENT_TYPE, investmentExchangePtn );      // 投資パターン
        cv.put( MySQLiteOpenHelper.RECOVERY, saveRec );                           // 回収
        cv.put( MySQLiteOpenHelper.RECOVERY_TYPE, recoveryExchangePtn );          // 回収パターン
        cv.put( MySQLiteOpenHelper.START_TIME, calcStartTime );         // 開始時間
        cv.put( MySQLiteOpenHelper.END_TIME, calcEndTime );             // 終了時間
        cv.put( MySQLiteOpenHelper.MEMO, saveMemo );             // メモ


        //　インテントデータ引継ぎ用
        Intent intent = new Intent();

        // 新規時
        if( exNewFlg ){
            exSearch = syushiDatabase.insert( "constants", null, cv );
        // 更新時
        }else{
            syushiDatabase.update( "constants", cv, "_id = "+exSearch, null );
        }


        cv.clear();

        // 指定されている店舗がデータベースに存在するか調べる
        constantsCursor = syushiDatabase.rawQuery(
                "SELECT " +
                        MySQLiteOpenHelper.TENPO + "," +
                        MySQLiteOpenHelper.EXCHANGE_BALL + "," +
                        MySQLiteOpenHelper.EXCHANGE_MEDAL +

                        " FROM " + DB_TBL_TENPO+
                        " WHERE " + MySQLiteOpenHelper.TENPO + " like '%" + saveTenpo + "%' ",
                null
        );

        // データがあるとき
        if( constantsCursor.moveToFirst( ) ){
            Trace.d("既存の店舗データがあるので保存しない");
        }
        // データがない場合はデータを保存する
        else{
            cv.put( MySQLiteOpenHelper.TENPO, saveTenpo );  // 店舗
            cv.put( MySQLiteOpenHelper.EXCHANGE_BALL, exchangeBall );  // 交換率(玉)
            cv.put( MySQLiteOpenHelper.EXCHANGE_MEDAL, exchangeSlot ); // 交換率(メダル)

            syushiDatabase.insert( MySQLiteOpenHelper.DB_TBL_TENPO, null, cv );
            Trace.d("店舗データの登録");
        }


        cv.clear();

        // 指定されている機種がデータベースに存在するか調べる
        constantsCursor = syushiDatabase.rawQuery(
                "SELECT " +
                        MySQLiteOpenHelper.KISYU + "," +
                        MySQLiteOpenHelper.TYPE +

                        " FROM " + DB_TBL_KISYU+
                        " WHERE " + MySQLiteOpenHelper.KISYU + " like '%" + saveKisyu + "%' ",
                null
        );

        // データがあるとき
        if( constantsCursor.moveToFirst( ) ){
            Trace.d("既存の機種データがあるので保存しない");
        }
        // データがない場合はデータを保存する
        else{
            cv.put( MySQLiteOpenHelper.KISYU, saveKisyu );  // 機種
            cv.put( MySQLiteOpenHelper.TYPE, pachiType );   // タイプ

            syushiDatabase.insert( DB_TBL_KISYU, null, cv );
            Trace.d("機種データの登録");
        }

        // 引継ぎ用
//        intent.putExtra("newFlg", exNewFlg);   //　新規作成かどうか
//        intent.putExtra("lvPos", exPos);        // ListViewのポス
        intent.putExtra("ymd", exToday );   // 日付(○年○月○日)
        intent.putExtra("data", toTwoDigits(currentYear)+toTwoDigits(currentMonth+1)+toTwoDigits(currentDay));

        // 結果を設定
        setResult(RESULT_OK, intent);

        return true;
    }

    /**
     * 戻るタップ時の保存確認処理
     */
    private void endReturnListActivity() {

        // メモ処理のみ別処理(変更がTextViewなので)
        if (!editTextMemoId.getText().toString().equals(memoBak)) {
            inputChangeFlg = true;
        }

        Trace.d( "endReturnListActivity dateChangeFlg = " + dateChangeFlg );
        Trace.d( "endReturnListActivity inputChangeFlg = " + inputChangeFlg );

        // 収支が変更されていない場合は終了
        if( !dateChangeFlg && !inputChangeFlg ) {
            finish();
        }
        // 収支が保存されていない場合は警告を出す。
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("確認");
            alertDialogBuilder.setMessage("収支が変更されています。\n収支を保存しますか？");

            alertDialogBuilder.setPositiveButton("はい",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            endAddListActivity();
                            finish();
                        }
                    });

            alertDialogBuilder.setNegativeButton("いいえ",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

            alertDialogBuilder.setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
/*
        int text1 = 0;
        int text2 = 0;
        int total = 0;

        //　入力された値で収支を計算する
        SpannableStringBuilder sb = (SpannableStringBuilder) buttonInvestment.getText();

        try {
            text1 = Integer.parseInt(sb.toString());
        } catch (Exception e) {
            text1 = 0;
        }

        sb = (SpannableStringBuilder) buttonRecoveryId.getText();
        try {
            text2 = Integer.parseInt(sb.toString());
        } catch (Exception e) {
            text2 = 0;
        }

        total = (text2 - text1);

        //　textViewに収支を代入する
        textViewTotalId.setText("" + total, BufferType.NORMAL);

        //　テキストの色を変更する
        changeTotalTextColor(textViewTotalId, total);
*/
        Trace.d( "afterTextChangedafterTextChangedafterTextChangedafterTextChanged" );
    }

    /**
	 * 収支によって、viewの色を変更するメソッド
	 */
    public void changeTotalTextColor( ) {
        // 投資
        int inv = Integer.parseInt( buttonInvestmentId.getText().toString());
        // 回収
        int rec = Integer.parseInt( buttonRecoveryId.getText().toString());

        //
        setBallMedalTotal();


        // 玉、枚が選ばれている場合は玉と枚数で計算する
        // 投資
        if( investmentExchangePtn != 0 ){
            inv = objInvestment;
        }
        // 回収
        if( recoveryExchangePtn != 0 ){
            rec = objRecovery;
        }

        // 収支計算
        int total = rec-inv;

        textViewTotalId.setText( ""+total);

        // ±0
        if (total == 0) {
            textViewTotalId.setTextColor(Color.BLACK);
        }
        // ＋収支
        else if (total > 0) {
            textViewTotalId.setTextColor(Color.BLUE);
        }
        // －収支
        else {
            textViewTotalId.setTextColor(Color.RED);
        }


        double tmp = (calcEndTime - calcStartTime) / 60;

        // 時間が記入されている場合は時給の計算を行う
        if( calcStartTime != 0 && calcEndTime != 0 && tmp >= 0 ){

            tmp = total / tmp;
            Trace.d( "tmp = " + tmp );
            // 四捨五入
            tmp = AppSetting.getBigDecimalDouble( tmp, 2, BigDecimal.ROUND_HALF_UP );
            Trace.d( "tmp = " + tmp );

            textViewJikyuId.setText(tmp+"円");
        }

    }

    /**
     * 投資、回収時に玉orメダルを指定した時のto-tal額をSetする
     */
    public void setBallMedalTotal( ) {

        double calc = 0;
//        BigDecimal bd;
        String unit = "玉";

        // 投資
        if( investmentExchangePtn != 0 ){

            // パチ
            if(investmentExchangePtn==1){
                calc = exchangeBall * Integer.parseInt( buttonInvestmentId.getText().toString());
                unit = "円("+exchangeBall + "円)";
            }

            // スロ
            if(investmentExchangePtn==2){
//                calc = exchangeSlot * Integer.parseInt( buttonInvestmentId.getText().toString());
                calc = (Integer.parseInt( buttonInvestmentId.getText().toString()) / exchangeSlot) *100;

                unit = "円("+exchangeSlot + "枚)";
            }

            // 切り上げ前
            Trace.d( "calc = " + calc );
/*
            // ROUND_HALF_UP→四捨五入　ROUND_DOWN→切り捨て　ROUND_UP→切り上げ
            bd = new BigDecimal(calc);
            BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_UP);  //小数第0位

            // 切り上げ後
            Trace.d( "calc = " + bd1.doubleValue() );
*/
            objInvestment = AppSetting.getBigDecimalInt(calc,0,BigDecimal.ROUND_UP);

            // セット！
            textViewInvestmentId.setText(objInvestment+unit);
        }
        // 初期化
        else{
            textViewInvestmentId.setText("");
        }

        // 回収
        if( recoveryExchangePtn != 0 ){

            // パチ
            if(recoveryExchangePtn==1){
                calc = exchangeBall * Integer.parseInt( buttonRecoveryId.getText().toString());
                unit = "円("+exchangeBall + "玉)";
            }

            // スロ
            if(recoveryExchangePtn==2){
//                calc = exchangeSlot * Integer.parseInt( buttonRecoveryId.getText().toString());
                calc = (Integer.parseInt( buttonRecoveryId.getText().toString()) / exchangeSlot) *100;
                unit = "円("+exchangeSlot + "枚)";
            }

            // 切り上げ前
            Trace.d( "calc = " + calc );

//            bd = new BigDecimal(calc);
//            BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_UP);  //小数第0位
//
//            // 切り上げ後
//            Trace.d( "calc = " + bd1.doubleValue() );
//
            objRecovery = AppSetting.getBigDecimalInt(calc,0,BigDecimal.ROUND_UP);;

            // セット
            textViewRecoveryId.setText( objRecovery+unit);

        // 初期化
        }else{
            textViewRecoveryId.setText("");
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // クローズ
        constantsCursor.close();
        syushiDatabase.close();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Trace.d("dispatchTouchEventdispatchTouchEventdispatchTouchEvent");
/*
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            // 長押し処理は全てここで解除する
            if (longClickInvestment_minus.getContinueFlg())
                longClickInvestment_minus.setContinueFlg(false);
            if (longClickInvestment_plus.getContinueFlg())
                longClickInvestment_plus.setContinueFlg(false);
            if (longClickRecovery_minus.getContinueFlg())
                longClickRecovery_minus.setContinueFlg(false);
            if (longClickRecovery_plus.getContinueFlg())
                longClickRecovery_plus.setContinueFlg(false);
        }
*/
        return super.dispatchTouchEvent(ev);
    }

/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Trace.d( "onTouchEventonTouchEventonTouchEventonTouchEventonTouchEvent" );
		return false;
	}
*/

    public void layout() {
/*
        // dpを20分割してレイアウトを構築する
        float dp = (int) (AppSetting.getDP_W() / 20);
        int px;
        int px_x;
        int px_y;

        // ウィジェットを取得する
        LinearLayout.LayoutParams layout;
        LinearLayout layout01 = (LinearLayout) findViewById(R.id.add_Layout_tenpo);
        LinearLayout layout02 = (LinearLayout) findViewById(R.id.add_Layout_kisyu);
        LinearLayout layout03 = (LinearLayout) findViewById(R.id.layout_investment);
        LinearLayout layout04 = (LinearLayout) findViewById(R.id.layout_recovery);
        LinearLayout layout05 = (LinearLayout) findViewById(R.id.add_Layout_total_number);
        LinearLayout layout06 = (LinearLayout) findViewById(R.id.add_Layout_memo);

        // layout01
        {
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(AppSetting.CONTENT_ADD_LIST_ITEM_1, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px_y);
            layout01.setLayoutParams(layout);
        }

        {
            Space ly01_01 = (Space) findViewById(R.id.space_tenpo_01);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 0.5f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly01_01.setLayoutParams(layout);
        }

        {
            ImageView ly01_02 = (ImageView) findViewById(R.id.imageview_tenpo);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 3.5f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
//	        layout = new LinearLayout.LayoutParams( px_x, LinearLayout.LayoutParams.WRAP_CONTENT );
            ly01_02.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly01_02.setPadding(px, px, px, px);
        }

        {
            EditText ly01_03 = (EditText) findViewById(R.id.add_edit_tenpo);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 12f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly01_03.setLayoutParams(layout);
            ly01_03.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));
        }
        {
            Space ly01_04 = (Space) findViewById(R.id.space_tenpo_02);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 1f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly01_04.setLayoutParams(layout);
        }
        {
            ImageButton ly01_05 = (ImageButton) findViewById(R.id.add_btn_tenpo);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
            ly01_05.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly01_05.setPadding(px, px, px, px);
        }
        {
            Space ly01_06 = (Space) findViewById(R.id.space_tenpo_03);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 1f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly01_06.setLayoutParams(layout);
        }

        // layout02
        {
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(AppSetting.CONTENT_ADD_LIST_ITEM_2, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px_y);
            layout02.setLayoutParams(layout);
        }
        {
            Space ly02_01 = (Space) findViewById(R.id.space_kisyu_01);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 0.5f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly02_01.setLayoutParams(layout);
        }
        {
            ImageView ly02_02 = (ImageView) findViewById(R.id.imageview_kisyu);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 3.5f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
//	        layout = new LinearLayout.LayoutParams( px_x, LinearLayout.LayoutParams.WRAP_CONTENT );

            ly02_02.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly02_02.setPadding(px, px, px, px);
        }
        {
            EditText ly02_03 = (EditText) findViewById(R.id.add_edit_kisyu);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 12f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly02_03.setLayoutParams(layout);
            ly02_03.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));
        }
        {
            Space ly02_04 = (Space) findViewById(R.id.space_kisyu_02);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 1f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly02_04.setLayoutParams(layout);
        }
        {
            ImageButton ly02_05 = (ImageButton) findViewById(R.id.add_btn_kisyu);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
            ly02_05.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly02_05.setPadding(px, px, px, px);
        }
        {
            Space ly02_06 = (Space) findViewById(R.id.space_kisyu_03);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 1f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly02_06.setLayoutParams(layout);
        }

        // layout03
        {
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(AppSetting.CONTENT_ADD_LIST_ITEM_3, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px_y);
            layout03.setLayoutParams(layout);
        }
        {
            Space ly03_01 = (Space) findViewById(R.id.space_investment);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 0.5f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly03_01.setLayoutParams(layout);
        }
        {
            ImageView ly03_02 = (ImageView) findViewById(R.id.imageview_investment);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 3.5f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
//	        layout = new LinearLayout.LayoutParams( px_x, LinearLayout.LayoutParams.WRAP_CONTENT );

            ly03_02.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly03_02.setPadding(px, px, px, px);
        }
        {
            EditText ly03_03 = (EditText) findViewById(R.id.add_edit_investment);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 12f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly03_03.setLayoutParams(layout);
            ly03_03.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));
        }
        {
            ImageButton ly03_04 = (ImageButton) findViewById(R.id.investment_btn_minus);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
            ly03_04.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly03_04.setPadding(px, px, px, px);
        }
        {
            ImageButton ly03_05 = (ImageButton) findViewById(R.id.investment_btn_plus);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
            ly03_05.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly03_05.setPadding(px, px, px, px);
        }


        // layout04
        {
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(AppSetting.CONTENT_ADD_LIST_ITEM_4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px_y);
            layout04.setLayoutParams(layout);
        }
        {
            Space ly04_01 = (Space) findViewById(R.id.space_recovery);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 0.5f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly04_01.setLayoutParams(layout);
        }
        {
            ImageView ly04_02 = (ImageView) findViewById(R.id.imageview_recovery);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 3.5f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
//	        layout = new LinearLayout.LayoutParams( px_x, LinearLayout.LayoutParams.WRAP_CONTENT );

            ly04_02.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly04_02.setPadding(px, px, px, px);
        }
        {
            EditText ly04_03 = (EditText) findViewById(R.id.add_edit_recovery);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 12f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly04_03.setLayoutParams(layout);
            ly04_03.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));
        }
        {
            ImageButton ly04_04 = (ImageButton) findViewById(R.id.recovery_btn_minus);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
            ly04_04.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly04_04.setPadding(px, px, px, px);
        }
        {
            ImageButton ly04_05 = (ImageButton) findViewById(R.id.recovery_btn_plus);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
            ly04_05.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly04_05.setPadding(px, px, px, px);
        }

        // layout05
        {
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(AppSetting.CONTENT_ADD_LIST_ITEM_5, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px_y);
            layout05.setLayoutParams(layout);
        }
        {
            Space ly05_01 = (Space) findViewById(R.id.space_total);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 0.5f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly05_01.setLayoutParams(layout);
        }
        {
            ImageView ly05_02 = (ImageView) findViewById(R.id.imageview_total);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 3.5f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
//	        layout = new LinearLayout.LayoutParams( px_x, LinearLayout.LayoutParams.WRAP_CONTENT );

            ly05_02.setLayoutParams(layout);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly05_02.setPadding(px, px, px, px);
        }
        {
            TextView ly05_03 = (TextView) findViewById(R.id.str_com_total_number);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 12f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, LinearLayout.LayoutParams.WRAP_CONTENT);
            ly05_03.setLayoutParams(layout);
            ly05_03.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));
        }

        // layout06
        {
            layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout06.setLayoutParams(layout);
        }
        {
            Space ly06_01 = (Space) findViewById(R.id.space_memo_01);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 0.5f), 1),
                    AppSetting.getDensity());
            layout = new LinearLayout.LayoutParams(px_x, px_y);
            ly06_01.setLayoutParams(layout);
        }

        {
            RelativeLayout.LayoutParams rl;

            Space ly06_99 = (Space) findViewById(R.id.space_memo_02);
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(AppSetting.CONTENT_ADD_LIST_ITEM_6, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, px_y);
            ly06_99.setLayoutParams(rl);


            ImageView ly06_02 = (ImageView) findViewById(R.id.imageview_memo);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 3.5f), 1),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 2f), 1),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, px_y);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            ly06_02.setLayoutParams(rl);
//           ly06_02.setId(2);
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly06_02.setPadding(px, px, px, px);
        }

        {
            RelativeLayout.LayoutParams rl;

            Space ly06_99 = (Space) findViewById(R.id.space_memo_03);
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(AppSetting.CONTENT_ADD_LIST_ITEM_6, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, px_y);
            ly06_99.setLayoutParams(rl);

            EditText ly06_03 = (EditText) findViewById(R.id.add_edit_memo);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp((dp * 14f), 1),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            MarginLayoutParams mlp = (MarginLayoutParams) rl;
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(10, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            mlp.setMargins(0, px, 0, px);
            ly06_03.setLayoutParams(mlp);
            ly06_03.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(16f, AppSetting.getContentWidthRatio())));
            px = AppSetting.getMypixel(
                    AppSetting.calcMyDp(4, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            ly06_03.setPadding(px, px, px, px);
        }

*/
    }

/*
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "AddList Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://jp.co.kaiwaredaikon320.syushi/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "AddList Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://jp.co.kaiwaredaikon320.syushi/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }
*/

    /**
     * 日付変更用処理
     *
     * @param view
     */
    public void onClickChangeToday( View view ){
        Trace.d( "onClickChangeToday" );

        DatePickerDialog datePickerDialog;

        //日付設定時のリスナ登録
        DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener( ){
            public void onDateSet( android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth ){

                // 日付が変更された場合は日付を書き換える
                if ( currentYear != year || currentMonth != monthOfYear || currentDay != dayOfMonth ) {
                    // 代入
                    currentYear = year;
                    currentMonth = monthOfYear;
                    currentDay = dayOfMonth;

                    exToday = currentYear+"年"+(currentMonth+1)+"月"+currentDay+"日";
                    buttonTodayId.setText(exToday);

                    dateChangeFlg = true;
                }
            }
        };

        //日付設定ダイアログの作成
        datePickerDialog = new DatePickerDialog( this, DateSetListener, currentYear, currentMonth, currentDay );

        //日付設定ダイアログの表示
        datePickerDialog.show( );
    }


    /**
     * 登録した店舗を検索する
     */
    public void onClickSearchTenpo(View view) {
        int len = lvd.itemList_tenpo.size();
        Trace.d( "onClickSearchTenpo list len = " + len );

        // itemが無い場合(機種データが登録されていない場合)はダイアログ抑制
        if (len == 0) {
            //  ダイアログ　機種データが保存されていませんを出す
            return;
        }

        lvd.createListViewDialog( 0, "店舗名", buttonTenpoId, (int)(windowWidth*0.8), (int)(windowHeight*0.8) );
//        lvd.createListViewDialog( 0, "店舗名", this, (int)(windowWidth*0.8), (int)(windowHeight*0.8) );

        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 新規で店舗を登録する
     */
    public void onClickAddTenpo(View view) {
        Trace.d( "onClickAddTenpo" );

        //レイアウトの呼び出し
        LayoutInflater factory = LayoutInflater.from(this);
        final View inputView = factory.inflate(R.layout.add_dialog_tenpo, null);
        final EditText newTenpo = (EditText)inputView.findViewById(R.id.dialog_edit_tenpo_text);
        final EditText excBallText = (EditText) inputView.findViewById(R.id.add_dialog_edit_01);
        final EditText excSlotText = (EditText) inputView.findViewById(R.id.add_dialog_edit_02);

        // すでに入力されている交換率を入力しておく
        excBallText.setText(""+exchangeBall);
        excSlotText.setText(""+exchangeSlot);

        // 機種未登録時はまっさらな状態(何も入力されていない状態)にしておく→入力をしやすいように
        if (!buttonTenpoId.getText().toString().equals("店舗未登録")) {
            newTenpo.setText(buttonTenpoId.getText());
        }

        //ダイアログの作成(AlertDialog.Builder)
        AlertDialog tenpoAddDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_add_list_new_tenpo)
                .setView(inputView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // 文字入力がないorデータ変更がないときは代入処理は行わない
                        if (!(newTenpo.getText().toString().equals("") || newTenpo.getText().toString().equals( buttonKisyuId.getText().toString() ) ) ) {
                            //  入力した機種名を登録する
                            buttonTenpoId.setText(newTenpo.getText(), BufferType.NORMAL);
                            inputChangeFlg = true;
                        }

                        // パチ交換率代入
                        if (!(excBallText.getText().toString().equals("") ) ) {
                            exchangeBall = Double.parseDouble(excBallText.getText().toString());
                            Trace.d( "exchangeBall = " + exchangeBall );
                            inputChangeFlg = true;
                        }
                        // スロ交換率代入
                        if (!(excSlotText.getText().toString().equals("") ) ) {
                            exchangeSlot = Double.parseDouble(excSlotText.getText().toString());
                            Trace.d( "exchangeSlot = " + exchangeSlot );
                            inputChangeFlg = true;
                        }
                        // 交換率描画を変更
                        textViewExchangeId.setText(exchangeBall+"円/"+exchangeSlot+"枚");

                        //　収支再計算
                        changeTotalTextColor( );
                        //　機種データの保存
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    /* キャンセル処理 */
                    }
                })
                .create();

        // 表示と同時にソフトキーボードを表示させるよう
        tenpoAddDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(newTenpo, 0);
            }
        });

        // 表示
        tenpoAddDialog.show();
    }


    /**
     * TextViewのみダイアログ表示の部分の共通処理
     * イベント、投資、回収
     */
    public void onClickAddCommon( final View view) {
        Trace.d( "onClickAddTenpo" );

        // 基本はイベントを代入しておく
        int dialogTitle = R.string.str_add_list_new_event;

        //レイアウトの呼び出し
        LayoutInflater factory = LayoutInflater.from(this);
        final View inputView = factory.inflate(R.layout.add_dialog_event, null);
        final EditText newEvent = (EditText)inputView.findViewById(R.id.dialog_edit_event_text);
        InputFilter[] _inputFilter = new InputFilter[1];

        // 共通処理なので、変更するものは変更する
        switch (view.getId()){

            // イベント
            case R.id.add_btn_event:
                newEvent.setText(buttonEventId.getText());
                break;

            // 台番
            case R.id.add_edit_number_01:
                // 入ってる文字をEditTextに代入しておく「
                newEvent.setText(buttonNumber.getText());

                dialogTitle = R.string.str_add_list_new_number;
                // 入力タイプを数字に
                newEvent.setInputType( InputType.TYPE_CLASS_NUMBER  );
                // 文字数制限(台番は４文字)
                _inputFilter[0] = new InputFilter.LengthFilter(4);
                newEvent.setFilters(_inputFilter);
                break;

            // 投資
            case R.id.add_btn_investment:
                // 入ってる文字をEditTextに代入しておく「
                newEvent.setText(buttonInvestmentId.getText());

                dialogTitle = R.string.str_add_list_new_investment;
                // 入力タイプを数字に
                newEvent.setInputType( InputType.TYPE_CLASS_NUMBER  );
                // 文字数制限(投資は6ケタ)
                _inputFilter[0] = new InputFilter.LengthFilter(6);
                newEvent.setFilters(_inputFilter);
                break;

            // 回収
            case R.id.add_btn_recovery:
                // 入ってる文字をEditTextに代入しておく「
                newEvent.setText(buttonRecoveryId.getText());

                dialogTitle = R.string.str_add_list_new_recovery;
                // 入力タイプを数字に
                newEvent.setInputType( InputType.TYPE_CLASS_NUMBER  );
                // 文字数制限(投資は6ケタ)
                _inputFilter[0] = new InputFilter.LengthFilter(6);
                newEvent.setFilters(_inputFilter);
                break;

        }

        //ダイアログの作成(AlertDialog.Builder)
        AlertDialog eventAddDialog = new AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setView(inputView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 文字入力がないorデータ変更がないときは代入処理は行わない
                        if (!(newEvent.getText().toString().equals("") ) ) {

                            inputChangeFlg = true;
                            // 共通処理なので、変更するものは変更する
                            switch (view.getId()){

                                // イベント
                                case R.id.add_btn_event:
                                        buttonEventId.setText(newEvent.getText(), BufferType.NORMAL);
                                    break;
                                // 台番
                                case R.id.add_edit_number_01:
                                        buttonNumber.setText(newEvent.getText(), BufferType.NORMAL);
                                        Trace.d( "buttonNumber = " + buttonNumber );
                                    break;

                                // 投資
                                case R.id.add_btn_investment:
                                    Trace.d( "buttonInvestmentId = " + newEvent.getText() );
                                    buttonInvestmentId.setText(""+Integer.parseInt(newEvent.getText().toString()), BufferType.NORMAL);
                                    changeTotalTextColor( );
                                    Trace.d( "buttonInvestmentId = " + buttonInvestmentId );
                                    break;

                                // 回収
                                case R.id.add_btn_recovery:
//                                    buttonRecoveryId.setText(newEvent.getText(), BufferType.NORMAL);
                                    buttonRecoveryId.setText(""+Integer.parseInt(newEvent.getText().toString()), BufferType.NORMAL);
                                    changeTotalTextColor( );
                                    Trace.d( "buttonRecoveryId = " + buttonRecoveryId );
                                    break;
                            }
                        }
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    /* キャンセル処理 */
                    }
                })
                .create();

        // 表示と同時にソフトキーボードを表示させるよう
        eventAddDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(newEvent, 0);
            }
        });

        // 表示
        eventAddDialog.show();
    }

    /**
     *
     * @param view
     */
    public void onClickYenPatiSlot( View view ){

        Button tmpBtn = (Button) view.findViewById(view.getId());
        int tmpPtn =  0;

        // 共通処理なので、変更するものは変更する
        switch (view.getId()){

            // 投資
            case R.id.add_investment_btn_01:
                investmentExchangePtn = (investmentExchangePtn + 1);

                // パチが選択されている場合にスロは飛ばす
                if(pachiType == 1 && investmentExchangePtn == 2){
                    investmentExchangePtn++;
                }

                // スロが選択されている場合にパチは飛ばす
                if(pachiType == 2 && investmentExchangePtn == 1){
                    investmentExchangePtn++;
                }
                // 補正
                investmentExchangePtn = investmentExchangePtn %3;
                tmpPtn = investmentExchangePtn;
                break;

            // 回収
            case R.id.add_recovery_btn_01:
                recoveryExchangePtn = (recoveryExchangePtn + 1);

                // パチが選択されている場合にスロは飛ばす
                if(pachiType == 1 && recoveryExchangePtn == 2){
                    recoveryExchangePtn++;
                }

                // スロが選択されている場合にパチは飛ばす
                if(pachiType == 2 && recoveryExchangePtn == 1){
                    recoveryExchangePtn++;
                }
                // 補正
                recoveryExchangePtn = recoveryExchangePtn %3;
                tmpPtn = recoveryExchangePtn;
                break;
        }

        Trace.d( "tmpPtn = " + tmpPtn );

        //
        switch (tmpPtn){
            // 円
            case 0:
                tmpBtn.setText(R.string.str_yen);
                break;

            // パチ
            case 1:
                tmpBtn.setText(R.string.str_ball);
                break;

            // スロ
            case 2:
                tmpBtn.setText(R.string.str_medal);
                break;
        }

        //
        //setBallMedalTotal();
        changeTotalTextColor( );

        inputChangeFlg = true;
    }

    /**
     * 時間を入力する(時給用)
     */
    public void onClickAddTime( final View view) {
        // 現在の時間を取得する
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        final Button tmpBtn = (Button) view.findViewById(view.getId());

        // すでに記入されている場合は代入しておく
        if( !tmpBtn.getText().toString().equals("")){
            String[] fruit = tmpBtn.getText().toString().split(":", 0);
            hour = Integer.parseInt( fruit[0] );
            minute = Integer.parseInt( fruit[1] );
        }

        Trace.d( "onClickAddTime hour = " + hour );
        Trace.d( "onClickAddTime minute = " + minute );

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker viewPicker, int hourOfDay, int minute) {

                        double tmp;
                        String strHourOfDay = String.valueOf(hourOfDay);
                        String strminute = String.valueOf(minute);

                        //　１ケタ数字を０付きの２ケタに補正する
                        if( hourOfDay < 10 ){
                            strHourOfDay = "0"+strHourOfDay;
                        }

                        if( minute < 10 ){
                            strminute = "0"+strminute;
                        }

                        //
                        tmpBtn.setText( strHourOfDay + ":" + strminute );

                        // 開始時間の計算
                        switch (view.getId()) {
                            case R.id.add_btn_jikyu_01:
                                calcStartTime = (hourOfDay * 60) + minute;
                                break;
                            case R.id.add_btn_jikyu_02:
                                calcEndTime = (hourOfDay * 60) + minute;
                                break;
                        }

                        // 稼働時間の計算
                        tmp = (calcEndTime - calcStartTime) / 60;

                        Trace.d( "tmp="+tmp );
                        Trace.d( "calcStartTime="+calcStartTime );
                        Trace.d( "calcEndTime="+calcEndTime );

                        // 何も入っていない時は０ｈを代入しておく。
                        if( calcStartTime != 0 && calcEndTime != 0 && tmp >= 0 ){
                            // 小数点第２で切り捨てる
                            tmp = AppSetting.getBigDecimalDouble( tmp, 2, BigDecimal.ROUND_DOWN );
                            textViewTimeId.setText(tmp + "h");
                            changeTotalTextColor( );
                        }
                        else{
                            textViewTimeId.setText("");
                        }

                        inputChangeFlg = true;
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }


    /**
     * 登録したイベントを検索
     */
    public void onClickSearchEvent( View view ){
        Trace.d( "onClickSearchEvent" );
        int len = lvd.itemList_event.size();

        // itemが無い場合(機種データが登録されていない場合)はダイアログ抑制
        if (len == 0) {
            //  ダイアログ　機種データが保存されていませんを出す
            return;
        }

        lvd.createListViewDialog( 2, "イベント", buttonEventId, (int)(windowWidth*0.8), (int)(windowHeight*0.8) );
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 新規で機種を登録する
     */
    public void onClickAddKisyu(View view) {
        Trace.d( "onClickAddKisyu" );

        //レイアウトの呼び出し
        LayoutInflater factory = LayoutInflater.from(this);
        final View inputView = factory.inflate(R.layout.add_dialog_kisyu, null);
        final EditText newKisyu = (EditText)inputView.findViewById(R.id.dialog_edit_kisyu_text);;
        final CheckBox check01 = (CheckBox)inputView.findViewById(R.id.dialog_checkBox_01);
        final CheckBox check02 = (CheckBox)inputView.findViewById(R.id.dialog_checkBox_02);

        final int pachiTypeBak = pachiType;


        // 初期値を設定しておく
        // スロ選択時
        if( pachiType == 2 ){
            check01.setChecked(false);
            check02.setChecked(true);
        }
        // 未選択orパチンコ選択時
        else{
            check01.setChecked(true);
            check02.setChecked(false);
        }

        // 機種未登録時はまっさらな状態(何も入力されていない状態)にしておく→入力をしやすいように
        if (!buttonKisyuId.getText().toString().equals("機種未登録")) {
            newKisyu.setText(buttonKisyuId.getText());
        }

        //  checkボックス01のOnOff
        check01.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // オン
                if(check01.isChecked()) {
                    check02.setChecked(false);
                }
                // おっふ
                else {
                    check02.setChecked(true);
                }
            }
        });

        //  checkボックス01のOnOff
        check02.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // オン
                if(check02.isChecked()) {
                    check01.setChecked(false);
                }
                // おっふ
                else {
                    check01.setChecked(true);
                }
            }
        });

        //ダイアログの作成(AlertDialog.Builder)
        AlertDialog kisyuAddDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_add_list_new_kisyu)
                .setView(inputView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // 文字入力がないorデータ変更がないときは代入処理は行わない
                        if (!(newKisyu.getText().toString().equals("") || newKisyu.getText().toString().equals( buttonKisyuId.getText().toString() ) ) ) {
                            //  入力した機種名を登録する
                            buttonKisyuId.setText(newKisyu.getText(), BufferType.NORMAL);
                            inputChangeFlg = true;
                        }

                        // パチ選択時
                        if(check01.isChecked()) {
                            pachiType = 1;
                        }
                        // スロ選択時
                        else {
                            pachiType = 2;
                        }

                        // 変更時
                        if( pachiTypeBak != pachiType ){
                            inputChangeFlg = true;
                        }
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();

        // 表示と同時にソフトキーボードを表示させるよう
        kisyuAddDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(newKisyu, 0);
                }
        });

        // 表示
        kisyuAddDialog.show();
    }


    /**
     * 登録した機種を検索
     */
    public void onClickSearchKisyu( View view ) {
        int len = lvd.itemList_kisyu.size();
        Trace.d("onClickSearchKisyu list len = " + len);

        // itemが無い場合はダイアログ抑制
        if (len == 0) {
            return;
        }

        lvd.createListViewDialog(1, "機種名", buttonKisyuId, (int)(windowWidth*0.8), (int)(windowHeight*0.8) );
//        lvd.createListViewDialog(1, "機種名", this, (int)(windowWidth*0.8), (int)(windowHeight*0.8) );

        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 前の画面に戻る
     */
    public void onClickLinearHeaderBackAdd( View view ){
        Trace.d( "onClickLinearHeaderBackAdd" );
        endReturnListActivity();
    }

    /**
     * データの保存
     */
    public void onClickLinearHeaderSave( View view ){
        Trace.d( "onClickLinearHeaderSave" );
        // 収支が入力されている場合は前の画面に戻る
        if (endAddListActivity() ) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            finish();
        }
        // 収支が入力されていない場合はtoastを表示させる
        else {
            Toast.makeText(this, "収支が入力されていません。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 数値を2桁に整形します
     * @param n 数値をセット
     * @return String 2桁の数値が戻ります
     */
    private String toTwoDigits(int n) {
        if (n >= 10) {
            return Integer.toString(n);
        } else {
            return "0" + n;
        }
    }


    /**
     * 選択した店舗の項目をセット
     *
     * @param ball パチンコの交換率
     * @param ball スロットのの交換率
     */
    public void setTenpoItem( String txt, double ball, double slot ) {

        exchangeBall = ball;
        exchangeSlot = slot;

        buttonTenpoId.setText( txt, BufferType.NORMAL);

        // 交換率の反映
        textViewExchangeId.setText(exchangeBall+"円/"+exchangeSlot+"枚");

        // 収支を計算し直す
        changeTotalTextColor( );
    }

    /**
     * 選択した店舗の項目をセット
     *
     * @param txt 機種名
     * @param type タイプ
     */
    public void setKisyuItem( String txt, int type ) {
        pachiType = type;
        buttonKisyuId.setText( txt, BufferType.NORMAL);
    }
}

