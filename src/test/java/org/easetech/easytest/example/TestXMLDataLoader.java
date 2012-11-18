
package org.easetech.easytest.example;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.loader.LoaderType;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(org.easetech.easytest.runner.DataDrivenTestRunner.class)
@DataLoader(filePaths = { "input-data.xml" }, loaderType = LoaderType.XML)
public class TestXMLDataLoader {
    
    

    @Test
    public Item getItemsDataFromXMLLoader(@Param(name = "libraryId")
    String libraryId, @Param(name = "itemId")
    String itemId, @Param(name = "itemType")
    String itemType, @Param(name = "expectedItems")
    String expectedItems) {
        System.out.print("Executing getItemsDataFromXMLLoader :");
        System.out.println("LibraryId :" + libraryId + " itemId : " + itemId + " itemType :" + itemType
            + " expectedItems :" + expectedItems);
        Item item=  new Item();
        item.setDescription("Description Modified");
        item.setItemId(itemId);
        item.setItemType(itemType);
        return item;
    }
    
    @Test
    @DataLoader(filePaths = { "input-data-mod.xml" }, loaderType = LoaderType.XML)
    public Item getItemsDataFromXMLLoaderModified(@Param(name = "libraryId")
    String libraryId, @Param(name = "itemId")
    String itemId, @Param(name = "itemType")
    String itemType, @Param(name = "expectedItems")
    String expectedItems) {
        System.out.print("Executing getItemsDataFromXMLLoaderModified :");
        System.out.println("LibraryId :" + libraryId + " itemId : " + itemId + " itemType :" + itemType
            + " expectedItems :" + expectedItems);
        Item item=  new Item();
        item.setDescription("Description Modified");
        item.setItemId(itemId);
        item.setItemType(itemType);
        return item;
    }

}
