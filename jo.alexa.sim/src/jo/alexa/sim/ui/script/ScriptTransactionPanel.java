package jo.alexa.sim.ui.script;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.ui.data.ScriptTransactionBean;

public class ScriptTransactionPanel extends JPanel
{
    private static final Color DESELECTED = Color.WHITE;
    private static final Color SELECTED = Color.LIGHT_GRAY;
    private static final Color PASS = new Color(0, 128, 0);
    private static final Color FAIL = Color.red;

    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private ScriptTransactionBean   mTrans;
    private int                     mIndex;
    private boolean                 mFocus;
    private boolean                 mSelected;
    
    private JLabel  mInputText;
    private JLabel  mExpectedOutputText;
    private JLabel  mExpectedOutputMode;
    private JLabel  mActualOutputText;
    private JLabel  mActualOutputState;
    private JPanel  mLine2;
    private JPanel  mLine3;
    
    public ScriptTransactionPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mInputText = new JLabel();
        mInputText.setForeground(Color.blue);
        mExpectedOutputText = new JLabel();
        mExpectedOutputMode = new JLabel();
        mActualOutputText = new JLabel();
        mActualOutputState = new JLabel();
    }

    private void initLayout()
    {
        setLayout(new GridLayout(3, 1));
        add(mInputText);
        mLine2 = new JPanel();
        add(mLine2);
        mLine2.setLayout(new BorderLayout());
        mLine2.add("Center",  mExpectedOutputText);
        mLine2.add("East", mExpectedOutputMode);
        mLine3 = new JPanel();
        add(mLine3);
        mLine3.setLayout(new BorderLayout());
        mLine3.add("Center",  mActualOutputText);
        mLine3.add("East", mActualOutputState);
    }

    private void initLink()
    {
    }

    public ScriptTransactionBean getTrans()
    {
        return mTrans;
    }

    public void setTrans(ScriptTransactionBean trans)
    {
        mTrans = trans;
        if ((trans.getRequestType() != null) && mTrans.getRequestType().equals(RequestLogic.LAUNCH_REQUEST))
            mInputText.setText("<launch session>");
        else if ((trans.getRequestType() != null) && mTrans.getRequestType().equals(RequestLogic.SESSION_ENDED_REQUEST))
            mInputText.setText("<session end>");
        else
            mInputText.setText(mTrans.getInputText());
        mExpectedOutputText.setText(mTrans.getOutputText());
        updateExpectedResult();
        if ((mTrans.getActualResult() != null) && (mTrans.getActualResult().getOutputText() != null))
        {
            mActualOutputText.setText(mTrans.getActualResult().getOutputText());
            boolean same = mTrans.getActualResult().getOutputText().equals(mTrans.getOutputText());
            mActualOutputText.setForeground((same == mTrans.isExpectedResult()) ? PASS : FAIL);
            mActualOutputState.setForeground((same == mTrans.isExpectedResult()) ? PASS : FAIL);
            mActualOutputState.setText((same == mTrans.isExpectedResult()) ? "\u2714" : "\u2716");
        }
        else
            mActualOutputText.setText("");
    }

    private void updateExpectedResult()
    {
        mExpectedOutputText.setForeground(mTrans.isExpectedResult() ? PASS : FAIL);
        mExpectedOutputMode.setText(mTrans.isExpectedResult() ? "\u2714" : "\u2716");
        mExpectedOutputMode.setForeground(mTrans.isExpectedResult() ? PASS : FAIL);
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
        mLine2.setBackground(mSelected ? SELECTED : DESELECTED);
        mLine3.setBackground(mSelected ? SELECTED : DESELECTED);
    }
}
