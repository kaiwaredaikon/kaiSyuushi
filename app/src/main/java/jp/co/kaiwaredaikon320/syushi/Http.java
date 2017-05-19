package jp.co.kaiwaredaikon320.syushi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


class Http implements Runnable
{
	public static final int STATUS_DOING   = 0;
	public static final int STATUS_DONE_OK = 1;
	public static final int STATUS_DONE_ER = 2;
	public static final int STATUS_DONE_FILEOVER = 3;

	private static Http http = new Http();
	private static int status = STATUS_DONE_ER;
	private static String toUrl;
	private static byte[] toPost;
	private static byte[] downloaded;

	public static int rcode;
	public static int maxsize,nsize;

	public static int stopflg;

	private static filemg file;

	public static String reshead;

	static void start(String url,byte[] post,filemg fm)
	{

		file = fm;

		status = STATUS_DOING;
		toUrl = url;
		toPost = post;

		stopflg = 0;
		maxsize = 0;
		new Thread(http).start();
	}

	static void start(String url,byte[] post)
	{

		start(url,post,(filemg)null);

	}

	static void retry()
	{
		status = STATUS_DOING;

		stopflg = 0;
		maxsize = 0;
		new Thread(http).start();
	}

	static boolean isDone()
	{
		return status != STATUS_DOING;
	}

	static boolean isOK()
	{
		return status == STATUS_DONE_OK;
	}

	static boolean isFileWriteErr()
	{
		return status == STATUS_DONE_FILEOVER;
	}

	static byte[] get()
	{
		byte[] b =  downloaded;
		downloaded = null;
		return b;
	}

	static byte[] getErr()
	{
	    String s = "Response" + rcode;
		byte[] b =  s.getBytes();
		downloaded = null;
		return b;
	}

	public void run()
	{

		int st = STATUS_DOING;

		HttpURLConnection  c = null;
		OutputStream  ps = null;
		InputStream is = null;
		ByteArrayOutputStream os = null;

		Trace.d("syushi:" + "run http = " + toUrl + "?0");

		reshead = "";

		try {

			URL url = new URL(toUrl);
			c = (HttpURLConnection)url.openConnection();

			if ( toUrl.startsWith("https")) {
				c = (HttpsURLConnection)url.openConnection();
				Trace.d("syushi:" + "https" + "?0");
			}
			else {
				c = (HttpURLConnection)url.openConnection();
			}

			String ua = System.getProperty("http.agent");
			c.setRequestProperty("User-Agent",ua);

			// コネクションタイムアウトを設定
			c.setConnectTimeout(1000*10);

			if (toPost == null) {
				c.setRequestMethod("GET");
				c.connect();
			}
			else {
				Trace.d("syushi:" + "post:" + toPost + "?0");

				c.setRequestMethod("POST");

				c.setDoOutput(true);// POSTでデータ送信

				c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

				ps = c.getOutputStream();
				ps.write(toPost);
				ps.flush();
				ps.close();
			}

			rcode = c.getResponseCode();

			reshead = c.getHeaderField("X-SANYO-API");			//X-Sanyo-R-Api");		//"X-SANYO-R-API"
			Trace.d("syushi:" + "res head:" + reshead + "?0");

			if (rcode != HttpURLConnection.HTTP_OK) {
				Trace.d("syushi:" + "http err ex" + "?0");
				throw new IOException();
			}

			maxsize = (int)c.getContentLength();
			nsize = 0;

			byte[] w = new byte[8*1024];
			Trace.d("syushi:" + "f size:" + maxsize + "?0");
			is = c.getInputStream();

			if (file == null){
				os = new ByteArrayOutputStream();
			}
			else {
				file.writeopen();
			}

			int size;

			while(true){
				if (stopflg != 0)break;
				size = is.read(w);
				if (size <= 0)break;

				if (file == null){
					os.write(w,0,size);
				}
				else {
					int ret = file.write(w,size);
					if (ret == 0){
						st = STATUS_DONE_FILEOVER;	//書き込み失敗
						break;
					}
				}

				nsize += size;
				try {
					Thread.yield();
				} catch ( Exception e ){}
			}
			w = null;

			is.close();
			c.disconnect();

			if (file == null){
				downloaded = os.toByteArray();
				os.close();
			}
			else {
				file.writeclose();
			}

			if (stopflg == 0 && maxsize == nsize){	//最後まで読んだ
				st = STATUS_DONE_OK;
			}
			else if (st == STATUS_DOING){
				st = STATUS_DONE_ER;	//何かしらで停止
			}

		} catch(IOException e) {
			e.printStackTrace();
			st = STATUS_DONE_ER;
			Trace.d("syushi:" + "http err" + "?0");
		} catch ( Exception e ){
			Trace.d("syushi:" + "http err3" + e + "?0");
			st = STATUS_DONE_ER;
		}

		try {
			if (c != null)c.disconnect();
			if (is != null)is.close();
			if (os != null)os.close();
			if (ps != null)ps.close();

			if (file != null)file.writeclose();

		} catch ( Exception e ){
		}

		if (file != null){
			if (st != STATUS_DONE_OK){
				file.delete();
			}
		}

		c = null;
		is = null;
		os = null;
		ps = null;

		status = st;

	}

	static void ignoreValidateCertification(HttpsURLConnection httpsconnection) throws NoSuchAlgorithmException, KeyManagementException {

		KeyManager[] km = null;

		TrustManager[] tm = { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			    throws CertificateException {
				}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			    throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		}
		};

		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(km, tm, new SecureRandom());
		httpsconnection.setSSLSocketFactory(sslcontext.getSocketFactory());

	}


}
