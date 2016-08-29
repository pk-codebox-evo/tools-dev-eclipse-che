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
package org.eclipse.che.api.factory.server;

import com.google.gson.JsonObject;

import org.eclipse.che.api.factory.shared.dto.Factory;
import org.eclipse.che.api.workspace.server.WorkspaceConfigMessageBodyAdapter;

import javax.inject.Singleton;
import java.io.IOException;

/**
 * Adapts an old format of {@link Factory#getWorkspace()} to a new one.
 *
 * @author Yevhenii Voevodin
 */
@Singleton
public class FactoryMessageBodyAdapter extends WorkspaceConfigMessageBodyAdapter {

    @Override
    public boolean canAdapt(Class<?> type) {
        return Factory.class.isAssignableFrom(type);
    }

    @Override
    protected JsonObject getWorkspaceConfigObj(JsonObject root) throws IOException {
        return root.getAsJsonObject("workspace");
    }
}
