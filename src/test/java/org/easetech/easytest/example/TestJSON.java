package org.easetech.easytest.example;

import org.easetech.easytest.annotation.PreserveContext;

import org.junit.After;

import org.junit.Before;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Repeat;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths="jsonBasedData.csv")
public class TestJSON {
    
    private int count = 1;
    
    @Before
    public void before() {
        count = count + 1;
        //System.out.println("BEFOREEEEEEEEE");
    }
    
    @After
    public void after() {
        count = count - 1;
        System.out.println(count);
    }

    @Test
    @PreserveContext
    //@Repeat(times=3)
    public void testJSONObjectCSV(@Param(name="Item")Item item , LibraryId libId){
        System.out.println(item);
        //System.out.println(libId);
    }
    

    @Test
    @PreserveContext
    //@Repeat(times=3)
    @DataLoader(filePaths="jsonBasedData.xls")
    public void testJSONObject(@Param(name="Item")Item item , LibraryId libId){
        System.out.println(item);
        //System.out.println(libId);
    }    

}
