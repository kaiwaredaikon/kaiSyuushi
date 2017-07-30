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
    import android.widget.RelativeLayout;
    import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<ListViewItem> {

    private int mResource;
    private LayoutInflater mInflater;

    //	private Context mContext;

    public ListViewAdapter(Context context, int resource, int textViewResourceId, List<ListViewItem> objects) {
        super(context, resource, textViewResourceId, objects);

        // initialize
        mResource = resource;
        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        //		mContext = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;

        final TextView typeText;         //　"機種名"

        final TextView investmentNumber; //　投資"金額"
        final TextView recoveryNumber;   //　回収"金額"
        final TextView totalNumber;     //　収支"金額"
        final TextView memo;            //　メモ"text"
        final TextView tenpo;           //　"店舗名"

        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        try {
            // レイアウトを調整
    //      layout(view);

            //　ウィジェットを取得する
            typeText = (TextView) view.findViewById(R.id.input_typeText);
            investmentNumber = (TextView) view.findViewById(R.id.input_investmentNumber);
            recoveryNumber = (TextView) view.findViewById(R.id.input_recoveryNumber);
            totalNumber = (TextView) view.findViewById(R.id.input_totalNumber);

            memo = (TextView) view.findViewById(R.id.input_typeText_memo);
            tenpo = (TextView) view.findViewById(R.id.input_typeText_tenpo);

        } catch (ClassCastException e) {
            Trace.e("BookAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "BookAdapter requires the resource ID to be a TextView", e);
        }

        ListViewItem listviewitem = this.getItem(position);

        // 機種名
        typeText.setText( listviewitem.getType() );

        //　金額関連
        investmentNumber.setText(AppSetting.separateComma( listviewitem.getInvestment() ) );
        recoveryNumber.setText(AppSetting.separateComma( listviewitem.getRecovery() ) );
        //　収支によって色を変える
        totalNumber.setText(AppSetting.separateComma( listviewitem.getTotal() ) );

        //　total収支によって色を変更する
        int _total = listviewitem.getTotal();

        // ±0
        if (_total == 0) {
            totalNumber.setTextColor(Color.BLACK);
        }
        // ＋収支
        else if (_total > 0) {
            totalNumber.setTextColor(Color.BLUE);
        }
        // －収支
        else {
            totalNumber.setTextColor(Color.RED);
        }

        //　メモを代入
        tenpo.setText(listviewitem.getTenpo());
        //　メモを代入
        memo.setText(listviewitem.getMemo());

        Trace.d("position = " + position);
        return view;
    }

    /**
    *<p>
    * layout調整
    *</p>
    */
    public void layout(View view) {
    /*
    //		int px;
        int px_x;
        int px_y;

    //		LinearLayout.LayoutParams ly;
        RelativeLayout.LayoutParams rl;

        ImageView img;
        TextView txt;

        // 列1
        {
            // 項目名 text
            txt = (TextView) view.findViewById(R.id.input_typeText_tenpo);
            txt.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(14f, AppSetting.getContentWidthRatio())));

            // 投資 image
            img = (ImageView) view.findViewById(R.id.input_investment_image);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp(48f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(20f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, px_y);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            img.setLayoutParams(rl);

            // 投資 text
            txt = (TextView) view.findViewById(R.id.input_investmentNumber);
            txt.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(14, AppSetting.getContentWidthRatio())));
        }
        // 列2
        {
            // 機種名 text
            txt = (TextView) view.findViewById(R.id.input_typeText);
            txt.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(14f, AppSetting.getContentWidthRatio())));

            // 回収 image
            img = (ImageView) view.findViewById(R.id.input_recovery_image);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp(48f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(20f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, px_y);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            img.setLayoutParams(rl);

            // 回収 text
            txt = (TextView) view.findViewById(R.id.input_recoveryNumber);
            txt.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(14f, AppSetting.getContentWidthRatio())));
        }

        // 列3
        {
            // メモ image
            img = (ImageView) view.findViewById(R.id.input_memo_image_01);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp(48f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(20f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, px_y);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            img.setLayoutParams(rl);

            // メモ image
            img = (ImageView) view.findViewById(R.id.input_memo_image_02);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp(48f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(20f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, px_y);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            img.setLayoutParams(rl);

            // 投資 text
            txt = (TextView) view.findViewById(R.id.input_typeText_memo);
            txt.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(16, AppSetting.getContentWidthRatio())));

            // 収支 image
            img = (ImageView) view.findViewById(R.id.input_total_image);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp(48f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(20f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, px_y);
            rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            img.setLayoutParams(rl);

            // 収支 text
            txt = (TextView) view.findViewById(R.id.input_totalNumber);
            txt.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (AppSetting.calcMyDp(14, AppSetting.getContentWidthRatio())));

        }

        // 矢印
        {
            // メモ image
            img = (ImageView) view.findViewById(R.id.arrow);
            px_x = AppSetting.getMypixel(
                    AppSetting.calcMyDp(24f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            px_y = AppSetting.getMypixel(
                    AppSetting.calcMyDp(60f, AppSetting.getContentWidthRatio()),
                    AppSetting.getDensity());
            rl = new RelativeLayout.LayoutParams(px_x, px_y);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rl.addRule(RelativeLayout.CENTER_VERTICAL);
            img.setLayoutParams(rl);
        }*/
    }

}
