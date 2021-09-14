package zikzakjack;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import zikzakjack.processors.ProviderApplicationProcessor;

@SpringBootApplication
public class BpmFlowableApplication {

	@Autowired
	ProviderApplicationProcessor providerApplicationProcessor;

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BpmFlowableApplication.class, args);
		printBeanNames(context);

		BpmFlowableApplication app = (BpmFlowableApplication) context.getBean("bpmFlowableApplication");
		app.providerApplicationProcessor.process();

	}

	private static void printBeanNames(ApplicationContext context) {
		String[] beanNames = context.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (int i = 0; i < beanNames.length; i++) {
			System.out.println("beanName:" + beanNames[i]);
		}
	}

}
