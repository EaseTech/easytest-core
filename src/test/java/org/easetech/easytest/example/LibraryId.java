
package org.easetech.easytest.example;

/**
 * 
 * An example of user defined Strongly typed object
 * 
 */
public class LibraryId {

    /**
     * The id
     */
    private Long id;

    /**
     * 
     * Construct a new LibraryId
     * 
     * @param id the id
     */
    public LibraryId(Long id) {
        this.id = id;
    }

    /**
     * @return the toString representation
     */
    @Override
    public String toString() {
        return "LibraryId [id=" + id + "]";
    }

}
