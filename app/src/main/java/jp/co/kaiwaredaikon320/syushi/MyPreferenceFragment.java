package jp.co.kaiwaredaikon320.syushi;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.io.File;


/**
* PreferenceFragmentを継承したクラス
* 個々で定義したPreferenceのリソースを設定します
*/
public class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    // プリファレンスの取得
    SharedPreferences pref;

    //
    Preference pref_tani;

    // key取得
    CharSequence keyId_tani;

    CharSequence keyId_destination;

    @SuppressLint("NewApi")
	@Override
    public void onCreate( Bundle savedInstanceState ){
	    super.onCreate( savedInstanceState );
	    addPreferencesFromResource( R.xml.preference );

		// プリファレンスの取得
        pref = PreferenceManager.getDefaultSharedPreferences( getActivity() );

        // key取得
        keyId_tani = getText(R.string.pref_key_tani);
        keyId_destination = getText(R.string.pref_key_destination );

        // findPreference
        pref_tani = findPreference(keyId_tani);

        // サマリー・プリファレンスの更新
        setSummaryAndPreference( keyId_tani, 0 );

	//　バックアップパス変更
		CharSequence[] entries;
		CharSequence[] entryValues;

		boolean typef = false;

		//
		ListPreference lp = (ListPreference)findPreference( "pref_key_destination" );

    	// Kitkat以上のみの処理
    	if( Build.VERSION.SDK_INT >= 19 ){

    		// 外部ストレージのパスを取得
    	    File[] extDirs = getActivity().getExternalCacheDirs( );

    	    // SDカードあり
    	    if( extDirs[ ( extDirs.length - 1 ) ] != null ){
    	    	typef = true;
    	    }
    	    // SDなし
    	    else{
    	    	typef = false;
    	    }
    	}
    	// Jerrybeans以下
    	else{
    	    // SDカードがない場合
    	    if( Filecopy.getMount_sd() != null ){
    	    	typef = true;
    	    }
    	    // SDあり
    	    else{
    	    	typef = false;
    	    }
    	}

    	// SDカード有り
    	if( typef == true ){

        	String[] entries2 = { "内部ストレージ", "SDカード"  };
        	String[] entryValues2 = { "0", "1" };

        	entries =  entries2;
        	entryValues =  entryValues2;

        	lp.setValue( "1" );
            MyPrefSetting.setDestination( 1 );
    	}
    	// SDカードなし
    	else{
        	String[] entries2 = { "内部ストレージ" };
        	String[] entryValues2 = { "0" };

        	entries =  entries2;
        	entryValues =  entryValues2;

        	lp.setValue("0");

            MyPrefSetting.setDestination( 0 );
    	}

    	// サマリーセット
    	lp.setSummary( entries[ Integer.parseInt( lp.getValue() ) ]);

		// エントリーセット
		lp.setEntries( entries );
		lp.setEntryValues( entryValues );

    }
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		Trace.d(" onSharedPreferenceChanged key = " + key );
		// プリファレンスに保存されたデータが更新されたら、値をSummaryに反映する
		if( key.equals( keyId_tani ) ){
			setSummaryAndPreference( keyId_tani, 0 );
		}
		else if( key.equals( keyId_destination ) ){
			setSummaryAndPreference( keyId_destination, 3 );
		}
	}

	// サマリー・プリファレンスを更新
	private void setSummaryAndPreference( CharSequence key, int type ) {

    	ListPreference prefFraction = (ListPreference)findPreference( key );

    	Trace.d(" setSummaryAndPreference ");

    	String getText = (String)prefFraction.getEntry();
    	String getVal = (String)prefFraction.getValue();

    	Trace.d("key = " + key +  " entry = " + getText + " getVal = " + getVal + " type = " + type  );


    	int data = Integer.parseInt( getVal );

        // Summary
        prefFraction.setSummary( getText );

        switch(type){
        	case 0:
                MyPrefSetting.setRate( data );
				MyPrefSetting.setDataRestorationFlg( true );
        		break;
        	case 1:
                MyPrefSetting.setSeekBarRate( data );
        		break;
        	case 2:
                MyPrefSetting.setSeekBarMax( data );
        		break;
        	case 3:
                MyPrefSetting.setDestination( data );
        		break;

        }

        SharedPreferences.Editor prefEditor = pref.edit();
    	prefEditor.putString( MyPrefSetting.key[ type ], "" + data );
        prefEditor.commit();

    }


}