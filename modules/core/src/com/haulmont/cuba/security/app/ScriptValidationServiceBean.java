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

package com.haulmont.cuba.security.app;

import com.haulmont.cuba.core.app.ScriptValidationService;
import com.haulmont.cuba.core.global.ScriptExecutionPolicy;
import com.haulmont.cuba.core.global.Scripting;
import groovy.lang.Binding;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ScriptValidationService.NAME)
public class ScriptValidationServiceBean implements ScriptValidationService {
    @Inject
    Scripting scripting;

    @Override
    public <T> T evaluateGroovy(String text, Binding binding, ScriptExecutionPolicy... policies) {
        int x = 5;
        System.out.println(text);
        //noinspection unchecked
        return (T) scripting.evaluateGroovy(text, binding, policies);
    }
}
