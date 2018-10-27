package cn.db.bean.ext;

import cn.db.bean.AllImportStock;

public class ExtAllImportStock extends AllImportStock {

	private static final long serialVersionUID = 3112539341754110984L;
	//1亿-50亿
	public static final String ONE_TO_FIFTY = "oneHmToFiftyHm";
	//50亿-100亿
	public static final String FIFTY_TO_ONE_HUNDRED = "fiftyHmToOneHundredHm";
	//100亿-300亿
	public static final String ONE_HUNDRED_TO_THREE_HUNDRED = "oneHundredHmToThreeHundredHm";
	//300亿-600亿
	public static final String THREE_HUNDRED_TO_SIX_HUNDRED = "threeHundredHmToSixHundredHm";
	//600亿-1000亿
	public static final String SIX_HUNDRED_TO_ONE_THOUSAND = "sixHundredHmToOneThousandHm";
	//1000亿-4000亿
	public static final String ONE_THOUSAND_TO_FOUR_THOUSAND = "oneThousandHmToFourThousandHm";
	//4000亿-7000亿
	public static final String FOUR_THOUSAND_TO_SEVEN_THOUSAND = "fourThousandHmToSevenThousandHm";
	//7000亿-13000亿
	public static final String SEVEN_THOUSAND_TO_THIRTEEN_THOUSAND = "sevenThousandHmToThirteenThousandHm";
	//13000亿-18000亿
	public static final String THIRTEEN_THOUSAND_TO_EIGHTEEN_THOUSAND = "thirteenThousandHmToEighteenThousandHm";
	//18000亿以上
	public static final String GREATER_EIGHTEEN_THOUSAND = "greaterEighteenThousandHm";
}
