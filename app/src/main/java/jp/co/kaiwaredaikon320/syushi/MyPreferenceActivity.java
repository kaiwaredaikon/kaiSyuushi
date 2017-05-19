package jp.co.kaiwaredaikon320.syushi;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class MyPreferenceActivity extends Activity{

	@Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace( android.R.id.content, new MyPreferenceFragment()).commit();

        // アイコン系を消す
        // タイトル非表示
//      getActionBar().setDisplayShowTitleEnabled(false);
        // アイコンを消す
        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_HOME);
        // Upアイコンの表示
        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		// アクションバーの戻るを押したときの処理
		if( id == android.R.id.home ){
	        finish( );
	        return true;
	    }
		return super.onOptionsItemSelected( item );
	}




}