package cn.db.bean.ext;

import cn.db.bean.DailyStock;

public class ExtDailyStock extends DailyStock {

	private static final long serialVersionUID = -7097863213818845919L;
    // 绝对低量: 小于1%
	public static final String LESS_ONE = "lessOne";
	// 成交低靡：1%-2%
	public static final String ONE_TO_TWO = "oneToTwo";
	// 成交温和：2%-3%
	public static final String TWO_TO_THREE = "twoToThree";
	// 成交活跃：3%-5%(相对活跃状态)
	public static final String THREE_TO_FIVE = "threeToFive";
	// 带量：5%-8%
	public static final String FIVE_TO_EIGHT = "fiveToEight";
	// 放量：8%-15%(高度活跃状态)
	public static final String EIGHT_TO_FIFTEEN = "eightToFifteen";
	// 巨量：15%-25%
	public static final String FIFTY_TO_TWENTY_FIVE = "fiftyToTwentyFive";
	// 成交怪异：大于25%
	public static final String LARGER_TWENTY_FIVE = "largerTwentyFive";
}
