package org.easetech.easytest.loader;

import org.easetech.easytest.annotation.DataLoader;

/**
 * 
 * An Enum identifying the type of loader to be used for loading the data.
 * A loader type can identify a loader to be framework based loader(CSV , Excel or XML)
 * or it can identify the loader to be user defined loader(CUSTOM).
 * Note that a user can provide a loader whose loaderType is already 
 * supported by the framework and still choose to mark it CUSTOM. In such a scenario, the framework
 * will use the Loader provided by the user to load the test data.
 *
 */
public enum LoaderType {

    /**
     * Identifies that the type of file is a framework based CSV file. 
     * This file should support the structure as identified in {@link CSVDataLoader}
     */
    CSV ,
    /**
     * Identifies that the type of file is a framework based XML file.
     * This is currently just a place holder and will be supported in future.
     */
    XML,
    /**
     * Identifies that the type of file is a framework based EXCEL file.
     * This is currently just a place holder and will be supported in future.
     */
    EXCEL,
    /**
     * Identifies that the type of file is a user defined custom type. 
     * This option is used in conjunction with {@link DataLoader} annotation.
     *  
     */
    CUSTOM,
    /**
     * Identifies that the user has not specified any specific loader type.
     * The framework should identify the type of file from its extension.
     * If the file type is any one of the available standard types(XML, Excel, XLS)
     * appropriate loaders will be called. Else the framework will assume the file to be loaded by 
     * the custom loader. In such a case, a custom loader implementation should be provided as
     * part of the {@link DataLoader}'s loader attribute declaration. 
     */
    NONE
}
