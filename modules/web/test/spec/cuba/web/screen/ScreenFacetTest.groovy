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

package spec.cuba.web.screen

import com.haulmont.cuba.gui.GuiDevelopmentException
import com.haulmont.cuba.gui.screen.OpenMode
import com.haulmont.cuba.web.gui.components.WebButton
import com.haulmont.cuba.web.gui.components.WebScreenFacet
import spec.cuba.web.UiScreenSpec
import spec.cuba.web.screen.screens.ScreenToOpenWithFacet
import spec.cuba.web.screen.screens.ScreenWithScreenFacet

class ScreenFacetTest extends UiScreenSpec {

    void setup() {
        exportScreensPackages(['spec.cuba.web.screen.screens'])
    }

    def 'Screen facet opens screen and injects props'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('mainWindow', OpenMode.ROOT)
        mainWindow.show()

        when: 'Screen with ScreenFacet is opened'

        def screenWithScreenFacet = screens.create(ScreenWithScreenFacet)
        def facet = screenWithScreenFacet.testScreenFacet

        then: 'Attribute values are propagated'

        facet.id == 'testScreenFacet'
        facet.screenId == 'test_ScreenToOpenWithFacet'
        facet.launchMode == OpenMode.NEW_TAB

        when: 'Show method is triggered'

        def screen = facet.show()

        then: 'Screen is shown and props are injected'

        screens.openedScreens.activeScreens.contains(screen)

        screen.boolProp
        screen.intProp == 42
        screen.doubleProp == 3.14159d

        screen.labelProp != null
        screen.dcProp != null
    }

    def 'Declarative Screen Action subscription'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('mainWindow', OpenMode.ROOT)
        mainWindow.show()

        def screen = screens.create(ScreenWithScreenFacet)
        screen.show()

        when: 'Screen target action performed'

        screen.screenAction.actionPerform(screen.screenButton)

        then: 'Screen is opened'

        screens.openedScreens.all.find { s -> s instanceof ScreenToOpenWithFacet }
    }

    @SuppressWarnings('GroovyAccessibility')
    def 'Declarative Screen Button subscription'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('mainWindow', OpenMode.ROOT)
        mainWindow.show()

        def screen = screens.create(ScreenWithScreenFacet)
        screen.show()

        when: 'Screen target button clicked'

        ((WebButton) screen.screenButton).buttonClicked(null)

        then: 'Screen is opened'

        screens.openedScreens.all.find { s -> s instanceof ScreenToOpenWithFacet }

    }

    @SuppressWarnings('GroovyAccessibility')
    def 'Screen should be bound to frame'() {
        def screen = new WebScreenFacet()

        when: 'Trying to show screen not bound to frame'

        screen.show()

        then: 'Exception is thrown'

        thrown IllegalStateException

        when: 'Trying to setup declarative subscriptions'

        screen.subscribe()

        then: 'Exception is thrown'

        thrown IllegalStateException
    }

    @SuppressWarnings('GroovyAccessibility')
    def 'Screen should have only one subscription'() {
        def screens = vaadinUi.screens

        def mainWindow = screens.create('mainWindow', OpenMode.ROOT)
        screens.show(mainWindow)

        def screen = screens.create(ScreenWithScreenFacet)

        def screenFacet = new WebScreenFacet()
        screenFacet.setOwner(screen.getWindow())
        screenFacet.setActionTarget('actionId')
        screenFacet.setButtonTarget('buttonId')

        when: 'Both action and button are set as Screen target'

        screenFacet.subscribe()

        then: 'Exception is thrown'

        thrown GuiDevelopmentException
    }

    @SuppressWarnings('GroovyAccessibility')
    def 'Screen target should not be missing'() {

        def screens = vaadinUi.screens

        def mainWindow = screens.create('mainWindow', OpenMode.ROOT)
        screens.show(mainWindow)

        def screen = screens.create(ScreenWithScreenFacet)

        def screenFacet = new WebScreenFacet()
        screenFacet.setOwner(screen.getWindow())

        when: 'Missing action is set to Screen'

        screenFacet.setActionTarget('missingAction')
        screenFacet.subscribe()

        then: 'Exception is thrown'

        thrown GuiDevelopmentException

        when: 'Missing button is set to Screen'

        screenFacet.setActionTarget(null)
        screenFacet.setButtonTarget('missingButton')
        screenFacet.subscribe()

        then: 'Exception is thrown'

        thrown GuiDevelopmentException
    }
}
