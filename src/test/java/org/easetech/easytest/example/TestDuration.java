package org.easetech.easytest.example;

import javax.inject.Inject;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Duration;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Repeat;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "classpath:org/easetech/data/testExcelData.xls" })
@TestConfigProvider({TestConfigProviderClass.class})
public class TestDuration {
    
    @Duration(timeInMillis=10)
    @Inject
    private RealItemService itemService;
    
    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(TestExcelDataLoader.class);
    
    @Before
    public void before(){
        System.out.println("BEFORE");
    }
    
    @Test
    @Duration(timeInMillis=3000 , forClass= RealItemService.class)   
    public Item testNoData() throws InterruptedException{
        System.out.println("Executing testNoData");
        return itemService.findItem(new LibraryId(Long.valueOf(1)), new ItemId(2L));
    }
    
    @Test
    @Repeat(times=2)
    public void a() {
        System.out.println("A Called");
        itemService.findItem(new LibraryId(1L), new ItemId(2L));
    }

    @Test
    @Repeat(times=3)
    public Item getExcelTestData(@Param(name="libraryId")
    Long libraryId, @Param(name="itemId")
    Long itemId) {
        return itemService.findItem(new LibraryId(libraryId), new ItemId(itemId));
        
    }
    
    

}
