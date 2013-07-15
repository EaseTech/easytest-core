package org.easetech.easytest.util;

import org.easetech.easytest.converter.ConverterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;
import org.easetech.easytest.reports.data.ReportDataContainer;
import org.easetech.easytest.reports.data.ReportParametersBean;
import org.easetech.easytest.reports.impl.ReportRunner;
import org.junit.AfterClass;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of {@link RunAfters} method to write the test data to the file at the end of executing all the test
 * methods in the test cases.
 * This class also encapsulates the logic of running the reports after the test execution.
 * The reports are run asynchronously so that the Test cases do not get halted for the expensive run of the reports.
 * 
 */
public class RunAftersWithOutputData extends Statement {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(RunAftersWithOutputData.class);

    /**
     * The actual data structure that contains both the input as well as output data
     */
    private Map<String, List<Map<String, Object>>> writableData;

    /**
     * An instance of {@link Statement}
     */
    private final Statement fNext;

    /**
     * The target class on which to invoke the {@link AfterClass} annotated method
     */
    private final Object fTarget;
    
    /**
     * The list of {@link TestInfo} objects that contains the information required to write data back to the file.
     */
    private final List<TestInfo> testInfoList;

    /**
     * List of {@link FrameworkMethod} that should be run as part of teh {@link AfterClass} annotation.
     */
    private final List<FrameworkMethod> fAfters;
    
    /**
     * The report container which holds all the reporting data
     */
    private ReportDataContainer testReportContainer;

    /**
     * Construct a new RunAftersWithOutputData
     * 
     * @param next the instance of {@link Statement} object
     * @param afters the list of {@link FrameworkMethod} that needs to be run after all the methods in the test class
     *            have been executed.
     * @param target the target instance of the class. In this case it will always be null since methods with
     *            {@link AfterClass} are always declared as static.
     * @param testInfoList the list of {@link TestInfo} containing information required to write data back to the file.
     * @param writableData the writable data that needs to be written to the file.
     * @param testReportContainer a container class representing everything required to generate reports
     */
    public RunAftersWithOutputData(Statement next, List<FrameworkMethod> afters, Object target,
        List<TestInfo> testInfoList, Map<String, List<Map<String, Object>>> writableData , ReportDataContainer testReportContainer) {
        super();
        this.fNext = next;
        this.fAfters = afters;
        this.fTarget = target;
        this.testInfoList = testInfoList;
        this.writableData = writableData;
        this.testReportContainer = testReportContainer;
    }

    /**
     * Evaluate all the test methods and then finally run all the afterClass methods.
     * Before afterClass annotated methods are executed, we start the asynchronous processing of the reports.
     * This will save us some time in case the afterClass annotation on the client 
     * does some process intensive tasks.
     * @see {@link RunAfters#evaluate()}
     * @throws Throwable
     */
    public void evaluate() throws Throwable {
        LOG.info("evaluate started");
        
        Future<Boolean> submit = null;
     
        List<Throwable> errors = new ArrayList<Throwable>();
        try {
            fNext.evaluate();
        } catch (Throwable e) {
            errors.add(e);
        } finally {
         // REPORTING first since we now have all the data to start the generation of reports
        	submit = processReports(testReportContainer);
        	for (FrameworkMethod each : fAfters)
                try {
                    each.invokeExplosively(fTarget);
                } catch (Throwable e) {
                    errors.add(e);
                }
        }
        MultipleFailureException.assertEmpty(errors);
        // Write any output test data to the file only if there is a write data associated with the test method.
        for (TestInfo testInfo : testInfoList) {
            if (testInfo.getFilePaths() != null && testInfo.getDataLoader() != null) {
                try {
                    ResourceLoader resourceLoader = new ResourceLoaderStrategy(testInfo.getTestClass().getJavaClass());
                    for(String filePath : testInfo.getFilePaths()){
                        testInfo.getDataLoader().writeData(resourceLoader.getResource(filePath), writableData, testInfo.getMethodName());
                    }
                } catch (Exception e) {
                    throw new ParameterizedAssertionError(e, testInfo.getMethodName(), testInfo);
                }
            }
        }
        
        if (submit != null) {
        	long start = System.nanoTime();
        	while(!submit.isDone());
        	long end = (System.nanoTime() - start) / 1000000;
        	LOG.debug("Writing reports took: {} ms.", end);
        }
        ConverterManager.cleanConverters();
        ConfigContext.cleanConfigContext();
        //DataContext.cleanData();
    }
    
    private Future<Boolean> processReports(ReportDataContainer testReportContainer) {
    	Future<Boolean> submit = null;
        if (testReportContainer != null) {
        	Report annotation = testReportContainer.getTestClass().getAnnotation(Report.class);
        	
        	ReportParametersBean reportParameters = null;
        	
        	if (System.getProperty("reports.generate") != null) {
        		reportParameters = new ReportParametersBean(System.getProperty("reports.format"), System.getProperty("reports.location"), System.getProperty("reports.package"));
        	} else if (annotation != null) {
                reportParameters = new ReportParametersBean(annotation.outputFormats(), annotation.outputLocation());
        	} else {
        		return null;
        	}
        	
        	String rawOutputLocation = reportParameters.getOutputLocation();
        	EXPORT_FORMAT[] outputFormats = reportParameters.getOutputFormats();
        	
            String absoluteLocation = CommonUtils.getAbsoluteLocation(rawOutputLocation);
            String outputLocation = CommonUtils.createFolder(absoluteLocation);
            
            if (outputLocation != null) {
                ExecutorService executor = Executors.newCachedThreadPool();
                
                LOG.info("Writing reports to folder: {} ", outputLocation);
                ReportRunner reportExecuter = new ReportRunner(testReportContainer, outputFormats,
                        outputLocation);
                submit = executor.submit(reportExecuter);
            } else {
                LOG.error("Can't write reports. Report output location {} "
                        + " can't be created.",  rawOutputLocation);
            }
        }
    	
    	return submit;
    }
    
    
}
