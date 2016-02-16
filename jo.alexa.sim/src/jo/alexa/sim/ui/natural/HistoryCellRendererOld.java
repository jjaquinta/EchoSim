package jo.alexa.sim.ui.natural;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TransactionBean;

public class HistoryCellRendererOld implements
        ListCellRenderer<TransactionBean>
{
    private RuntimeBean mRuntime;
    
    public HistoryCellRendererOld(RuntimeBean runtime)
    {
        mRuntime = runtime;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends TransactionBean> list,
            TransactionBean value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        HistoryTransactionPanel panel = new HistoryTransactionPanel(mRuntime);
        panel.setTrans(value);
        panel.setIndex(index);
        panel.setSelected(isSelected);
        panel.setFocus(cellHasFocus);
        return panel;
    }

}
