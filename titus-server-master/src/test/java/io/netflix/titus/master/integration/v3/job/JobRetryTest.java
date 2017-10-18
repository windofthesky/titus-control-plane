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

package io.netflix.titus.master.integration.v3.job;

import com.netflix.titus.grpc.protogen.TaskStatus.TaskState;
import io.netflix.titus.api.jobmanager.model.job.Capacity;
import io.netflix.titus.api.jobmanager.model.job.JobDescriptor;
import io.netflix.titus.api.jobmanager.model.job.JobModel;
import io.netflix.titus.api.jobmanager.model.job.ext.BatchJobExt;
import io.netflix.titus.api.jobmanager.model.job.ext.ServiceJobExt;
import io.netflix.titus.master.integration.v3.scenario.JobsScenarioBuilder;
import io.netflix.titus.master.integration.v3.scenario.ScenarioTemplates;
import io.netflix.titus.master.integration.v3.scenario.TaskScenarioBuilder;
import io.netflix.titus.testkit.junit.category.IntegrationTest;
import io.netflix.titus.testkit.junit.master.TitusStackResource;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static io.netflix.titus.master.integration.v3.scenario.ScenarioTemplates.startTasksInNewJob;
import static io.netflix.titus.testkit.model.job.JobDescriptorGenerator.oneTaskBatchJobDescriptor;
import static io.netflix.titus.testkit.model.job.JobDescriptorGenerator.oneTaskServiceJobDescriptor;

/**
 * TODO These tests are not stable.
 */
@Category(IntegrationTest.class)
public class JobRetryTest {

    private static final JobDescriptor<BatchJobExt> ONE_TASK_BATCH_JOB = oneTaskBatchJobDescriptor().toBuilder()
            .withApplicationName("myApp")
            .withExtensions(BatchJobExt.newBuilder()
                    .withSize(1)
                    .withRuntimeLimitMs(600000)
                    .withRetryPolicy(JobModel.newImmediateRetryPolicy().withRetries(1).build())
                    .build()
            )
            .build();
    private static final JobDescriptor<ServiceJobExt> ONE_TASK_SERVICE_JOB = oneTaskServiceJobDescriptor().toBuilder()
            .withApplicationName("myApp")
            .withExtensions(ServiceJobExt.newBuilder()
                    .withCapacity(Capacity.newBuilder().withMin(0).withDesired(1).withMax(2).build())
                    .withRetryPolicy(JobModel.newImmediateRetryPolicy().withRetries(1).build())
                    .build()
            )
            .build();

    @ClassRule
    public static final TitusStackResource titusStackResource = TitusStackResource.aDefaultStack();

    private static JobsScenarioBuilder jobsScenarioBuilder;

    @BeforeClass
    public static void setUp() throws Exception {
        jobsScenarioBuilder = new JobsScenarioBuilder(titusStackResource.getStack().getTitusOperations());
    }

    @Test(timeout = 30_000)
    @Ignore
    public void testBatchJobRetry() throws Exception {
        jobsScenarioBuilder.schedule(ONE_TASK_BATCH_JOB, jobScenarioBuilder -> jobScenarioBuilder
                .template(startTasksInNewJob())
                .inTask(0, TaskScenarioBuilder::failTaskExecution)
                .inTask(0, taskScenarioBuilder -> taskScenarioBuilder.expectStateUpdateSkipOther(TaskState.Finished))
                .expectAllTasksCreated()
                .allTasks(TaskScenarioBuilder::expectTaskOnAgent)
                .assertTasks(task -> task.get(1).getResubmitNumber() == 1)
                .inTask(1, taskScenarioBuilder -> taskScenarioBuilder.template(ScenarioTemplates.startTask()))
                .inTask(1, taskScenarioBuilder -> taskScenarioBuilder.template(ScenarioTemplates.completeTask()))
                .expectJobEventStreamCompletes()
        );
    }

    @Test(timeout = 30_000)
    @Ignore
    public void testServiceJobRetry() throws Exception {
        jobsScenarioBuilder.schedule(ONE_TASK_SERVICE_JOB, jobScenarioBuilder -> jobScenarioBuilder
                .template(startTasksInNewJob())
                .inTask(0, taskScenarioBuilder -> taskScenarioBuilder.transitionUntil(TaskState.Finished))
                .inTask(0, taskScenarioBuilder -> taskScenarioBuilder.expectStateUpdateSkipOther(TaskState.Finished))
                .expectAllTasksCreated()
                .allTasks(TaskScenarioBuilder::expectTaskOnAgent)
                .assertTasks(task -> task.get(1).getResubmitNumber() == 1)
                .inTask(1, taskScenarioBuilder -> taskScenarioBuilder.template(ScenarioTemplates.startTask()))
                .inTask(1, taskScenarioBuilder -> taskScenarioBuilder.template(ScenarioTemplates.completeTask()))
                .expectJobEventStreamCompletes()
        );
    }

    @Test(timeout = 30_000)
    @Ignore
    public void testBatchJobFailsAfterRetrying() throws Exception {
        jobsScenarioBuilder.schedule(ONE_TASK_BATCH_JOB, jobScenarioBuilder -> jobScenarioBuilder
                .template(startTasksInNewJob())
                .inTask(0, TaskScenarioBuilder::failTaskExecution)
                .inTask(0, taskScenarioBuilder -> taskScenarioBuilder.expectStateUpdateSkipOther(TaskState.Finished))
                .expectAllTasksCreated()
                .allTasks(TaskScenarioBuilder::expectTaskOnAgent)
                .inTask(0, TaskScenarioBuilder::failTaskExecution)
                .expectJobEventStreamCompletes()
        );
    }

    @Test(timeout = 30_000)
    @Ignore
    public void testServiceJobFailsAfterRetrying() throws Exception {
        jobsScenarioBuilder.schedule(ONE_TASK_SERVICE_JOB, jobScenarioBuilder -> jobScenarioBuilder
                .template(startTasksInNewJob())
                .inTask(0, TaskScenarioBuilder::failTaskExecution)
                .inTask(0, taskScenarioBuilder -> taskScenarioBuilder.expectStateUpdateSkipOther(TaskState.Finished))
                .expectAllTasksCreated()
                .allTasks(TaskScenarioBuilder::expectTaskOnAgent)
                .inTask(0, TaskScenarioBuilder::failTaskExecution)
                .expectJobEventStreamCompletes()
        );
    }
}