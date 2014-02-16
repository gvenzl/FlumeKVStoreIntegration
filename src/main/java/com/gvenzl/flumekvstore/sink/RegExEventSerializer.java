package com.gvenzl.flumekvstore.sink;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import oracle.kv.Key;
import oracle.kv.Value;

import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache Flume event serializer with Regular Expression support.
 * @author gvenzl
 *
 */
public class RegExEventSerializer implements NoSQLDBEventSerializer {

	/**
	 * Static variable for the "key" group.
	 */
	private static final String KEY = "key";
	/**
	 * Static variable for the "majorKey" group.
	 */
	private static final String MAJORKEY = "majorKey";
	/**
	 * Static variable for the "minorKey" group.
	 */
	private static final String MINORKEY = "minorKey";
	/**
	 * Static variable for the "value" group.
	 */
	private static final String VALUE = "value";
	
	/**
	 * The global logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(RegExEventSerializer.class);
	/**
	 * The key type.
	 */
	private Pattern keyPattern;
	
	/**
	 * The key prefix.
	 */
	private String keyPrefix;
	
	@Override
	public final void initialize(final String type, final String prefix)
	{
		keyPrefix = prefix;
		
		try {
			keyPattern = Pattern.compile(type);	
		}
		catch (PatternSyntaxException e) {
			LOG.error("Error parsing regular expression");
			LOG.error(e.getMessage());
			throw e;
		}
	}

	@Override
	public final Key getKey(final Event event)
	{
		Key key = null;
		Matcher m = keyPattern.matcher(new String(event.getBody()));
		if (!m.matches()) {
			throw new RuntimeException("No valid key could be obtained for this event!");
		}
		else {
			try {
				key = Key.createKey(m.group(KEY));
			// No key group defined in match
			} catch (IllegalArgumentException e)
			{ 
				LOG.debug("No \"" + KEY + "\" group within regular expression. Looking for \"" + MAJORKEY + "\" and \"" + MINORKEY + "\" components.");
				try {
					String major = m.group(MAJORKEY);
					try {
						key = Key.createKey(major, m.group(MINORKEY));
					}
					// No minor key group specified, create key with only major component
					catch (IllegalArgumentException e2) {
						LOG.debug("No \"" + MINORKEY + "\" group within the regular expression.");
						key = Key.createKey(major);
					}
				// No majorKey group in match, default to group 1
				} catch (IllegalArgumentException e1) {
					LOG.info("There was no \"" + KEY + "\" nor a \"" + MAJORKEY + "\" group within the regular expression.");
					LOG.info("Defaulting to group 1 as key for event: " + m.group(1));
					key = Key.createKey(m.group(1));
				}
			}
		}
		
		// Check whether a prefix was defined for the key
		if (null != keyPrefix && !keyPrefix.isEmpty()) {
			key = Key.fromString("/" + keyPrefix + key.toString());
		}
		return key;
	}

	@Override
	public final Value getValue(final Event event) 
	{
		Matcher m = keyPattern.matcher(new String(event.getBody()));
		// See whether a value group was specified
		if (!m.matches()) {
			throw new RuntimeException("No valid value could be obtained for this event!");
		}
		else {
			try {
				return Value.createValue(m.group(VALUE).getBytes());
			} catch (IllegalArgumentException e) {
				// No value group was found, use entire event as Value.
				return Value.createValue(event.getBody());
			}
		}
	}

	@Override
	public void stop() {
	}
}
