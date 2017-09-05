package SelectRFTestCase.SelectRFTestCase;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.bind.JavaScriptMethod;


import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * @author hzxu
 *
 */
public class SelectRFTestCase extends ParameterDefinition {

	private static final long serialVersionUID = 9032072543915872650L;

	private static final Logger LOGGER = Logger.getLogger(SelectRFTestCase.class.getName());
		
	
	static String sDisplayName = "Select RF Test Cases";
	static String sNameCanNotBeEmpty = "Name can not be empty";
	static String sPathCanNotBeEmpty = "PathCanNotBeEmpty=Path can not be empty";
	static String sPathDoesntExist = "Path %s doesn't seem to exist.";
	static String sNoObjectsFound = "No object found in path %s.";
	static String sRegExPatternNotValid = "No valid regular expression '%s'. Fail message: %s.";
	static String sNoObjectsFoundAtPath = "No objects of type FILE and matching include pattern %s and not matching exclude pattern %s found at path %s.";
	static String sSymlinkDetectionError = "Error in symbolic link detection for file %s.";
	static String workspace=System.getProperties().getProperty("user.home");
	
	public static enum FsObjectTypes implements Serializable {
		FILE
	}
	
	@Extension
	public static class DescriptorImpl extends ParameterDescriptor {
		String valid_path="C:\\Scripts\\RobotDemo";
		@Override
		public String getDisplayName() {
			return sDisplayName;
		}
		
		public FormValidation doCheckName(@QueryParameter final String name) throws IOException {
			if(StringUtils.isBlank(name)) {
				return FormValidation.error(sNameCanNotBeEmpty);
			}
			return FormValidation.ok();
		}
		
		public FormValidation doCheckPath(@QueryParameter final String path) throws IOException {

			LOGGER.warning("in DescriptorImpl");
			LOGGER.warning("check path:"+workspace+File.separator+path);
			valid_path = path;
			if(StringUtils.isBlank(workspace+File.separator+path)) {
				return FormValidation.error(sPathCanNotBeEmpty);
			}
						
			File dir = new File(workspace+File.separator+path);
			if(!dir.exists()) {
				LOGGER.warning(workspace+File.separator+path+" not be found");
				return FormValidation.error(sPathDoesntExist, workspace+File.separator+path);
			}
						
			if(dir.list().length == 0) {
				return FormValidation.warning(sNoObjectsFound, workspace+File.separator+path);
			}
			valid_path = path;
			return FormValidation.ok();

		}
		
		
		public FormValidation doCheckRegexIncludePattern(@QueryParameter final String regexIncludePattern) {
			return checkRegex(regexIncludePattern);
			
		}
		public FormValidation doCheckRegexExcludePattern(@QueryParameter final String regexExcludePattern) {
			return checkRegex(regexExcludePattern);
			
		}
		
		public ListBoxModel doFillDefaultTestCasesItems(@QueryParameter String path) {
			LOGGER.warning("in doFillDefaultTestCasesItems "+path);
            ListBoxModel m = new ListBoxModel();
            for (String s : getFileList(path))
                m.add(s,s);
            
            return m;
        }
		

		private FormValidation checkRegex(String regex) {
			try {
				Pattern.compile(regex);
				return FormValidation.ok();
			} catch (PatternSyntaxException pse) {
				return FormValidation.error(sRegExPatternNotValid, regex, pse.getDescription());
			}
		}
		
		
		public String[] getFileList(String robot_path){
			LOGGER.warning("in getFileList "+robot_path);
			File rootDir = new File(workspace+File.separator+robot_path);
			return rootDir.list();
		}
		

		
		
		public DescriptorImpl() {
			LOGGER.warning("in DescriptorImpl");
			
			Properties properties = System.getProperties(); 
			LOGGER.warning("user.home:"+properties.getProperty("user.home")); 
			LOGGER.warning("properties:"+properties.toString()); 
			/*
			Iterator it =  properties.entrySet().iterator();  
			while(it.hasNext())  
			{  
			    Entry entry = (Entry)it.next();  
			    LOGGER.warning(entry.getKey()+"=");  
			    LOGGER.warning(entry.getValue().toString());  
			}  
			*/
            load();
        }
		
		public boolean configure(org.kohsuke.stapler.StaplerRequest req,
                net.sf.json.JSONObject json)
         throws Descriptor.FormException{
			LOGGER.warning("in configure");
			return false;
		}
		
