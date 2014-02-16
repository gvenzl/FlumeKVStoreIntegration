package com.gvenzl.flumekvstore.sink;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TestRegExEventSerializer is a JUnit test class for {@link RegExEventSerializer}.
 * @author gvenzl
 *
 */
public class TestRegExEventSeralizer
{
	/**
	 * The test Flume event for this class.
	 */
	private List<Event> testEvents;
	
	/**
	 * The test GeneratorEventSerializer instance.
	 */
	private RegExEventSerializer regExSerializer;
	
	/**
	 * Sets up the test Flume event and the test GeneratorEventSeralizer for each test case.
	 */
	@Before
	public final void setup()
	{
		testEvents = new ArrayList<Event>();
		testEvents.add(EventBuilder.withBody("21:23:20.193716 IP home-nas.afpovertcp > localhost.52127: Flags [.], seq 56479760:56481208, ack 7669, win 3213, options [nop,nop,TS val 276367245 ecr 950314817], length 1448", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193752 IP localhost.52127 > home-nas.afpovertcp: Flags [.], ack 56462384, win 29179, options [nop,nop,TS val 950314835 ecr 276367245], length 0", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193803 IP localhost.52127 > home-nas.afpovertcp: Flags [.], ack 56473968, win 29735, options [nop,nop,TS val 950314835 ecr 276367245], length 0", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193835 IP home-nas.afpovertcp > localhost.52127: Flags [.], seq 56481208:56482656, ack 7669, win 3213, options [nop,nop,TS val 276367245 ecr 950314817], length 1448", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193838 IP home-nas.afpovertcp > localhost.52127: Flags [.], seq 56482656:56484104, ack 7669, win 3213, options [nop,nop,TS val 276367245 ecr 950314817], length 1448", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193842 IP home-nas.afpovertcp > localhost.52127: Flags [.], seq 56484104:56485552, ack 7669, win 3213, options [nop,nop,TS val 276367245 ecr 950314817], length 1448", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193845 IP home-nas.afpovertcp > localhost.52127: Flags [.], seq 56485552:56487000, ack 7669, win 3213, options [nop,nop,TS val 276367245 ecr 950314817], length 1448", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193864 IP home-nas.afpovertcp > localhost.52127: Flags [P.], seq 56487000:56488448, ack 7669, win 3213, options [nop,nop,TS val 276367245 ecr 950314817], length 1448", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193867 IP home-nas.afpovertcp > localhost.52127: Flags [.], seq 56488448:56489896, ack 7669, win 3213, options [nop,nop,TS val 276367246 ecr 950314828], length 1448", Charset.forName("UTF-8")));
		testEvents.add(EventBuilder.withBody("21:23:20.193869 IP home-nas.afpovertcp > localhost.52127: Flags [.], seq 56489896:56491344, ack 7669, win 3213, options [nop,nop,TS val 276367246 ecr 950314828], length 1448", Charset.forName("UTF-8")));

		regExSerializer = new RegExEventSerializer();
	}
	
	/**
	 * Tests the initialize functionality with a valid regular expression.
	 */
	@Test
	public final void testInitialize() {
		regExSerializer.initialize("(.*$)", "JUnit");
	}
	
	/**
	 * Tests the initialize functionality with an invalid regular expression.
	 */
	@Test (expected = PatternSyntaxException.class)
	public final void testNegativeIntialize() {
		regExSerializer.initialize("IN(VALID].[!!!!", "JUnit");
	}
	
	/**
	 * Test the regular expression with the "VALUE" path.
	 */
	@Test
	public final void testGetValueWithValueComponent()
	{
		regExSerializer.initialize("(?<key>(\\d{2}:\\d{2}:\\d{2}\\.\\d{6}) (.*) > (.*)): (?<value>.*$)", "JUnit");
		for (int i = 0; i < testEvents.size(); i++) {
			Assert.assertNotNull(regExSerializer.getValue(testEvents.get(i)));
		}
	}
	
	/**
	 * Test the regular expression without the "VALUE" path.
	 */
	@Test
	public final void testGetValueWithoutValueComponent()
	{
		regExSerializer.initialize("(?<key>(\\d{2}:\\d{2}:\\d{2}\\.\\d{6}) (.*) > (.*)): (.*$)", "JUnit");
		for (int i = 0; i < testEvents.size(); i++) {
			Assert.assertNotNull(regExSerializer.getValue(testEvents.get(i)));
		}
	}
	
	/**
	 * Tests the regular expression with the "KEY" path.
	 */
	@Test
	public final void testGetKeyWithKeyComponent()
	{
		regExSerializer.initialize("(?<key>(\\d{2}:\\d{2}:\\d{2}\\.\\d{6}) (.*) > (.*)): (?<value>.*$)", "JUnit");
		for (int i = 0; i < testEvents.size(); i++) {
			Assert.assertNotNull(regExSerializer.getKey(testEvents.get(i)));
		}
	}
	
	/**
	 * Test the regular expression with the "MAJOR" component path.
	 */
	@Test
	public final void testGetKeyWithMajorComponentOnly()
	{
		regExSerializer.initialize("(?<majorKey>(\\d{2}:\\d{2}:\\d{2}\\.\\d{6}) (.*) > (.*)): (?<value>.*$)", "JUnit");
		for (int i = 0; i < testEvents.size(); i++) {
			Assert.assertNotNull(regExSerializer.getKey(testEvents.get(i)));
		}
	}
	
	/**
	 * Test the regular expression with the "MAJOR" and "MINOR" component path.
	 */
	@Test
	public final void testGetKeyWithMajorAndMinorComponent()
	{
		regExSerializer.initialize("(?<majorKey>(\\d{2}:\\d{2}:\\d{2}\\.\\d{6})) (?<minorKey>(.*) > (.*)): (?<value>.*$)", "JUnit");
		for (int i = 0; i < testEvents.size(); i++) {
			Assert.assertNotNull(regExSerializer.getKey(testEvents.get(i)));
		}
	}
}
