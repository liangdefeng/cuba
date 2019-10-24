/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.gui.components.data.options;

import com.haulmont.bali.events.EventHub;
import com.haulmont.bali.events.Subscription;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.data.Options;
import com.haulmont.cuba.gui.components.data.meta.EntityOptions;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Options based on a map that contains entities.
 *
 * @param <E> entity type
 */
public class MapEntityOptions<E extends Entity> extends MapOptions<E> implements Options<E>, EntityOptions<E> {

    protected EventHub events = new EventHub();

    protected E selectedItem = null;

    public MapEntityOptions(Map<String, E> options) {
        super(options);
    }

    @Override
    public void setSelectedItem(E item) {
        this.selectedItem = item;
    }

    public E getSelectedItem() {
        return selectedItem;
    }

    @Override
    public boolean containsItem(E item) {
        return getItemsCollection().containsValue(item);
    }

    @Override
    public void updateItem(E item) {
        Map<String, E> itemsCollection = getItemsCollection();
        if (itemsCollection.containsValue(item)) {
            itemsCollection.entrySet()
                    .stream()
                    .filter(entry -> Objects.equals(entry.getValue(), item))
                    .findFirst()
                    .get()
                    .setValue(item);
        }
    }

    @Override
    public void refresh() {
        // do nothing
    }

    @SuppressWarnings("unchecked")
    @Override
    public Subscription addValueChangeListener(Consumer<ValueChangeEvent<E>> listener) {
        return events.subscribe(ValueChangeEvent.class, (Consumer) listener);
    }

    @Override
    public MetaClass getEntityMetaClass() {
        MetaClass metaClass = null;
        if (selectedItem != null) {
            metaClass = selectedItem.getMetaClass();
        } else {
            Map<String, E> itemsCollection = getItemsCollection();
            if (!itemsCollection.isEmpty()) {
                metaClass = itemsCollection.entrySet()
                        .stream()
                        .findFirst()
                        .get()
                        .getValue()
                        .getMetaClass();
            }
        }
        return metaClass;
    }
}
