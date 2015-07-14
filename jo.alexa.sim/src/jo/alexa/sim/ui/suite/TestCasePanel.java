package jo.alexa.sim.ui.suite;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import jo.alexa.sim.ui.data.TestCaseBean;
import jo.alexa.sim.ui.logic.ScriptLogic;

public class TestCasePanel extends JPanel
{
    private static final Color DESELECTED = Color.WHITE;
    private static final Color SELECTED = Color.LIGHT_GRAY;
    private static final Color PASS = new Color(0, 128, 0);
    private static final Color FAIL = Color.red;

    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private TestCaseBean   mCase;
    private int                     mIndex;
    private boolean                 mFocus;
    private boolean                 mSelected;
    
    private JLabel  mName;
    private JLabel  mState;
    
    public TestCasePanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mName = new JLabel();
        mState = new JLabel();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mName);
        add("East", mState);
    }

    private void initLink()
    {
    }

    public TestCaseBean getTestCase()
    {
        return mCase;
    }

    public void setTestCase(TestCaseBean trans)
    {
        mCase = trans;
        mName.setText(mCase.getName());
        Boolean same = ScriptLogic.pass(mCase);
        if (same == null)
        {
            mState.setForeground(null);
            mState.setText("-");
        }
        else
        {
            mState.setForeground(same ? PASS : FAIL);
            mState.setText(same ? "\u2714" : "\u2716");
        }
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
        //mName.setBackground(mSelected ? SELECTED : DESELECTED);
    }
}
