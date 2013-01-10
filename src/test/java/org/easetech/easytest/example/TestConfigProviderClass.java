package org.easetech.easytest.example;

import java.util.Properties;
import org.easetech.easytest.annotation.TestBean;
import org.easetech.easytest.annotation.TestProperties;
import org.junit.Ignore;

@Ignore
@TestProperties({"config.properties", "anotherConfig.properties"})
public class TestConfigProviderClass {
    
    private Properties props;
    
    @TestBean("itemService") public RealItemService itemService(){
        String propValue = props.getProperty("simple.property");
        System.out.println(propValue);
        return new RealItemService();
    }

}
