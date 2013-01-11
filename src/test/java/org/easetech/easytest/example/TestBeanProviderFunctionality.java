package org.easetech.easytest.example;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.Provided;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.loader.LoaderType;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(DataDrivenTestRunner.class)
@TestConfigProvider({TestConfigProviderClass.class})
public class TestBeanProviderFunctionality {
    
    @Provided
    public ItemService itemService;

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(TestExcelDataLoader.class);


    @Test
    @DataLoader(filePaths={"classpath:overrideExcelData.csv"})
    public Item getExcelTestDataWithDouble(@Param("libraryId")
    Double libraryId, @Param("itemId")
    Double itemId) {
        Assert.assertNotNull(itemService);
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

}
