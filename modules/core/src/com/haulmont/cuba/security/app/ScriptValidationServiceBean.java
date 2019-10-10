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
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.sys.ScriptingImpl;
import org.codehaus.groovy.runtime.MethodClosure;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service(ScriptValidationService.NAME)
public class ScriptValidationServiceBean implements ScriptValidationService {
    @Inject
    Security security;
    @Inject
    ScriptingImpl scripting;
    @Inject
    UserSessionSource userSessionSource;

    @Override
    public <T> T evaluateConstraintScript(Entity entity, String groovyScript) {
        Map<String, Object> context = new HashMap<>();
        context.put("__entity__", entity);
        context.put("parse", new MethodClosure(security, "parseValue"));
        context.put("userSession", userSessionSource.getUserSession());
        fillGroovyConstraintsContext(context);
        return scripting.evaluateGroovy(groovyScript.replace("{E}", "__entity__"), context);
    }

    /**
     * Override if you need specific context variables in Groovy constraints.
     *
     * @param context passed to Groovy evaluator
     */
    protected void fillGroovyConstraintsContext(Map<String, Object> context) {
    }
}
