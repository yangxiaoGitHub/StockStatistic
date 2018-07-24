package cn.db.bean.ext;

import cn.com.ObjectUtils;
import cn.db.bean.StatisticStock;

public class ExtStatisticStock extends StatisticStock {
	private static final long serialVersionUID = -6186640571359530601L;

	private Integer upDownNumber;
	private Integer upNumber;
	private Integer downNumber;
	//ÕÇµø·ù
	private Double maxDeclineRate;
	private Double maxRiseRate;
	private Double nowChangeRate;
	private Double recentMaxDeclineRate;
	private Double recentMaxRiseRate;
	
	public ExtStatisticStock(StatisticStock statisticStock) {

		//ObjectUtils.copyProperties(this, statisticStock);
		ObjectUtils.copy_Properties(statisticStock, this);
	}
	
	public Double getMaxDeclineRate() {
		return maxDeclineRate;
	}

	public void setMaxDeclineRate(Double maxDeclineRate) {
		this.maxDeclineRate = maxDeclineRate;
	}

	public Double getMaxRiseRate() {
		return maxRiseRate;
	}

	public void setMaxRiseRate(Double maxRiseRate) {
		this.maxRiseRate = maxRiseRate;
	}

	public Double getNowChangeRate() {
		return nowChangeRate;
	}

	public void setNowChangeRate(Double nowChangeRate) {
		this.nowChangeRate = nowChangeRate;
	}

	public Double getRecentMaxDeclineRate() {
		return recentMaxDeclineRate;
	}

	public void setRecentMaxDeclineRate(Double recentMaxDeclineRate) {
		this.recentMaxDeclineRate = recentMaxDeclineRate;
	}

	public Double getRecentMaxRiseRate() {
		return recentMaxRiseRate;
	}

	public void setRecentMaxRiseRate(Double recentMaxRiseRate) {
		this.recentMaxRiseRate = recentMaxRiseRate;
	}

	public Integer getUpDownNumber() {
		return upDownNumber;
	}

	public void setUpDownNumber(Integer upDownNumber) {
		this.upDownNumber = upDownNumber;
	}

	public Integer getUpNumber() {
		return upNumber;
	}

	public void setUpNumber(Integer upNumber) {
		this.upNumber = upNumber;
	}

	public Integer getDownNumber() {
		return downNumber;
	}

	public void setDownNumber(Integer downNumber) {
		this.downNumber = downNumber;
	}
}
