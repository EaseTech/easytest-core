package org.easetech.easytest.example;

import org.easetech.easytest.annotation.Repeat;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths="jsonBasedData.csv")
public class TestJSON {
    

    @Test
    @Repeat(times=3)
    public void testJSONObjectCSV(@Param(name="Item")Item item , LibraryId libId){
        System.out.println(item);
        //System.out.println(libId);
    }
    

    @Test
    @Repeat(times=3)
    @DataLoader(filePaths="jsonBasedData.xls")
    public void testJSONObject(@Param(name="Item")Item item , LibraryId libId){
        System.out.println(item);
        //System.out.println(libId);
    }    

}
