package com.optit.flumekvstore.sink;

import oracle.kv.Key;

import org.junit.Assert;
import org.junit.Test;

/**
 * TestSimpleKeyGenerator is a JUnit test class for {@link SimpleKeyGenerator}.
 * @author gvenzl
 *
 */
public class TestSimpleKeyGenerator
{
	/**
	 * Tests the key generation for keys based on random longs.
	 * It generates 2 keys each with and without prefix and compares them
	 * The test fails if the same key gets generated.
	 */
	@Test
	public final void testGetRandomKey()
	{
		Assert.assertNotEquals(new SimpleKeyGenerator().getRandomKey(null), new SimpleKeyGenerator().getRandomKey(null));
		Assert.assertNotEquals(new SimpleKeyGenerator().getRandomKey("JUnit"), new SimpleKeyGenerator().getRandomKey("JUnit"));
	}
	
	/**
	 * Tests the key generation for milli seconds timestamp keys.
	 * It generates 2 keys each with and without prefix and compares them
	 * The test fails if the same key gets generated.
	 * @throws Exception InterruptedException by Thread.sleep()
	 */
	@Test
	public final void testGetTimestampKey() throws Exception
	{
		Key key1 = new SimpleKeyGenerator().getTimestampKey(null);
		Thread.sleep(1);
		Key key2 = new SimpleKeyGenerator().getTimestampKey(null);
		Assert.assertNotEquals(key1, key2);
		
		key1 = new SimpleKeyGenerator().getTimestampKey("JUnit");
		Thread.sleep(1);
		key2 = new SimpleKeyGenerator().getTimestampKey("JUnit");
		Assert.assertNotEquals(key1, key2);
		
	}
	
	/**
	 * Tests the key generation for nano seconds timestamp keys.
	 * It generates 2 keys each with and without prefix and compares them
	 * The test fails if the same key gets generated.
	 * @throws Exception InterruptedException by Thread.sleep()
	 */
	@Test
	public final void testGetNanoTimestampKey() throws Exception
	{

		Key key1 = new SimpleKeyGenerator().getNanoTimestampKey(null);
		Thread.sleep(1);
		Key key2 = new SimpleKeyGenerator().getNanoTimestampKey(null);
		Assert.assertNotEquals(key1, key2);
		
		key1 = new SimpleKeyGenerator().getNanoTimestampKey("JUnit");
		Thread.sleep(1);
		key2 = new SimpleKeyGenerator().getNanoTimestampKey("JUnit");
		Assert.assertNotEquals(key1, key2);
		}

}
