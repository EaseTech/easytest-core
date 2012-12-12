
package org.easetech.easytest.io;

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

public class FileSystemResource implements Resource {

    private String path;
    
    private File file;


    public FileSystemResource(String path) {
        if(path == null || path.isEmpty()){
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = CommonUtils.cleanPath(path);
        this.file = new File(path);
    }
    
    public FileSystemResource(File file){
        if(path == null || path.isEmpty()){
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


    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(this.file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL getURL() {
        try {
            return this.file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public String getResourceName() {
        return this.file.getPath();
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            return new FileOutputStream(this.file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
