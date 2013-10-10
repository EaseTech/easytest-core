
package org.easetech.easytest.example;

import javax.inject.Inject;
import junit.framework.Assert;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Intercept;
import org.easetech.easytest.annotation.Parallel;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Report;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "classpath:org/easetech/data/testExcelData.xls" })
@Report
@TestConfigProvider({TestConfigProviderClass.class})
@Parallel(threads=8)
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
    
    @Test
    public String testNoData(){
        System.out.println("Executing testNoData");
        return "Anuj";
    }

    @Test
    public void getExcelTestData(@Param(name="libraryId")
    Float libraryId, @Param(name="itemId")
    Float itemId) {
        System.out.println("Executing getExcelTestData :");
        
    }

    @Test
    @DataLoader(filePaths={"classpath:overrideExcelData.csv"})
    public Item getExcelTestDataWithDouble(@Param(name="libraryId")
    Double libraryId, @Param(name="itemId")
    Double itemId) {
        System.out.println("Executing getExcelTestDataWithDouble :");
        // if(itemId.equals(11568.0D)){
        // Assert.fail("ItemId is 11568 but should be 2");
        // }
        
        //itemService.testString = "String";
        Item item = itemService.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
            new ItemId(Long.valueOf(itemId.longValue())));
        return item;
    }

    @Test
    public void getExcelTestDataWithString(@Param(name="libraryId")
    String libraryId, @Param(name="itemId")String itemId, @Param(name="expected") String expectedItems) {
        System.out.println("Executing getExcelTestDataWithString :");
        
    }

    @Test(expected=NumberFormatException.class)
    public void getExcelTestDataNumberFormat() {
        System.out.println("Executing getExcelTestDataNumberFormat :");
        throw new NumberFormatException();
        
        //throw new RuntimeException("testqwe");
    }

    @Test
    @DataLoader(filePaths = { "classpath:org/easetech/data/test-update.xls" })
    public Item getExcelTestDataWithReturnType(@Param(name="libraryId")
    Float libraryId, @Param(name="itemId")
    Float itemId) {
        System.out.println("Executing  getExcelTestDataWithReturnType : ");
        LOG.debug("LibraryId is :" + libraryId + " and Item Id is :" + itemId);
        if(libraryId == null){
            return null;
        }
        if(itemId == null){
            return null;
        }
        Item item = itemService.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
            new ItemId(Long.valueOf(itemId.longValue())));
        LOG.debug("return item: " + item.toString());
        
//        Boolean test = Boolean.FALSE;
//        
//        Assert.assertTrue( "test TRUE expected" ,test);
        return item;
    }
    
    @Test
    public void testEmptyCellData(LibraryId libId , ItemId itemId){
        System.out.println("Executing testEmptyCellData");
        if(libId.getId() != null){
            Assert.fail("libId should be NULL");
        }else if(itemId.getId() != null){
            Assert.fail("itemId should be NULL");
        }
        
    }
    
    
    
   

}
