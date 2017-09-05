package com.services.game.enums;

public interface ValueRanges<T extends Enum<T>> {

	String name();
	public Number getLowerLimit();
	public Number getHigherLimit();
}
