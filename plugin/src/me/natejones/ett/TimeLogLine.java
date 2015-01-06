package me.natejones.ett;

public final class TimeLogLine {
	
	private String line;
	private TimeLogEntry entry;
	
	public TimeLogLine(){
		line = null;
		entry = null;
	}
	
	public TimeLogLine(String line, TimeLogEntry entry) {
		super();
		this.line = line;
		this.entry = entry;
	}
	
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public TimeLogEntry getEntry() {
		return entry;
	}
	public void setEntry(TimeLogEntry entry) {
		this.entry = entry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entry == null) ? 0 : entry.hashCode());
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeLogLine other = (TimeLogLine) obj;
		if (entry == null) {
			if (other.entry != null)
				return false;
		} else if (!entry.equals(other.entry))
			return false;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeLogLine [line=" + line + ", entry=" + entry + "]";
	}
}
