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
package org.eclipse.che.plugin.languageserver.ide.navigation.declaration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.action.AbstractPerspectiveAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.plugin.languageserver.ide.location.OpenLocationPresenter;
import org.eclipse.che.plugin.languageserver.ide.location.OpenLocationPresenterFactory;
import org.eclipse.che.plugin.languageserver.ide.service.TextDocumentServiceClient;
import org.eclipse.che.plugin.languageserver.shared.lsapi.LocationDTO;
import org.eclipse.che.plugin.languageserver.shared.lsapi.PositionDTO;
import org.eclipse.che.plugin.languageserver.shared.lsapi.TextDocumentIdentifierDTO;
import org.eclipse.che.plugin.languageserver.shared.lsapi.TextDocumentPositionParamsDTO;

import javax.validation.constraints.NotNull;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.workspace.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class FindDefinitionAction extends AbstractPerspectiveAction {


    private final EditorAgent                   editorAgent;
    private final TextDocumentServiceClient client;
    private final DtoFactory dtoFactory;
    private final OpenLocationPresenter presenter;

    @Inject
    public FindDefinitionAction(EditorAgent editorAgent, OpenLocationPresenterFactory presenterFactory,
                                TextDocumentServiceClient client, DtoFactory dtoFactory) {
        super(singletonList(PROJECT_PERSPECTIVE_ID), "Find Definition", "Find Definition", null, null);
        this.editorAgent = editorAgent;
        this.client = client;
        this.dtoFactory = dtoFactory;
        presenter = presenterFactory.create("Find Definition");
    }

    @Override
    public void updateInPerspective(@NotNull ActionEvent event) {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
        //TODO need to check editor somehow
        event.getPresentation().setEnabledAndVisible(activeEditor != null && activeEditor instanceof TextEditor);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();

        //TODO replace this
        if (!(activeEditor instanceof TextEditor)) {
            return;
        }
        TextEditor textEditor = ((TextEditor)activeEditor);
        String path = activeEditor.getEditorInput().getFile().getPath();
        TextDocumentPositionParamsDTO paramsDTO = dtoFactory.createDto(TextDocumentPositionParamsDTO.class);

        PositionDTO positionDTO = dtoFactory.createDto(PositionDTO.class);
        positionDTO.setLine(textEditor.getCursorPosition().getLine());
        positionDTO.setCharacter(textEditor.getCursorPosition().getCharacter());

        TextDocumentIdentifierDTO identifierDTO = dtoFactory.createDto(TextDocumentIdentifierDTO.class);
        identifierDTO.setUri(path);

        paramsDTO.setUri(path);
        paramsDTO.setPosition(positionDTO);
        paramsDTO.setTextDocument(identifierDTO);
        Promise<List<LocationDTO>> promise = client.definition(paramsDTO);
        presenter.openLocation(promise);
    }
}
