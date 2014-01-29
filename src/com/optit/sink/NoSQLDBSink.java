package com.optit.sink;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;

/**
 * Apache Flume sink for Oracle NoSQL DB
 * @author gvenzl
 *
 */

public class NoSQLDBSink extends AbstractSink implements Configurable
{
	//TODO: Integrate with Flume logging
	//TODO: Exception handling
	
	String kvHost;
	String kvStoreName;
	String kvPort;
	
	KVStore kvStore;

	@Override
	public void configure(Context context)
	{
		// Get configuration properties for KV store
		kvHost = context.getString("kvHost", "localhost");
		kvStoreName = context.getString("kvStoreName", "kvstore");
		kvPort = context.getString("kvPort", "5000");
		
		System.out.println("Configuration settings:");
		System.out.println("kvHost: " + kvHost);
		System.out.println("kvStoreName: " + kvStoreName);
		System.out.println("kvPort: " + kvPort);
	}
	
	@Override
	public void start()
	{
		System.out.println("Establishing connection to KV store");
		// Open connection to KV Store
		kvStore = KVStoreFactory.getStore(new KVStoreConfig(kvStoreName, kvHost + ":" + kvPort));
		System.out.println("Connection established.");
	}
	
	@Override
	public void stop()
	{
		// Close KV store connection
		kvStore.close();
	}
	
	@Override
	public Status process() throws EventDeliveryException
	{
		System.out.println("New event coming in...");
		Status status = null;
		
		// Start transaction
		Channel ch = getChannel();
		Transaction txn = ch.getTransaction();
		txn.begin();
		System.out.println("Transaction context established");
		
		try
		{
			// This try clause includes whatever Channel operations you want to do
			Event event = ch.take();
			
			//TODO: Think about how to derive key from message
			// Send the Event to the external repository.
			kvStore.put(Key.createKey("myKey"), Value.createValue(event.getBody()));
			
			txn.commit();
			status = Status.READY;
			
			System.out.println("Event successfully stored in KV store");
		}
		catch (Throwable t)
		{
			// Rollback transaction
			txn.rollback();
			
			// Log exception, handle individual exceptions as needed
			status = Status.BACKOFF;
			
			// re-throw all Errors
			if (t instanceof Error)
			{
				throw (Error)t;
			}
		}
		
		finally
		{
			// Close Flume transaction
			txn.close();
		}
		
		// Return status to Flume (either Status.READY or Status.BACKOFF)
		return status;
	}
}
