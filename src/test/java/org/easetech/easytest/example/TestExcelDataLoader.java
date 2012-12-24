
package org.easetech.easytest.example;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Intercept;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Provided;
import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.Report.EXPORT_FORMAT;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.loader.LoaderType;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "classpath:org/easetech/data/testExcelData.xls" }, loaderType = LoaderType.EXCEL)
@Report(outputFormats = {EXPORT_FORMAT.PDF})
@TestConfigProvider({TestConfigProviderClass.class})
public class TestExcelDataLoader {
    
    @Intercept
    @Provided
    private ItemService itemService;
    
    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(TestExcelDataLoader.class);
    
    @Before
    public void before(){
        System.out.println("IS THIS GETTING CALLEDDDDDDDDDDDDDDDDD");
    }
    
   

    @Test
    public void getExcelTestData(@Param(name = "libraryId")
    Float libraryId, @Param(name = "itemId")
    Float itemId) {
        System.out.print("Executing getExcelTestData :");
        System.out.println("LibraryId is :" + libraryId + " and Item Id is :" + itemId);
    }

    @Test
    @DataLoader(filePaths={"classpath:overrideExcelData.csv"} , loaderType=LoaderType.CSV)
    public Item getExcelTestDataWithDouble(@Param(name = "libraryId")
    Double libraryId, @Param(name = "itemId")
    Double itemId) {
        System.out.print("Executing getExcelTestDataWithDouble :");
        // if(itemId.equals(11568.0D)){
        // Assert.fail("ItemId is 11568 but should be 2");
        // }
        System.out.println("LibraryId Anuj is :" + libraryId + " and Item Id is :" + itemId);
        //itemService.testString = "String";
        Item item = itemService.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
            new ItemId(Long.valueOf(itemId.longValue())));
        return item;
    }

    @Test
    public void getExcelTestDataWithString(@Param(name = "libraryId")
    String libraryId, @Param(name = "itemId")
    String itemId) {
        System.out.print("Executing getExcelTestDataWithString :");
        System.out.println("LibraryId is :" + libraryId + " and Item Id is :" + itemId);
    }

    @Test
    public void getExcelTestDataNumberFormat() {
        System.out.print("Executing getExcelTestDataNumberFormat :");
        System.out.println("This is a simple test");
        //throw new RuntimeException("testqwe");
    }

    @Test
    @DataLoader(filePaths = { "classpath:org/easetech/data/test-update.xls" }, loaderType = LoaderType.EXCEL)
    public Item getExcelTestDataWithReturnType(@Param(name = "libraryId")
    Float libraryId, @Param(name = "itemId")
    Float itemId) {
        System.out.println("Executing  getExcelTestDataWithReturnType : ");
        LOG.debug("LibraryId is :" + libraryId + " and Item Id is :" + itemId);
        Item item = itemService.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
            new ItemId(Long.valueOf(itemId.longValue())));
        LOG.debug("return item: " + item.toString());
        
//        Boolean test = Boolean.FALSE;
//        
//        Assert.assertTrue( "test TRUE expected" ,test);
        return item;
    }
    
   

}
