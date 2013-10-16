package org.easetech.easytest.example;

import java.util.Date;
import junit.framework.Assert;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths={"paramTestConditions.csv"})
public class TestDates {

    @Test
    public void testDatesFormating(@Param(name="date") Date date){
        Assert.assertNotNull(date);
        System.out.println("testDatesFormating : "+date);
    }
    
    @Test
    public void testEmptyInteger(@Param(name="day") Integer day , @Param(name="str")String str , @Param(name="long")Long longVal, @Param(name="Double")Double doubleVal){
        System.out.print("day = " + day);
        System.out.print(" str = " + str);
        System.out.print(" long = " + longVal);
        System.out.println(" Double = " + doubleVal);
    }
}
