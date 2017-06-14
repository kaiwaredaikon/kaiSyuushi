/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package jp.co.kaiwaredaikon320.syushi.view.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jp.co.kaiwaredaikon320.syushi.MainActivity;
import jp.co.kaiwaredaikon320.syushi.R;
import jp.co.kaiwaredaikon320.syushi.Trace;
import jp.co.kaiwaredaikon320.syushi.util.AnimationHelper;
import jp.co.kaiwaredaikon320.syushi.util.StrUtil;
import jp.co.kaiwaredaikon320.syushi.view.FixableViewFlipper;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;



/**
 * カレンダービューを提供します
 *
 * @author Kazz.
 * @since JDK1.5 Android Level 4
 *
 */
public class CalendarView extends LinearLayout implements GestureDetector.OnGestureListener{

	protected GestureDetector detector;

    protected static final String TAG = CalendarView.class.getSimpleName();

    private static final Animation inFromLeft = AnimationHelper.inFromLeftAnimation();
    private static final Animation outToRight = AnimationHelper.outToRightAnimation();
    private static final Animation inFromRight = AnimationHelper.inFromRightAnimation();
    private static final Animation outToLeft = AnimationHelper.outToLeftAnimation();

    private static final Animation inFromTop = AnimationHelper.inFromTopAnimation();
    private static final Animation outToBottom = AnimationHelper.outToBottomAnimation();
    private static final Animation inFromBottom = AnimationHelper.inFromBottomAnimation();
    private static final Animation outToTop = AnimationHelper.outToTopAnimation();

    private static final int DIRECTION_HORIZONTAL = 0;
    private static final int DIRECTION_VERTICAL = 1;

    protected java.util.Calendar calendar = java.util.Calendar.getInstance();
    protected ViewFlipper viewFlipper;
    protected MonthlyCalendarView mViewPrevious;
    protected MonthlyCalendarView mViewNext;
    protected float lastTouchX;
    protected float lastTouchY;
    protected TextView txtHeader;

    //各色のデフォルト値 (デザイン時に使用する)
    protected int c_background = Color.parseColor( "#f0ffffff" );
    protected int c_foreground = Color.parseColor( "#ff000000" );
    protected int c_dark = Color.parseColor( "#6456648f" );
    protected int c_highlight = Color.parseColor( "#ffffffff" );
    protected int c_light = Color.parseColor( "#64c6d4ef" );
    protected int c_holiday = Color.parseColor( "#ffFF0000" );
    protected int c_saturday = Color.parseColor( "#ff0000FF" );
    protected int c_selected = Color.parseColor( "#64FFA500" );

    private MainActivity mActivity;

    //リスナリスト
    protected ArrayList<OnCalendarSelectionListener> listenerList = new ArrayList<OnCalendarSelectionListener>();

    // 月日
    protected static final String[] monthNames = { "01月", "02月", "03月", "04月", "05月", "06月", "07月", "08月", "09月", "10月", "11月", "12月" };
/*
    protected static final String[] monthNames;// = new DateFormatSymbols().getShortMonths();

    static {
        monthNames = new String[] {
            DateUtils.getMonthString(Calendar.JANUARY, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.FEBRUARY, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.MARCH, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.APRIL, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.MAY, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.JUNE, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.JULY, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.AUGUST, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.SEPTEMBER, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.OCTOBER, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.NOVEMBER, DateUtils.LENGTH_LONG),
            DateUtils.getMonthString(Calendar.DECEMBER, DateUtils.LENGTH_LONG)
        };
        //new DateFormatSymbols().getShortWeekdays();
    }
*/

    /**
     * コンストラクタ
     * @param context 親のコンテキストをセット
     */
    public CalendarView( Context context ) {
		super( context );
		this.init( context, null );
		Trace.d( "CalendarView 1 " );
    }

    /**
     * コンストラクタ
     * @param context 親のコンテキストをセット
     * @param attrs 外部(XML)から取り込むアトリビュートをセット
     */
    public CalendarView( Context context, AttributeSet attrs ){
       super( context, attrs );
       this.init( context, attrs );
       Trace.d( "CalendarView 2 " );
    }

	public void setView( MainActivity mainActivity ){
		mActivity = mainActivity;
        this.txtHeader = (TextView) mActivity.findViewById(R.id.header_text);

        // 月日もここでSetする
        this.setHeader( this.calendar.get( java.util.Calendar.YEAR ),
                this.calendar.get( java.util.Calendar.MONTH  ) );
	}

