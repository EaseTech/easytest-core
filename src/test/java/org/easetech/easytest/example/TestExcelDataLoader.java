
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
@TestPolicy(TestExcelDataLoaderPolicy.class)
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

    @Display(fields={"libraryId"})
    @Test
    public void getExcelTestData(@Param(name="libraryIds")
    String libraryId, @Param(name="itemId")
    Float itemId) {
        System.out.println("Executing getExcelTestData :");
        
    }

    @Test
    @DataLoader(filePaths={"classpath:overrideExcelData.csv"})
    public Item getExcelTestDataWithDouble(@Param(name="libraryId")
    Double libraryId, @Param(name="itemId")
    Double itemId) {
        
        Assert.fail("FAILING");
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
    @DataLoader(filePaths = { "classpath:org/easetech/data/test-update.xls" } , writeData=false)
    @Inject
    public Item getExcelTestDataWithReturnType(@Param(name="libraryId")
    Float libraryId, @Param(name="itemId")
    Float itemId , RealItemService localItemService) {
        Assert.assertNotNull(localItemService);
        System.out.println("Executing  getExcelTestDataWithReturnType : ");
        LOG.debug("LibraryId is :" + libraryId + " and Item Id is :" + itemId);
        if(libraryId == null){
            return null;
        }
        if(itemId == null){
            return null;
        }
        Item item = localItemService.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
            new ItemId(Long.valueOf(itemId.longValue())));
        
        Item itemFromClassService = itemService.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
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
    
    
    @Test
    @Inject
    public void testInjectData( RealItemService itemService){
        Assert.assertNotNull(itemService);
        
    }
   

}
