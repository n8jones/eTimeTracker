package me.natejones.ett;

import java.util.List;
import java.util.Vector;

public final class TimeLog {
	private final List<TimeLogLine> lines = new Vector<TimeLogLine>();
	public List<TimeLogLine> getLines(){
		return lines;
	}
}
