package jp.co.kaiwaredaikon320.syushi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.util.ArrayList;

public class DataPrefirenceFragment extends PreferenceFragment {

    public void onCreate( Bundle savedInstanceState ){

	    super.onCreate( savedInstanceState );
	    addPreferencesFromResource( R.xml.dataprefirence );

	    Preference prefMonth = findPreference( "dpre_key_month_syushi" );	// 0
	    Preference prefYear = findPreference( "dpre_key_year_syushi" );		// 1
	    Preference prefTotal = findPreference( "dpre_key_total_syushi" );	// 2

	    Preference prefTenpo = findPreference( "dpre_key_tenpo_syushi" );	// 3
	    Preference prefType = findPreference( "dpre_key_type_syushi" );		// 4

        // データ
		SQLiteDatabase syushiDatabase = ( new MySQLiteOpenHelper( getActivity() ) ).getWritableDatabase( );
		Cursor constantsCursor = syushiDatabase.rawQuery(
									    			"SELECT DISTINCT _id "+
									    			"FROM constants "+
									    			"ORDER BY _id",
									    			null );

		final int count = constantsCursor.getCount();

		constantsCursor.close();
        syushiDatabase.close();

		Trace.d( "getCount = " + count );

	    // 月別収支
	    prefMonth.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {

				// 一個以上の時のみ移行させる
				if( count > 1 ){
/*
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.putExtra( "idid", 0 );
					//　スタックに追加せずに起動
					//　その他オプション詳細はここ→http://www.saturn.dti.ne.jp/npaka/android/LaunchMode/
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
			        startActivity(intent);
*/
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.putExtra( "idid", 0 );
					//　スタックに追加せずに起動
					//　その他オプション詳細はここ→http://www.saturn.dti.ne.jp/npaka/android/LaunchMode/
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					MyAsyncTask task = new MyAsyncTask( getActivity(), getFragmentManager(), intent, new ArrayList<ListViewDrawAdapter>() );
					task.execute( 0 );
				}
				else{
					Toast.makeText( getActivity(), "データがありません", Toast.LENGTH_SHORT ).show( );
				}

				// 月間収支に飛ぶ
				return true;
			}
		});

	    //　年間収支
	    prefYear.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {

				// 一個以上の時のみ移行させる
				if( count > 1 ){
	/*
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 1 );
					startActivity(intent);
	*/
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 1 );
					MyAsyncTask task = new MyAsyncTask( getActivity(), getFragmentManager(), intent, new ArrayList<ListViewDrawAdapter>() );
					task.execute( 0 );
				}
				else{
					Toast.makeText( getActivity(), "データがありません", Toast.LENGTH_SHORT ).show( );
				}
				return true;
			}
		});

	    // 累計収支
	    prefTotal.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// 一個以上の時のみ移行させる
                if( count > 1 ){
	/*
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 2 );
					startActivity(intent);
	*/

					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 2 );
					MyAsyncTask task = new MyAsyncTask( getActivity(), getFragmentManager(), intent, new ArrayList<ListViewDrawAdapter>() );
					task.execute( 0 );
				}
				else{
					Toast.makeText( getActivity(), "データがありません", Toast.LENGTH_SHORT ).show( );
				}
				return true;
			}
		});

	    // 店舗別収支
	    prefTenpo.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {

				// 一個以上の時のみ移行させる
				if( count > 1 ){
	/*
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 3 );
					startActivity(intent);
	*/
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 3 );
					MyAsyncTask task = new MyAsyncTask( getActivity(), getFragmentManager(), intent, new ArrayList<ListViewDrawAdapter>() );
					task.execute( 0 );
				}
				else{
					Toast.makeText( getActivity(), "データがありません", Toast.LENGTH_SHORT ).show( );
				}
				return true;
			}
		});

	    // 機種別収支
	    prefType.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// 一個以上の時のみ移行させる
				if( count > 1 ){
	/*
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 4 );
					// トランジションアニメーションを行わずに起動
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
					startActivity(intent);
	*/
					Intent intent = new Intent( getActivity(), DrawDataActivity.class );
					intent.setFlags( Intent.FLAG_ACTIVITY_NO_HISTORY );
					intent.putExtra( "idid", 4 );
					MyAsyncTask task = new MyAsyncTask( getActivity(), getFragmentManager(), intent, new ArrayList<ListViewDrawAdapter>() );
					task.execute( 0 );
				}
				else{
					Toast.makeText( getActivity(), "データがありません", Toast.LENGTH_SHORT ).show( );
				}
				return true;
			}
		});
    }

}
