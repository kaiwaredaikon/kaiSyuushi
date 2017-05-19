package jp.co.kaiwaredaikon320.syushi;

	/**
    *
    * 設定画面の項目作成用
    *
    */
	public class ListViewDrawAdapter{

		private String mItem;		//　項目

		private int mInvestment;	//　投資
		private int mRecovery;		//　回収
		private int mTotal;			//　収支

		private int mWin;			//　勝ち数
		private int mLose;			//　負け数
		private int mDraw;			//　引き分け

		private String mWord;		//　サーチワード

		// コンストラクタ
		public ListViewDrawAdapter( String item,
									int investment, int recovery, int total,
									int win, int lose, int draw
									){
			mItem = item;

			mInvestment = investment;
			mRecovery   = recovery;
			mTotal      = total;

			//　勝敗
			mWin  = win;
			mLose = lose;
			mDraw = draw;

			mWord = null;
		}

		// コンストラクタ
		public ListViewDrawAdapter( String item,
									int investment, int recovery, int total,
									int win, int lose, int draw,
									String word
									){
			mItem = item;

			mInvestment = investment;
			mRecovery   = recovery;
			mTotal      = total;

			//　勝敗
			mWin  = win;
			mLose = lose;
			mDraw = draw;

			mWord = word;
		}

		// 項目
		public void setItem( String item ) {
			mItem = item;
		}
		public String getItem() {
			return mItem;
		}

		//　投資額
		public void setInvestment( int investment ) {
			mInvestment = investment;
		}
		public int getInvestment() {
			return mInvestment;
		}
		// 回収
		public void setRecovery( int recovery ) {
			mRecovery = recovery;
		}
		public int getRecovery(){
			return mRecovery;
		}
		//　収支
		public void setTotal( int total ) {
			mTotal = total;
		}
		public int getTotal() {
			return mTotal;
		}


		// 勝ち
		public void setWin( int win ) {
			mWin = win;
		}
		public int getWin() {
			return mWin;
		}

		// 負け
		public void setLose( int lose ) {
			mLose = lose;
		}
		public int getLose() {
			return mLose;
		}

		// 引き分け
		public void setD( int draw ) {
			mDraw = draw;
		}
		public int getDraw() {
			return mDraw;
		}

		//　サーチワード
		protected void setWord( String word ) {
			mWord = word;
		}
		protected String getWord() {
			return mWord;
		}


	}
