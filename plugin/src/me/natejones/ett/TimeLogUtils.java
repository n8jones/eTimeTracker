package me.natejones.ett;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.LineReader;

public class TimeLogUtils {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public static TimeLog read(InputStream input) throws IOException{
		TimeLog timelog = new TimeLog();
		List<TimeLogLine> lines = timelog.getLines();
		Pattern pattern = Pattern.compile("^(?<time>\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d:\\d\\d:\\d\\d) +(?<message>\\S.+)");
		LineReader reader = new LineReader(new BufferedReader(new InputStreamReader(input)));
		String line = reader.readLine();
		while(line != null){
			Matcher m = pattern.matcher(line);
			TimeLogEntry entry = null;
			if(m.matches()){
				String timeGrp = m.group("time");
				String messageGrp = m.group("message");
				try {
					Date time = dateFormat.parse(timeGrp);
					entry = new TimeLogEntry(time, messageGrp);
				} catch (ParseException e) {
					entry = null;
				}
			}
			lines.add(new TimeLogLine(line, entry));
			line = reader.readLine();
		}
		return timelog;
	}
	
	public static void write(OutputStream output, TimeLog timeLog) throws IOException{
		PrintStream out = new PrintStream(output);
		for(TimeLogLine line : timeLog.getLines()){
			TimeLogEntry entry = line.getEntry();
			if(entry == null){
				out.println(line.getLine());
			}
			else{
				out.print(dateFormat.format(entry.getTime()));
				out.print(" ");
				out.println(entry.getMessage());
			}
		}
		out.flush();
	}
	
	public static byte[] toBytes(TimeLog timeLog){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			write(baos, timeLog);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}
}
