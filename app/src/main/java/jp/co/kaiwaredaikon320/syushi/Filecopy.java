package jp.co.kaiwaredaikon320.syushi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

public class Filecopy{

	private Context con;

    // 階層
    public static final String FILE_DIR2 = "/Android/data/jp.co.kaiwaredaikon320.syushi/cache";
    public static final String FILE_DIR = "収太郎";

    // コンストラクタ
	public Filecopy( Context context ){
		// contextの引継ぎ
		con = context;
	}

	/**
	 *<p>
	 *ファイルのコピー（チャネルを使用）
	 *</p>
	 * @throws Exception
	 */
    public void filecopy( String file_src, String file_dist, String err_mes, String success_mes, int type ) throws Exception {

		int err;

		String txt = null;
		FileInputStream fis;
		FileOutputStream fos;

		err=0;

		try {

			File fi = new File( file_src );
			File fo = new File( file_dist );

			fis = new FileInputStream(fi);
			FileChannel chi = fis.getChannel( );

			fos = new FileOutputStream(fo);
			FileChannel cho = fos.getChannel( );

			long sendsize = chi.transferTo( 0, chi.size( ), cho );
			long chisize = chi.size( );

			fis.close();
			fos.close();

			chi.close( );
			cho.close( );

			Trace.d( "sendsize = " + sendsize );
			Trace.d( "chisize = " + chisize );

			// しっかりとデータが転送できたかチェック
			if( sendsize != chisize ){
				throw new Exception( );
			}

		}
		catch( FileNotFoundException e ){
//			err=1;
//			txt = err_mes + " code:"+err;
			throw new FileNotFoundException( );
		}
/*
		catch( IOException e ){
			err=2;
			txt = "" + err_mes + " code:"+err;
		}
*/
		catch( Exception e ){
			throw new Exception( );
		}

		// 成功
		if(err==0) {
			txt = "" + success_mes;
			// 復元時
			if( type == 1 ){
			    MyPrefSetting.setDataRestorationFlg( true );
			}
			// toast表示
			Toast.makeText( con.getApplicationContext(), txt, Toast.LENGTH_LONG).show();
		}
//		Toast.makeText( con.getApplicationContext(), txt, Toast.LENGTH_LONG).show();
	}


 // SDカードのマウント先をゲットするメソッド
    public static String getMount_sd() {
       List<String> mountList = new ArrayList<String>();
       String mount_sdcard = null;

       Scanner scanner = null;
       try {
          // システム設定ファイルにアクセス
          File vold_fstab = new File("/system/etc/vold.fstab");
          scanner = new Scanner(new FileInputStream(vold_fstab));
          // 一行ずつ読み込む
          while (scanner.hasNextLine()) {
             String line = scanner.nextLine();
             // dev_mountまたはfuse_mountで始まる行の
             if (line.startsWith("dev_mount") || line.startsWith("fuse_mount")) {
                // 半角スペースではなくタブで区切られている機種もあるらしいので修正して
                // 半角スペース区切り３つめ（path）を取得
                String path = line.replaceAll("\t", " ").split(" ")[2];
                // 取得したpathを重複しないようにリストに登録
                if (!mountList.contains(path)){
                   mountList.add(path);
                }
             }
          }
       } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
       } finally {
          if (scanner != null) {
             scanner.close();
          }
       }

       // Environment.isExternalStorageRemovable()はGINGERBREAD以降しか使えない
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
          // getExternalStorageDirectory()が罠であれば、そのpathをリストから除外
          if (!Environment.isExternalStorageRemovable()) {   // 注1
             mountList.remove(Environment.getExternalStorageDirectory().getPath());
          }
       }

       // マウントされていないpathは除外
       for (int i = 0; i < mountList.size(); i++) {
          if (!isMounted(mountList.get(i))){
             mountList.remove(i--);
          }
       }

       // 除外されずに残ったものがSDカードのマウント先
       if(mountList.size() > 0){
          mount_sdcard = mountList.get(0);
       }

       // マウント先をreturn（全て除外された場合はnullをreturn）
       return mount_sdcard;
    }


    // 引数に渡したpathがマウントされているかどうかチェックするメソッド
    public static boolean isMounted(String path) {
       boolean isMounted = false;

       Scanner scanner = null;
       try {
          // マウントポイントを取得する
          File mounts = new File("/proc/mounts");   // 注2
          scanner = new Scanner(new FileInputStream(mounts));
          // マウントポイントに該当するパスがあるかチェックする
          while (scanner.hasNextLine()) {
             if (scanner.nextLine().contains(path)) {
                // 該当するパスがあればマウントされているってこと
                isMounted = true;
                break;
             }
          }
       } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
       } finally {
          if (scanner != null) {
          scanner.close();
          }
       }

       // マウント状態をreturn
       return isMounted;
    }

}