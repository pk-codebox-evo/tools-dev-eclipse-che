/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.core.rest;

import com.google.common.annotations.Beta;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Yevhenii Voevodin
 */
@Beta
public class MessageBodyAdapterInterceptor implements MethodInterceptor {

    @Inject
    private Set<MessageBodyAdapter> adapters;

    private final ConcurrentMap<Class<?>, MessageBodyAdapter> cache = new ConcurrentHashMap<>();

    /**
     * This interceptor must be bound for the method
     * {@link CheJsonProvider#readFrom(Class, Type, Annotation[], MediaType, MultivaluedMap, InputStream)}.
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Object[] args = invocation.getArguments();
        final Class<?> type = (Class<?>)args[0];
        final InputStream entityStreamObj = (InputStream)args[args.length - 1];

        // check if adapter for given types is cached and use it if it is
        if (cache.containsKey(type)) {
            args[args.length - 1] = cache.get(type).adapt(entityStreamObj);
            return invocation.proceed();
        }

        // find the first message body adapter that can adapt the entity stream
        for (MessageBodyAdapter candidate : adapters) {
            if (candidate.canAdapt(type)) {
                cache.put(type, candidate);
                args[args.length - 1] = candidate.adapt(entityStreamObj);
                return invocation.proceed();
            }
        }
        return invocation.proceed();
    }
}
