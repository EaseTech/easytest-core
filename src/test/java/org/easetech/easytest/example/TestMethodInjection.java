package org.easetech.easytest.example;

import javax.inject.Inject;
import junit.framework.Assert;
import org.easetech.easytest.annotation.TestPolicy;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataDrivenTestRunner.class)
@TestPolicy(TestExcelDataLoaderPolicy.class)
public class TestMethodInjection {
    
    @Test
    @Inject
    public void testInjectData( RealItemService itemService){
        Assert.assertNotNull(itemService);
        
    }

}
