package com.gvenzl.flumekvstore.sink;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Event;

/**
 * Apache Flume Event Serializer for keys passed on within the Flume event header.
 * @author gvenzl
 *
 */
public class HeaderEventSerializer implements NoSQLDBEventSerializer
{
	/**
	 * The header key for the retrieval of the NoSQL DB Key for the event.
	 */
	private String headerKey;
	
	/**
	 * The prefix to use for the key.
	 */
	private String keyPrefix;
	
	@Override
	public final void initialize(final String keyType, final String prefix)
	{
		headerKey = keyType;
		keyPrefix = prefix;
	}

	@Override
	public final Key getKey(final Event event)
	{
		String key = event.getHeaders().get(headerKey);
		if (null != keyPrefix && !keyPrefix.isEmpty()) {
			key = "/" + keyPrefix + key;
		}
		return Key.fromString(key);
	}

	@Override
	public final Value getValue(final Event event)
	{
		return Value.createValue(event.getBody());
	}

	@Override
	public void stop() {
	}

}
