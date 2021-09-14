package zikzakjack.services;

import java.util.Set;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class ProviderApplicationEnrollmentProcessingDefaultOrgAssignment implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("ProviderApplicationEnrollmentProcessingDefaultOrgAssignment ...");
		execution.setVariable("orgUnit", "Provider Enrollment");
		Set<String> variableNames = execution.getVariableNames();
		for (String variableName : variableNames) {
			System.out.print("\t >>\t" + variableName + " : " + execution.getVariable(variableName) + "\t");
		}
		System.out.println();
	}

}
