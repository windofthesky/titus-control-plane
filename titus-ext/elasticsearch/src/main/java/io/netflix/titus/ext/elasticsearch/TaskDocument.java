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

package io.netflix.titus.ext.elasticsearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.netflix.titus.api.endpoint.v2.rest.representation.TitusJobType;
import io.netflix.titus.api.endpoint.v2.rest.representation.TitusTaskState;
import io.netflix.titus.api.jobmanager.model.job.Capacity;
import io.netflix.titus.api.jobmanager.model.job.Container;
import io.netflix.titus.api.jobmanager.model.job.ContainerResources;
import io.netflix.titus.api.jobmanager.model.job.ExecutableStatus;
import io.netflix.titus.api.jobmanager.model.job.Image;
import io.netflix.titus.api.jobmanager.model.job.Job;
import io.netflix.titus.api.jobmanager.model.job.JobDescriptor;
import io.netflix.titus.api.jobmanager.model.job.JobGroupInfo;
import io.netflix.titus.api.jobmanager.model.job.Task;
import io.netflix.titus.api.jobmanager.model.job.TaskState;
import io.netflix.titus.api.jobmanager.model.job.TaskStatus;
import io.netflix.titus.api.jobmanager.model.job.ext.BatchJobExt;
import io.netflix.titus.api.jobmanager.model.job.ext.ServiceJobExt;
import io.netflix.titus.api.model.v2.WorkerNaming;
import io.netflix.titus.api.store.v2.V2WorkerMetadata;
import io.netflix.titus.common.util.CollectionsExt;
import io.netflix.titus.common.util.StringExt;
import io.netflix.titus.master.endpoint.v2.rest.representation.TitusJobSpec;

public class TaskDocument {
    private String id;
    private String instanceId;
    private String jobId;
    private String host;
    private String hostInstanceId;
    private String zone;
    private String asg;
    private String instanceType;
    private String region;
    private String message;
    private String submittedAt;
    private String launchedAt;
    private String startingAt;
    private String startedAt;
    private String finishedAt;
    private String state;
    private ComputedFields computedFields;
    private Map<String, String> titusContext;

    /* Job Spec Fields */
    private String name;
    private String applicationName;
    private String appName;
    private String user;
    private TitusJobType type;
    private Map<String, String> labels;
    private String version;
    private String entryPoint;
    private Boolean inService;
    private int instances;
    private int instancesMin;
    private int instancesMax;
    private int instancesDesired;
    private double cpu;
    private double memory;
    private double networkMbps;
    private double disk;
    private int gpu;
    private Map<String, String> env;
    private int retries;
    private boolean restartOnSuccess;
    private Long runtimeLimitSecs;
    private boolean allocateIpAddress;
    private String iamProfile;
    private List<String> securityGroups;
    private List<String> softConstraints;
    private List<String> hardConstraints;
    private String jobGroupStack;
    private String jobGroupDetail;
    private String jobGroupSequence;
    private String capacityGroup;

