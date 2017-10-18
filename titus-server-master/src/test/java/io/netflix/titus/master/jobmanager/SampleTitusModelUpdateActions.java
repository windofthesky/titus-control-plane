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

package io.netflix.titus.master.jobmanager;

import java.util.Optional;

import io.netflix.titus.api.jobmanager.model.event.JobManagerEvent;
import io.netflix.titus.api.jobmanager.service.common.action.ActionKind;
import io.netflix.titus.api.jobmanager.service.common.action.TitusModelUpdateAction;
import io.netflix.titus.common.framework.reconciler.EntityHolder;
import io.netflix.titus.common.util.tuple.Pair;

/**
 */
public final class SampleTitusModelUpdateActions {
    private SampleTitusModelUpdateActions() {
    }

    public static TitusModelUpdateAction any() {
        return new JobUpdateAction("jobId");
    }


    public static TitusModelUpdateAction any(String jobId) {
        return new JobUpdateAction(jobId);
    }

    private static class JobUpdateAction extends TitusModelUpdateAction {

        protected JobUpdateAction(String id) {
            super(ActionKind.Job, Model.Reference, JobManagerEvent.Trigger.API, id, "Job update action");
        }

        @Override
        public Pair<EntityHolder, Optional<EntityHolder>> apply(EntityHolder rootHolder) {
            return Pair.of(rootHolder, Optional.empty());
        }
    }
}