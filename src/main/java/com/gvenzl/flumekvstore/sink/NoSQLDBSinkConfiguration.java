package com.gvenzl.flumekvstore.sink;

/**
 * This class holds the constants used for configuration of the sink.
 * @author gvenzl
 *
 */
public final class NoSQLDBSinkConfiguration
{
	/**
	 * Default constructor hindering an instanziation of the class.
	 */
	private NoSQLDBSinkConfiguration() { };
	/**
	 * The host that should be used to connect to.
	 */
	public static final String KVHOST = "kvHost";
	
	/**
	 * The port that should be used to connect to.
	 */
	public static final String KVPORT = "kvPort";
	
	/**
	 * The kvStore name to connect to.
	 */
	public static final String KVSTORE = "kvStoreName";
	
	/**
	 * The durability policy that should be used.<br>
	 * SYNC = Commit onto disk at master and replicate to simple majority of replicas<br>
	 * WRITE_NO_SYNC = Commit onto disk at master but do not replicate<br>
	 * NO_SYNC = Commit only into master memory and do not replicate
	 */
	public static final String DURABILITY = "durability";
	
	/**
	 * The policy of the key retrieval.
	 */
	public static final String KEYPOLICY = "keyPolicy";
	
	/**
	 * The values for the {@link KEYPOLICY}.<br>
	 * GENERATE = Generates a simple key<br>
	 * HEADER = Retrieves the key from the header of the event<br>
	 * REGEX = Retrieves the key directly from the event via a regular expression
	 */
	public static enum KEYPOLICYVALUES { GENERATE, HEADER, REGEX };
	
	/**
	 * Specifies the type of the key based upon the key policy chosen.<br>
	 * random = Generates a new key for each event based on a random long<br>
	 * timestamp = Generates a new milli seconds timestamp based key for each event<br>
	 * nanotimestamp = Generates a new nano seconds timestamp based key for each event<br>
	 * HEADER KEY = The HashMap key to be used to retrieve the actual key from the event header<br>
	 * REGULAR EXPRESSION = The regular expression to be used to retrieve the key from the event
	 */
	public static final String KEYTYPE = "keyType";
	
	/**
	 * Prefix to be used for the major key.
	 */
	public static final String KEYPREFIX = "keyPrefix";
	
	/**
	 * Defines the batch size to be used for batching Apache Flume events.
	 */
	public static final String BATCHSIZE = "batchSize";

}
