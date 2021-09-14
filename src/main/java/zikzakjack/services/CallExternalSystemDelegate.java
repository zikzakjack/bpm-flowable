package zikzakjack.services;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class CallExternalSystemDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		String employee = (String) execution.getVariable("employee");
		Integer nrOfHolidays = (Integer) execution.getVariable("nrOfHolidays");
		String description = (String) execution.getVariable("description");
		System.out.println("Leave request recorded in the system >> " + employee + " applied " + nrOfHolidays
				+ " of holidays for the reason of " + description);
	}

}