		@Override
        public SelectRFTestCase newInstance(StaplerRequest req,
                JSONObject formData)
                throws hudson.model.Descriptor.FormException
        {
			LOGGER.warning("in newInstance");
			LOGGER.warning(formData.toString());
			
			/*use multiple select instead of checkbox to select default test case; support doFill***
			JSONObject jsonObject = formData.getJSONObject("parameter");

			LOGGER.warning(jsonObject.toString());
			JSONArray jsonArray = jsonObject.getJSONArray("value");
			LOGGER.warning(jsonArray.toString());
			

			List<String> list = new ArrayList<String>();
			String[] files = getFileList(formData.getString("path"));
			for (int index = 0; index < jsonArray.size(); index ++)
			{
				if (jsonArray.getBoolean(index)){

					LOGGER.warning(files[index]+"has been added into list");
					list.add(files[index]);
				}
			}
			LOGGER.warning(list.toString());
			*/
			JSONArray jsonArray = formData.getJSONArray("defaultTestCases");
			List<String> list = new ArrayList<String>();
			for (int index = 0; index < jsonArray.size(); index ++)
			{
				LOGGER.warning(jsonArray.getString(index)+"has been added into list");
				list.add(jsonArray.getString(index));
			}
            return new SelectRFTestCase(
                    formData.getString("name"),
                    null,//formData.getString("description"),
                    formData.getString("path"),

                    list
                    //bindJSONWithDescriptor(req, formData, "choiceListProvider", ChoiceListProvider.class)
                    
            );
        }
		
	}
	

		

	private String path;

	private String defaultValue=null;
	private List<String> defaultlist=null;
	private String regexIncludePattern;
	private String regexExcludePattern;

	private Formatter formatter;

	SortedMap<String, Long> map;
	String newSelected;
	
	
	
	/**
	 * @param name
	 * @param description
	 */
	@DataBoundConstructor
	public SelectRFTestCase(String name, String description, String path, List<String> defaultlist) {

		super(name, description);
		LOGGER.warning("@@@@@@@@@in SelectRFTestCase");
		
		this.path = Util.fixNull(path);
		this.formatter = new Formatter();
		this.defaultlist = defaultlist;
		

		LOGGER.warning("@@@@@@@@this.path:"+this.path);
		LOGGER.warning("@@@@@@@@this.defaultlist:"+this.defaultlist.toString());
		LOGGER.warning("@@@@@@@@this.name:"+name);

	}

	
	
	@Override
	public ParameterValue createValue(StaplerRequest request) {
		LOGGER.warning("in createValue(StaplerRequest request)");
		String value[] = request.getParameterValues(getName());
		if(value == null) {
			return getDefaultParameterValue();
		}
		return null;
	}

