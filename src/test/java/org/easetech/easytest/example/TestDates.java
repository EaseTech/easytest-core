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
    public void testEmptyInteger(@Param(name="day") Integer day){
        //Assert.assertNotNull(day);
        System.out.println("testEmptyInteger : "+day);
    }
}
