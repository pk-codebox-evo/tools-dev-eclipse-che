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
package org.eclipse.che.api.workspace.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.workspace.WorkspaceConfig;
import org.eclipse.che.api.core.rest.StringMessageBodyAdapter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;

/**
 * @author Yevhenii Voevodin
 */
@Singleton
public class WorkspaceConfigMessageBodyAdapter extends StringMessageBodyAdapter {

    private static final Pattern CONTAINS_ENVIRONMENTS_ARRAY_PATTERN = Pattern.compile(".*\"environments\"\\s*:\\s*\\[.*", DOTALL);

    @Inject
    private WorkspaceConfigAdapter configAdapter;

    @Override
    public boolean canAdapt(Class<?> type) {
        return WorkspaceConfig.class.isAssignableFrom(type);
    }

    @Override
    public String adapt(String body) throws IOException {
        if (!CONTAINS_ENVIRONMENTS_ARRAY_PATTERN.matcher(body).matches()) {
            return body;
        }
        try {
            final JsonParser parser = new JsonParser();
            final JsonElement root = parser.parse(body);
            if (!root.isJsonObject()) {
                return body;
            }
            return configAdapter.adapt(root.getAsJsonObject()).toString();
        } catch (BadRequestException | ServerException | RuntimeException x) {
            throw new IOException(x.getMessage(), x);
        }
    }
}
