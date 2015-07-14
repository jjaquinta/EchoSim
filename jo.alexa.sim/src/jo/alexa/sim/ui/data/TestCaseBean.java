package jo.alexa.sim.ui.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestCaseBean
{
    private File                        mFile;
    private String                      mName;
    private List<ScriptTransactionBean> mScript;
    
    public TestCaseBean()
    {
        mName = "Test Case "+System.currentTimeMillis();
        mScript = new ArrayList<ScriptTransactionBean>();
    }
    
    public File getFile()
    {
        return mFile;
    }
    public void setFile(File file)
    {
        mFile = file;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public List<ScriptTransactionBean> getScript()
    {
        return mScript;
    }
    public void setScript(List<ScriptTransactionBean> script)
    {
        mScript = script;
    }
}
