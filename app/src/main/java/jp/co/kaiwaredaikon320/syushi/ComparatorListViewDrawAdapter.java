package jp.co.kaiwaredaikon320.syushi;

import java.util.Comparator;

class ComparatorListViewDrawAdapter implements Comparator<ListViewDrawAdapter>{

	private static final int ASC = 1;   //昇順 (1.2.3....)
	private static final int DESC = -1; //降順 (3.2.1....)

    private int sortType;

	/**
	 * <p>
	 * ソートを降順か昇順か指定
	 * </p>
	 * @param type 0→　降順　1→昇順　
	 */
	public ComparatorListViewDrawAdapter( int type ) {

		// 降順
		if( type== 0){
			sortType = DESC;
		}
		else{
			sortType = ASC;
		}
	}


	@Override
	public int compare(ListViewDrawAdapter lhs, ListViewDrawAdapter rhs) {
        if (lhs == null && rhs == null) {
            return 0;
        } else if (lhs == null) {
            return 1 * sortType;
        } else if (rhs == null) {
            return -1 * sortType;
        }
        return (lhs.getTotal() - rhs.getTotal()) * sortType;
	}
}
