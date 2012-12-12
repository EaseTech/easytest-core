package org.easetech.easytest.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;
import org.easetech.easytest.reports.data.ReportDataContainer;
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
     * @see {@link RunAfters#evaluate()}
     * @throws Throwable
     */
    @Override
    public void evaluate() throws Throwable {
        LOG.info("evaluate started");
        List<Throwable> errors = new ArrayList<Throwable>();
        try {
            fNext.evaluate();
        } catch (Throwable e) {
            errors.add(e);
        } finally {
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
        
     // REPORTING
        if (testReportContainer != null) {
            Report annotation = testReportContainer.getTestClass().getAnnotation(Report.class);
            if (annotation != null) {
                String outputLocationFromAnnotation = annotation.outputLocation();
                String absoluteLocation = CommonUtils.getAbsoluteLocation(outputLocationFromAnnotation);
                String outputLocation = CommonUtils.createFolder(absoluteLocation);
                if (outputLocation != null) {
                    EXPORT_FORMAT[] outputFormats = annotation.outputFormats();
                    LOG.debug("Reporting phase started " + new Date());
                    LOG.debug("Writing reports to folder: " + outputLocation);
                    ReportRunner testReportHelper = new ReportRunner(testReportContainer);
                    testReportHelper.runReports(outputFormats, outputLocation);
                    LOG.debug("Reporting phase finished " + new Date());
                } else {
                    LOG.error("Can't write reports. Report output location " + outputLocationFromAnnotation + " can't be created.");
                }
            }
        }
        LOG.debug("evaluate finished");
    }

}
