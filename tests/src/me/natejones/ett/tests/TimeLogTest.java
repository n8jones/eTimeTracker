package me.natejones.ett.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import junit.framework.TestSuite;
import me.natejones.ett.TimeLog;
import me.natejones.ett.TimeLogUtils;

import org.junit.Test;

public class TimeLogTest {

	@Test
	public void test_timelog_read() {
		TimeLog timeLog = TimeLogUtils.read(new ByteArrayInputStream(("2013-08-13T09:00:00 Arrive\n"
+"2013-08-13T12:15:00 Development #Compass\n"
+"2013-08-13T12:48:47 Lunch*\n"
+"2013-08-13T17:30:00 Development #Compass\n").getBytes()));
		assertNotNull("timeLog is not null", timeLog);
	}

}
