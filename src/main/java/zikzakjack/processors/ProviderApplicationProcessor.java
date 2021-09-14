package zikzakjack.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderApplicationProcessor {

	@Autowired
	ProcessEngine processEngine;

	@Autowired
	RuntimeService runtimeService;

	@Autowired
	TaskService taskService;

	@Autowired
	HistoryService historyService;

	public void process() {

		// upload bomn file
		String bpmnFile = "provider_application.bpmn";
		uploadBpmn(bpmnFile, processEngine);

		String continueAgain = "1";
		System.out.println("\n\n=== Do you want to continue ? Enter 0 to Quit ===\n\n");
		Scanner scanner = new Scanner(System.in);
		continueAgain = scanner.nextLine().trim();

		while (!continueAgain.equals("0")) {

//			try {
				// key process variables
				System.out.println("\nEnter applicationId ? ");
				String applicationId = scanner.nextLine().trim();

				System.out.println("\nEnter hasAdminProfile Y/N ? ");
				boolean hasAdminProfile = scanner.nextLine().toLowerCase().equals("y");

				System.out.println("\nEnter assignee ? ");
				String assigneeStr = scanner.nextLine().trim();
				String assignee = assigneeStr.equals("0") ? null : assigneeStr;

				// providerApplicationStartEvent
				Map<String, Object> variables = new HashMap<String, Object>();
				ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("providerApplication",
						variables);

				// providerApplicationSubmission
				boolean isEnrollment;
				System.out.println("\nEnrollment or Modification Y/N ? ");
				isEnrollment = scanner.nextLine().toLowerCase().equals("y");
				providerApplicationSubmission(taskService, variables, applicationId, hasAdminProfile, assignee,
						isEnrollment);

				// providerApplicationAssignment
				providerApplicationAssignment(taskService, variables, hasAdminProfile, assignee);

				// providerApplicationProcessing
				System.out.println("\nEnrollment Approved or Rejected Y/N ? ");
				boolean isApplicationApproved = scanner.nextLine().toLowerCase().equals("y");
				providerApplicationProcessing(taskService, variables, isEnrollment, isApplicationApproved);

				// providerApplicationProcessFlow
				providerApplicationProcessFlow(historyService, processInstance);
//			} catch (Exception exception) {
//				System.out.println(exception);
//			}
			System.out.println("\n\n=== Do you want to continue ? Enter 0 to Quit ===\n\n");
			continueAgain = scanner.nextLine().trim();
		}
		System.out.println("\n\n=== Exited Successfully ... ===\n\n");
		System.exit(0);
	}

	private static void uploadBpmn(String bpmnFile, ProcessEngine processEngine) {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment().addClasspathResource(bpmnFile).deploy();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deployment.getId()).singleResult();
		System.out.println("\nFound process definition : " + processDefinition.getName());
	}

	private void providerApplicationSubmission(TaskService taskService, Map<String, Object> variables,
			String applicationId, boolean hasAdminProfile, String assignee, boolean isEnrollment) {
		String businessKey;
		if (isEnrollment) {
			businessKey = "PE" + applicationId;
		} else {
			businessKey = "PR" + applicationId;
		}
		variables.put("isEnrollment", isEnrollment);
		variables.put("hasAdminProfile", hasAdminProfile);
		variables.put("assignee", assignee);
		variables.put("businessKey", businessKey);
		variables.put("applicationId", applicationId);

		List<Task> tasks = taskService.createTaskQuery().list();
		for (Task task : tasks) {
			System.out.println("\n=== providerApplicationSubmission ===");
			System.out.println(task.getName() + " >> " + task.getId());
			taskService.complete(task.getId(), variables);
		}
	}

	private void providerApplicationAssignment(TaskService taskService, Map<String, Object> variables,
			boolean hasAdminProfile, String currentAssignee) {
		List<Task> tasks = taskService.createTaskQuery().list();
		for (Task task : tasks) {
			System.out.println("\n=== providerApplicationAssignment ===");
			System.out.println(task.getName() + " >> " + task.getId());
			System.out.println("\nEnter New assignee ? ");
			Scanner scanner = new Scanner(System.in);
			String newAssigneeStr = scanner.nextLine().trim();
			String newAssignee = newAssigneeStr.equals("0") ? null : newAssigneeStr;

			if (variables != null) {
				if (hasAdminProfile && currentAssignee == null) {
					System.out.println("Task Assigned to " + newAssignee);
				}
				if (hasAdminProfile && currentAssignee != null) {
					System.out.println("Task Reassigned to " + newAssignee);
				}
				if (!hasAdminProfile && currentAssignee == null) {
					System.out.println("Task Claimed by " + newAssignee);
				}
				variables.put("assignee", newAssignee);
				task.setAssignee(newAssignee);
			}
			taskService.saveTask(task);
			taskService.complete(task.getId(), variables);
		}
	}

	private void providerApplicationProcessing(TaskService taskService, Map<String, Object> variables,
			boolean isEnrollment, boolean isApplicationApproved) {
		if (isEnrollment) {
			variables.put("isEnrollmentApproved", isApplicationApproved);
		} else {
			variables.put("isModificationApproved", isApplicationApproved);
		}

		List<Task> tasks = taskService.createTaskQuery().list();
		for (Task task : tasks) {
			System.out.println("\n=== providerApplicationProcessing ===");
			System.out.println(task.getName() + " >> " + task.getId());
			taskService.complete(task.getId(), variables);
		}
	}

	private void providerApplicationProcessFlow(HistoryService historyService, ProcessInstance processInstance) {
		List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstance.getId()).finished().orderByHistoricActivityInstanceEndTime().asc()
				.list();

		System.out.println("\n\n=== providerApplicationProcessFlow ===\n");
		for (HistoricActivityInstance activity : activities) {
			System.out.println(String.format("%90s took %5s ms ... >> %25s >> %s", activity.getActivityId(),
					activity.getDurationInMillis(), activity.getActivityType(), activity.getAssignee()));
		}
	}

}
