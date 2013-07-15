package org.easetech.easytest.example;

import javax.inject.Inject;
import javax.inject.Named;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.annotation.TestConfigProvider;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@TestConfigProvider({TestConfigProviderClass.class})
public class TestConstructorFunctionality {
    
    @Inject
    @Named("itemService")
    public ItemService testSubject;
    
    @Test
    @DataLoader(filePaths = { "classpath:org/easetech/data/test-update.xls" })
    public Item getExcelTestDataWithReturnType(@Param(name="libraryId")
    LibraryId libraryId, @Param(name="itemId")
    ItemId itemId) {
        System.out.println("Executing  getExcelTestDataWithReturnType : ");
        
        if(libraryId == null){
            return null;
        }
        if(itemId == null){
            return null;
        }
        Item item = testSubject.findItem(libraryId, itemId);
            
        
//        Boolean test = Boolean.FALSE;
//        
//        Assert.assertTrue( "test TRUE expected" ,test);
        return item;
    }

}