	@Override
	public ParameterValue createValue(StaplerRequest request, JSONObject jO) {
		Object value = jO.get("value");
		String strValue = "";
		List<String> list = new ArrayList<String>();
		List<String> files = null;

		LOGGER.warning("in createValue(StaplerRequest request, JSONObject jO)");
		LOGGER.warning("jO:"+jO.toString()); 
		if(value instanceof String) {
			LOGGER.warning("value "+value);
			strValue = (String)value;
			strValue = defaultValue;
		}
		else if(value instanceof JSONArray) {
			JSONArray jsonValues = (JSONArray)value;
			//strValue = StringUtils.join(jsonValues.iterator(), ',');

			try {
				files = getFsObjectsList();
				for (int index = 0; index < jsonValues.size(); index ++)
				{
					if (jsonValues.getBoolean(index)){
						list.add(files.get(index));
					}
				}
				strValue = StringUtils.join(list.iterator(), ',');
				LOGGER.warning("strValue "+strValue);
				defaultlist=list;
				defaultValue=strValue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new FileSystemListParameterValue(getName(), strValue);
	}

	@Override
	public ParameterValue getDefaultParameterValue() {
		//String defaultValue = "";
		
		try {
			defaultValue = getEffectiveDefaultValue();
		} catch (IOException e) {
			LOGGER.warning(formatter.format(sSymlinkDetectionError, defaultValue).toString());
		}
		if(!StringUtils.isBlank(defaultValue)) {
			return new FileSystemListParameterValue(getName(), defaultValue);
		}
		return super.getDefaultParameterValue();
	}

	
	private String getEffectiveDefaultValue() throws IOException {
		
		List<String> defaultList = getFsObjectsList();
		//String defaultValue = defaultList.get(0);
		if (defaultValue == null){defaultValue = defaultList.get(0);}
		return defaultValue;
	}

	public List<String> getDefaultList(){
		LOGGER.warning("in getDefaultList");
		return defaultlist;
	}
	
	public String getDefaultString(){
		return defaultValue;
	}
	


	public List<String> getFsObjectsList() throws IOException {
		LOGGER.warning("in getFsObjectsList");
		
		LOGGER.warning("WORKSPACE:"+workspace);
		
		if (path != null) {LOGGER.warning("path:"+path);}
		else {LOGGER.warning("path is null");}
		map = new TreeMap<String, Long>();
		File rootDir = new File(workspace+File.separator+path);
		File[] listFiles = rootDir.listFiles();
		
				createFileMap(listFiles);
		

		return sortList();
	}
	
	



	List<String> sortList() {
		List<String> list;
		
		if (map.isEmpty()) {
			list = new ArrayList<String>();
			String msg = formatter.format(sNoObjectsFoundAtPath, getRegexIncludePattern(), getRegexExcludePattern(), getPath()).toString() ;
			LOGGER.warning(msg);
			list.add(msg);
		}else {
			// Sorting:
			list = createTimeSortedList();
		}
		
		return list;
	}
	



	List<String> createTimeSortedList() {
		List<String> list = new ArrayList<String>();
		
		Collection<Long> valuesC = map.values();
		List<Long> sortList = new ArrayList<Long>(valuesC);
		Collections.sort(sortList);

		// iterate over sorted values
		for (Long value : sortList) {

			if (map.containsValue(value)) {

				// key with lowest value will be added first
				for (String key : map.keySet()) {
					if (value == map.get(key)) {
						list.add(key);
					}
				}
			}
		}
		
		return list;
	}



	private boolean isSymlink(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("File must not be null");
		File canon;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File canonDir = file.getParentFile().getCanonicalFile();
			canon = new File(canonDir, file.getName());
		}
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}
	

	private boolean isPatternMatching(String name) {

		if (getRegexIncludePattern().equals("") && getRegexExcludePattern().equals("")) {
			return true;
		}
		
		if (!getRegexIncludePattern().equals("") && getRegexExcludePattern().equals("")) {
			return name.matches(getRegexIncludePattern());
		}
		if (getRegexIncludePattern().equals("") && !getRegexExcludePattern().equals("")) {
			return !name.matches(getRegexExcludePattern());
		}
						
		return name.matches(getRegexIncludePattern()) && !name.matches(getRegexExcludePattern());
	}

		
	private void createSymlinkMap(File[] listFiles) throws IOException {
		
		for (File file : listFiles) {
			if (!file.isHidden() && isSymlink(file) && isPatternMatching(file.getName())) {
				map.put(file.getName(),file.lastModified());
				LOGGER.finest("add " + file);
			}
		}
	}


	private void createDirectoryMap(File[] listFiles) throws IOException {
		
		for (File file : listFiles) {
			if (!file.isHidden() && file.isDirectory() && !isSymlink(file) && isPatternMatching(file.getName())) {
				map.put(file.getName(),file.lastModified());
				LOGGER.finest("add " + file);
			}
		}
	}


	private void createFileMap(File[] listFiles) throws IOException {
		
		for (File file : listFiles) {
			if (!file.isHidden() && file.isFile() && !isSymlink(file) && isPatternMatching(file.getName())) {
				map.put(file.getName(),file.lastModified());
				LOGGER.finest("add " + file);
			}
		}
	}





	private void createAllObjectsMap(File[] listFiles) {
		
		for (File file : listFiles) {
			if (!file.isHidden() && isPatternMatching(file.getName())) {
				map.put(file.getName(),file.lastModified());
				LOGGER.finest("add " + file);
			}
		}
	}



	/*
		Creates list to display in config.jelly
	*/
	public List<String> getJellyFsObjectTypes() {

		LOGGER.warning("in getJellyFsObjectTypes");
		ArrayList<String> list = new ArrayList<String>();
			for (FsObjectTypes type : FsObjectTypes.values()) {
				String string = type.toString();
				LOGGER.finest("# add " + string);
				list.add(string);
			}
			
		
		return list;
	}
	
	

	public String getPath() {
		return path;
	}


	public String getRegexIncludePattern() {
		return "";
	}

	public String getRegexExcludePattern() {
		return "";
	}
	
	private int tmp;
	 /**
     * The annotation exposes this method to JavaScript proxy.
     */
    @JavaScriptMethod
    public int increment(int n) {
        return tmp+=n;
    }
    

    
    

}