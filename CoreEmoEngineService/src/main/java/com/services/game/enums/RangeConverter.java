package com.services.game.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RangeConverter implements AttributeConverter<ValueRanges, String> {

	@Override
	public String convertToDatabaseColumn(ValueRanges valueRanges) {
		return valueRanges.name();
	}

	@Override
	public ValueRanges convertToEntityAttribute(String valueRangeName) {
		for (ValueRangesDecimal valueRangesDecimal : ValueRangesDecimal.values()) {
			if (valueRangesDecimal.name().equals(valueRangeName)) {
				return valueRangesDecimal;
			}
		}
		
		for (ValueRangesInteger valueRangesInteger : ValueRangesInteger.values()) {
			if (valueRangesInteger.name().equals(valueRangeName)) {
				return valueRangesInteger;
			}
		}
		
		return null;
	}

}
