package zikzakjack.services;

import java.util.Set;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ProviderApplicationEnrollmentProcessingMailToOrg implements JavaDelegate {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${mail.providerapplication.from}")
	String from;

	@Value("${mail.providerapplication.enrollment.org.to}")
	String to;

	@Value("${mail.providerapplication.enrollment.org.cc}")
	String[] cc;

	@Value("${mail.providerapplication.enrollment.org.subject}")
	String subject;

	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("ProviderApplicationEnrollmentProcessingMailToOrg ...");
		Set<String> variableNames = execution.getVariableNames();
		for (String variableName : variableNames) {
			System.out.print("\t >>\t" + variableName + " : " + execution.getVariable(variableName) + "\t");
		}
		String message = "\n\t >> \tMail :: >>\tDear Org Users, Enrollment Application is assigned to Org Unit for further processing ...";
		System.out.println(message);
		prepareAndSend(from, to, cc, subject, message);
	}

	public void prepareAndSend(String from, String to, String[] cc, String subject, String message) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setSubject(subject);
		msg.setFrom(from);
		msg.setTo(to);
		msg.setCc(cc);
		msg.setText(message);
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			// simply log it and go on...
			System.err.println(ex.getMessage());
		}
	}
}
