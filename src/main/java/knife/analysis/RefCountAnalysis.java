package knife.analysis;

import knife.Common;
import knife.ListOutput;
import knife.Output;

import org.apache.commons.cli.CommandLine;


import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import com.google.inject.internal.Preconditions;

/**
 * RefCountAnalysis
 * 
 * o Options:
 *   -n Show which classes reference it for all classes that have 
 *      less than or equal to n references. Default is 3.
 *   
 * Displays a list of all classes used by the input files, ordered 
 * by how many times they are referenced, in ascending order.
 * 
 */
public class RefCountAnalysis implements Analysis {

    private final CommandLine opts;
    private final UsageMap depMap;
    private final ListOutput out;

    @Inject
    public RefCountAnalysis(CommandLine opts, UsageMap depMap, ListOutput out)
    {
        this.opts = opts;
        this.depMap = depMap;
        this.out = out;
        
    }
    
    public Output execute() {
        
        int MAX_COUNT = opts.hasOption(Common.OPT_COUNT) 
                ? Integer.parseInt(opts.getOptionValue(Common.OPT_COUNT))
                : 3;

        Preconditions.checkArgument(MAX_COUNT >= 0, "Can not specify a negative maximum of classes to show");
                
        SetMultimap<String, String> usages = depMap.getUsagesByClass();

        for (String dep: usages.keySet()) {
            int cnt = usages.get(dep).size();
            String s = "";
            s += cnt + " " + dep;
            if (cnt <= MAX_COUNT) {
                s += (": " + Common.orderedJoin(", ", usages.get(dep)));
            }
            out.add(s);
        }
        
        return out;
    }
    
}
