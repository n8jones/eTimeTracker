package me.natejones.ett.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import me.natejones.ett.*;

import org.junit.Test;

import com.google.common.collect.Lists;

public class TimeLogTest {

	@Test
	public void test_timelog_read() throws IOException {
		TimeLog timeLog = TimeLogUtils.read(new ByteArrayInputStream(testFileContent.getBytes()));
		assertNotNull("timeLog is not null", timeLog);
		List<TimeLogEntry> entries = Lists.newArrayList(timeLog.getEntries());
		assertEquals("timeLog should have 4 entries", 4, entries.size());
		List<TimeLogLine> lines = timeLog.getLines();
		assertEquals("Expected number of lines", testFileLines.length, lines.size());
		for(int i=0; i<lines.size(); i++){
			TimeLogLine expected = testFileLines[i];
			TimeLogLine found = lines.get(i);
			assertEquals("Line["+i+"] raw line equals", 
					expected.getLine(), found.getLine());
			TimeLogEntry expectedEntry = expected.getEntry();
			TimeLogEntry foundEntry = found.getEntry();
			if(expectedEntry == null){
				assertNull("Line["+i+"] entry equals", foundEntry);
			}
			else{
				assertEquals("Line["+i+"] entry.message equals", 
						expectedEntry.getMessage(), foundEntry.getMessage());
				assertEquals("Line["+i+"] entry.time equals", 
						expectedEntry.getTime(), foundEntry.getTime());
			}
		}
	}
	
	@Test
	public void test_timelog_write() throws IOException{
		TimeLog timeLog = new TimeLog();
		List<TimeLogLine> lines = timeLog.getLines();
		for(TimeLogLine line : testFileLines) lines.add(line);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TimeLogUtils.write(output, timeLog);
		String content = output.toString();
		assertEquals("Written file contents", 
				testFileContent.replace("\n", System.lineSeparator()), content);
	}

	private static final String testFileContent = 
		 "2013-08-13T09:00:00 Arrive\n"
		+"This line is a comment.  It should not be an entry.\n"
		+"2013-08-13T12:15:00 Development #Project1\n"
		+"2013-08-13T12:48:47 Lunch*\n"
		+"2013-08-13T17:30:00 Development #Project1\n";
	
	@SuppressWarnings("deprecation")
	private static final TimeLogLine[] testFileLines = new TimeLogLine[]{
		new TimeLogLine("2013-08-13T09:00:00 Arrive", 
				new TimeLogEntry(new Date(113, 7, 13, 9, 0, 0), "Arrive")),
		new TimeLogLine("This line is a comment.  It should not be an entry.", null),
		new TimeLogLine("2013-08-13T12:15:00 Development #Project1", 
				new TimeLogEntry(new Date(113, 7, 13, 12, 15, 0), "Development #Project1")),
		new TimeLogLine("2013-08-13T12:48:47 Lunch*", 
				new TimeLogEntry(new Date(113, 7, 13, 12, 48, 47), "Lunch*")),
		new TimeLogLine("2013-08-13T17:30:00 Development #Project1", 
				new TimeLogEntry(new Date(113, 7, 13, 17, 30, 0), "Development #Project1")),
	};
}
