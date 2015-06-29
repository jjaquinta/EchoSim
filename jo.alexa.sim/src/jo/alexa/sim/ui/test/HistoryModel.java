package jo.alexa.sim.ui.test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TransactionBean;

public class HistoryModel extends AbstractListModel<TransactionBean> implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -90087385111360011L;

    private RuntimeBean mRuntime;
    
    public HistoryModel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        mRuntime.addPropertyChangeListener("history", this);
    }
    
    @Override
    public int getSize()
    {
        return mRuntime.getHistory().size();
    }

    @Override
    public TransactionBean getElementAt(int index)
    {
        return mRuntime.getHistory().get(index);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        fireContentsChanged(this, 0, getSize());
    }

}
