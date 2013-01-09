
package org.easetech.easytest.io;

import org.easetech.easytest.annotation.DataLoader;

import org.easetech.easytest.util.CommonUtils;

import java.io.FileOutputStream;

import java.net.MalformedURLException;

import java.io.FileNotFoundException;

import java.io.FileInputStream;

import java.io.OutputStream;

import org.junit.Assert;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * A FileSystem based {@link Resource} implementation.
 * The {@link ResourceLoaderStrategy} will auto instantiate this class if the {@link DataLoader}
 * annotation specifies the filePaths attribute to contain a FileSystem resource. For eg.
 * <br><b>@DataLoader(filePahts={C:\\myComputer\\testData.csv})</b><br>
 *
 *@author Anuj Kuamr
 */
public class FileSystemResource implements Resource {

    /**
     * The path associated with the resource
     */
    private String path;
    
    /**
     * The {@link File} object representing the resource
     */
    private File file;


    /**
     * 
     * Construct a new FileSystemResource from the provided simple path
     * @param path the path where the file system resource resides
     */
    public FileSystemResource(String path) {
        if(path == null || path.length() <= 0){
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = CommonUtils.cleanPath(path);
        this.file = new File(path);
    }
    
    /**
     * 
     * Construct a new FileSystemResource from the provided File instance
     * @param file representing the file system resource
     */
    public FileSystemResource(File file){
        if(path == null || path.length() <= 0){
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = CommonUtils.cleanPath(file.getPath());
        this.file = file;
    }


    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Identifies whether the resource represented by this instance exists or not
     * @return boolean
     */
    public boolean exists() {
        return this.file.exists();
    }

    /**
     * Get the input stream represented by this resource
     * @return {@link InputStream}
     */
    public InputStream getInputStream() {
        try {
            return new FileInputStream(this.file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gwet the URL represented by this resource
     * @return URL instance
     */
    public URL getURL() {
        try {
            return this.file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the File object instance represented by this Resource
     * @return File instance
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Return the name of the resource
     * @return resource name
     */
    public String getResourceName() {
        return this.file.getPath();
    }

    /**
     * Get the {@link OutputStream} for the given resource
     * @return {@link OutputStream}
     */
    public OutputStream getOutputStream() {
        try {
            return new FileOutputStream(this.file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return
     */
    public String toString() {
        return "FileSystemResource [path=" + path + ", file=" + file + "]";
    }
    
    

}
