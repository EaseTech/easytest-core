package org.easetech.easytest.example;

public class ComparableObject implements Comparable<ComparableObject> {
    
    private Long item;
    
    

    /**
     * Construct a new ComparableObject
     * @param item
     */
    public ComparableObject(Long item) {
        super();
        this.item = item;
    }



    @Override
    public String toString() {
        return "ComparableObject [item=" + item + "]";
    }



    public Long getItem() {
        return item;
    }



    public void setItem(Long item) {
        this.item = item;
    }



    /**
     * @param o
     * @return
     */
    @Override
    public int compareTo(ComparableObject o) {
        return 1;
    }

}
