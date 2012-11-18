
package org.easetech.easytest.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RealItemService implements ItemService {
    
    public String testString;

    @SuppressWarnings("unchecked")
    @Override
    public List<Item> getItems(LibraryId libraryId, String searchText, String itemType) {
        System.out.println("getItems Called");
        return Collections.EMPTY_LIST;
    }

    @Override
    public Item findItem(LibraryId libraryId, ItemId itemId) {
        System.out.println("findItems Called");
        Item item = new Item();
        item.setDescription("Item Description Modified Again");
        item.setItemId(itemId.toString());
        item.setItemType("BOOK");
        return item;
    }

    public static void main(String[] args) {
        ArrayList<String> stringList = new ArrayList<String>();
        stringList.add(null);
        Iterator<String> strItr = stringList.iterator();
        while (strItr.hasNext()) {
            System.out.println(strItr.next());
        }
        for (String str : stringList) {
            System.out.println(str);
        }
    }

}
