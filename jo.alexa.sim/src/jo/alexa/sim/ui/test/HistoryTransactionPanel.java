package jo.alexa.sim.ui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TransactionBean;
import jo.alexa.sim.ui.data.TransactionRenderOpsBean;
import jo.alexa.sim.ui.logic.TransactionLogic;

public class HistoryTransactionPanel extends JPanel implements PropertyChangeListener
{
    public static final Color DESELECTED = Color.WHITE;
    public static final Color SELECTED = Color.LIGHT_GRAY;

    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean       mRuntime;
    private TransactionBean   mTrans;
    private int                     mIndex;
    private boolean                 mFocus;
    private boolean                 mSelected;
    
    private JTextPane mClient;
    
    public HistoryTransactionPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mClient = new JTextPane();
        mClient.setEditable(false);
        mClient.setContentType("text/html");
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mClient);
    }

    private void initLink()
    {
        mRuntime.addPropertyChangeListener("renderOps", this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ev)
            {
                updateSizes();
            }
        });
    }

    public TransactionBean getTrans()
    {
        return mTrans;
    }

    public void setTrans(TransactionBean trans)
    {
        mTrans = trans;
        updateData();
    }
    
    private void updateData()
    {        
        TransactionRenderOpsBean ops = mRuntime.getRenderOps();
        String html = TransactionLogic.renderAsHTML(mTrans, ops);
        mClient.setText(html);
    }
    
    private void updateSizes()
    {
    }

    public int getIndex()
    {
        return mIndex;
    }

    public void setIndex(int index)
    {
        mIndex = index;
    }

    public boolean isFocus()
    {
        return mFocus;
    }

    public void setFocus(boolean focus)
    {
        mFocus = focus;
        setBorder(mFocus ? new MatteBorder(1, 5, 1, 1, Color.DARK_GRAY) : null);
    }

    public boolean isSelected()
    {
        return mSelected;
    }

    public void setSelected(boolean selected)
    {
        mSelected = selected;
        setBackground(mSelected ? SELECTED : DESELECTED);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (getParent() == null)
        {
            mRuntime.removePropertyChangeListener("renderOps", this);
            return;
        }
        updateData();
    }
}
