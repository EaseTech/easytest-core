package org.easetech.easytest.annotation;

import org.easetech.easytest.loader.EmptyLoader;
import org.easetech.easytest.loader.Loader;
import org.easetech.easytest.loader.LoaderType;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * A method or class level annotation providing users with the ability to specify a data Loader strategy for their test class.
 * EasyTest supports CSV, EXCEL and XML based data loading. But it may not be sufficient in all the cases.
 * Also EasyTest's Data Loading Strategy may not suit every user. In such a case, a user can use the loader 
 * attribute along with the attribute <B>loaderType = {@link LoaderType#CUSTOM}</B> 
 * to supply your own custom Loader.<br>
 * 
 * For eg. this is how you can use it :
 * <code>
 *   @Theory
 *   @DataLoader(loader=MyCustomDataLoader.class, loaderType=LoaderType.CUSTOM)<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *OR
 *<br>
 *<code>
 *   @Theory
 *   @DataLoader(filePaths={testData.csv} , loader=MyCustomDataLoader.class, loaderType=LoaderType.CUSTOM)<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 *
 *OR
 *
 *<br>
 *<code>
 *   @Theory
 *   @DataLoader(filePaths={testData.csv}, loaderType=LoaderType.CSV)<br>
 *    public void testGetItems(........<br>
 * </code>
 *<br>
 * Note that the custom Loader must implement the {@link Loader} interface and should have a no arg constructor.
 * 
 *  @author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD , ElementType.TYPE})
public @interface DataLoader {
    
    /** The list of files representing the input test data for the given test method. */
    String[] filePaths() default {};

    /** The type of file that contains the data. Defaults to "csv"*/
    LoaderType loaderType() default LoaderType.CSV;

    
    /** The custom Loader class that will be used by EasyTest to load the test data*/
    Class<? extends Loader> loader() default EmptyLoader.class;

}
