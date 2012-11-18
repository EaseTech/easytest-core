
package org.easetech.easytest.example;

/**
 * 
 * An example of user defined Strongly typed object
 * 
 */
public class ItemId {

    /**
     * The id
     */
    private Long id;

    /**
     * 
     * Construct a new ItemId
     * 
     * @param id theid
     */
    public ItemId(Long id) {
        this.id = id;
    }

    /**
     * @return the toString representation
     */
    @Override
    public String toString() {
        return "ItemId [id=" + id + "]";
    }

}
