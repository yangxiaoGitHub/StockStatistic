package cn.db.bean.ext;

import cn.db.bean.DailyStock;

public class ExtDailyStock extends DailyStock {

	private static final long serialVersionUID = -7097863213818845919L;
    // ���Ե���: С��1%
	public static final String LESS_ONE = "lessOne";
	// �ɽ����ң�1%-2%
	public static final String ONE_TO_TWO = "oneToTwo";
	// �ɽ��ºͣ�2%-3%
	public static final String TWO_TO_THREE = "twoToThree";
	// �ɽ���Ծ��3%-5%(��Ի�Ծ״̬)
	public static final String THREE_TO_FIVE = "threeToFive";
	// ������5%-8%
	public static final String FIVE_TO_EIGHT = "fiveToEight";
	// ������8%-15%(�߶Ȼ�Ծ״̬)
	public static final String EIGHT_TO_FIFTEEN = "eightToFifteen";
	// ������15%-25%
	public static final String FIFTY_TO_TWENTY_FIVE = "fiftyToTwentyFive";
	// �ɽ����죺����25%
	public static final String LARGER_TWENTY_FIVE = "largerTwentyFive";
}
