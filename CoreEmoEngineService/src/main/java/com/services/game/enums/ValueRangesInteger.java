package com.services.game.enums;

public enum ValueRangesInteger {
	
	ZERO(0, 0), ONE(1, 1), ZERO_TO_FIVE(0, 5), FIVE_TO_TEN(5, 10), TEN_TO_FIFTEEN(10, 15), FIFTEEN_TO_TWENTY(15, 20),
	GREATER_THAN_TWENTY(20, 100);
	
	private Integer lowerLimit;
	private Integer higherLimit;
	
	ValueRangesInteger(Integer lowerLimit, Integer higherLimit) {
		this.lowerLimit = lowerLimit;
		this.higherLimit = higherLimit;
	}
	
	public Integer getLowerLimit() { return lowerLimit; }
	public Integer getHigherLimit() { return higherLimit; }

}
