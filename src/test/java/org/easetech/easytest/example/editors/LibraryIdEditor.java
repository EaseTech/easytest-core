
package org.easetech.easytest.example.editors;

import org.easetech.easytest.example.LibraryId;

import java.beans.PropertyEditor;

import java.beans.PropertyEditorSupport;

/**
 * {@link PropertyEditor} for {@link LibraryId}
 * 
 */
public class LibraryIdEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String s) {
        try {
            setValue(new LibraryId(Long.valueOf(s)));
        } catch (NumberFormatException pe) {
            IllegalArgumentException iae = new IllegalArgumentException(
                "The passed value is not a Number. The passed value is :" + s);
            throw iae;
        }
    }

}
