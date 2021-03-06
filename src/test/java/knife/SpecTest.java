package knife;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import knife.AnalysisRunner;
import knife.analysis.AnalysisModule;
import knife.spec.SpecLoader;
import knife.spec.TestSpec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.CountingOutputStream;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.Lists;

/**
 * 
 * Uses a combination of YAML files and parameterized JUnit tests to drive
 * testing directly from spec, eg specifying input and asserting output without
 * 
 * @author marcusf
 * 
 */
@RunWith(Parameterized.class)
public class SpecTest {

    private final TestSpec spec;
    private final Injector injector;

    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private CountingOutputStream errput = new CountingOutputStream(
            new ByteArrayOutputStream());

    private final String testCaseDir;
    private final int testNum;

    public SpecTest(String testCaseDir, int testNum, TestSpec spec) 
    {
        this.testCaseDir = testCaseDir;
        this.testNum = testNum;
        this.spec = spec;

        List<String> argv = getArguments(spec.getOptions());

        injector = Guice.createInjector(new AnalysisModule(spec.getAnalysis()),
                new TestModule(argv, output, errput, testCaseDir));
    }

    @Test
    public void run_test() throws Exception
    {
        AnalysisRunner target = injector.getInstance(AnalysisRunner.class);
        Output result = target.startAnalysis();
        result.write();

        // For now, split on line breaks to get list.
        assertEquals(fmtMessage("Bad output"),
                Strings.nullToEmpty(spec.getExpected()),
                Strings.nullToEmpty(output.toString()));

        assertEquals(fmtMessage((spec.getExpectError() ? "E" : "No e")
                + "rror expected"), spec.getExpectError(),
                errput.getCount() > 0);
    }

    private String fmtMessage(String msg)
    {
        return msg + " in test " + testCaseDir + "[" + testNum + "] ("
                + spec.getDescription() + ")";
    }

    @Parameters
    public static Collection<Object[]> constructTest() throws Exception
    {

        List<Object[]> results = Lists.newArrayList();

        List<String> availableTests = Resources.readLines(ClassLoader
                .getSystemClassLoader().getResource("spec-tests"), Charset
                .forName("UTF-8"));
        for (String testCase : availableTests) {
            int i = 0;
            List<TestSpec> specs = SpecLoader.loadSpecs(testCase);
            for (TestSpec spec : specs) {
                results.add(new Object[] { testCase, i, spec });
                i++;
            }
        }
        return results;
    }

    private List<String> getArguments(String options)
    {
        return Lists.newArrayList(Splitter.on(" ").split(options));
    }
}
