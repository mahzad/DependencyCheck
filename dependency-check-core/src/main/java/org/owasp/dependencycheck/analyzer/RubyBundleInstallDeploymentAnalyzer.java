package org.owasp.dependencycheck.analyzer;

import java.io.File;
import java.io.FilenameFilter;

import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.analyzer.exception.AnalysisException;
import org.owasp.dependencycheck.dependency.Dependency;

public class RubyBundleInstallDeploymentAnalyzer extends RubyGemspecAnalyzer {
	
	private static final String SPECIFICATIONS = "specifications";
	private static final String GEMS = "gems";

    /**
     * The logger.
     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(RubyBundleInstallDeploymentAnalyzer.class);
    
    /**
     * Only accept *.gemspec stubs generated by "bundle install --deployment" under "specifications" folder.
     */
	@Override
    public boolean accept(File pathname) {
		
        boolean accepted = super.accept(pathname);
        if(accepted == true) {
	        File parentDir = pathname.getParentFile();
	        accepted = parentDir != null && parentDir.exists() && parentDir.getName().equals(SPECIFICATIONS);
        }
        
        return accepted;
    }
	
	@Override
    protected void analyzeFileType(Dependency dependency, Engine engine)
            throws AnalysisException {
        super.analyzeFileType(dependency, engine);
        
        //find the corresponding gem folder for this .gemspec stub by "bundle install --deployment"
        File gemspecFile = dependency.getActualFile();
        String gemFileName = gemspecFile.getName();
        final String gemName = gemFileName.substring(0, gemFileName.lastIndexOf(".gemspec"));
        File specificationsDir = gemspecFile.getParentFile();
    	if(specificationsDir != null && specificationsDir.getName().equals(SPECIFICATIONS) && specificationsDir.exists()) {
    		File parentDir = specificationsDir.getParentFile();
    		if(parentDir != null && parentDir.exists()) {
    			File gemsDir = new File(parentDir, GEMS);
    			if(gemsDir != null && gemsDir.exists()) {
		    		File[] matchingFiles = gemsDir.listFiles(new FilenameFilter() {
		    		    public boolean accept(File dir, String name) {
		    		        return name.equals(gemName);
		    		    }
		    		});
		    		
		    		if(matchingFiles.length > 0) {
		    			String gemPath = matchingFiles[0].getAbsolutePath();
		    			if(gemPath != null)
		    				dependency.setPackagePath(gemPath);
		    		}
    			}
    		}
    	}
	}
}
