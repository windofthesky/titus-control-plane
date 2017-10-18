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

package io.netflix.titus.master.taskmigration.job;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.netflix.spectator.api.DefaultRegistry;
import io.netflix.titus.api.jobmanager.service.V3JobOperations;
import io.netflix.titus.master.job.V2JobOperations;
import io.netflix.titus.master.taskmigration.TaskMigrationDetails;
import io.netflix.titus.master.taskmigration.TaskMigrationManager;
import io.netflix.titus.master.taskmigration.TaskMigrationManagerFactory;
import io.netflix.titus.master.taskmigration.V2TaskMigrationDetails;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceJobTaskMigratorTest {

    private final TestScheduler scheduler = Schedulers.test();
    private final ServiceJobTaskMigratorConfig migratorConfig = mock(ServiceJobTaskMigratorConfig.class);
    private final TaskMigrationManager migrationManager = mock(TaskMigrationManager.class);
    private final TaskMigrationManagerFactory managerFactory = mock(TaskMigrationManagerFactory.class);
    private final V2JobOperations v2JobOperations = mock(V2JobOperations.class);
    private final V3JobOperations v3JobOperations = mock(V3JobOperations.class);

    private final ServiceJobTaskMigrator serviceJobTaskMigrator = new ServiceJobTaskMigrator(scheduler,
            v2JobOperations, v3JobOperations, migratorConfig, managerFactory, new DefaultRegistry());


    @Test
    public void testMigrateServiceJobs() throws Exception {

        when(migratorConfig.isServiceTaskMigratorEnabled()).thenReturn(true);
        when(migratorConfig.getSchedulerDelayMs()).thenReturn(1000L);
        when(migratorConfig.getSchedulerTimeoutMs()).thenReturn(300000L);

        TaskMigrationDetails serviceJobOneTaskOne = generateTaskMigrationDetails("Titus-1-worker-0-1", "Titus-1");
        TaskMigrationDetails serviceJobOneTaskTwo = generateTaskMigrationDetails("Titus-1-worker-1-1", "Titus-1");
        TaskMigrationDetails serviceJobTwoTask = generateTaskMigrationDetails("Titus-2-worker-0-1", "Titus-2");
        TaskMigrationDetails serviceJobThreeTask = generateTaskMigrationDetails("Titus-3-worker-0-1", "Titus-3");

        List<TaskMigrationDetails> migrationDetailsList = Lists.newArrayList(serviceJobOneTaskOne, serviceJobOneTaskTwo, serviceJobTwoTask, serviceJobThreeTask);
        migrationDetailsList.forEach(i -> serviceJobTaskMigrator.taskMigrationDetailsMap.put(i.getId(), i));

        when(migrationManager.getState()).thenReturn(TaskMigrationManager.State.Running);

        when(managerFactory.newTaskMigrationManager(any(V2TaskMigrationDetails.class))).thenReturn(migrationManager);

        serviceJobTaskMigrator.enterActiveMode();
        scheduler.advanceTimeBy(0L, TimeUnit.MILLISECONDS);
        verify(migrationManager, times(3)).update(any());
    }

    @Test
    public void testMigrateServiceJobSchedulerTimeout() throws Exception {

        when(migratorConfig.isServiceTaskMigratorEnabled()).thenReturn(true);
        when(migratorConfig.getSchedulerDelayMs()).thenReturn(1000L);
        when(migratorConfig.getSchedulerTimeoutMs()).thenReturn(300000L);

        TaskMigrationDetails serviceJobOneTask = generateTaskMigrationDetails("Titus-1-worker-0-1", "Titus-1");

        List<TaskMigrationDetails> migrationDetailsList = Collections.singletonList(serviceJobOneTask);
        migrationDetailsList.forEach(i -> serviceJobTaskMigrator.taskMigrationDetailsMap.put(i.getId(), i));

        when(migrationManager.getState()).thenReturn(TaskMigrationManager.State.Running);

        when(managerFactory.newTaskMigrationManager(any(V2TaskMigrationDetails.class))).thenReturn(migrationManager);

        ServiceJobTaskMigrator spy = Mockito.spy(serviceJobTaskMigrator);

        Observable<Void> delay = Observable.timer(1, TimeUnit.HOURS).flatMap(o -> Observable.empty());
        doReturn(delay).when(spy).run();

        spy.enterActiveMode();
        scheduler.advanceTimeBy(400000L, TimeUnit.MILLISECONDS);
        verify(spy, times(2)).run();
    }

    private TaskMigrationDetails generateTaskMigrationDetails(String taskId, String jobId) {
        TaskMigrationDetails taskMigrationDetails = mock(V2TaskMigrationDetails.class);
        when(taskMigrationDetails.getId()).thenReturn(taskId);
        when(taskMigrationDetails.getJobId()).thenReturn(jobId);
        when(taskMigrationDetails.isActive()).thenReturn(true);

        return taskMigrationDetails;
    }
}