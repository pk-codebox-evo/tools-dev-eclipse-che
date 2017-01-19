/*
 * Copyright (c) 2015-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
'use strict';

import {RoutingRedirect} from './routing-redirect.factory';
import {RouteHistory} from './route-history.service';

export class RoutingConfig {

  constructor(register) {

    register.factory('routingRedirect', RoutingRedirect);

    register.service('routeHistory', RouteHistory);

  }
}
