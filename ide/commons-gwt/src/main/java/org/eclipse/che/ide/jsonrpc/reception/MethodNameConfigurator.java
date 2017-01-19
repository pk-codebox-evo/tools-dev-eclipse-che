/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jsonrpc.reception;

import org.eclipse.che.ide.jsonrpc.JsonRpcFactory;
import org.eclipse.che.ide.jsonrpc.RequestHandlerRegistry;
import org.eclipse.che.ide.util.loging.Log;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Method configurator is used to define method name that the request handler
 * will be associated with.
 */
public class MethodNameConfigurator {
    private final RequestHandlerRegistry registry;
    private final JsonRpcFactory         factory;


    @Inject
    public MethodNameConfigurator(RequestHandlerRegistry registry, JsonRpcFactory factory) {
        this.registry = registry;
        this.factory = factory;
    }

    public ParamsConfigurator methodName(String name) {
        checkNotNull(name, "Method name must not be null");
        checkArgument(!name.isEmpty(), "Method name must not be empty");

        Log.debug(getClass(), "Configuring incoming request method name name: " + name);

        return new ParamsConfigurator(registry, factory, name);
    }
}
