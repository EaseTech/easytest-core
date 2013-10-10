package org.easetech.easytest.example;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TestConverters.class , TestConvertersForSuite.class})

public class TestSuiteFunctionality {

}
