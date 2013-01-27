
package org.easetech.easytest.example;

public class Item {

    private String description;

    private String itemType;

    private ItemId itemId;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType the itemType to set
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * @return the itemId
     */
    public ItemId getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "Item [description=" + description + ", itemType=" + itemType + ", itemId=" + itemId + "]";
    }

}
