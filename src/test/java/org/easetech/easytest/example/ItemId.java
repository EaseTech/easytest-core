
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
    
    public ItemId() {
    }

    /**
     * @return the toString representation
     */
    @Override
    public String toString() {
        return "ItemId [id=" + id + "]";
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    

}
