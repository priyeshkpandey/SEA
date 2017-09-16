package com.services.game.enums;

public enum ValueRangesInteger implements ValueRanges<ValueRangesInteger> {
	
	ZERO(0l, 0l), ONE(1l, 1l), ZERO_TO_FIVE(0l, 5l), FIVE_TO_TEN(5l, 10l), TEN_TO_FIFTEEN(10l, 15l), FIFTEEN_TO_TWENTY(15l, 20l),
	GREATER_THAN_TWENTY(20l, 100l);
	
	private Long lowerLimit;
	private Long higherLimit;
	
	ValueRangesInteger(Long lowerLimit, Long higherLimit) {
		this.lowerLimit = lowerLimit;
		this.higherLimit = higherLimit;
	}
	
	public Long getLowerLimit() { return lowerLimit; }
	public Long getHigherLimit() { return higherLimit; }

}
