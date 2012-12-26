package org.easetech.easytest.example;

import org.easetech.easytest.annotation.TestBean;
import org.junit.Ignore;

@Ignore
public class TestConfigProviderClass {
    
    @TestBean("itemService") public RealItemService itemService(){
        return new RealItemService();
    }

}
