package org.easetech.easytest.example;

import javax.inject.Named;

import javax.inject.Inject;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.easetech.easytest.runner.TransactionalTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(TransactionalTestRunner.class)
@TestConfigProvider({TestConfigProviderClass.class})
public class TestBeanProviderFunctionality {
    
    @Inject
    @Named("itemService")
    public ItemService testSubject;

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(TestExcelDataLoader.class);


    @Test
    @DataLoader(filePaths={"classpath:overrideExcelData.csv"})
    public Item getExcelTestDataWithDouble(@Param(name="libraryId")
    Double libraryId, @Param(name="itemId")
    Double itemId) {
        Assert.assertNotNull(testSubject);
        System.out.print("Executing getExcelTestDataWithDouble :");
        // if(itemId.equals(11568.0D)){
        // Assert.fail("ItemId is 11568 but should be 2");
        // }
        System.out.println("LibraryId Anuj is :" + libraryId + " and Item Id is :" + itemId);
        //itemService.testString = "String";
        Item item = testSubject.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
            new ItemId(Long.valueOf(itemId.longValue())));
        return item;
    }

}
