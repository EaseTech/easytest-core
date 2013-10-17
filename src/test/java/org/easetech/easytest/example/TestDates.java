package org.easetech.easytest.example;

import java.sql.Date;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.TestPolicy;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@TestPolicy(TestDatesPolicy.class)
public class TestDates {

    @Test   
    public void testDatesFormating(@Param(name="date") Date date){
        Assert.assertNotNull(date);
        System.out.println("testDatesFormating : "+date);
    }
    
    @Test
    public void testEmptyInteger(@Param(name="day") Integer day , @Param(name="str", convertEmptyToNull=true)String str , @Param(name="long")Long longVal, @Param(name="Double")Double doubleVal){
        System.out.print("day = " + day + ",");
        System.out.print("str = " + str + ",");
        System.out.print("long = " + longVal + ",");
        System.out.println("Double = " + doubleVal);
    }
}
