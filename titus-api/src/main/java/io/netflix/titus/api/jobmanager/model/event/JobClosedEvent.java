/*
 * Copyright 2017 Netflix, Inc.
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

package io.netflix.titus.api.jobmanager.model.event;

import java.util.Optional;

import io.netflix.titus.api.jobmanager.service.common.action.TitusModelUpdateAction;

/**
 * The last job event, marking job as completed and removed from the storage/memory.
 */
public class JobClosedEvent extends JobEvent {
    public JobClosedEvent(EventType eventType, TitusModelUpdateAction updateAction) {
        super(eventType, updateAction, Optional.empty());
    }
}