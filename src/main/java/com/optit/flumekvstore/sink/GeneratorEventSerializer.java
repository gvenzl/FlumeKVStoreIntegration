package com.optit.flumekvstore.sink;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Event;

/**
 * Apache Flume Event Serializer for generated keys
 * @author geraldvenzl
 *
 */
public class GeneratorEventSerializer implements NoSQLDBEventSerializer
{
	private String keyType;
	private String prefix;
	private SimpleKeyGenerator generator;
	
	@Override
	public void initialize(String keyType, String prefix)	{
		this.keyType = keyType;
		this.prefix = prefix;
		generator = new SimpleKeyGenerator();
	}

	@Override
	public Key getKey(Event event) 
	{
		switch (keyType)
		{
			case "random": return generator.getRandomKey(prefix);
			case "timestamp": return generator.getTimestampKey(prefix);
			case "nanotimestamp": return generator.getNanoTimestampKey(prefix);
			default: return null;
		}
	}

	@Override
	public Value getValue(Event event) {
		return Value.createValue(event.getBody());
	}

	@Override
	public void stop() {
	}

}
