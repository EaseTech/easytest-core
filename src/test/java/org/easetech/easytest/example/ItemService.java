
package org.easetech.easytest.example;

import java.util.List;

public interface ItemService {

    public List<Item> getItems(LibraryId libraryId, String searchText, String itemType);

    public Item findItem(LibraryId libraryId, ItemId itemId);

}
