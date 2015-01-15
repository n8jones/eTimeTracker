package me.natejones.ett;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

public final class TimeLogEntry {
   private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
         this);

   private Date time;
   private String message;

   public TimeLogEntry() {
      time = null;
      message = null;
   }

   public TimeLogEntry(Date time, String message) {
      this.time = time;
      this.message = message;
   }

   public Date getTime() {
      return time;
   }

   public void setTime(Date time) {
      firePropertyChange("time", this.time, this.time = time);
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      firePropertyChange("message", this.message, this.message = message);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((message == null) ? 0 : message.hashCode());
      result = prime * result + ((time == null) ? 0 : time.hashCode());
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
      TimeLogEntry other = (TimeLogEntry) obj;
      if (message == null) {
         if (other.message != null)
            return false;
      } else if (!message.equals(other.message))
         return false;
      if (time == null) {
         if (other.time != null)
            return false;
      } else if (!time.equals(other.time))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "TimeLogEntry [time=" + time + ", message=" + message + "]";
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      changeSupport.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      changeSupport.removePropertyChangeListener(listener);
   }

   public void addPropertyChangeListener(String propertyName,
         PropertyChangeListener listener) {
      changeSupport.addPropertyChangeListener(propertyName, listener);
   }

   public void removePropertyChangeListener(String propertyName,
         PropertyChangeListener listener) {
      changeSupport.removePropertyChangeListener(propertyName, listener);
   }

   protected void firePropertyChange(String propertyName, Object oldValue,
         Object newValue) {
      changeSupport.firePropertyChange(propertyName, oldValue, newValue);
   }
}
