package com.optit.flumekvstore.sink;

import oracle.kv.Durability;
import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;

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
	String kvPort;
	String kvStoreName;
	String kvStoreDurability;
	String keyPolicy;
	String keyType;
	String keyPrefix;
	
	KVStore kvStore;
	NoSQLDBEventSerializer serializer;

	@Override
	public void configure(Context context)
	{
		// Get configuration properties for KV store
		kvHost = context.getString(NoSQLDBSinkConfiguration.KVHOST, "localhost");
		kvPort = context.getString(NoSQLDBSinkConfiguration.KVPORT, "5000");
		kvStoreName = context.getString(NoSQLDBSinkConfiguration.KVSTORE, "kvstore");
		kvStoreDurability = context.getString(NoSQLDBSinkConfiguration.DURABILITY, "WRITE_NO_SYNC");
		keyPolicy = context.getString(NoSQLDBSinkConfiguration.KEYPOLICY);
		keyType = context.getString(NoSQLDBSinkConfiguration.KEYTYPE);
		keyPrefix = context.getString(NoSQLDBSinkConfiguration.KEYPREFIX);
		
		LOG.info("Configuration settings:");
		LOG.info(NoSQLDBSinkConfiguration.KVHOST + ": " + kvHost);
		LOG.info(NoSQLDBSinkConfiguration.KVPORT + ": " + kvPort);
		LOG.info(NoSQLDBSinkConfiguration.KVSTORE + ": " + kvStoreName);
		LOG.info(NoSQLDBSinkConfiguration.DURABILITY + ": " + kvStoreDurability);
		LOG.info(NoSQLDBSinkConfiguration.KEYPOLICY + ": " + keyPolicy);
		LOG.info(NoSQLDBSinkConfiguration.KEYTYPE + ": " + keyType);
		LOG.info(NoSQLDBSinkConfiguration.KEYPREFIX + ": " + keyPrefix);
	}
	
	@Override
	public void start()
	{
		try
		{
			KVStoreConfig config = new KVStoreConfig(kvStoreName, kvHost + ":" + kvPort);
			
			// Set durability configuration
			switch (kvStoreDurability)
			{
				case "SYNC": { config.setDurability(Durability.COMMIT_SYNC); break; }
				case "WRITE_NO_SYNC": { config.setDurability(Durability.COMMIT_WRITE_NO_SYNC); break; }
				case "NO_SYNC": { config.setDurability(Durability.COMMIT_NO_SYNC); break; }
				default:
				{
					LOG.info("Invalid durability setting: " + kvStoreDurability);
					LOG.info("Proceeding with default WRITE_NO_SYNC");
					config.setDurability(Durability.COMMIT_WRITE_NO_SYNC);
				}
			}

			kvStore = KVStoreFactory.getStore(config);
			
			// Set key policy
			switch (keyPolicy)
			{
				case "generate":
				{
					serializer = new GeneratorEventSerializer();
					serializer.initialize(keyType, keyPrefix);
					break;
				}
				//TODO: Implement both serializers
				//case "header": { serializer = new Object(); break; }
				//case "regex": { serializer = new Object(); break; }
			}
		}
		catch (FaultException e) {
			LOG.error("Could not establish connection to KV store!");
			LOG.error(e.getMessage());
		}
		
		LOG.info("Connection to KV store established");
	}
	
	@Override
	public void stop()
	{
		serializer.stop();
		kvStore.close();
		LOG.trace("Connection to KV store closed");
	}
	
	@Override
	public Status process() throws EventDeliveryException
	{
		LOG.debug("New event coming in, begin processing...");
		Status status = Status.READY;
		
		LOG.trace("Get Flume channel");
		Channel ch = getChannel();
		LOG.trace("Start transaction");
		Transaction txn = ch.getTransaction();
		txn.begin();
		LOG.trace("Transaction context established");
		
		// This try clause includes whatever Channel operations you want to do
		try
		{
			Event event = ch.take();
			if (null != event) {
				LOG.trace("Event received: " + event.toString());
				kvStore.put(serializer.getKey(event), serializer.getValue(event));
				LOG.debug("Event stored in KV store");
				txn.commit();
				LOG.debug("Transaction commited!");
			}
			else {
				txn.rollback();
				status = Status.BACKOFF;
			}
		}
		catch (Throwable t)
		{
			// Rollback transaction
			txn.rollback();
			status = Status.BACKOFF;
			
			LOG.error("Error processing event!");
			LOG.error(t.toString());
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
