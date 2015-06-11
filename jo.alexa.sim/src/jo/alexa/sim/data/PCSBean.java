package jo.alexa.sim.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PCSBean
{
    protected PropertyChangeSupport   mPCS;
    
    public PCSBean()
    {
        mPCS = new PropertyChangeSupport(this);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        mPCS.addPropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String property, PropertyChangeListener listener)
    {
        mPCS.addPropertyChangeListener(property, listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        mPCS.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue,
            Object newValue)
    {
        mPCS.firePropertyChange(propertyName, oldValue, newValue);
    }
}
