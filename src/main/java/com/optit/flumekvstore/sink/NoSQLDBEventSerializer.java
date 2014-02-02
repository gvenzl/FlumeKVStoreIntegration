package com.optit.flumekvstore.sink;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Event;

public interface NoSQLDBEventSerializer
{
	/**
	 * Initializes the event serializer
	 * @param keyType The key type for the serializer
	 * @param prefix The key prefix to be used for the serializer
	 */
	public void initialize(String keyType, String prefix);
	
	/**
	 * Returns the key to be used for persistence
	 * @param event The event from which the key should be retrieved
	 * @return A new key to be used for persistence
	 */
	public Key getKey(Event event);
	
	/**
	 * Returns the value of the event to be persisted into the NoSQL DB
	 * @param event The event from which the value should be retrieved
	 * @return The value to be persisted into the NoSQL DB
	 */
	public Value getValue(Event event);
	
	/**
	 * Cleanup routine that will be called when the sink is stopped
	 */
	public void stop();
}
