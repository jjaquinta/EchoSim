package jo.alexa.sim.ui.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestSuiteBean
{
    private File                        mFile;
    private String                      mName;
    private List<File>                  mCaseFiles;
    private List<TestCaseBean>          mCases;
    
    public TestSuiteBean()
    {
        mName = "Test Suite "+System.currentTimeMillis();
        mCases = new ArrayList<TestCaseBean>();
        mCaseFiles = new ArrayList<File>();
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
    public List<TestCaseBean> getCases()
    {
        return mCases;
    }
    public void setCases(List<TestCaseBean> cases)
    {
        mCases = cases;
    }

    public List<File> getCaseFiles()
    {
        return mCaseFiles;
    }

    public void setCaseFiles(List<File> caseFiles)
    {
        mCaseFiles = caseFiles;
    }

}
