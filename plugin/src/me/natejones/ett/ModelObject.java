package me.natejones.ett;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Credit to http://www.vogella.com/tutorials/EclipseDataBinding/article.html
 */
public abstract class ModelObject {
   private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
         this);

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