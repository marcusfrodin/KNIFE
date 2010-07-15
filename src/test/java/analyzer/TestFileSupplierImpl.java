package analyzer;

import java.io.File;
import java.util.List;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.apache.commons.cli.CommandLine;

import com.google.inject.Inject;
import com.google.inject.internal.Lists;
import com.google.inject.name.Named;

public class TestFileSupplierImpl extends FileSupplierBase implements FileSupplier {

    private final String testCase;
    private final StandardJavaFileManager fm;
    
    @Inject
    public TestFileSupplierImpl(@Named("testCase") String testCase, 
                                CommandLine arguments, 
                                StandardJavaFileManager fm) 
    {
        super(arguments);
        this.fm = fm;
        this.testCase = testCase;
    }
    
    @Override
    public Iterable<? extends JavaFileObject> getFiles()
    {
        List<File> returnVal = Lists.newArrayList();
        List<String> fileNames = getFileNames();
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        for (String fileName: fileNames) {
            String fname = testCase + "/" + fileName;
            File f = new File(loader.getResource(fname).getPath());
            returnVal.add(f);
        }
        return fm.getJavaFileObjectsFromFiles(returnVal);
    }
}