    /**
     * Calendarを取得します
     * @return java.util.Calendar Calendarが戻ります

    public java.util.Calendar getCalandar( ){
        return this.calendar;
    }
     */


    /**
     * Calendarを設定します
     * @param calandar calendarをセットします
     */
    public void setCalandar( java.util.Calendar calandar ){
        this.calendar = calandar;

        if ( this.viewFlipper != null ) {
            MonthlyCalendarView view = ( MonthlyCalendarView )this.viewFlipper.getChildAt( this.viewFlipper.getDisplayedChild( ) );

            // viewはnullもあり得る
            if( view != null ){
                view.setCalendar( this.calendar );
                if ( this.calendar.equals( calandar ) ){
                    view.setToDay( this.calendar.get( Calendar.DAY_OF_MONTH ) );
                }
            }
        }
    }


     /**
     * 対象の年、付きを設定します
     * @param year 年をセットします
     * @param month 月をセットします
     */
    public void setCalendar( int year, int month ){
        this.calendar.clear( );
        this.calendar.set( year, month - 1, 1 ); // 引数: 1月: 0, 2月: 1, ...

        if ( this.viewFlipper != null ) {
            MonthlyCalendarView view = ( MonthlyCalendarView )this.viewFlipper.getChildAt( this.viewFlipper.getDisplayedChild( ) );
            if ( view != null ) { //viewはnullもあり得る
                view.setCalendar( this.calendar );
            }
        }
    }


