package com.gvenzl.flumekvstore.sink;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Event;

/**
 * Apache Flume Event Serializer for generated keys.
 * @author gvenzl
 *
 */
public class GeneratorEventSerializer implements NoSQLDBEventSerializer
{
	/**
	 * The generation type of the key.
	 */
	private String keyType;
	/**
	 * The prefix for the key.
	 */
	private String prefix;
	/**
	 * The generator for the key.
	 */
	private SimpleKeyGenerator generator;
	
	@Override
	public final void initialize(final String type, final String pref)	{
		keyType = type;
		prefix = pref;
		generator = new SimpleKeyGenerator();
	}

	@Override
	public final Key getKey(final Event event) 
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
	public final Value getValue(final Event event) {
		return Value.createValue(event.getBody());
	}

	@Override
	public void stop() {
	}

}
