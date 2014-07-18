
package org.easetech.easytest.example;

import javax.inject.Inject;
import junit.framework.Assert;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Display;
import org.easetech.easytest.annotation.Intercept;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.TestPolicy;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "classpath:org/easetech/data/testExcelData.xls" })
public class TestExcelDataLoader {
    
    @Intercept
    @Inject
    private ItemService itemService;
    
    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(TestExcelDataLoader.class);
    
    @Before
    public void before(){
        System.out.println("BEFORE");
    }
    
    @After
    public void after(){
        System.out.println("AFTER");
    }
    
//    @Test
//    public void testException(){
//        throw new RuntimeException("Failed");
//    }

    @Test
    public void getExcelTestData(@Param(name="libraryId")
    Float libraryId, @Param(name="itemId")
    Float itemId) {
        System.out.print("Executing getExcelTestData :");
        System.out.println("LibraryId is :" + libraryId + " and Item Id is :" + itemId);
    }
   
   

}
