package SelectRFTestCase.SelectRFTestCase;


import java.io.Serializable;
import java.util.List;
import hudson.ExtensionPoint;
import hudson.DescriptorExtensionList;
import hudson.model.Hudson;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.AbstractDescribableImpl;

/**
 * The abstract base class of modules provides choices.
 * 
 * Create a new choice provider in following steps:
 * <ol>
 *    <li>Define a new class derived from ChoiceListProvider</li>
 *    <li>Override getChoiceList(), which returns the choices.</li>
 *    <li>Define the internal public static class named DescriptorImpl, derived from Descriptor&lt;ChoiceListProvider&gt;</li>
 *    <li>annotate the DescriptorImpl with Extension</li>
 * </ol>
 */
abstract public class ChoiceListProvider extends AbstractDescribableImpl<ChoiceListProvider> implements ExtensionPoint, Serializable
{
    private static final long serialVersionUID = 8965389708210167871L;
    
    /**
     * Returns the choices.
     * 
     * @return the choices list.
     */
    abstract public List<String> getChoiceList();
    
    /**
     * Returns the default choice value.
     * 
     * null indicates the first one is the default value.
     * 
     * @return the default choice value.
     */
    public String getDefaultChoice()
    {
        return null;
    }
    
    /**
     * Called when a build is triggered
     * 
     * Implementations can override this method, and do custom behavior.
     * Default implementation do nothing at all.
     * 
     * @param job the job with which this choice provider is used.
     * @param def the parameter definition the value specified
     * @param value the value specified.
     */
    public void onBuildTriggeredWithValue(
            AbstractProject<?, ?> job,
            SelectRFTestCase def,
            String value
    )
    {
        // Nothing to do.
    }
    
    /**
     * Called when a build is completed
     * 
     * Implementations can override this method, and do custom behavior.
     * Default implementation do nothing at all.
     * 
     * @param build the build with which this choice provider is used.
     * @param def the parameter definition the value specified
     * @param value the value specified.
     */
    public void onBuildCompletedWithValue(
            AbstractBuild<?, ?> build,
            SelectRFTestCase def,
            String value
    )
    {
        // Nothing to do.
    }
    
    /**
     * Returns all the ChoiceListProvider subclass whose DescriptorImpl is annotated with Extension.
     * @return DescriptorExtensionList of ChoiceListProvider subclasses.
     */
    static public DescriptorExtensionList<ChoiceListProvider,Descriptor<ChoiceListProvider>> all()
    {
        return Hudson.getInstance().<ChoiceListProvider,Descriptor<ChoiceListProvider>>getDescriptorList(ChoiceListProvider.class);
    }
}