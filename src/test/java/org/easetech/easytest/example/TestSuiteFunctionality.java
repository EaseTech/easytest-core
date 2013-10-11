package org.easetech.easytest.example;

import org.easetech.easytest.annotation.Parallel;

import org.easetech.easytest.runner.EasyTestSuite;

import org.easetech.easytest.annotation.ParallelSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test Parallel Suite functionality
 * 
 *
 */
@RunWith(EasyTestSuite.class)
@SuiteClasses({TestConverters.class , TestConvertersForSuite.class, TestExcelDataLoader.class,TestCombinedLoadingAndWriting.class, TestBeanProviderFunctionality.class})
@ParallelSuite
@Parallel
public class TestSuiteFunctionality {

}
