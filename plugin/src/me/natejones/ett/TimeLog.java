package me.natejones.ett;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public final class TimeLog {
	private final List<TimeLogLine> lines;
	
	public TimeLog(){
		lines = Lists.newArrayList();
	}
	
	public TimeLog(Iterable<TimeLogEntry> entries){
		lines = Lists.newArrayList(Iterables.transform(entries, new Function<TimeLogEntry, TimeLogLine>(){
			@Override
			public TimeLogLine apply(TimeLogEntry arg0) {
				return new TimeLogLine(null, arg0);
			}
		}));
	}
	
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
