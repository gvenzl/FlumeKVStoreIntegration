package com.optit.flumekvstore.sink;

import oracle.kv.FaultException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache Flume sink for Oracle NoSQL DB
 * @author gvenzl
 *
 */

public class NoSQLDBSink extends AbstractSink implements Configurable
{
	private static final Logger LOG = LoggerFactory.getLogger(NoSQLDBSink.class);

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
		
		LOG.info("Configuration settings:");
		LOG.info("kvHost: " + kvHost);
		LOG.info("kvStoreName: " + kvStoreName);
		LOG.info("kvPort: " + kvPort);
	}
	
	@Override
	public void start()
	{
		try {
			kvStore = KVStoreFactory.getStore(new KVStoreConfig(kvStoreName, kvHost + ":" + kvPort));
		}
		catch (FaultException e) {
			LOG.error("Could not establish connection to KV store!");
			LOG.error(e.getMessage());
		}
		
		System.out.println("Connection to KV store established");
	}
	
	@Override
	public void stop()
	{
		kvStore.close();
		LOG.trace("Connection to KV store closed");
	}
	
	@Override
	public Status process() throws EventDeliveryException
	{
		LOG.info("New event coming in, begin processing...");
		Status status = null;
		
		LOG.trace("Get Flume channel");
		Channel ch = getChannel();
		LOG.trace("Start transaction");
		Transaction txn = ch.getTransaction();
		txn.begin();
		LOG.trace("Transaction context established");
		
		// This try clause includes whatever Channel operations you want to do
		try
		{
			Key key = null;
			Event event = ch.take();
			if (null != event.getHeaders().get("Key")) {
				key = Key.fromString(event.getHeaders().get("Key"));
			} else {
				key = Key.createKey("myKey");
			}
			
			LOG.trace("Event received: " + event.toString());
			
			//TODO: Think about how to derive key from message
			kvStore.put(key, Value.createValue(event.getBody()));
			
			txn.commit();
			status = Status.READY;
			
			LOG.info("Event successfully stored in KV store");
		}
		catch (Throwable t)
		{
			// Rollback transaction
			txn.rollback();
			status = Status.BACKOFF;
			
			LOG.error("Error processing event!");
			LOG.error(t.getMessage());
			
			// re-throw all Errors
			if (t instanceof Error)
			{
				throw (Error)t;
			}
		}
		
		finally {
			txn.close();
			LOG.trace("Transaction closed.");
		}
		
		// Return status to Flume (either Status.READY or Status.BACKOFF)
		return status;
	}
}
