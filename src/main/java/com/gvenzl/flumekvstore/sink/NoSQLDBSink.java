package com.gvenzl.flumekvstore.sink;

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

import com.gvenzl.flumekvstore.sink.NoSQLDBSinkConfiguration.KEYPOLICYVALUES;

/**
 * Apache Flume sink for Oracle NoSQL DB.
 * @author gvenzl
 *
 */

public class NoSQLDBSink extends AbstractSink implements Configurable {
	/**
	 * The logger instance to be used.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(NoSQLDBSink.class);

	/**
	 * The host of the KV store.
	 */
	private String kvHost;
	/**
	 * The port of the KV store.
	 */
	private String kvPort;
	/**
	 * The KV store name.
	 */
	private String kvStoreName;
	/**
	 * The durability to be used for persisting values.
	 */
	private String kvStoreDurability;
	/**
	 * The policy of the key retrieval.
	 */
	private String keyPolicy;
	/**
	 * The type of the key retrieval.
	 */
	private String keyType;
	/**
	 * The prefix used for the key.
	 */
	private String keyPrefix;
	/**
	 * The KV store.
	 */
	private KVStore kvStore;
	/**
	 * The event serializer.
	 */
	private NoSQLDBEventSerializer serializer;

	@Override
	public final void configure(final Context context)
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
	public final void start()
	{
		// Create seralizer based on keyPolicy
		switch (NoSQLDBSinkConfiguration.KEYPOLICYVALUES.valueOf(keyPolicy.toUpperCase()))
		{
			// If no or an invalid key policy has been specified, LOG error and fall through to the GeneratorEventSerializer - no break
			default: { LOG.error("Invalid key policy specified. Using default: " + KEYPOLICYVALUES.GENERATE.toString().toLowerCase()); }
			case GENERATE: { serializer = new GeneratorEventSerializer(); break; }
			case HEADER: { serializer = new HeaderEventSerializer(); break; }
			//TODO: Implement regex serializers
			//case "regex": { serializer = new Object(); break; }
		}
		serializer.initialize(keyType, keyPrefix);
		
		// Build KV store config
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
				break;
			}
		}

		// Connect to KV store
		try {
			kvStore = KVStoreFactory.getStore(config);
			LOG.info("Connection to KV store established");
		}
		catch (FaultException e) {
			LOG.error("Could not establish connection to KV store!");
			LOG.error(e.getMessage());
			// Throw error
			throw e;
		}
	}
	
	@Override
	public final void stop()
	{
		serializer.stop();
		kvStore.close();
		LOG.trace("Connection to KV store closed");
	}
	
	@Override
	public final Status process() throws EventDeliveryException
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
			if (t instanceof Error) {
				throw (Error) t;
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
