package jp.co.kaiwaredaikon320.syushi;

/**
*
* ListViewの表示の中身用クラス
*
*/
public class ListViewItem{

    private long mID;			//　データベースのID

    private String mTenpo;		//　店舗
    private String mType;		//　機種
    private int mInvestment;	//　投資
    private int mRecovery;		//　回収
    private int mTotal;			//　収支
    private String mWord;		//　サーチワード
    private String mMemo;		//　メモ

    // コンストラクタ
    //	public ListViewItem( long id, String tenpo, String type, int investment, int recovery, int total ) {
    public ListViewItem( long id, String tenpo, String type, int investment, int recovery, int total, String memo ) {

        mID = id;
        mTenpo = tenpo;
        mType = type;
        mInvestment = investment;
        mRecovery   = recovery;
        mTotal      = total;
        mMemo      = memo;
        mWord      = null;
    }

    // コンストラクタ
    public ListViewItem( String type, int investment, int recovery, int total, String word ) {

        mID = -1;
        mTenpo = null;
        mType = type;
        mInvestment = investment;
        mRecovery   = recovery;
        mTotal      = total;
        mWord      = word;
        mMemo      = null;
    }


    // ID
    protected void setID( long id ) {
        mID = id;
    }
    public long getID() {
        return mID;
    }

    // 機種名
    protected void setTenpo( String tenpo ) {
        mTenpo = tenpo;
    }
    protected String getTenpo() {
        return mTenpo;
    }

    // 機種名
    protected void setType( String type ) {
        mType = type;
    }
    protected String getType() {
        return mType;
    }

    //　投資額
    protected void setInvestment( int investment ) {
        mInvestment = investment;
    }
    protected int getInvestment() {
        return mInvestment;
    }

    // 回収
    protected void setRecovery( int recovery ) {
        mRecovery = recovery;
    }
    protected int getRecovery(){
        return mRecovery;
    }

    //　収支
    protected void setTotal( int total ) {
        mTotal = total;
    }
    protected int getTotal() {
        return mTotal;
    }

    //　サーチワード
    protected void setWord( String word ) {
        mWord = word;
    }
    protected String getWord() {
        return mWord;
    }

    //　メモ
    protected void setMemo( String memo ) {
        mMemo = memo;
    }
    protected String getMemo() {
        return mMemo;
    }

    }