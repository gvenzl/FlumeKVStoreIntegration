package com.optit.flumekvstore.sink;

import java.nio.charset.Charset;

import oracle.kv.Key;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TestGeneratorEventSerializer is a JUnit test class for {@link GeneratorEventSerializer}.
 * @author gvenzl
 *
 */
public class TestGeneratorEventSerializer
{
	/**
	 * The test Flume event for this class.
	 */
	private Event testEvent;
	
	/**
	 * The test GeneratorEventSerializer instance.
	 */
	private GeneratorEventSerializer gen;
	
	/**
	 * Sets up the test Flume event and the test GeneratorEventSeralizer for each test case.
	 */
	@Before
	public final void setup()
	{
		testEvent = EventBuilder.withBody("Test event with some characters", Charset.forName("UTF-8"));
		gen = new GeneratorEventSerializer();
	}
	
	/**
	 * Tests the initialize method.
	 */
	@Test
	public final void testInitialize()
	{
		gen.initialize("random", "TEST");
	}
	
	/**
	 * Tests whether a Key is returned for a Flume event.
	 * The test fails if no Key is returned or the same Key is returned again.
	 */
	@Test
	public final void testGetKey()
	{	
		gen.initialize("random", "JUNIT");
		
		Assert.assertNotNull(gen.getKey(testEvent));
		Assert.assertNotEquals(gen.getKey(testEvent), gen.getKey(testEvent));
	}
	
	/**
	 * Tests whether a value is returned for a Flume event.
	 * The test fails if no value is returned or the same method called twice with the same event produces different Values.
	 */
	@Test
	public final void testGetValue()
	{
		gen.initialize("random", "JUNIT");
		
		Assert.assertNotNull(gen.getValue(testEvent));
		Assert.assertEquals(gen.getValue(testEvent), gen.getValue(testEvent));
	}
}
