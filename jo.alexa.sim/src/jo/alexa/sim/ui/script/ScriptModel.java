package jo.alexa.sim.ui.script;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.ScriptTransactionBean;

public class ScriptModel extends AbstractListModel<ScriptTransactionBean> implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -90087385111360011L;

    private RuntimeBean mRuntime;
    
    public ScriptModel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        mRuntime.addPropertyChangeListener("script", this);
    }
    
    @Override
    public int getSize()
    {
        return mRuntime.getScript().getScript().size();
    }

    @Override
    public ScriptTransactionBean getElementAt(int index)
    {
        return mRuntime.getScript().getScript().get(index);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        fireContentsChanged(this, 0, getSize());
    }

}
