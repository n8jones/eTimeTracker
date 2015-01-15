package me.natejones.ett.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import me.natejones.ett.TimeLog;
import me.natejones.ett.TimeLogEntry;
import me.natejones.ett.TimeLogLine;
import me.natejones.ett.TimeLogUtils;

import org.eclipse.core.internal.databinding.beans.BeanPropertyListenerSupport;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TimeLogTest {

   @Test
   public void test_timelog_read() throws IOException {
      TimeLog timeLog = TimeLogUtils.read(new ByteArrayInputStream(
            testFileContent.getBytes()));
      assertNotNull("timeLog is not null", timeLog);
      List<TimeLogEntry> entries = Lists.newArrayList(timeLog.getEntries());
      assertEquals("timeLog should have 4 entries", 4, entries.size());
      List<TimeLogLine> lines = timeLog.getLines();
      assertEquals("Expected number of lines", testFileLines.length,
            lines.size());
      for (int i = 0; i < lines.size(); i++) {
         TimeLogLine expected = testFileLines[i];
         TimeLogLine found = lines.get(i);
         assertEquals("Line[" + i + "] raw line equals", expected.getLine(),
               found.getLine());
         TimeLogEntry expectedEntry = expected.getEntry();
         TimeLogEntry foundEntry = found.getEntry();
         if (expectedEntry == null) {
            assertNull("Line[" + i + "] entry equals", foundEntry);
         } else {
            assertEquals("Line[" + i + "] entry.message equals",
                  expectedEntry.getMessage(), foundEntry.getMessage());
            assertEquals("Line[" + i + "] entry.time equals",
                  expectedEntry.getTime(), foundEntry.getTime());
         }
      }
   }

   @Test
   public void test_timelog_write() throws IOException {
      TimeLog timeLog = new TimeLog();
      List<TimeLogLine> lines = timeLog.getLines();
      for (TimeLogLine line : testFileLines)
         lines.add(line);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      TimeLogUtils.write(output, timeLog);
      String content = output.toString();
      assertEquals("Written file contents",
            testFileContent.replace("\n", System.lineSeparator()), content);
   }

   @SuppressWarnings("restriction")
   @Test
   public void test_timelogentry_beaninfo() {
      final TimeLogEntry entry = new TimeLogEntry();
      final List<Boolean> results = Lists.newArrayList();
      PropertyChangeListener listener = new PropertyChangeListener() {
         @Override
         public void propertyChange(PropertyChangeEvent arg0) {
            results.add(true);
         }
      };
      BeanPropertyListenerSupport.hookListener(entry, "time", listener);
      entry.setTime(new Date());
      assertFalse("The property change listener should have fired.",
            results.isEmpty());
      BeanPropertyListenerSupport.unhookListener(entry, "time", listener);
   }

   private static final String testFileContent = "2013-08-13T09:00:00 Arrive\n"
         + "This line is a comment.  It should not be an entry.\n"
         + "2013-08-13T12:15:00 Development #Project1\n"
         + "2013-08-13T12:48:47 Lunch*\n"
         + "2013-08-13T17:30:00 Development #Project1\n";

   @SuppressWarnings("deprecation")
   private static final TimeLogLine[] testFileLines = new TimeLogLine[] {
         new TimeLogLine("2013-08-13T09:00:00 Arrive", new TimeLogEntry(
               new Date(113, 7, 13, 9, 0, 0), "Arrive")),
         new TimeLogLine("This line is a comment.  It should not be an entry.",
               null),
         new TimeLogLine("2013-08-13T12:15:00 Development #Project1",
               new TimeLogEntry(new Date(113, 7, 13, 12, 15, 0),
                     "Development #Project1")),
         new TimeLogLine("2013-08-13T12:48:47 Lunch*", new TimeLogEntry(
               new Date(113, 7, 13, 12, 48, 47), "Lunch*")),
         new TimeLogLine("2013-08-13T17:30:00 Development #Project1",
               new TimeLogEntry(new Date(113, 7, 13, 17, 30, 0),
                     "Development #Project1")), };
}
