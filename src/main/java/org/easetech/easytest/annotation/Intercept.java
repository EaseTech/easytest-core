
package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.sf.cglib.proxy.MethodInterceptor;
import org.easetech.easytest.interceptor.DefaultMethodIntercepter;
import org.easetech.easytest.interceptor.MethodIntercepter;

/**
 * 
 * A field level annotation that can be used to intercept calls to methods within the subject under test. A user can
 * specify the interceptor it wants to use to intercept the call to the test subject's method. The interceptor should
 * implement {@link MethodIntercepter}. The default interceptor is {@link DefaultMethodIntercepter} that simply prints
 * the time taken in nano seconds by the test method on the console. <br>
 * Look here for a simple example :
 * https://github.com/EaseTech/easytest-core/blob/master/src/test/java/org/easetech/easytest
 * /example/TestExcelDataLoader.java
 * 
 * @author Anuj Kumar
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Intercept {
    /**
     * The {@link MethodInterceptor} to use to intercept method calls.
     */
    Class<? extends MethodIntercepter> interceptor() default DefaultMethodIntercepter.class;

}
