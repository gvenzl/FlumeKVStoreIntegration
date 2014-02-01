package com.optit.flumekvstore.sink;

import java.util.Random;

import oracle.kv.Key;

/**
 * Generates simple master keys for NoSQL DB
 * @author gvenzl
 *
 */
public class SimpleKeyGenerator
{	
	/**
	 * Generates a new NoSQL DB major key based on a random number
	 * @param prefix Prefix to be used for the key. This can be used to logically separate different sources
	 * @return A NoSQL DB key based on a random number
	 */
	public Key getRandomKey(String prefix) {
		return Key.createKey(String.valueOf(prefix + new Random().nextLong()));
	}
	
	/**
	 * Generates a new NoSQL DB major key based on a milli second timestamp
	 * @param prefix Prefix to be used for the key. This can be used to logically separate different sources
	 * @return A NoSQL DB key based on milli second timestamp
	 */
	public Key getTimestampKey(String prefix) {
		return Key.createKey(String.valueOf(prefix + System.currentTimeMillis()));
	}
	
	/**
	 * Generates a new NoSQL DB major key based on a nano second timestamp
	 * @param prefix Prefix to be used for the key. This can be used to logically separate different sources
	 * @return A NoSQL DB key based on a nano second timestamp
	 */
	public Key getNanoTimestampKey(String prefix) {
		return Key.createKey(String.valueOf(prefix + System.nanoTime()));
	}

}
