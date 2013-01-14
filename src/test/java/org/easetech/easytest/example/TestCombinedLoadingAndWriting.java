package org.easetech.easytest.example;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(org.easetech.easytest.runner.DataDrivenTestRunner.class)
public class TestCombinedLoadingAndWriting {

    @Test
    @DataLoader(filePaths = { "input-data-mod-again.xml" })
    public Item getItemsDataFromXMLLoaderModified(@Param("libraryId")
    String libraryId, @Param("itemId")
    String itemId, @Param("itemType")
    String itemType, @Param("expectedItems")
    String expectedItems) {
        System.out.print("Executing TestCombinedLoadingAndWriting#getItemsDataFromXMLLoaderModified :");
        System.out.println("LibraryId :" + libraryId + " itemId : " + itemId + " itemType :" + itemType
            + " expectedItems :" + expectedItems);
        Item item=  new Item();
        item.setDescription("Description Modified Once Again");
        item.setItemId(itemId);
        item.setItemType(itemType);
        return item;
    }
    
    @Test
    @DataLoader(filePaths = { "org/easetech/data/test-combined.xls" })
    public Item getExcelTestDataWithReturnType(@Param("libraryId")
    Float libraryId, @Param("itemId")
    Float itemId) {
        System.out.print("Executing  TestCombinedLoadingAndWriting#getExcelTestDataWithReturnType : ");
        ItemService itemService = new RealItemService();
        if(libraryId == null || itemId == null){
            return null;
        }
        Item item = itemService.findItem(new LibraryId(Long.valueOf(libraryId.longValue())),
            new ItemId(Long.valueOf(itemId.longValue())));
        return item;
    }
    
    @Test  
    public void getExcelTestDataWithReturnTypeFail() {
    	System.out.println("print");
//        Assert.fail("Failed");
    }

}