    public String getName() {
        return name;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getAppName() {
        return appName;
    }

    public String getUser() {
        return user;
    }

    public TitusJobType getType() {
        return type;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public String getVersion() {
        return version;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public Boolean getInService() {
        return inService;
    }

    public int getInstances() {
        return instances;
    }

    public int getInstancesMin() {
        return instancesMin;
    }

    public int getInstancesMax() {
        return instancesMax;
    }

    public int getInstancesDesired() {
        return instancesDesired;
    }

    public double getCpu() {
        return cpu;
    }

    public double getMemory() {
        return memory;
    }

    public double getNetworkMbps() {
        return networkMbps;
    }

    public double getDisk() {
        return disk;
    }

    public int getGpu() {
        return gpu;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public int getRetries() {
        return retries;
    }

    public boolean isRestartOnSuccess() {
        return restartOnSuccess;
    }

    public Long getRuntimeLimitSecs() {
        return runtimeLimitSecs;
    }

    public boolean isAllocateIpAddress() {
        return allocateIpAddress;
    }

    public String getIamProfile() {
        return iamProfile;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public List<String> getSoftConstraints() {
        return softConstraints;
    }

    public List<String> getHardConstraints() {
        return hardConstraints;
    }

    public String getJobGroupStack() {
        return jobGroupStack;
    }

    public String getJobGroupDetail() {
        return jobGroupDetail;
    }

    public String getJobGroupSequence() {
        return jobGroupSequence;
    }

    public String getCapacityGroup() {
        return capacityGroup;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public String getLaunchedAt() {
        return launchedAt;
    }

    public String getStartingAt() {
        return startingAt;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getJobId() {
        return jobId;
    }

    public String getState() {
        return state;
    }

    public String getHost() {
        return host;
    }

    public String getZone() {
        return zone;
    }

    public String getRegion() {
        return region;
    }

    public ComputedFields getComputedFields() {
        return computedFields;
    }

    public Map<String, String> getTitusContext() {
        return titusContext;
    }

    public String getAsg() {
        return asg;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public String getHostInstanceId() {
        return hostInstanceId;
    }

    public static class ComputedFields {
        Long msFromSubmittedToLaunched;
        Long msFromLaunchedToStarting;
        Long msToStarting;
        Long msFromStartingToStarted;
        Long msToStarted;
        Long msFromStartedToFinished;
        Long msToFinished;

        public Long getMsFromSubmittedToLaunched() {
            return msFromSubmittedToLaunched;
        }

        public Long getMsFromLaunchedToStarting() {
            return msFromLaunchedToStarting;
        }

        public Long getMsToStarting() {
            return msToStarting;
        }

        public Long getMsFromStartingToStarted() {
            return msFromStartingToStarted;
        }

        public Long getMsToStarted() {
            return msToStarted;
        }

        public Long getMsFromStartedToFinished() {
            return msFromStartedToFinished;
        }

        public Long getMsToFinished() {
            return msToFinished;
        }
    }

    public static TaskDocument fromV2Task(V2WorkerMetadata v2WorkerMetadata, TitusJobSpec jobSpec, SimpleDateFormat dateFormat, Map<String, String> context) {
        TaskDocument taskDocument = new TaskDocument();
        taskDocument.name = jobSpec.getName();
        taskDocument.applicationName = jobSpec.getApplicationName();
        taskDocument.appName = jobSpec.getAppName();
        taskDocument.user = jobSpec.getUser();
        taskDocument.type = jobSpec.getType();
        taskDocument.labels = jobSpec.getLabels();
        taskDocument.version = jobSpec.getVersion();
        taskDocument.entryPoint = jobSpec.getEntryPoint();
        taskDocument.inService = jobSpec.isInService();
        taskDocument.instances = jobSpec.getInstances();
        taskDocument.instancesMin = jobSpec.getInstancesMin();
        taskDocument.instancesMax = jobSpec.getInstancesMax();
        taskDocument.instancesDesired = jobSpec.getInstancesDesired();
        taskDocument.cpu = jobSpec.getCpu();
        taskDocument.memory = jobSpec.getMemory();
        taskDocument.networkMbps = jobSpec.getNetworkMbps();
        taskDocument.disk = jobSpec.getDisk();
        taskDocument.gpu = jobSpec.getGpu();
        taskDocument.env = jobSpec.getEnv();
        taskDocument.retries = jobSpec.getRetries();
        taskDocument.restartOnSuccess = jobSpec.isRestartOnSuccess();
        taskDocument.runtimeLimitSecs = jobSpec.getRuntimeLimitSecs();
        taskDocument.allocateIpAddress = jobSpec.isAllocateIpAddress();
        taskDocument.iamProfile = jobSpec.getIamProfile();
        taskDocument.securityGroups = jobSpec.getSecurityGroups();
        taskDocument.jobGroupStack = jobSpec.getJobGroupStack();
        taskDocument.jobGroupDetail = jobSpec.getJobGroupDetail();
        taskDocument.jobGroupSequence = jobSpec.getJobGroupSequence();
        taskDocument.capacityGroup = jobSpec.getCapacityGroup();
        taskDocument.softConstraints = CollectionsExt.nonNull(jobSpec.getSoftConstraints()).stream().map(Enum::name).collect(Collectors.toList());
        taskDocument.hardConstraints = CollectionsExt.nonNull(jobSpec.getHardConstraints()).stream().map(Enum::name).collect(Collectors.toList());

        taskDocument.id = WorkerNaming.getWorkerName(v2WorkerMetadata.getJobId(), v2WorkerMetadata.getWorkerIndex(), v2WorkerMetadata.getWorkerNumber());
        taskDocument.instanceId = v2WorkerMetadata.getWorkerInstanceId();
        taskDocument.jobId = v2WorkerMetadata.getJobId();
        taskDocument.state = TitusTaskState.getTitusState(v2WorkerMetadata.getState(), v2WorkerMetadata.getReason()).name();
        taskDocument.host = v2WorkerMetadata.getSlave();
        taskDocument.computedFields = new ComputedFields();

        final String region = v2WorkerMetadata.getSlaveAttributes().get("region");
        if (region != null) {
            taskDocument.region = region;
        }
        final String zone = v2WorkerMetadata.getSlaveAttributes().get("zone");
        if (zone != null) {
            taskDocument.zone = zone;
        }

        final String asg = v2WorkerMetadata.getSlaveAttributes().get("asg");
        if (asg != null) {
            taskDocument.asg = asg;
        }

        final String instanceType = v2WorkerMetadata.getSlaveAttributes().get("itype");
        if (instanceType != null) {
            taskDocument.instanceType = instanceType;
        }

        final String instanceId = v2WorkerMetadata.getSlaveAttributes().get("id");
        if (instanceId != null) {
            taskDocument.hostInstanceId = instanceId;
        }

        if (v2WorkerMetadata.getAcceptedAt() > 0) {
            taskDocument.submittedAt = dateFormat.format(new Date(v2WorkerMetadata.getAcceptedAt()));
        }

        if (v2WorkerMetadata.getLaunchedAt() > 0) {
            taskDocument.launchedAt = dateFormat.format(new Date(v2WorkerMetadata.getLaunchedAt()));
            taskDocument.computedFields.msFromSubmittedToLaunched = v2WorkerMetadata.getLaunchedAt() - v2WorkerMetadata.getAcceptedAt();
        }

        if (v2WorkerMetadata.getStartingAt() > 0) {
            taskDocument.startingAt = dateFormat.format(new Date(v2WorkerMetadata.getStartingAt()));
            taskDocument.computedFields.msFromLaunchedToStarting = v2WorkerMetadata.getStartingAt() - v2WorkerMetadata.getLaunchedAt();
            taskDocument.computedFields.msToStarting = v2WorkerMetadata.getStartingAt() - v2WorkerMetadata.getAcceptedAt();
        }

        if (v2WorkerMetadata.getStartedAt() > 0) {
            taskDocument.startedAt = dateFormat.format(new Date(v2WorkerMetadata.getStartedAt()));
            taskDocument.computedFields.msFromStartingToStarted = v2WorkerMetadata.getStartedAt() - v2WorkerMetadata.getStartingAt();
            taskDocument.computedFields.msToStarted = v2WorkerMetadata.getStartedAt() - v2WorkerMetadata.getAcceptedAt();
        }

        if (v2WorkerMetadata.getCompletedAt() > 0) {
            taskDocument.finishedAt = dateFormat.format(new Date(v2WorkerMetadata.getCompletedAt()));
            taskDocument.computedFields.msFromStartedToFinished = v2WorkerMetadata.getCompletedAt() - v2WorkerMetadata.getStartedAt();
            taskDocument.computedFields.msToFinished = v2WorkerMetadata.getCompletedAt() - v2WorkerMetadata.getAcceptedAt();
        }

        taskDocument.message = v2WorkerMetadata.getCompletionMessage();
        taskDocument.titusContext = context;

        return taskDocument;
    }

    public static TaskDocument fromV3Task(Task task, Job job, SimpleDateFormat dateFormat, Map<String, String> context) {
        TaskDocument taskDocument = new TaskDocument();
        JobDescriptor jobDescriptor = job.getJobDescriptor();
        Container container = jobDescriptor.getContainer();
        Image image = container.getImage();
        ContainerResources containerResources = container.getContainerResources();
        JobGroupInfo jobGroupInfo = jobDescriptor.getJobGroupInfo();

        taskDocument.name = jobDescriptor.getApplicationName();
        taskDocument.applicationName = image.getName();
        taskDocument.appName = jobDescriptor.getApplicationName();
        taskDocument.user = jobDescriptor.getOwner().getTeamEmail();
        taskDocument.labels = container.getAttributes();
        taskDocument.version = image.getTag();
        taskDocument.entryPoint = StringExt.concatenate(container.getEntryPoint(), " ");
        taskDocument.cpu = containerResources.getCpu();
        taskDocument.memory = containerResources.getMemoryMB();
        taskDocument.networkMbps = containerResources.getNetworkMbps();
        taskDocument.disk = containerResources.getDiskMB();
        taskDocument.gpu = containerResources.getGpu();
        taskDocument.allocateIpAddress = containerResources.isAllocateIP();
        taskDocument.env = container.getEnv();
        taskDocument.iamProfile = container.getSecurityProfile().getIamRole();
        taskDocument.securityGroups = container.getSecurityProfile().getSecurityGroups();
        taskDocument.softConstraints = new ArrayList<>(container.getSoftConstraints().keySet());
        taskDocument.hardConstraints = new ArrayList<>(container.getHardConstraints().keySet());
        taskDocument.capacityGroup = jobDescriptor.getCapacityGroup();
        taskDocument.jobGroupStack = jobGroupInfo.getStack();
        taskDocument.jobGroupDetail = jobGroupInfo.getDetail();
        taskDocument.jobGroupSequence = jobGroupInfo.getSequence();

        JobDescriptor.JobDescriptorExt jobDescriptorExt = jobDescriptor.getExtensions();
        if (jobDescriptorExt instanceof BatchJobExt) {
            BatchJobExt batchJobExt = (BatchJobExt) jobDescriptorExt;
            taskDocument.runtimeLimitSecs = batchJobExt.getRuntimeLimitMs();
            taskDocument.type = TitusJobType.batch;
            taskDocument.inService = false;
            taskDocument.instances = batchJobExt.getSize();
            taskDocument.instancesMin = batchJobExt.getSize();
            taskDocument.instancesMax = batchJobExt.getSize();
            taskDocument.instancesDesired = batchJobExt.getSize();
            taskDocument.retries = batchJobExt.getRetryPolicy().getRetries();
            taskDocument.restartOnSuccess = false;
        } else if (jobDescriptorExt instanceof ServiceJobExt) {
            ServiceJobExt serviceJobExt = (ServiceJobExt) jobDescriptorExt;
            taskDocument.runtimeLimitSecs = 0L;
            taskDocument.type = TitusJobType.service;
            taskDocument.inService = serviceJobExt.isEnabled();
            Capacity capacity = serviceJobExt.getCapacity();
            taskDocument.instances = capacity.getDesired();
            taskDocument.instancesMin = capacity.getMin();
            taskDocument.instancesMax = capacity.getMax();
            taskDocument.instancesDesired = capacity.getDesired();
            taskDocument.retries = serviceJobExt.getRetryPolicy().getRetries();
            taskDocument.restartOnSuccess = false;
        }

        Map<String, String> taskContext = task.getTaskContext();
        taskDocument.id = task.getId();
        taskDocument.instanceId = taskContext.getOrDefault("v2.taskInstanceId", task.getId());
        taskDocument.jobId = task.getJobId();
        taskDocument.state = toV2TaskState(task.getStatus()).name();
        taskDocument.host = taskContext.get("agent.host");
        taskDocument.computedFields = new ComputedFields();

        final String region = taskContext.get("agent.region");
        if (region != null) {
            taskDocument.region = region;
        }
        final String zone = taskContext.get("agent.zone");
        if (zone != null) {
            taskDocument.zone = zone;
        }

        final String asg = taskContext.get("agent.asg");
        if (asg != null) {
            taskDocument.asg = asg;
        }

        final String instanceType = taskContext.get("agent.itype");
        if (instanceType != null) {
            taskDocument.instanceType = instanceType;
        }

        final String instanceId = taskContext.get("agent.id");
        if (instanceId != null) {
            taskDocument.hostInstanceId = instanceId;
        }

        long acceptedAt = findTaskStatus(task, TaskState.Accepted).map(ExecutableStatus::getTimestamp).orElse(0L);
        long launchedAt = findTaskStatus(task, TaskState.Launched).map(ExecutableStatus::getTimestamp).orElse(0L);
        long startingAt = findTaskStatus(task, TaskState.StartInitiated).map(ExecutableStatus::getTimestamp).orElse(0L);
        long startedAt = findTaskStatus(task, TaskState.Started).map(ExecutableStatus::getTimestamp).orElse(0L);
        long completedAt = findTaskStatus(task, TaskState.Finished).map(ExecutableStatus::getTimestamp).orElse(0L);

        if (acceptedAt > 0) {
            taskDocument.submittedAt = dateFormat.format(new Date(acceptedAt));
        }

        if (launchedAt > 0) {
            taskDocument.launchedAt = dateFormat.format(new Date(launchedAt));
            taskDocument.computedFields.msFromSubmittedToLaunched = launchedAt - acceptedAt;
        }

        if (startingAt > 0) {
            taskDocument.startingAt = dateFormat.format(new Date(startingAt));
            taskDocument.computedFields.msFromLaunchedToStarting = startingAt - launchedAt;
            taskDocument.computedFields.msToStarting = startingAt - acceptedAt;
        }

        if (startedAt > 0) {
            taskDocument.startedAt = dateFormat.format(new Date(startedAt));
            taskDocument.computedFields.msFromStartingToStarted = startedAt - startingAt;
            taskDocument.computedFields.msToStarted = startedAt - acceptedAt;
        }

        if (completedAt > 0) {
            taskDocument.finishedAt = dateFormat.format(new Date(completedAt));
            taskDocument.computedFields.msFromStartedToFinished = completedAt - startedAt;
            taskDocument.computedFields.msToFinished = completedAt - acceptedAt;
        }

        taskDocument.message = task.getStatus().getReasonMessage();
        taskDocument.titusContext = context;

        return taskDocument;
    }

    private static Optional<TaskStatus> findTaskStatus(Task task, TaskState taskState) {
        if (task.getStatus().getState() == taskState) {
            return Optional.of(task.getStatus());
        } else {
            return task.getStatusHistory().stream().filter(taskStatus -> taskStatus.getState() == taskState).findFirst();
        }
    }

    private static TitusTaskState toV2TaskState(TaskStatus taskStatus) {
        switch (taskStatus.getState()) {
            case Accepted:
                return TitusTaskState.QUEUED;
            case Launched:
                return TitusTaskState.DISPATCHED;
            case StartInitiated:
                return TitusTaskState.STARTING;
            case Started:
                return TitusTaskState.RUNNING;
            case KillInitiated:
                return TitusTaskState.RUNNING;
            case Finished:
                String reasonCode = taskStatus.getReasonCode();
                if (reasonCode.equalsIgnoreCase("normal")) {
                    return TitusTaskState.FINISHED;
                } else if (reasonCode.equalsIgnoreCase("failed")) {
                    if (taskStatus.getReasonMessage().contains("Killed")) {
                        return TitusTaskState.STOPPED;
                    } else {
                        return TitusTaskState.FAILED;
                    }
                }
                return TitusTaskState.FAILED;
            default:
                return TitusTaskState.FAILED;
        }
    }
}