package jp.co.kaiwaredaikon320.syushi;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class WebviewActivity extends Activity{

	private FrameLayout m_BaseLayout = null;
	private WebView m_WebView = null;
	private LinearLayout m_ProgressView = null;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		// アクションバーの戻るを押したときの処理
		if( id == android.R.id.home ){
//	        finish( );
			closeWeb();
			return true;
	    }
		return super.onOptionsItemSelected( item );
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // タイトル非表示
//      getActionBar().setDisplayShowTitleEnabled(false);
        // アイコンを消す
		getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
//      getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_HOME);

        // Upアイコンの表示
        getActionBar().setDisplayHomeAsUpEnabled (true);

        setContentView(R.layout.layout_webview );
		//レイアウトで指定したWebViewのIDを指定する。
		m_BaseLayout = (FrameLayout)findViewById( R.id.base_layout );
		// WebView
		m_WebView = (WebView)m_BaseLayout.findViewById( R.id.web_view );
		// Progress
		m_ProgressView = (LinearLayout)m_BaseLayout.findViewById( R.id.web_progress );

		// JavaScript有効
		// JavaScriptを有効にする
		m_WebView.getSettings().setJavaScriptEnabled(true); // 脆弱性関連の警告が出ると思います。一応わざと警告が出るまま残しています。
		// Flashを有効にする
//		m_WebView.getSettings().setPluginsEnabled(true);

/*
		// Web Storageを有効にする
		m_WebView.getSettings().setDomStorageEnabled(true);
		// ズームを有効にする
		m_WebView.getSettings().setSupportZoom(true);
		m_WebView.getSettings().setBuiltInZoomControls(true);
		// ダブルタップでズームを有効にする
		m_WebView.getSettings().setUseWideViewPort(true);
		// キャッシュ設定
		m_WebView.getSettings().setAppCacheEnabled(true);
*/

		// リンク先で新しいブラウザで開かないようにする&プログレス表示
		m_WebView.setWebViewClient(
			new WebViewClient()
			{
				@Override
				public void onReceivedHttpAuthRequest( WebView webview, HttpAuthHandler handler, String host, String realm )
				{
					Trace.d("webviewActivity onReceivedHttpAuthRequest");
				}

				@Override
				public void onPageStarted( WebView webview, String weburl, Bitmap favicon )
				{
					Trace.d("webviewActivity onPageStarted");
					super.onPageStarted( webview, weburl, favicon );
					m_ProgressView.setVisibility( View.VISIBLE );
				}

				@Override
				public void onPageFinished( WebView webview, String weburl )
				{
					Trace.d("webviewActivity onPageFinished");
					super.onPageFinished( webview, weburl );
					m_ProgressView.setVisibility( View.INVISIBLE );
				}

				@Override
				public void onReceivedError( WebView view, int errorCode, String description, String failingUrl )
				{
					Trace.d("webviewActivity onReceivedError errorCode = " + errorCode + " description = " + description + " failingUrl = " + failingUrl  );
					// ネットワーク未接続エラー
//					m_Listener.onError( SPWMenuBarListener.ERR_NETNOTCONNECT );
				}

			    @Override
			    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
					Trace.d("webviewActivity onReceivedSslError");
			    }
			}
		);

		// キーリスナー
		m_WebView.setOnKeyListener(
			new OnKeyListener()
			{
				@Override
				public boolean onKey( View v, int key_code, KeyEvent event )
				{
					int ev_act = event.getAction();
					WebView webview = (WebView)v;

					// Backキーでウェブページを戻るようにする
					if( key_code == KeyEvent.KEYCODE_BACK )
					{
						if( ev_act == KeyEvent.ACTION_UP )
						{
							if( webview.canGoBack() )
							{
								webview.goBack();
							}
							else
							{
								// 閉じる
								closeWeb();
							}
						}

						return true;
					}

					return false;
				}
			}
		);
		// フォーカスを受け取る
		m_WebView.requestFocus( View.FOCUS_DOWN );

		// 読み込み
		m_WebView.loadUrl( AppSetting.APP_HELP_URL );

//		m_BaseLayout.addView( m_WebLayout );
	}

	/**
	 *	ウェブを閉じる
	 */
	private void closeWeb()
	{
		// nullチェック
		if(m_WebView != null){
			Trace.d("---------------> webviewの破棄 ");
			m_WebView.stopLoading();
			m_WebView.setWebChromeClient(null);
			m_WebView.setWebViewClient(null);
			// viewを消す
			m_BaseLayout.removeView(m_WebView);
			m_WebView.removeAllViews();
			m_WebView.destroy();
			m_WebView = null;
		}
		// ここに移動処理を追加する
        finish( );
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if( event.getAction() == KeyEvent.ACTION_DOWN ){

			switch (event.getKeyCode()) {
			// backキー
			case KeyEvent.KEYCODE_BACK:
			}
		}
		return super.dispatchKeyEvent(event);
	}


	@Override
	protected void onDestroy() {
		Trace.d("aaaaaaaaaaa onDestroy ");
		super.onDestroy();
	}

}

