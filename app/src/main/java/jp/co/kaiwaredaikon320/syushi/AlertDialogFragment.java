package jp.co.kaiwaredaikon320.syushi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

	// getSimpleName → ソースコードに記述された単純名（パッケージ名などを除外した名前）を返す。
	private static final String TAG = AlertDialogFragment.class.getSimpleName();

	private String mTitleStr;
	private String mMesStr;

	public AlertDialogFragment() {
		mTitleStr = "内部ストレージエラー";
		mMesStr = "内部ストレージの容量確保に失敗しました。\n" +
				"内部ストレージの容量を確保してからもう一度お試しください。";
	}
	/**
	 * @param titleStr title文言
	 * @param mesStr ダイアログ文言
	 */
	@SuppressLint("ValidFragment")
    public AlertDialogFragment(String titleStr, String mesStr) {
		mTitleStr = titleStr;
		mMesStr = mesStr;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// アラートダイアログの生成
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(mTitleStr)
				.setMessage(mMesStr)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ダイアログ終了
						dialog.dismiss();
					}
				});
		/*
				    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				        	// Cancelがクリックされた時の処理
				        	Toast.makeText(getActivity(), "Cancelクリック", Toast.LENGTH_SHORT).show();
				        }
				    })
				    .setNeutralButton("Neutral", new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				            // Newtralがクリックされた時の処理
				        	Toast.makeText(getActivity(), "Neutralクリック", Toast.LENGTH_SHORT).show();
				        }
				    });
		*/
		return builder.create();
	}

	/*
	public static final void show(FragmentManager manager) {
		AlertDialogFragment dialog = new AlertDialogFragment();
		dialog.show(manager, TAG);
	}

	public static final void update(FragmentManager manager) {
		Trace.d("AleartDialog update");

		Fragment temp = manager.findFragmentByTag(TAG);

		// instanceof	var instanceof type
		// varには変数名を、typeにはクラスやインターフェイス名を指定します
		// varがtypeで指定されたクラスやインターフェイスを実装していればtrueが返されます
		// 実装していなければfalseが返されます
		if (temp instanceof AlertDialogFragment) {
			AlertDialogFragment dialog = (AlertDialogFragment) temp;

			// タイトル変更、ChoicePosition調整、リストデータ更新、etc...
			dialog.getDialog().setTitle("変更！");
		}
	}
*/
}
