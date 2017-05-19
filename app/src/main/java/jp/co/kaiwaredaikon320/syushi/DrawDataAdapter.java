package jp.co.kaiwaredaikon320.syushi;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class DrawDataAdapter extends ArrayAdapter<ListViewDrawAdapter>{
    private int mResource;
	private LayoutInflater mInflater;
//	private Context mContext;
	public int visibility;

	public DrawDataAdapter(Context context, int resource, int textViewResourceId, List<ListViewDrawAdapter> objects) {
		super(context, resource, textViewResourceId, objects);

		// initialize
		mResource = resource;
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

//		mContext = context;
	}


	public View getView( final int position, View convertView, ViewGroup parent) {

		View view;

		final TextView typeText;			//　"機種名"

//		final TextView investmentText;		//　"投資"
//		final TextView recoveryText;		//　"回収"
//		final TextView totalText;			//	"収支"

		final TextView investmentNumber;	//　投資"金額"
		final TextView recoveryNumber;		//　回収"金額"
		final TextView totalNumber;			//　収支"金額"

		final TextView totalVictory;		//　勝敗


		if (convertView == null) {
			view = mInflater.inflate(mResource, parent, false);
		} else {
			view = convertView;
		}

		try {

			// レイアウトを調整
//			layout( view );

			//　ウィジェットを取得する
			typeText = (TextView)view.findViewById( R.id.data_item_text );

//			investmentText = (TextView)view.findViewById(R.id.data_investmentText);
//			recoveryText   = (TextView)view.findViewById(R.id.data_recoveryText);
//			totalText 	   = (TextView)view.findViewById(R.id.data_totalText);

			investmentNumber = (TextView)view.findViewById(R.id.data_investmentNumber);
			recoveryNumber   = (TextView)view.findViewById(R.id.data_recoveryNumber);
			totalNumber 	 = (TextView)view.findViewById(R.id.data_totalNumber);

			totalVictory     = (TextView)view.findViewById(R.id.data_victory_defeat);

			// 矢印
			ImageView arrow= (ImageView)view.findViewById(R.id.arrow);
			// 次へのカーソル表示のon/off
			if( getVisibility( ) == 0 ){
				// 表示しない
				arrow.setVisibility(View.GONE);
			}
			else{
				// 表示する
				arrow.setVisibility(View.VISIBLE);
			}

		} catch (ClassCastException e) {
            Trace.e("BookAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "BookAdapter requires the resource ID to be a TextView", e);
		}

		ListViewDrawAdapter listviewitem = this.getItem(position);

		// 項目
		typeText.setText( listviewitem.getItem( ) );

		//　金額関連
		investmentNumber.setText( AppSetting.separateComma( listviewitem.getInvestment( ) ) );
		recoveryNumber.setText( AppSetting.separateComma( listviewitem.getRecovery( ) ) );
		//　収支によって色を変える
		totalNumber.setText( AppSetting.separateComma( listviewitem.getTotal( ) ) );

	//　total収支によって色を変更する
		int _total = listviewitem.getTotal( );

		// ±0
		if( _total == 0 ){
			totalNumber.setTextColor(Color.BLACK);
		}
		// ＋収支
		else if( _total > 0 ){
			totalNumber.setTextColor(Color.BLUE);
		}
		// －収支
		else{
			totalNumber.setTextColor(Color.RED);
		}

		// 勝敗を変更
		totalVictory.setText(
								(listviewitem.getWin() + listviewitem.getLose() + listviewitem.getDraw() ) +"戦"+
								listviewitem.getWin() + "勝"+
								listviewitem.getLose() + "負"+
								listviewitem.getDraw() + "分");

//		Trace.d( "position = " + position );
		return view;
	}

	// DrawDataのsetVisibilityを登録する
	public void setVisibility( int atai ){
		visibility = atai;
	}

	// DrawDataのgetVisibilityを取得する
	public int getVisibility( ){
		return visibility;
	}


	/**
	*<p>
	* layout調整
	*</p>
	*/
/*	public void layout( View view ) {

		int px;
		int px_x;
		int px_y;

		LinearLayout.LayoutParams ly;
		TableLayout.LayoutParams tb;
		RelativeLayout.LayoutParams rl;

		ImageView img;
		TextView txt;


		// 列1
		{
			// 店舗名 text
			txt = (TextView)view.findViewById(R.id.data_item_text);
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 32f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			ly = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, px_y );
			txt.setLayoutParams(ly);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );

		}

		// 列2
		{
			// 投資 image
			img = (ImageView)view.findViewById(R.id.data_investmentText);
			px_x = AppSetting.getMypixel(
			AppSetting.calcMyDp( 74f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 20f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
			rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT );
			rl.addRule(RelativeLayout.CENTER_VERTICAL );
			img.setLayoutParams(rl);

			// 投資 text
			txt = (TextView)view.findViewById(R.id.data_investmentNumber);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );


			// 回収 image
			img = (ImageView)view.findViewById(R.id.data_recoveryText);
			px_x = AppSetting.getMypixel(
			AppSetting.calcMyDp( 74f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 20f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
			rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT );
			rl.addRule(RelativeLayout.CENTER_VERTICAL );
			img.setLayoutParams(rl);

			// 回収 text
			txt = (TextView)view.findViewById(R.id.data_recoveryNumber);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );


			// 収支 image
			img = (ImageView)view.findViewById(R.id.data_totalText);
			px_x = AppSetting.getMypixel(
			AppSetting.calcMyDp( 74f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 20f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
			rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT );
			rl.addRule(RelativeLayout.CENTER_VERTICAL );
			img.setLayoutParams(rl);


			// 収支 text
			txt = (TextView)view.findViewById(R.id.data_totalNumber);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );



			// 結果 text
			txt = (TextView)view.findViewById(R.id.data_victory_defeat);
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 20f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			ly = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, px_y );
			txt.setLayoutParams(ly);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );

		}


		// 矢印
		{
			// 投資 image
			img = (ImageView)view.findViewById(R.id.arrow);
			px_x = AppSetting.getMypixel(
			AppSetting.calcMyDp( 24f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 80f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
			rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT );
			rl.addRule(RelativeLayout.CENTER_VERTICAL );
			img.setLayoutParams(rl);
		}
	}
    */
}
