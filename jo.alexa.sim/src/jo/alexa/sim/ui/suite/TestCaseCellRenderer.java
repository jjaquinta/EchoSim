package jo.alexa.sim.ui.suite;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jo.alexa.sim.ui.data.TestCaseBean;

public class TestCaseCellRenderer implements
        ListCellRenderer<TestCaseBean>
{

    @Override
    public Component getListCellRendererComponent(
            JList<? extends TestCaseBean> list,
            TestCaseBean value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        TestCasePanel panel = new TestCasePanel();
        panel.setTestCase(value);
        panel.setIndex(index);
        panel.setSelected(isSelected);
        panel.setFocus(cellHasFocus);
        return panel;
    }

}
