package com.services.game.enums;

public enum ValueRangesDecimal {
	
	LESS_THAN_MINUS_ONE(-100.0, -1.0), MINUS_ONE_TO_MINUS_HALF(-1.0, -0.5), MINUS_HALF_TO_ZERO(-0.5, 0.0),
	ZERO_TO_HALF(0.0, 0.5), HALF_TO_ONE(0.5, 1.0), GREATER_THAN_ONE(1.0, 100.0);
	
	private Double lowerLimit;
	private Double higherLimit;
	
	ValueRangesDecimal(Double lowerLimit, Double higherLimit) {
		this.lowerLimit = lowerLimit;
		this.higherLimit = higherLimit;
	}

	public Double getLowerLimit() { return lowerLimit; }
	public Double getHigherLimit() { return higherLimit; }
}
