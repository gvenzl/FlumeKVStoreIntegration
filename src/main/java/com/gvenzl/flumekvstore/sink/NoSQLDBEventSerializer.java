package com.gvenzl.flumekvstore.sink;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Event;

/**
 * The interface for the NoSQL DB Event serializer.
 * This interface defines the methods that need to be implemented by the serializer in order to be used.
 * @author gvenzl
 * 
 */
public interface NoSQLDBEventSerializer
{
	/**
	 * Initializes the event serializer.
	 * @param keyType The key type for the serializer
	 * @param prefix The key prefix to be used for the serializer
	 */
	void initialize(final String keyType, final String prefix);
	
	/**
	 * Returns the key to be used for persistence.
	 * @param event The event from which the key should be retrieved
	 * @return A new key to be used for persistence
	 */
	Key getKey(final Event event);
	
	/**
	 * Returns the value of the event to be persisted into the NoSQL DB.
	 * @param event The event from which the value should be retrieved
	 * @return The value to be persisted into the NoSQL DB
	 */
	Value getValue(final Event event);
	
	/**
	 * Cleanup routine that will be called when the sink is stopped.
	 */
	void stop();
}
