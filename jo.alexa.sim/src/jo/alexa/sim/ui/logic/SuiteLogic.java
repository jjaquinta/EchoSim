package jo.alexa.sim.ui.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TestCaseBean;
import jo.alexa.sim.ui.data.TestSuiteBean;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class SuiteLogic
{
    public static Boolean pass(TestSuiteBean suite)
    {
        Boolean result = null;
        for (TestCaseBean casa : suite.getCases())
        {
            Boolean r = ScriptLogic.pass(casa);
            if (r == null)
                continue;
            if (r == Boolean.FALSE)
                return Boolean.FALSE;
            result = true;
        }
        return result;
    }

    public static void insertCurrentCase(RuntimeBean runtime, int idx)
    {
        if (idx > runtime.getSuite().getCases().size())
            idx = runtime.getSuite().getCases().size();
        TestCaseBean casa = runtime.getScript();
        runtime.getSuite().getCases().add(idx, casa);
        runtime.getSuite().getCaseFiles().add(idx, casa.getFile());
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }

    public static void clearResults(RuntimeBean runtime)
    {
        for (TestCaseBean casa : runtime.getSuite().getCases())
            ScriptLogic.clearResults(casa);
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }

    public static void clearSuite(RuntimeBean runtime)
    {
        runtime.getSuite().getCases().clear();
        runtime.getSuite().getCaseFiles().clear();
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }

    public static void runSuite(final RuntimeBean runtime)
    {
        runtime.setScriptRunning(true);
        Thread t = new Thread("Suite Run") { public void run() { doRun(runtime); } };
        t.start();
    }
    
    public static void doRun(RuntimeBean runtime)
    {
        for (int i = 0; i < runtime.getSuite().getCaseFiles().size(); i++)
        {
            try
            {
                File caseFile = runtime.getSuite().getCaseFiles().get(i);
                ScriptLogic.load(runtime, caseFile);
                TestCaseBean casa = runtime.getScript();
                if (runtime.getSuite().getCases().size() > i)
                    runtime.getSuite().getCases().remove(i);
                runtime.getSuite().getCases().add(i, casa);
                ScriptLogic.run(runtime);
                while (runtime.isScriptRunning())
                    try
                    {
                        Thread.sleep(250);
                    }
                    catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                runtime.firePropertyChange("suite", null, runtime.getSuite());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
        }
        runtime.setScriptRunning(false);
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }

    public static void load(RuntimeBean runtime, File source) throws IOException
    {
        InputStream is = new FileInputStream(source);
        Reader rdr = new InputStreamReader(is, "utf-8");
        JSONObject jtestsuite;
        try
        {
            jtestsuite = (JSONObject)RuntimeLogic.mParser.parse(rdr);
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }
        rdr.close();
        TestSuiteBean suite = FromJSONLogic.fromJSONTestSuite(jtestsuite, source);
        suite.setFile(source);
        runtime.setSuite(suite);
        RuntimeLogic.setProp("app.suite", source.toString());
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }

    public static void save(RuntimeBean runtime, File source) throws IOException
    {
        runtime.getSuite().setFile(source);
        JSONObject testsuite = ToJSONLogic.toJSONTestSuite(runtime.getSuite());
        OutputStream os = new FileOutputStream(source);
        Writer wtr = new OutputStreamWriter(os, "utf-8");
        wtr.write(testsuite.toJSONString());
        wtr.close();
        RuntimeLogic.setProp("app.suite", source.toString());
    }

    public static void deleteCases(RuntimeBean runtime, List<TestCaseBean> sel)
    {
        for (TestCaseBean casa : sel)
        {
            int idx = runtime.getSuite().getCases().indexOf(casa);
            if (idx >= 0)
            {
                runtime.getSuite().getCases().remove(idx);
                runtime.getSuite().getCaseFiles().remove(idx);
            }
        }
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }
    
    public static void setName(RuntimeBean runtime, String newValue)
    {
        runtime.getSuite().setName(newValue);
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }

    public static void moveUp(RuntimeBean runtime, int[] selectedIndices)
    {
        List<int[]> intervals = ScriptLogic.makeIntervals(selectedIndices);
        for (int[] interval : intervals)
            if (interval[0] > 0)
            {
                TestCaseBean trans = runtime.getSuite().getCases().get(interval[0] - 1);
                runtime.getSuite().getCases().remove(interval[0] - 1);
                runtime.getSuite().getCases().add(interval[1], trans);
            }
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }

    public static void moveDown(RuntimeBean runtime, int[] selectedIndices)
    {
        List<int[]> intervals = ScriptLogic.makeIntervals(selectedIndices);
        for (int[] interval : intervals)
            if (interval[1] < runtime.getSuite().getCases().size() - 1)
            {
                TestCaseBean trans = runtime.getSuite().getCases().get(interval[1] + 1);
                runtime.getSuite().getCases().remove(interval[1] + 1);
                runtime.getSuite().getCases().add(interval[0], trans);
            }
        runtime.firePropertyChange("suite", null, runtime.getSuite());
    }
}
