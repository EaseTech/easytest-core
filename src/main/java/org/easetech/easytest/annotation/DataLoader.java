package org.easetech.easytest.annotation;

import java.lang.annotation.Inherited;

import org.easetech.easytest.loader.EmptyLoader;
import org.easetech.easytest.loader.Loader;
import org.easetech.easytest.loader.LoaderType;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * A method or class level annotation providing users with the ability to specify a data {@link Loader} strategy for their test class.
 * EasyTest supports CSV, EXCEL and XML based data loading. But it may not be sufficient in all the cases.
 * Also EasyTest's Data Loading Strategy may not suit every user. In such a case, a user can write his own Custom Loader and pass it to the Data Loader annotation
 * to supply your own custom Loader.<br>
 * 
 * For eg. this is how you can use it :<br>
 * <code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(loader=MyCustomDataLoader.class)<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *OR
 *<br><br>
 *<code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(filePaths={testData.json} , loader=MyCustomDataLoader.class)<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *
 *OR
 *
 *<br>
 *<code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(filePaths={testData.csv})<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *
 *OR
 *
 *<br>
 *<code>
 *   {@literal @}Test
 *   {@literal @}DataLoader(filePaths={testDataExcel.xls})<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 * Note that the custom Loader must implement the {@link Loader} interface and should have a no arg constructor.
 * 
 * 
 * @author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD , ElementType.TYPE})
@Inherited
public @interface DataLoader {
    
    /** The list of files representing the input test data for the given test method. */
    String[] filePaths() default {};

    /** The type of file that contains the data. Defaults to "none"*/
    LoaderType loaderType() default LoaderType.NONE;

    
    /** The custom Loader class that will be used by EasyTest to load the test data*/
    Class<? extends Loader> loader() default EmptyLoader.class;

}
