package me.natejones.ett;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public final class TimeLog {
	private final List<TimeLogLine> lines = new ArrayList<TimeLogLine>();
	
	public List<TimeLogLine> getLines(){
		return lines;
	}
	
	public Iterable<TimeLogEntry> getEntries(){
		Iterable<TimeLogLine> iter = Iterables.filter(lines,new Predicate<TimeLogLine>(){
			@Override
			public boolean apply(TimeLogLine arg0) {
				return arg0.getEntry() != null;
			}
		});
		return Iterables.transform(iter, new Function<TimeLogLine, TimeLogEntry>(){
			@Override
			public TimeLogEntry apply(TimeLogLine arg0) {
				return arg0.getEntry();
			}
		});
	}
}
