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

package spec.cuba.web.components.tokenlist

import com.google.common.collect.Lists
import com.haulmont.cuba.gui.components.TokenList
import com.haulmont.cuba.gui.components.data.options.ContainerOptions
import com.haulmont.cuba.gui.components.data.options.ListEntityOptions
import com.haulmont.cuba.gui.components.data.options.MapEntityOptions
import com.haulmont.cuba.gui.screen.OpenMode
import com.haulmont.cuba.security.entity.Constraint
import spec.cuba.web.UiScreenSpec
import spec.cuba.web.components.tokenlist.screens.TokenListScreen

@SuppressWarnings("GroovyAssignabilityCheck")
class TokenListTest extends UiScreenSpec {

    protected Constraint constraint1
    protected Constraint constraint2

    void setup() {
        exportScreensPackages(['spec.cuba.web.components.tokenlist.screens', 'com.haulmont.cuba.web.app.main'])

        constraint1 = metadata.create(Constraint.class)
        constraint2 = metadata.create(Constraint.class)
    }

    def "List options is propagated to TokenList optionsContainer using setOptionsList"() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create("mainWindow", OpenMode.ROOT)
        screens.show(mainWindow)

        def tokenListScreen = screens.create(TokenListScreen)
        tokenListScreen.show()

        def tokenList = tokenListScreen.tokenList as TokenList<Constraint>
        def list = Lists.newArrayList(constraint1, constraint2)

        when: 'List options is set to TokenList'
        tokenList.setOptionsList(list)

        then: 'List options is propagated to TokenList'
        tokenList.getOptionsList() == list
    }

    def "Map options is propagated to TokenList optionsContainer using setOptionsMap"() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create("mainWindow", OpenMode.ROOT)
        screens.show(mainWindow)

        def tokenListScreen = screens.create(TokenListScreen)
        tokenListScreen.show()

        def tokenList = tokenListScreen.tokenList as TokenList<Constraint>

        def map = new HashMap<String, Constraint>()
        map.put(constraint1.getId(), constraint1)
        map.put(constraint2.getId(), constraint2)

        when: 'Map options is set to TokenList'
        tokenList.setOptionsMap(map)

        then: 'Map options is propagated to TokenList'
        tokenList.getOptionsMap() == map
    }

    def "ListEntityOptions is propagated to TokenList optionsContainer using setOptions"() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create("mainWindow", OpenMode.ROOT)
        screens.show(mainWindow)

        def tokenListScreen = screens.create(TokenListScreen)
        tokenListScreen.show()

        def tokenList = tokenListScreen.tokenList as TokenList<Constraint>
        def list = Lists.newArrayList(constraint1, constraint2)
        def options = new ListEntityOptions(list)

        when: 'ListEntityOptions is set to TokenList'
        tokenList.setOptions(options)

        then: 'ListEntityOptions is propagated to TokenList'
        tokenList.getOptions() == options
        tokenList.getOptions().getClass() == ListEntityOptions
    }

    def "MapEntityOptions is propagated to TokenList optionsContainer using setOptions"() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create("mainWindow", OpenMode.ROOT)
        screens.show(mainWindow)

        def tokenListScreen = screens.create(TokenListScreen)
        tokenListScreen.show()

        def tokenList = tokenListScreen.tokenList as TokenList<Constraint>

        def map = new HashMap<String, Constraint>()
        map.put(constraint1.getId(), constraint1)
        map.put(constraint2.getId(), constraint2)
        def options = new MapEntityOptions(map)

        when: 'MapEntityOptions is set to TokenList'
        tokenList.setOptions(options)

        then: 'MapEntityOptions is propagated to TokenList'
        tokenList.getOptions() == options
        tokenList.getOptions().getClass() == MapEntityOptions
    }

    def "ContainerOptions is propagated to TokenList optionsContainer using setOptions"() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create("mainWindow", OpenMode.ROOT)
        screens.show(mainWindow)

        def tokenListScreen = screens.create(TokenListScreen)
        tokenListScreen.show()

        def tokenList = tokenListScreen.tokenList as TokenList<Constraint>

        def container = dataComponents.createCollectionContainer(Constraint.class)
        container.setItems(Lists.newArrayList(constraint1, constraint2))
        def options = new ContainerOptions(container)

        when: 'ContainerOptions is set to TokenList'
        tokenList.setOptions(options)

        then: 'ContainerOptions is propagated to TokenList'
        tokenList.getOptions() == options
        tokenList.getOptions().getClass() == ContainerOptions
    }
}
