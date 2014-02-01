package com.optit.flumekvstore.sink;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Event;

public interface NoSQLDBEventSerializer
{
	/**
	 * Initializes the event serializer
	 * @param event The event that should be posted to NoSQL DB
	 */
	public void initialize(Event event);
	
	/**
	 * Returns the key to be used for persistence
	 * @return A new key to be used for persistence
	 */
	public Key getKey();
	
	/**
	 * Returns the value of the event to be persisted into the NoSQL DB
	 * @return The value to be persisted into the NoSQL DB
	 */
	public Value getValue();
	
	/**
	 * Cleanup routine that will be called when the sink is stopped
	 */
	public void stop();
}
