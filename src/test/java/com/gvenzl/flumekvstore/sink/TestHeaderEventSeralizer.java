package com.gvenzl.flumekvstore.sink;

import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gvenzl.flumekvstore.sink.HeaderEventSerializer;

/**
 * TestHeaderEventSerializer is a JUnit test class for {@link HeaderEventSerializer}.
 * @author gvenzl
 *
 */
public class TestHeaderEventSeralizer
{
	/**
	 * The header key to be used for the tests.
	 */
	private String headerKey = "myKey";
	
	/**
	 * The test Key to use (Oracle NoSQL DB key).
	 */
	private String testKey = "/Test/Key/works";
	
	/**
	 * The seralizer to test.
	 */
	private HeaderEventSerializer seralizer;
	
	/**
	 * The Flume event for the test.
	 */
	private Event testEvent;
	
	/**
	 * Initializing the seralizer.
	 */
	@Before
	public final void setup()
	{
		seralizer = new HeaderEventSerializer();

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(headerKey, testKey);
		headers.put("stuff", "djfa;wkejf[ewaijfwa");
		headers.put("some more stuff", "jdf;ajf;kajdaksjf;a");
		
		testEvent = EventBuilder.withBody("This is a test message with some meaningless body", Charset.forName("UTF-8"), headers);
	}
	
	/**
	 * Tests the initialization of the seralizer.
	 */
	@Test
	public final void testInitialize()
	{
		seralizer.initialize("test", "test");
	}
	
	/**
	 * Tests the getKey() method with a given prefix.
	 * The test fails if the key returned by the seralizer is not the same as the test key.
	 */
	@Test
	public final void testGetKeyWithPrefix()
	{
		String prefix = "JUNIT";
		seralizer.initialize(headerKey, prefix);

		Assert.assertEquals("/" + prefix + testKey, seralizer.getKey(testEvent).toString());
	}
	
	/**
	 * Tests the getKey() method without prefix.
	 * The test fails if the key returned by the seralizer is not the same as the test key.
	 */
	@Test
	public final void testGetKeyWithoutPrefix()
	{
		seralizer.initialize(headerKey, null);

		Assert.assertEquals(testKey, seralizer.getKey(testEvent).toString());
	}
	
	/**
	 * Tests whether a value is returned for a Flume event.
	 * The test fails if no value is returned or the value is not the same as the test value.
	 */
	@Test
	public final void testGetValue()
	{
		Assert.assertEquals(testEvent.getBody(), seralizer.getValue(testEvent).getValue());
	}
}
