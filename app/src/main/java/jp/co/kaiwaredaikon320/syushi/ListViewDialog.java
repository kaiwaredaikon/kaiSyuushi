package jp.co.kaiwaredaikon320.syushi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import java.util.ArrayList;
import java.util.List;
//import android.view.View.OnClickListener;


public class ListViewDialog extends Activity{

    // ID
    private final int LIST_ID_TENPO = 0;
    private final int LIST_ID_KISYU = 1;
    private final int LIST_ID_EVENT = 2;


    Context con;

    CustomAdapter adapter;
    ListView listView;
	AlertDialog dialog;

    ArrayList<Item> itemList_tenpo;
    ArrayList<Item> itemList_kisyu;
    ArrayList<Item> itemList_event;

    // コンストラクタ
	public ListViewDialog( Context context ){
		// contextの引継ぎ
		con = context;
		// リスト作成
		itemList_tenpo = new ArrayList<Item>();
		itemList_kisyu = new ArrayList<Item>();
        itemList_event = new ArrayList<Item>();
	}

	/**
		Itemを登録する
	**/
	public void setItem( int type, String txt ){
	    Item item = new Item();
	    item.setStringItem( txt );

		switch( type ){
            // 店舗
            case LIST_ID_TENPO:
                itemList_tenpo.add(item);
                break;
			// 機種
			case LIST_ID_KISYU:
		    	itemList_kisyu.add(item);
				break;
            // イベント
            case LIST_ID_EVENT:
                itemList_event.add(item);
                break;
		}
	}

    /**
        店舗情報の登録
     **/
    public void setItemTenpo( String txt, double ball, double slot ){
        Item item = new Item();

        // 項目
        item.setStringItem( txt );

        // 交換率をSetする
        item.setDoubleItem1(ball);
        item.setDoubleItem2(slot);

        //  リストにセットする
        itemList_tenpo.add(item);
    }

    /**
        機種データの登録
     **/
    public void setItemKisyu( String txt, int type ){
        Item item = new Item();

        // 項目
        item.setStringItem( txt );

        // 交換率をSetする
        item.setIntItem(type);

        //  リストにセットする
        itemList_kisyu.add(item);
    }





	/**
		店舗リストをダイアログで表示する
	**/
	public void createListViewDialog( int type, String label, final TextView txt, int width, int height ){

	    final ArrayList<Item> tmpList;

		switch( type ){
            // 店舗(0)
            case LIST_ID_TENPO:
                tmpList = itemList_tenpo;
                break;
			// 機種(1)
			case LIST_ID_KISYU:
				tmpList = itemList_kisyu;
				break;
            // 機種(2)
//            case LIST_ID_EVENT:
            default:
                tmpList = itemList_event;
                break;
		}

		adapter = new CustomAdapter(con.getApplicationContext(), 0, tmpList );
		listView = new ListView( con.getApplicationContext() );

        // 線の色を変更する
        ColorDrawable separate_line_color = new ColorDrawable( con.getApplicationContext().getResources().getColor(R.color.black) );
        listView.setDivider(separate_line_color);
        listView.setDividerHeight(2);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Item get = tmpList.get(position);
//        		txt.setText( get.getStringItem());
        		txt.setText( get.getStringItem(), BufferType.NORMAL );
            	// ダイアログ終了
            	dialog.dismiss();
            }
		});

		dialog = new AlertDialog.Builder( con, R.style.ListViewDialogStyle )
		.setTitle(label)
        .setNegativeButton( R.string.button_cancel, null)
		.setView(listView).create();

		dialog.show();

        // listViewのサイズを変更する
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width  = width;
        lp.height = height;
        dialog.getWindow().setAttributes(lp);
	}

    /**
        店舗リストをダイアログで表示する
     **/
    public void createListViewDialog( final int type, String label, final AddListActivity act, int width, int height ){

        final ArrayList<Item> tmpList;

        switch( type ){
            // 店舗(0)
            case LIST_ID_TENPO:
                tmpList = itemList_tenpo;
                break;
            // 機種(1)
            case LIST_ID_KISYU:
                tmpList = itemList_kisyu;
                break;
            // 機種(2)
//            case LIST_ID_EVENT:
            default:
                tmpList = itemList_event;
                break;
        }

        adapter = new CustomAdapter(con.getApplicationContext(), 0, tmpList );
        listView = new ListView( con.getApplicationContext() );

        // 線の色を変更する
        ColorDrawable separate_line_color = new ColorDrawable( con.getApplicationContext().getResources().getColor(R.color.black) );
        listView.setDivider(separate_line_color);
        listView.setDividerHeight(2);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item get = tmpList.get(position);

                switch( type ){
                    // 店舗(0)
                    case LIST_ID_TENPO:
                        // 交換率をセット
                        act.setTenpoItem( get.getStringItem(),get.getDoubleItem1(), get.getDoubleItem2() );
                        break;
                    // 機種(1)
                    case LIST_ID_KISYU:
                        act.setKisyuItem( get.getStringItem(), get.getintItem() );
                        break;
                    // 機種(2)
//            case LIST_ID_EVENT:
                    default:
//                        tmpList = itemList_event;
                        break;
                }

                // ダイアログ終了
                dialog.dismiss();
            }
        });

//        dialog = new AlertDialog.Builder( con, R.style.ListViewDialogStyle )
                dialog = new AlertDialog.Builder( this )
                .setTitle(label)
                .setNegativeButton( R.string.button_cancel, null)
                .setView(listView).create();

        dialog.show();

        // listViewのサイズを変更する
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.width  = width;
//        lp.height = height;
//        dialog.getWindow().setAttributes(lp);
    }

	// ListViewで使用するItemClass
    public class Item{

    	private String stringItem;
        private double doubleItem1;     // 玉の交換率
        private double doubleItem2;     // スロの交換率
        private int    intItem;         // 機種のタイプ

        //
        public void setStringItem(String stringItem){
            this.stringItem = stringItem;
        }
        public String getStringItem(){
            return this.stringItem;
        }

        // 店舗
        public void setDoubleItem1(double doubleItem1){
            this.doubleItem1 = doubleItem1;
        }
        public void setDoubleItem2(double doubleItem2){
            this.doubleItem2 = doubleItem2;
        }
        public double getDoubleItem1(){
            return this.doubleItem1;
        }
        public double getDoubleItem2(){
            return this.doubleItem2;
        }

        // 機種
        public void setIntItem(int intItem){
            this.intItem = intItem;
        }
        public int getintItem(){
            return this.intItem;
        }

    }

    // ListViewで使用するadapter
    public class CustomAdapter extends ArrayAdapter<Item>{
        private LayoutInflater inflater;

        public CustomAdapter(Context context, int resource, List<Item> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            Item item = (Item)getItem(position);
            if (null == v) v = inflater.inflate(R.layout.list_view_dialog_textview, null);

 // 削除
 /*
            TextView intTextView = (TextView)v.findViewById(R.id.int_item);
            intTextView.setText(item.getIntItem()+"");
*/
            TextView stringTextView = (TextView)v.findViewById(R.id.list_view_dialog_textview);
            stringTextView.setText(item.getStringItem());
            return v;
        }
    }

}
