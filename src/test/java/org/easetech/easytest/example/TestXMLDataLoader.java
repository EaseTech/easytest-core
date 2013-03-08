
package org.easetech.easytest.example;

import java.util.Properties;

import org.easetech.easytest.annotation.TestProperties;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "input-data.xml" })
@TestProperties({ "config.properties" })
public class TestXMLDataLoader {

    /**
     * Automatically provided by the Framework with properties from files part of {@link TestProperties} annotation
     */
    @TestProperties({ "anotherConfig.properties" })
    private Properties loadedProperties;

    @Test
    public Item getItemsDataFromXMLLoader(@Param(name = "libraryId")
    String libraryId, @Param(name = "itemId")
    String itemId, @Param(name = "itemType")
    String itemType, @Param(name = "expectedItems")
    String expectedItems) {
        System.out.println("Print Property :" + loadedProperties.getProperty("simple.property"));
        System.out.print("Executing getItemsDataFromXMLLoader :");
        System.out.println("LibraryId :" + libraryId + " itemId : " + itemId + " itemType :" + itemType
            + " expectedItems :" + expectedItems);
        Item item = new Item();
        item.setDescription("Description Modified");
        item.setItemId(new ItemId(Long.valueOf(itemId)));
        item.setItemType(itemType);
        return item;
    }

    @Test
    @DataLoader(filePaths = { "input-data-mod.xml" })
    public Item getItemsDataFromXMLLoaderModified(@Param(name = "libraryId")
    String libraryId, @Param(name = "itemId")
    String itemId, @Param(name = "itemType")
    String itemType, @Param(name = "expectedItems")
    String expectedItems) {
        System.out.print("Executing getItemsDataFromXMLLoaderModified :");
        System.out.println("LibraryId :" + libraryId + " itemId : " + itemId + " itemType :" + itemType
            + " expectedItems :" + expectedItems);
        Item item = new Item();
        item.setDescription("Description Modified");
        item.setItemId(new ItemId(Long.valueOf(itemId)));
        item.setItemType(itemType);
        return item;
    }

    @Test
    public Long getDataWithExpectedResultSet(@Param(name = "libraryId")
    String libraryId, @Param(name = "itemId")
    Long itemId, @Param(name = "itemType")
    String itemType) {
        return itemId;
    }
    
    @Test
    @DataLoader(filePaths = { "input-data-mod.xml" })
    public void methodReturningNoData(@Param(name = "libraryId")
    String libraryId, @Param(name = "itemId")
    Long itemId, @Param(name = "itemType")
    String itemType){
        System.out.println("methodReturningNoData called. Only Duration should be written");
    }
    
    @Test
    @DataLoader(filePaths = { "input-data-mod.xml" })
    public Long methodTakingNoData(){
        System.out.println("methodTakingNoData called.");
        return 21L;
    }

}
