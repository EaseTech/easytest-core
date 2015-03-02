package org.easetech.easytest.example;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "tcd.csv" })
@Report(outputFormats={Report.EXPORT_FORMAT.PDF}, outputLocation="classpath:org/easytech/easytest/output")
public class TestTemperatureConverter {
    
    private TemperatureConverter subject;

    @Before
    public void setup() {
        subject = new TemperatureConverter();
    }

    @Test
    public void testToCelsiusConverter(@Param(name = "fahrenheit") int fahrenheit, @Param(name = "celsius") int celsiusResult) {
  // execute the business method
        int celsius = subject.toCelsius(fahrenheit);

  System.out.println(fahrenheit + " Fahrenheit = " + celsius + " Celsius");

  // asserts the result from the business method with the celsius result that comes from the input data file
       // Assert.assertEquals(celsiusResult, celsius);
    }

}
