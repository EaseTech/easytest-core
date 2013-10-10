package org.easetech.easytest.example;

import org.easetech.easytest.annotation.Converters;

import org.junit.Test;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths = { "getDDTData.csv" })
@Converters({ItemConverter.class})
public class TestConverters {
    
    @Test
    public String testDDTConverter(Item item) {
        System.out.println("Item is :" + item);
        return item.toString();
    }

}
