package zikzakjack.services;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class RestCallServiceTaskJavaDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		String employee = (String) execution.getVariable("employee");
		System.out.println("Dear " + employee + ", Your Leave request is rejected..");
		System.out.println("Finished Invoking RestCallServiceTaskJavaDelegate ...");
	}

}
