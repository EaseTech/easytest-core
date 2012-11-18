
package org.easetech.easytest.example;

import org.easetech.easytest.converter.AbstractConverter;

import java.util.Map;

public class ItemConverter extends AbstractConverter<Item> {

    @Override
    public Item convert(Map<String, Object> convertFrom) {
        Item item = null;

        if (convertFrom != null) {
            item = new Item();
            item.setDescription((String) convertFrom.get("itemDescription"));
            item.setItemId((String) convertFrom.get("itemId"));
            item.setItemType((String) convertFrom.get("itemType"));
        }
        return item;
    }

}
