package com.optit.flumekvstore.sink;

/**
 * This class holds the constants used for configuration of the sink
 * @author gvenzl
 *
 */
public class NoSQLDBSinkConfiguration
{
	/**
	 * The host that should be used to connect to
	 */
	public static final String KVHOST = "kvHost";
	
	/**
	 * The port that should be used to connect to
	 */
	public static final String KVPORT = "kvPort";
	
	/**
	 * The kvStore name to connect to
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
	 * The key policy that should be used.<br>
	 * random = Generate a new Random key for each event<br>
	 * timestamp = Generate a new milli seconds timestamp based key for each event<br>
	 * nanotimestamp = Generate a new nano seconds timestamp based key for each event<br>
	 */
	public static final String KEYPOLICY = "keyPolicy";
	
	/**
	 * Prefix to be used for the major key
	 */
	public static final String KEYPREFIX = "keyPrefix";

}
