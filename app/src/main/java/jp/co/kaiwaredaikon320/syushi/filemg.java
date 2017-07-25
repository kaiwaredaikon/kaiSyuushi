package jp.co.kaiwaredaikon320.syushi;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class filemg {

	private Context Ct;
	private String FileName;
	private int Id;

	private InputStream InS;
	private DataInputStream DInS;

	private FileInputStream FInS;
	private BufferedInputStream BInS;

	private FileOutputStream FOutS;

	private String SDPath;

	private int AesFlg;


	private byte [] keytbl = {20,11,11,8,19,38,40,0,20,11,11,8,19,38,40,0};
	SecretKeySpec key;

	public filemg(Context context){
		Ct = context;
	}

	public void set(int id,String filename){

		close();

		Id = id;
		FileName = filename;
		AesFlg = 0;

		if (id == (2)){
			SDPath = Environment.getExternalStorageDirectory().getPath() + "/" + Ct.getPackageName();
			System.out.println("smmv:" + "sdpath" + SDPath + "?0");
		}

	}

	public void setaes(int f){

		AesFlg = f;

		if (f != 0){		//キー作成
			key = new SecretKeySpec(keytbl, "AES");
		}
	}

	public String getfilename(){
		return FileName;
	}

	//オープン
	public int open(){

		if (DInS != null){
			System.out.println("smmv:" + "opend:" + FileName + "?0");
			return 1;
		}

		try {
			if (Id == (3)){
				AssetManager Asm = Ct.getResources().getAssets();
				InS = Asm.open(FileName);
				DInS = new DataInputStream(InS);
			}
			else if (Id == (1)){

				if (FileName.indexOf('/') == -1){	//ディレクトリ無しはこちら
					FInS = Ct.openFileInput(FileName);
				}
				else {
					FInS = new FileInputStream(new File(Ct.getFilesDir(),FileName));
				}

				BInS = new BufferedInputStream(FInS);
				DInS = new DataInputStream(BInS);
			}
			else if (Id == (2)){

				FInS = new FileInputStream(SDPath + "/" + FileName);
				BInS = new BufferedInputStream(FInS);
				DInS = new DataInputStream(BInS);

			}
		} catch ( Exception e ){
			System.out.println("smmv:" + "open error:" + FileName + "?0");
			close();
			return 0;
		}

		return 1;

	}

	public void skipBytes(int n){
		try {
			DInS.skipBytes(n);
		} catch (Exception e) {}
	}

	public void readFully(byte[] b){
		try {
			DInS.readFully(b);
		} catch (Exception e) {}
	}

	public void readFully(byte[] b,int off,int len){
		try {
			DInS.readFully(b,off,len);
		} catch (Exception e) {}
	}

	public int read(byte[] b){

		int size = 0;

		try {
			size = DInS.read(b);
		} catch (Exception e) {}

		return size;
	}

	public int read(byte[] b,int off,int len){

		int size = 0;

		try {
			size = DInS.read(b,off,len);
		} catch (Exception e) {}

		return size;
	}

	public byte readByte(){

		byte d = 0;

		try {
			d = DInS.readByte();
		} catch (Exception e) {}

		return d;
	}

	public int readUnsignedByte(){

		int d = 0;

		try {
			d = DInS.readUnsignedByte();
		} catch (Exception e) {}

		return d;
	}

	public short readShort(){

		short d = 0;

		try {
			d = DInS.readShort();
		} catch (Exception e) {}

		return d;
	}

	public int readUnsignedShort(){

		int d = 0;

		try {
			d = DInS.readUnsignedShort();
		} catch (Exception e) {}

		return d;
	}

	public int readInt(){

		int d = 0;

		try {
			d = DInS.readInt();
		} catch (Exception e) {}

		return d;
	}

	//INT配列読み込み
	public void readFully(int[] dst){

		byte[] src = new byte [dst.length * 4];

		readFully(src);

		int i,k;
		int c;

		for (i = k = 0;i != dst.length;i++,k+=4){
			c = (src[k + 0] & 0xff);
			c += ((src[k + 1] << 8) & 0xff00);
			c += ((src[k + 2] << 16) & 0xff0000);
			c += ((src[k + 3] << 24) & 0xff000000);

			dst[i] = c;
		}

	}

	//クローズ
	public void close(){

		try {
			if (DInS != null)DInS.close();
			if (InS != null)InS.close();
			if (FInS != null)FInS.close();
			if (BInS != null)BInS.close();
		} catch (Exception e) {
			System.out.println("smmv:" + "close error:" + FileName + "?0");
		}

		DInS = null;
		InS = null;
		FInS = null;
		BInS = null;

	}

//============================================================
//		APP_FILE_FILEデータフォルダ用のFileクラス
//ディレクトリあり、無しで分ける
//============================================================

	private File GetFileFileClass(String path){

		File file;

		if (FileName.indexOf('/') == -1){	//ディレクトリ無しはこちら
			file = Ct.getFileStreamPath(path);
		}
		else {
			file = new File(Ct.getFilesDir(),path);
		}

		return file;

	}

//============================================================
//	ファイルクラスを取得
//============================================================

	public File GetFileClass(String path){

		File file = null;

		try {
			if (Id == (3)){
				return null;
			}
			else if (Id == (1)){
				file = GetFileFileClass(path);
			}
			else if (Id == (2)){

				file = new File(SDPath +  "/" + path);
			}
		} catch ( Exception e ){
			System.out.println("smmv:" + "file class error:" + FileName + "?0");
			return null;
		}

		return file;

	}

//============================================================
//	ファイルサイズを取得(open必要)
//============================================================

	public int available(){

		int oflg = 0;

		if (DInS == null){	//未openの場合
			open();
			oflg = 1;
		}

		int size = 0;

		try {
			size = DInS.available();
		} catch (Exception e) {}


		if (oflg != 0){		//openしたらcloseする
			close();
		}


		return size;
	}


//============================================================
//		ファイルの存在チェック openはいらない
//============================================================

	public boolean isexists(){

		boolean ret = false;
		File file;

		try {
			if (Id == (3)){
				if (DInS != null)ret = true;	//すでにオープンに成功している
				else {
					if (open() != 0)ret = true;	//試しにオープンする
					close();
				}
			}
			else if (Id == (1)){

				file = GetFileFileClass(FileName);
				ret = file.exists();
			}
			else if (Id == (2)){

				file = new File(SDPath +  "/" + FileName);
				ret = file.exists();
			}
		} catch ( Exception e ){
			System.out.println("smmv:" + "isexists error:" + FileName + ":" + e + "?0");
			return false;
		}

		System.out.println("smmv:" + "is exists:" + ret + "?0");

		return ret;
	}

//============================================================
//		書き込み openはいらない
//============================================================

	public int write(int[] src){

		int i,k;
		byte[] dst = new byte[src.length * 4];

		for (i = k = 0;i != src.length;i++,k+=4){
			dst[k+0] = (byte)(src[i] & 0xff);
			dst[k+1] = (byte)((src[i] & 0xff00) >> 8);
			dst[k+2] = (byte)((src[i] & 0xff0000) >> 16);
			dst[k+3] = (byte)((src[i] & 0xff000000) >> 24);
		}

		return write(dst);
	}

//============================================================
//		書き込み openはいらない
//============================================================

	public int write(byte[] b){

		if (writeopen() == 0)return 0;

		int size = write(b,b.length);

		if (writeclose() == 0)return 0;

		return size;
	}

//============================================================
//		書き込み オープン（部分書き込み用）
//============================================================

	public int writeopen(){

		int ret = 1;

		try {
			if (Id == (3)){
				System.out.println("smmv:" + "no support:" + FileName + "?0");
				return 0;
			}
			else if (Id == (1)){

				File file = new File(Ct.getFilesDir(),FileName);
				File subdir = file.getParentFile();

				System.out.println("smmv:" + "open:" + file + "?0");
				try{
					if(!subdir.exists()){
						subdir.mkdirs();
					}
				}catch(SecurityException ex){
					// 例外発生時の処理
					System.out.println("smmv:" + "dir.mkdir:Failed" + "?0");
				}

				if (FileName.indexOf('/') == -1){	//ディレクトリ無しはこちら
					FOutS = Ct.openFileOutput(FileName, Context.MODE_PRIVATE);
				}
				else {
					FOutS = new FileOutputStream(file,false);
				}

			}
			else if (Id == (2)){

				File file = new File(SDPath,FileName);
				File subdir = file.getParentFile();

				System.out.println("smmv:" + "dir:" + subdir + "?0");
				try{
					if(!subdir.exists()){
						subdir.mkdirs();
					}
				}catch(SecurityException ex){
					// 例外発生時の処理
					System.out.println("smmv:" + "dir.mkdir:Failed" + "?0");
				}

				FOutS = new FileOutputStream(file,false);	//上書きモード

			}
		} catch ( Exception e ){
			System.out.println("smmv:" + "write open error:" + FileName + "?0");

			ret = 0;
		}

		if (ret == 0){
			try {
				if (FOutS != null)FOutS.close();
				FOutS = null;
			} catch ( Exception e ){}
		}

		return ret;

	}


//============================================================
//		書き込み （部分書き込み用）
//============================================================

	public int write(byte[] b,int size){

		try {
			if (Id == (3)){
				System.out.println("smmv:" + "no support:" + FileName + "?0");
				return 0;
			}
			else if (Id == (1)){

				FOutS.write(b,0,size);

			}
			else if (Id == (2)){

				FOutS.write(b,0,size);

			}
		} catch ( Exception e ){
			System.out.println("smmv:" + "write error:" + FileName + "size:" + size + "?0");

			size = 0;
		}

		if (size == 0){
			try {
				if (FOutS != null)FOutS.close();
				FOutS = null;
			} catch ( Exception e ){}
		}

		return size;

	}

//============================================================
//		書き込み クロース（部分書き込み用）
//============================================================

	public int writeclose(){

		int ret = 1;

		try {
			if (Id == (3)){
				System.out.println("smmv:" + "no support:" + FileName + "?0");
				return 0;
			}
			else if (Id == (1)){

				FOutS.flush();
			}
			else if (Id == (2)){

				FOutS.flush();
			}
		} catch ( Exception e ){
			System.out.println("smmv:" + "write close error:" + FileName + "?0");

			ret = 0;
		}

		try {
			if (FOutS != null)FOutS.close();
			FOutS = null;
		} catch ( Exception e ){}

		return ret;

	}

//============================================================
//		ファイル削除
//============================================================

	public int delete(){

		File file;

		System.out.println("smmv:" + "delete file" + FileName + "?0");

		try {
			if (Id == (3)){
				System.out.println("smmv:" + "no support:" + FileName + "?0");
				return 0;
			}
			else if (Id == (1)){

				file = GetFileFileClass(FileName);
				if (file.exists()){
					file.delete();
				}
			}
			else if (Id == (2)){

				file = new File(SDPath + "/" +  FileName);
				if (file.exists()){
					file.delete();
				}
			}
		} catch ( Exception e ){
			System.out.println("smmv:" + "delete error:" + FileName + "?0");
			return 0;
		}

		return 1;

	}


//============================================================
//		zipファイル特定ファイルの解凍
//============================================================

	public byte[] UnZipData(String filename){

		byte[] d = null;
		String path = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream in = null;
		ZipInputStream  zis = null;
		ZipEntry ze = null;
		ZipFile zf = null;
		File file;

		System.out.println("smmv:" + "unzip:" + filename + "?0");

		if (Id == (3)){
			try {
				AssetManager am = Ct.getResources().getAssets();
				in = am.open(FileName, AssetManager.ACCESS_STREAMING);
				zis = new ZipInputStream(in);
				ze  = zis.getNextEntry();

				if (ze != null) {

					while ((ze = zis.getNextEntry()) != null) {
						if (filename.equals(ze.getName()) == false)continue;

						baos = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						int count;

						while ((count = zis.read(buffer)) != -1) {
							 baos.write(buffer, 0, count);
						}
						d = baos.toByteArray();

						break;
					}
					if (d == null){
						System.out.println("smmv:" + "zip no entry" + filename + "?0");
					}

					zis.closeEntry();
				}
				else {
					System.out.println("smmv:" + "zip no entry" + filename + "?0");
				}

			} catch (Exception e) {
				System.out.println("smmv:" + "zip err" + e + "?0");
				d = null;
			}

			try {
				if (baos != null)baos.close();
				if (in != null)in.close();
				if (zis != null)zis.close();
			}
			catch ( Exception e ){}

		}
		else {

			if (Id == (1)){
				file = GetFileFileClass(FileName);
			}
			else if (Id == (2)){
				file = new File(SDPath +  "/" + FileName);

			}
			else return null;

			if (AesFlg != 0)filename += ".a";

			try{
				zf = new ZipFile( file );
				ze = zf.getEntry( filename );
				in = zf.getInputStream( ze );

				byte[] buffer = new byte[ 1024*4 ];    // 1024*4 = DEFAULT BUFFER SIZE
				int r = 0;

				while( -1 != ( r = in.read(buffer)) ){
					 baos.write(buffer, 0, r);
				}
				d = baos.toByteArray();

			}catch( Exception e ){
				System.out.println("smmv:" + "error:" + filename + "?0");
				System.out.println("smmv:" + "error:" + e + "?0");
				d = null;
			}

			try {
				if (baos != null)baos.close();
				if (in != null)in.close();
				if (zf != null)zf.close();

			}
			catch ( Exception e ){}

		}

//		DPRINT("unzip:" + d.length);

		//暗号化している場合は戻す
		if (d != null && AesFlg != 0){
			d = decode(d,key);
		}

		return d;

	}

	//複合化
	private byte[] decode(byte[] src, Key skey) {

		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			final int BLOCK_SIZE = cipher.getBlockSize();

			AlgorithmParameters iv = AlgorithmParameters.getInstance("AES");
			byte[] ib = new byte[2 + BLOCK_SIZE];
			ib[0] = 4; 		// getEncoded()で取った値がこういう数字になっている
			ib[1] = (byte) BLOCK_SIZE; // 動きはするけど、これで正しいのかどうかは不明
			System.arraycopy(src, 0, ib, 2, BLOCK_SIZE);
			iv.init(ib);

			cipher.init(Cipher.DECRYPT_MODE, skey, iv);
			return cipher.doFinal(src, BLOCK_SIZE, src.length - BLOCK_SIZE);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

//============================================================
//		zipファイル特定ファイルの解凍
//============================================================

	public byte[] UnZipData(String filename,String savefile){

		byte[] d = null;
		String path = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream in = null;
		ZipInputStream  zis = null;
		ZipEntry ze = null;
		ZipFile zf = null;
		File file,sfile;

		FileOutputStream sout = null;

		System.out.println("smmv:" + "unzip:" + filename + "?0");

		if (Id == (3)){
			try {
				AssetManager am = Ct.getResources().getAssets();
				in = am.open(FileName, AssetManager.ACCESS_STREAMING);
				zis = new ZipInputStream(in);
				ze  = zis.getNextEntry();

				if (ze != null) {

					while ((ze = zis.getNextEntry()) != null) {
						if (filename.equals(ze.getName()) == false)continue;

						baos = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						int count;

						while ((count = zis.read(buffer)) != -1) {
							baos.write(buffer, 0, count);
						}
						d = baos.toByteArray();

						break;
					}
					if (d == null){
						System.out.println("smmv:" + "zip no entry" + filename + "?0");
					}

					zis.closeEntry();
				}
				else {
					System.out.println("smmv:" + "zip no entry" + filename + "?0");
				}

			} catch (Exception e) {
				System.out.println("smmv:" + "zip err" + e + "?0");
				d = null;
			}

			try {
				if (baos != null)baos.close();
				if (in != null)in.close();
				if (zis != null)zis.close();
			}
			catch ( Exception e ){}

		}
		else {

			if (Id == (1)){
				file = GetFileFileClass(FileName);
			}
			else if (Id == (2)){
				file = new File(SDPath +  "/" + FileName);
			}
			else return null;

			if (AesFlg != 0)filename += ".a";

			try{
				zf = new ZipFile( file );
				ze = zf.getEntry( filename );
				in = zf.getInputStream( ze );

				sfile = new File(SDPath,savefile);
				sout = new FileOutputStream(sfile,false);	//上書きモード

				byte[] buffer = new byte[ 1024*4 ];    // 1024*4 = DEFAULT BUFFER SIZE
				int r = 0;

				while( -1 != ( r = in.read(buffer)) ){
					sout.write(buffer,0,r);
				}

				sout.flush();

			}catch( Exception e ){
				System.out.println("smmv:" + "error:" + filename + "?0");
				System.out.println("smmv:" + "error:" + e + "?0");
				d = null;
			}

			try {
				if (sout != null)sout.close();
				if (baos != null)baos.close();
				if (in != null)in.close();
				if (zf != null)zf.close();

			}
			catch ( Exception e ){}

		}

//		DPRINT("unzip:" + d.length);

		//暗号化している場合は戻す
		if (d != null && AesFlg != 0){
			d = decode(d,key);
		}

		return d;
	}

}
