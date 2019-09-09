package com.gitlab.jmeter.gitJmeter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import static org.apache.jmeter.JMeter.JMETER_REPORT_OUTPUT_DIR_PROPERTY;


public class App 
{
    public static void main( String[] args ) throws Exception
    {
        File jmeterHome = new File(System.getenv("JMETER_HOME"));
        String slash = System.getProperty("file.separator");

        if (jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
            	
                //JMeter Engine
		        StandardJMeterEngine jmeter = new StandardJMeterEngine();
		
		        JMeterUtils.setJMeterHome(jmeterHome.getPath());
		        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
		        
		        // Initialize Properties, logging, locale, etc.
		        //JMeterUtils.loadProperties("C:/apache-jmeter-4.0/bin/user.properties");
		        
		        JMeterUtils.setProperty("saveservice_properties", "/bin/saveservice.properties");
		        JMeterUtils.setProperty("search_paths", System.getProperty("java.class.path"));
		        //JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
		        JMeterUtils.initLocale();
		
		        // Initialize JMeter SaveService
		        SaveService.loadProperties();
		        
		        // Load existing .jmx Test Plan
		        //script.close();
		        File jmeterJmx = new File(jmeterHome.getPath() + slash + "bin" + slash + "protrade.jmx");
		        HashTree testPlanTree = SaveService.loadTree(new File(jmeterJmx.getPath()));
		        
		        Summariser summer = null;
		        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
		        if (summariserName.length() > 0) {
		            summer = new Summariser(summariserName);
		        }
		
		
		        // Store execution results into a .jtl file
		        String pattern = "yyyy-MM-dd";
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		        String date = simpleDateFormat.format(new Date());
		        
		        File jmeterReport = new File(jmeterHome.getPath() + slash + "testplans" + slash + "reports"+ slash + date);
		        String logFile = jmeterReport.getPath()+  slash + "example.jtl";
		        String report = jmeterReport.getPath()+  slash + "report";
		        FileUtils.deleteDirectory(jmeterReport); 
		        ResultCollector logger = new ResultCollector(summer);
		        logger.setFilename(logFile);
		        testPlanTree.add(testPlanTree.getArray()[0], logger);
		
		        // Run JMeter Test
		        jmeter.configure(testPlanTree);
		        jmeter.run();
		        
			    System.out.println("Test completed. See example.jtl file for results "+jmeterReport);

			    JMeterUtils.setProperty(JMETER_REPORT_OUTPUT_DIR_PROPERTY, report);
		        ReportGenerator generator = new ReportGenerator(logFile, null);
		        generator.generate();
            }
        }
    }
}