    /**
     *  コンポーネントの初期化を実施します
     * @param context コンテキストをセット
     * @param attrs アトリビュートをセット
     */
    private void init( Context context, AttributeSet attrs ){

    	// フォントのサイズを計算する(Dpで16文字計算)
    	float fontsize = ( getContext().getResources().getDisplayMetrics().widthPixels / getContext().getResources().getDisplayMetrics().density ) / 16;

        // GestureDetectorインスタンス作成
        this.detector = new GestureDetector( context, this );

        //フレームワークリソースから色を取得
        Resources res = context.getResources( );

        if ( res != null ) {
            //  背景色
            this.c_background = res.getColor( R.color.calendar_background );

            //  区切り線
            this.c_dark = res.getColor( R.color.calendar_dark );
            this.c_highlight = res.getColor( R.color.calendar_highlight );

            //  曜日の色
            this.c_foreground = res.getColor( R.color.calendar_foreground );    //  何もなし
            this.c_holiday = res.getColor( R.color.calendar_holiday );         //  日曜日・祝日
            this.c_saturday = res.getColor( R.color.calendar_saturday );       //  土曜日

            this.c_light = res.getColor( R.color.calendar_light );
            this.c_selected = res.getColor( R.color.calendar_selected );
        }

        //XMLから属性読み込み
        if ( attrs != null ) {

            try {
                // date = "yyyy/MM"
                String dateStr = attrs.getAttributeValue( null, "date" );

                //if ( dateStr != null && dateStr.length() > 0 )
                if ( StrUtil.isNotEmpty(dateStr)) {
                    SimpleDateFormat format = new SimpleDateFormat( "yyyy/MM" );
                    Date date = format.parse( dateStr );
                    Calendar cal = Calendar.getInstance( );
                    cal.setTime( date );
                    this.setCalendar( cal.get(Calendar.YEAR), cal.get( Calendar.MONTH ) + 1 );
                }

                // background = "#f0ffffff" (#ARGB)
                String backgroundStr = attrs.getAttributeValue(null, "background");

                if ( StrUtil.isNotEmpty(backgroundStr)) {
                    this.c_background = Color.parseColor(backgroundStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        this.setClickable(true);

        this.setOrientation( LinearLayout.VERTICAL);
        this.setGravity(Gravity.TOP | Gravity.CENTER) ;
        // CalendarViewの色変更はここ↓
        this.setBackgroundColor(this.c_background);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

Trace.d( "aaaa" );

        //ビューフリッパーの追加
        this.viewFlipper = new FixableViewFlipper( context );
        this.viewFlipper.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ) );
        {
            //カレンダービューの追加
            this.mViewPrevious = new MonthlyCalendarView(context);
            this.mViewPrevious.setCalendar(this.calendar);
            this.mViewPrevious.setBackgroundColor(this.c_background);
            this.viewFlipper.addView(this.mViewPrevious);

            //カレンダービューの追加
            this.mViewNext = new MonthlyCalendarView(context);
            this.mViewNext.setCalendar(this.calendar);
            this.mViewNext.setBackgroundColor(this.c_background);
            this.viewFlipper.addView(this.mViewNext);
        }
Trace.d( "bbb" );
        this.addView(this.viewFlipper);
Trace.d( "ccc" );
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	Trace.d( "CalendarView onSizeChanged w:" + w + " h:" + h + " oldw:" + oldw + " oldh:" + oldh   );
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * カレンダーの特定日が選択された際の処理を記述します
     * @param currentView　対象になっているカレンダービューをセットします
     */
    protected void performSelectionCalendar( MonthlyCalendarView currentView ){

        DateInfo dateInfo = currentView.getSelectedDateInfo( );

        Trace.d( "performSelectionCalendar dateInfo " + dateInfo );

        if ( dateInfo != null ) {
            //Log.d(TAG, "onCalendarSelection: dateInfo=" + dateInfo );
            int size = this.listenerList.size( );
            if ( size > 0 ) {
                for (int i = size-1; i >= 0; i--) {
                    this.listenerList.get( i ).onCalendarSelection( new CalendarSelectionEvent( this, dateInfo ) );
                }
            }
        }
    }


    /**
     * カレンダーの特定日を選択します
     * @param year 年をセット
     * @param month 月をセット
     * @param day 日をセット
     */
    public void selectCalendar( int year, int month, int day ){

        MonthlyCalendarView current = ( MonthlyCalendarView )this.viewFlipper.getCurrentView( );

        if ( current != null ) {
            current.setCalendar( year, month, day );
            DateInfo dateInfo = current.getSelectedDateInfo( );
            //	Log.d(TAG, "onCalendarSelection: dateInfo=" + dateInfo );
            int size = this.listenerList.size( );
            if ( size > 0 ) {
                for (int i = size-1; i >= 0; i--) {
                    this.listenerList.get( i ).onCalendarSelection( new CalendarSelectionEvent( this, dateInfo ) );
                }
            }
        }
    }

    /**
     * 現在選択されている日付情報を取得します
     * @return MonthlyCalendarView.DateInfo 選択されている日付情報が戻ります
     */
    public DateInfo getDateInfo() {
        MonthlyCalendarView currentView = ( MonthlyCalendarView )viewFlipper.getCurrentView( );
        return ( currentView.getSelectedDateInfo( ) );
    }

    /**
     * カレンダ選択監視リスナを追加します
     * @param listener リスナをセット
     */
    public void addOnCalendarSelectionListener( OnCalendarSelectionListener listener ){
        this.listenerList.add( listener );
    }

    /**
     * カレンダ選択リスナを取り除きます
     * @param listener リスナをセット
     */
    public void removeOnCalendarSelectionListener( OnCalendarSelectionListener listener ){
       this.listenerList.remove( listener );
    }

    /**
     * datapickerdialogの設定
     */
    public void setDataPickerDialog( ){

    	Context ctx = this.getContext( );

    	DatePickerDialog datePickerDialog;

		final MonthlyCalendarView currentView = ( MonthlyCalendarView )viewFlipper.getCurrentView( );
        final int currentYear = currentView.getCalendar( ).get( Calendar.YEAR );
        final int currentMonth = currentView.getCalendar( ).get( Calendar.MONTH );
        final int currentDay = currentView.getCalendar( ).get( Calendar.DATE );

        Trace.d( "currentYear = " +currentYear );
        Trace.d( "currentMonth = " +currentMonth );
        Trace.d( "currentDay = " +currentDay );

        //日付設定時のリスナ登録
        DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener( ){
            public void onDateSet( android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth ){

                if ( currentYear != year || currentMonth != monthOfYear ) {
                      showYearMonth( year, ( monthOfYear + 1 ), dayOfMonth );
                }
                // 日にちのみ変更は日付のみ移動をさせる
                else if( currentDay != dayOfMonth ){
                	currentView.setToDay( dayOfMonth );
                }
            }
        };

        //日付設定ダイアログの作成
        datePickerDialog = new DatePickerDialog( ctx, DateSetListener, currentYear, currentMonth, currentDay );

//      datePickerDialog.setTitle("日付移動");

        //日付設定ダイアログの表示
        datePickerDialog.show( );
    }


    /**
     * 前月を表示する
     * @param direction モーションの方向をセット(DIRECTION_VERTICAL|DIRECTION_HORIZONTAL)
     */
    private void showPreviousMonth( int direction ){

        int index = this.viewFlipper.getDisplayedChild( ) == 0 ? 1 : 0;

        MonthlyCalendarView calendarView = ( MonthlyCalendarView )this.viewFlipper.getChildAt( index );

        if ( calendarView != null ) {

            calendarView.setCalendar( this.calendar );

            this.setHeader( this.calendar.get( java.util.Calendar.YEAR ),
            				calendar.get( java.util.Calendar.MONTH ) );
            if ( direction == DIRECTION_VERTICAL ) {
                this.viewFlipper.setInAnimation( inFromTop );
                this.viewFlipper.setOutAnimation( outToBottom );
            }else{
                this.viewFlipper.setInAnimation( inFromLeft );
                this.viewFlipper.setOutAnimation( outToRight );
            }
            this.viewFlipper.showPrevious( );
        }

        // リスナーイベント？
        MonthlyCalendarView currentView = ( MonthlyCalendarView )viewFlipper.getCurrentView( );
        if ( currentView != null ) {
        	performSelectionCalendar( calendarView );
        }
    }


    /**
     * 次月へのボタンを押したときの処理
     */
    public void onClickNextMonth(  ) {
        calendar.add( Calendar.MONTH, 1 );
        showNextMonth( DIRECTION_HORIZONTAL );
    }

    /**
     * 前月へのボタンを押したときの処理
     */
    public void onClickPrevMonth(  ){
        calendar.add(Calendar.MONTH, -1);
        showPreviousMonth(DIRECTION_HORIZONTAL);
    }


    /**
     * 次月を表示する
     * @param direction モーションの方向をセット(DIRECTION_VERTICAL|DIRECTION_HORIZONTAL)
     */
    private void showNextMonth( int direction ){

        int index = this.viewFlipper.getDisplayedChild( ) == 0 ? 1 : 0;

        MonthlyCalendarView calendarView = ( MonthlyCalendarView )this.viewFlipper.getChildAt( index );

        if ( calendarView != null ) {

            calendarView.setCalendar( this.calendar );

            this.setHeader( this.calendar.get( java.util.Calendar.YEAR ), calendar.get( java.util.Calendar.MONTH ) );

            if ( direction == DIRECTION_VERTICAL ) {
                this.viewFlipper.setInAnimation( inFromBottom );
                this.viewFlipper.setOutAnimation( outToTop );
            } else {
                this.viewFlipper.setInAnimation( inFromRight );
                this.viewFlipper.setOutAnimation( outToLeft );
            }
            this.viewFlipper.showNext( );
        }

        // リスナーイベント
        MonthlyCalendarView currentView = ( MonthlyCalendarView )viewFlipper.getCurrentView( );

        if ( currentView != null ) {
        	performSelectionCalendar( currentView );
        }
    }

    /**
     * 対象の年月を表示します
     * @param year 年をセット
     * @param month 月をセット
     */
    private void showYearMonth(int year, int month) {
        int currentYear = this.calendar.get(java.util.Calendar.YEAR);
        int currentMonth = this.calendar.get(java.util.Calendar.MONTH)+1;

//        int currentdata = this.calendar.get(java.util.Calendar.DATE);

        if ( year == currentYear && month == currentMonth) return;

        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month-1);
//      this.calendar.set(Calendar.DATE, currentdata);

        if ( year > currentYear ) {
            this.showNextMonth(DIRECTION_VERTICAL);
        }else{
            if ( year < currentYear ) {
                this.showPreviousMonth(DIRECTION_VERTICAL);
            } else {
                if ( month > currentMonth ) {
                    this.showNextMonth(DIRECTION_VERTICAL);
                } else {
                    this.showPreviousMonth(DIRECTION_VERTICAL);
                }
            }
        }
    }


    /**
     * 対象の年月を表示します
     * @param year 年をセット
     * @param month 月をセット
     * @param day 日にちをセット
     */
    private void showYearMonth(int year, int month, int day) {
        int currentYear = this.calendar.get(java.util.Calendar.YEAR);
        int currentMonth = this.calendar.get(java.util.Calendar.MONTH)+1;
        int currentDay = this.calendar.get(java.util.Calendar.DATE);

        if ( year == currentYear && month == currentMonth && day == currentDay ) return;

        this.calendar.set( Calendar.YEAR, year );
        this.calendar.set( Calendar.MONTH, month-1 );
        this.calendar.set( Calendar.DAY_OF_MONTH, day );

        if ( year > currentYear ) {
            this.showNextMonth(DIRECTION_VERTICAL);
        }else{
            if ( year < currentYear ) {
                this.showPreviousMonth(DIRECTION_VERTICAL);
            } else {
                if ( month > currentMonth ) {
                    this.showNextMonth(DIRECTION_VERTICAL);
                } else {
                    this.showPreviousMonth(DIRECTION_VERTICAL);
                }
            }
        }
    }




    /**
     * ヘッダの年月を設定します
     * @param year 年をセット
     * @param month 月をセット
     */
    private void setHeader(int year, int month) {

        Trace.d( "setHeader " );

        this.txtHeader.setText( year
                + (Locale.getDefault().equals(Locale.JAPAN) ? "年" : " ")
                + monthNames[month]);
    }


    @Override
    public boolean onTouchEvent( MotionEvent event ){
	    // false 親にイベントを戻す  true ここでイベント止める
	   if( this.detector.onTouchEvent( event ) ) return true;
	   return super.onTouchEvent( event );
    }



    /**
     * フリックかタップかにより画面を変えます
     * @param currentX 現在のX座標
     * @param velocityX X座標の移動量
     * @param currentY 現在のY座標
     * @param velocityY Y座標の移動量
     */
    private boolean flickOrTaptoDate(float currentX, float velocityX, float currentY, float velocityY) {

        //基準の移動距離はViewConfigurationを使用する
        int minimumFlingVelocity =  ( ViewConfiguration.getMinimumFlingVelocity() * 2 );

        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);

        Trace.d( "minimumFlingVelocity = " + minimumFlingVelocity );


        // X軸が一定以上動いていない場合は月を移動させない。
        if ( velocityX < minimumFlingVelocity ) {

            //変量 < 閾値の場合タップ
            MonthlyCalendarView currentView = (MonthlyCalendarView)viewFlipper.getCurrentView( );

            if ( currentView != null ) {

            	Trace.d( "flickOrTaptoDate" );

                boolean result = currentView.performClick(); //対象ビューのクリックを発生させる
//              performSelectionCalendar(currentView);

                Trace.d( "result = " + result );

                return result;
            }
        }else{

            //変量の大きい方にアニメーション
            int direction = DIRECTION_HORIZONTAL;

            //次月(上、又は左にモーション)? 前月(下又は右にモーション)?
            boolean showNext = ( currentX < lastTouchX ) ;

            //カレンダー加減算
            calendar.add( Calendar.MONTH, showNext ? 1 : -1 );

            //適切な月に移動
            if ( showNext ) {
                showNextMonth(direction);
            }else{
                showPreviousMonth(direction);
            }

            Trace.d( "showNext = " + showNext );
        }
        return true;
    }


	@Override
	public boolean onDown(MotionEvent e) {
		Trace.d( "onDown" );
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

		Trace.d( "onShowPress" );

		DateInfo dateInfo = getDateInfo();

		if( dateInfo != null ){
			mActivity.intentConfigCommonProcess( dateInfo );
			mActivity.onShowPressFlg = true;
		}
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// 画面をシングルタップしたときに呼び出される。なお、ダブルタップの時にも呼ぶ出される。
		return false;
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distnceX, float distanceY) {
		// 画面をドラックした時に呼ばれる
		Trace.d( "onScroll" );
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// 長押しイベント
		Trace.d( "onLongPress" );
	}

	/**
	 * @param e1 ダウン時のタッチイベント
     * @param e2 スクロール時のタッチイベント
     * @param velocityX x軸のスクロール時の距離
     * @param velocityY y軸のスクロール時の距離
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

    	// 収支入力画面に移行処理が入ってる場合は処理を行わない。
    	if( MainActivity.onShowPressFlg == true ){
    		return true;
    	}

		// 画面を押したときに呼ばれる-
        lastTouchX = e1.getX();
        lastTouchY = e1.getY();

        //Log.d("viewFlipper", "++ otionEvent.ACTION_CANCEL ++ " + event.toString());
        float currentX = e2.getX();
        float currentY = e2.getY();
        this.flickOrTaptoDate( currentX, currentX - lastTouchX, currentY, currentY - lastTouchY);

		Trace.d( "onFling" );
		return false;
	}

	/**
	 * @return 月の総投資額を返す
	 */
	public int getCalMonthInvariment( ){
		MonthlyCalendarView currentView = (MonthlyCalendarView)viewFlipper.getCurrentView();
		return currentView.retMonthInvestment;
	}

	/**
	 * @return 月の総投回収額を返す
	 */
	public int getCalMonthRecovery( ){
		MonthlyCalendarView currentView = (MonthlyCalendarView)viewFlipper.getCurrentView();
		return currentView.retMonthRecovery;
	}


	/**
	 * @return 月間収支の更新
	 */
	public void renewalMonthTotal(){
		MonthlyCalendarView currentView = (MonthlyCalendarView)viewFlipper.getCurrentView();
		currentView.calcCalendarMatrix( true );
	}
}


