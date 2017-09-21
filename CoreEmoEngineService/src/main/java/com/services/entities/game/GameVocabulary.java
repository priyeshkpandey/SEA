package com.services.entities.game;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import com.services.game.enums.RangeConverter;
import com.services.game.enums.ValueRanges;

@SuppressWarnings("serial")
@Entity
@Table(name="game_vocabulary")
@Proxy(lazy=false)
public class GameVocabulary implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	Long id;
	
	@Column(name="variable")
	String variable;
	
	@Column(name="value_type")
	String valueType;
	
	@Column(name="range")
	@Convert(converter = RangeConverter.class)
	ValueRanges range;
	
	@Column(name="statement")
	String statement;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public ValueRanges getRange() {
		return range;
	}

	public void setRange(ValueRanges range) {
		this.range = range;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}
	
	

}
