package knife;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.apache.commons.cli.CommandLine;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;


abstract class FileSupplierBase implements FileSupplier {

    private String[] argv;
    private String excludePattern;


    protected FileSupplierBase(CommandLine arguments) {
        argv = arguments.getArgs(); 
        excludePattern = arguments.getOptionValue(Common.OPT_EXCLUDE); 
    }
    
    protected List<String> getFileNames()
    {
        boolean hasExcludePattern = Strings.emptyToNull(excludePattern) != null;

        List<String> unfiltered = Lists.transform(newArrayList(argv), pathNormalizer());

        if (hasExcludePattern) {
            // Very windows hostile
            String excludePackage = excludePattern.replace('.', '/');
            return newArrayList(filter(unfiltered, not(containsPattern(excludePackage))));                
        }
        return unfiltered;
    }


    private static Function<String, String> pathNormalizer() {
        return new NormalizePathNames();
    }

    private static class NormalizePathNames implements Function<String, String>
    {
        public String apply(String input)
        {
            return on('/').skipNulls().join(Splitter.onPattern("/").omitEmptyStrings().split(input));
        }
    }
    
}