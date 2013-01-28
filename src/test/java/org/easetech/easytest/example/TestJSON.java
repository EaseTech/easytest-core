package org.easetech.easytest.example;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths="jsonBasedData.xls")
public class TestJSON {
    

    @Test
    public void testJSONObject(@Param(name="Item")Item item , LibraryId libId){
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + item);
        //System.out.println(libId);
    }


}
