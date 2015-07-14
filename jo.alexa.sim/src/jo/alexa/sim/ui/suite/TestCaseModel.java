package jo.alexa.sim.ui.suite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TestCaseBean;

public class TestCaseModel extends AbstractListModel<TestCaseBean> implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = -90087385111360011L;

    private RuntimeBean mRuntime;
    
    public TestCaseModel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        mRuntime.addPropertyChangeListener("suite", this);
    }
    
    @Override
    public int getSize()
    {
        return mRuntime.getSuite().getCases().size();
    }

    @Override
    public TestCaseBean getElementAt(int index)
    {
        return mRuntime.getSuite().getCases().get(index);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        fireContentsChanged(this, 0, getSize());
    }

}
