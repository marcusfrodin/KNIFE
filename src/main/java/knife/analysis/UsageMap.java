package knife.analysis;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.inject.BindingAnnotation;
import com.google.inject.Singleton;
import static com.google.inject.internal.Preconditions.*;

/**
 * A mapping of dependencies between the classes in the input set.
 * Keeps track of which classes use what other classes, and also
 * a reverse index of what classes are used by which other classes.  
 */
@Singleton
public class UsageMap {

    private Set<String> globalUses                     = Sets.newHashSet();
    private Set<String> classes                        = Sets.newHashSet();
    private SetMultimap<String, String> importsByClass = HashMultimap.create();
    private SetMultimap<String, String> usagesByClass  = null;
    
    public void addFilesUsedByClass(String className, List<String> uses)
    {
        checkNotNull(className);
        checkArgument(className.length() > 0);
        importsByClass.putAll(className, uses);
        globalUses.addAll(uses);
        classes.addAll(uses);
        classes.add(className);
    }

    public void addFilesUsedByClass(String className, String use) {
        checkNotNull(className);
        checkArgument(className.length() > 0);
        importsByClass.put(className, use);
        globalUses.add(use);
        classes.add(className);
        classes.add(use);
    }

    public Set<String> getUsedGlobal()
    {
        return globalUses;
    }

    public Set<String> getImportsByClass(String className)
    {
        return importsByClass.get(className);
    }
    
    
    public SetMultimap<String, String> getImportsByClass()
    {
        return importsByClass;
    }
    
    public boolean hasClass(String className) {
        return classes.contains(className);
    }
    
    public Set<String> getUsagesByClass(String className)
    {
        return getUsagesByClass().get(className);
    }
    
    public SetMultimap<String, String> getUsagesByClass() 
    {
        if (usagesByClass == null) {
            usagesByClass = HashMultimap.create();
            Multimaps.invertFrom(importsByClass, usagesByClass);
        }
        return usagesByClass;
    }  

    public Set<String> getClasses()
    {
        return classes;
    }
    
    @BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public @interface New {}
    
}
