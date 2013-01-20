package org.easetech.easytest.example;

import java.beans.PropertyEditorManager;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that checks for NULL, 0 and negative values as part of the Test Data
 *
 */
@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths={"borderConditions.csv"})
public class TestBorderConditions {
    
    public static void before(){
        PropertyEditorManager.registerEditor(ItemId.class, ItemIdEditor.class);
    }

    private ItemService itemService = new RealItemService();
    
    @Test
    public void checkForNull(@Param(name="itemId") ItemId itemId){
        System.out.println("CSV Item Id is :" + itemId);
        itemService.findItem(null, itemId);
    }
    
    @Test
    public void checkForNullWithTypedParam(@Param(name="itemId") ItemId itemId){
        System.out.println("CSV Item Id is :" + itemId);
        itemService.findItem(null, itemId);
    }
    
    @DataLoader(filePaths={"borderConditions.xls"})
    @Test
    public void checkForNullWithExcel(@Param(name="itemId") Long itemId){
        System.out.println("Excel Item Id is :" + itemId);
        itemService.findItem(null, new ItemId(itemId));
    }
    
    @DataLoader(filePaths={"borderConditions.xls"})
    @Test
    public void checkForNullTypedParamWithExcel(@Param(name="itemId") ItemId itemId){
        System.out.println("Excel Item Id is :" + itemId);
        itemService.findItem(null, itemId);
    }
    
    @DataLoader(filePaths={"borderConditions.xml"})
    @Test
    public void checkForNullWithXML(@Param(name="itemId") Long itemId){
        System.out.println("XML Item Id is :" + itemId);
        itemService.findItem(null, new ItemId(itemId));
    }

    @Test
    public void checkForNullTypedParameterWithXML(@Param(name="itemId") ItemId itemId){
        System.out.println("XML Item Id is :" + itemId);
        itemService.findItem(null, itemId);
    }
}
