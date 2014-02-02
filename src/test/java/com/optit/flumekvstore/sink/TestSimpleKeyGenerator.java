package com.optit.flumekvstore.sink;

import oracle.kv.Key;

import org.junit.Assert;
import org.junit.Test;


public class TestSimpleKeyGenerator
{
	@Test
	public void testGetRandomKey()
	{
		Assert.assertNotEquals(new SimpleKeyGenerator().getRandomKey(null), new SimpleKeyGenerator().getRandomKey(null));
		Assert.assertNotEquals(new SimpleKeyGenerator().getRandomKey("JUnit"), new SimpleKeyGenerator().getRandomKey("JUnit"));
	}
	
	@Test
	public void testGetTimestampKey() throws Exception
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
	
	@Test
	public void testGetNanoTimestampKey() throws Exception
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
