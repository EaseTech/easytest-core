package org.easetech.easytest.example;

import org.junit.Test;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.loader.LoaderType;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(appendData=true, filePaths={"MultipleFileDataExampleData1.csv","MultipleFileDataExampleData2.csv"}, loaderType=LoaderType.CSV , writeData=true)
public class TestMultipleFileDataLoadedExample {
    
    @Test
    public void multipleTestDataTest(@Param(name="libraryId")String libraryId , @Param(name="itemId")Integer itemId) {
        System.out.println("Lib Id : " + libraryId);
        System.out.println("Item Id : " + itemId);
    }
    

}
