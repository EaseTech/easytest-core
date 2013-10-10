package org.easetech.easytest.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import net.sf.cglib.proxy.MethodInterceptor;
import org.easetech.easytest.interceptor.DefaultMethodIntercepter;
import org.easetech.easytest.interceptor.Empty;
import org.easetech.easytest.interceptor.MethodIntercepter;

/**
 * Duration is similar to {@link Intercept} annotation in that it intercepts the call to the methods
 * of the Class that it is annotating. But it has got extra attributes for the user to specify the 
 * maximum duration a method under test should take to execute.
 * If the total time taken by the method exceeds the time specified (using the attribute <i><b>timeInMillis</b></i>),
 * the test method will fail.
 *  <br>
 * This annotation can be used both at the FIELD level as well as the METHOD level
 * <br><br>
 * <B>Usage at Field Level:</B>
 * 
 * <pre>{@code}class TestClass {
 * 
 *      {@literal @}Duration(timeInMillis=100)
 *      private ItemService testSubject;
 * }</pre>
 * <br>
 * OR , in case the user also needs to specify its custom {@link MethodIntercepter} :
 * <pre>{@code}class TestClass {
 * 
 *      {@literal @}Duration(timeInMillis=100 , interceptor=MyCustomInterceptor.class)
 *      private ItemService testSubject;
 * }</pre>
 * 
 * In both the above cases, EasyTest will measure the time taken by each method of ItemService.
 * If the time taken by the method is more than the time specified, the test will fail with appropriate failure reason.
 * <br><br>
 * <B>Usage at Test Method Level</B>
 * 
 * <pre>
 * {@code} class TestClass {
 * 
 *      private ItemService testSubject;
 *      
 *      {@literal @}Test
 *      {@literal @}Duration(forClass=ItemService.class , timeInMillis=50)
 *      private Item testGetItem({@literal @}Param(name='itemId') String itemId) {
 *          return testSubject.getItem(itemId);
 *      }
 * }</pre>
 * 
 * In this case, EasyTest will compare the time taken, by the method under test, with the time specified in the Duration annotation,
 * only for this test method.
 * <br><br>
 * <B>Special case : When the Duration annotation is specified both at the field level and at the method level</B>
 * <pre>
 * {@code} class TestClass {
 *      {@literal @}Duration(timeInMillis=100)
 *      private ItemService testSubject;
 *      
 *      {@literal @}Test
 *      {@literal @}Duration(forClass=ItemService.class , timeInMillis=50)
 *      private Item testGetItem({@literal @}Param(name='itemId') String itemId) {
 *          return testSubject.getItem(itemId);
 *      }
 * }</pre>
 * In this case, EasyTest will override the <i>timeInMillis</i> value supplied at the field level, with the value supplied at the method level.
 * Once the method is finished, it will revert back to the original value specified at the field level. Thus if you have second test method 
 * that does not have Duration annotation, then the measure of time taken by the method will be 100 milliseconds and not 50 ms.
 * 
 * @author Anuj Kumar
 *  
 */
@Target({ ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Duration {
    
    /** The time in milliseconds. This is the maximum time a method under test should take to execute */
    long timeInMillis();
    
    /** The Class that should be proxied to provide the time comparison functionality.
     * Only required in case the Duration annotation is specified at the test method level.
     * Its value will be ignored in case it is specified when the annotation is used at the field level
     */
    Class<?> forClass() default Empty.class;
    
    /**
     * The {@link MethodInterceptor} to use to intercept method calls.
     */
    Class<? extends MethodIntercepter> interceptor() default DefaultMethodIntercepter.class;

}
