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
package org.eclipse.che.api.core.websocket.impl;

import org.eclipse.che.api.core.websocket.WebSocketMessageReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;

/**
 * Duplex WEB SOCKET endpoint, handles messages, errors, session open/close events.
 *
 * @author Dmitry Kuleshov
 */
public class BasicWebSocketEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(BasicWebSocketEndpoint.class);

    private final WebSocketSessionRegistry registry;
    private final MessagesReSender         reSender;
    private final WebSocketMessageReceiver receiver;


    public BasicWebSocketEndpoint(WebSocketSessionRegistry registry,
                                  MessagesReSender reSender,
                                  WebSocketMessageReceiver receiver) {

        this.registry = registry;
        this.reSender = reSender;
        this.receiver = receiver;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("endpoint-id") String endpointId) {
        LOG.info("Web socket session opened");
        LOG.info("Endpoint: {}", endpointId);

        session.setMaxIdleTimeout(0);

        registry.add(endpointId, session);
        reSender.resend(endpointId);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("endpoint-id") String endpointId) {
        LOG.debug("Receiving a web socket message.");
        LOG.debug("Endpoint: {}", endpointId);
        LOG.debug("Message: {}", message);

        receiver.receive(endpointId, message);
    }

    @OnClose
    public void onClose(CloseReason closeReason, @PathParam("endpoint-id") String endpointId) {
        LOG.info("Web socket session closed");
        LOG.debug("Endpoint: {}", endpointId);
        LOG.debug("Close reason: {}:{}", closeReason.getReasonPhrase(), closeReason.getCloseCode());

        registry.remove(endpointId);
    }

    @OnError
    public void onError(Throwable t, @PathParam("endpoint-id") String endpointId) {
        LOG.info("Web socket session error");
        LOG.debug("Endpoint: {}", endpointId);
        LOG.debug("Error: {}", t);
    }
}
