package org.easetech.easytest.example;

import org.easetech.easytest.annotation.Converters;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "getItemsData.csv" })
@Converters({ItemConverter.class})
public class TestConvertersForSuite {
    
    @Test
    public void testConverter(Item item) {
        System.out.println("Item value is :" + item.toString());
    }

}
