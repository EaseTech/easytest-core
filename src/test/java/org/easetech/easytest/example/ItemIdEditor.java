
package org.easetech.easytest.example;

import java.beans.PropertyEditor;

import java.beans.PropertyEditorSupport;

/**
 * {@link PropertyEditor} for {@link ItemId}
 * 
 */
public class ItemIdEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String s) {
        try {
            setValue(new ItemId(Long.valueOf(s)));
        } catch (NumberFormatException pe) {
            IllegalArgumentException iae = new IllegalArgumentException(
                "The passed value is not a Number. The passed value is :" + s);
            throw iae;
        }
    }

}
