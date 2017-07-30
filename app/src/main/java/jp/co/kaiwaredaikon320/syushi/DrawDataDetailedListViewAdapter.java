package jp.co.kaiwaredaikon320.syushi;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DrawDataDetailedListViewAdapter extends ArrayAdapter<ListViewItem> {

    private int mResource;
	private LayoutInflater mInflater;
//	private Context mContext;
    public int hierarchy;

	public DrawDataDetailedListViewAdapter(Context context, int resource, int textViewResourceId, List<ListViewItem> objects) {
		super(context, resource, textViewResourceId, objects);

		// initialize
		mResource = resource;
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

//		mContext = context;
	}


	@Override
	public View getView( final int position, View convertView, ViewGroup parent) {

		View view;

		final TextView typeText;			//　"機種名"



		final TextView investmentNumber;	//　投資"金額"
		final TextView recoveryNumber;		//　回収"金額"
		final TextView totalNumber;			//　収支"金額"

		if (convertView == null) {
			view = mInflater.inflate(mResource, parent, false);

			// layoutの調整
//			layout( view );

		} else {
			view = convertView;
		}

		try {

			//　ウィジェットを取得する
			typeText = (TextView)view.findViewById( R.id.input_typeText );
			investmentNumber = (TextView)view.findViewById(R.id.input_investmentNumber);
			recoveryNumber   = (TextView)view.findViewById(R.id.input_recoveryNumber);
			totalNumber 	 = (TextView)view.findViewById(R.id.input_totalNumber);

			ImageView arrow             = (ImageView)view.findViewById(R.id.arrow);

			// 次へのカーソル表示のon/off
			if( getHierarchy( ) == 0 ){
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

		ListViewItem listviewitem = this.getItem(position);

		// 機種名
		typeText.setText( listviewitem.getType( ) );

		//　金額関連
		investmentNumber.setText( ""+AppSetting.separateComma( listviewitem.getInvestment( ) ) );
		recoveryNumber.setText( ""+AppSetting.separateComma( listviewitem.getRecovery( ) ) );
		//　収支によって色を変える
		totalNumber.setText( ""+AppSetting.separateComma( listviewitem.getTotal( ) ) );

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

		Trace.d( "position = " + position );
		return view;
	}

	// DrawDataDetailedListActivtyのhierarchyを登録する
	public void setHierarchy( int atai ){
		hierarchy = atai;
	}

	// DrawDataDetailedListActivtyのhierarchyを取得する
	public int getHierarchy( ){
		return hierarchy;
	}

	/**
	*<p>
	* layout調整
	*</p>
	*/
/*	public void layout( View view ) {

//		int px;
		int px_x;
		int px_y;

		FrameLayout.LayoutParams fr;
//		LinearLayout.LayoutParams ly;
//		TableLayout.LayoutParams tb;
		RelativeLayout.LayoutParams rl;

		ImageView img;
		TextView txt;


		// 列1
		{
			// 項目 text
			txt = (TextView)view.findViewById(R.id.input_typeText);
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 32f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			fr = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, px_y );
			txt.setLayoutParams(fr);
			txt.setTextSize(
			TypedValue.COMPLEX_UNIT_DIP,
			( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );

			// 項目 矢印
			img = (ImageView)view.findViewById(R.id.arrow);
			px_x = AppSetting.getMypixel(
			AppSetting.calcMyDp( 24f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			px_y = AppSetting.getMypixel(
			AppSetting.calcMyDp( 32f, AppSetting.getContentWidthRatio() ),
			AppSetting.getDensity() );
			rl = new RelativeLayout.LayoutParams( px_x, px_y );
			rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT );
			rl.addRule(RelativeLayout.CENTER_VERTICAL );
			img.setLayoutParams(rl);
		}

		// 列2
		{
			// 投資 image
			img = (ImageView)view.findViewById(R.id.input_image_investment);
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

			// 投資 text
			txt = (TextView)view.findViewById(R.id.input_investmentNumber);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );


			// 回収 image
			img = (ImageView)view.findViewById(R.id.input_image_recovery);
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

			// 回収 text
			txt = (TextView)view.findViewById(R.id.input_recoveryNumber);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );


			// 収支 image
			img = (ImageView)view.findViewById(R.id.input_image_total);
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

			// 収支 text
			txt = (TextView)view.findViewById(R.id.input_totalNumber);
			txt.setTextSize(
				TypedValue.COMPLEX_UNIT_DIP,
				( AppSetting.calcMyDp( 16f, AppSetting.getContentWidthRatio() ) ) );
		}
	}*/
}
