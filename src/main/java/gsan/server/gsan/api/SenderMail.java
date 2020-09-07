package gsan.server.gsan.api;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import gsan.server.gsan.api.service.model.task;


public class SenderMail {
	
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public void sendEmailWithoutTemplating(String email, JavaMailSender sender, task t) throws UnsupportedEncodingException{

		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		// Set From: header field of the header.
		try {
			helper.setFrom(new InternetAddress("no-reply-gsan@labri.fr", "NO-REPLY"));
			// Set To: header field of the header.
			helper.setTo(new InternetAddress(email));
			// Set Subject: header field
			message.setSubject("[GSAn] Analysis finished");

		
			// Fill the message
			helper.setText("** This is an automatic email, Please don't reply to it **"+
					"\n\n"+
							"Your analysis sent on "+ t.getDate().toLocalDateTime().format(format) +" is finished and you can access to the results using the following link:"+
							"\n https://gsan.labri.fr/"+t.getId()+
							"\n\n"+
									"Regards,"+
									"\n\n"+
									"GSAn team\n\n"+
									"**  If you have any questions about GSAn, please contact us in  https://gsan.labri.fr/contact **\r\n"
									);
	    sender.send(message);
	    log.debug("Sent message successfully....");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			log.error("The email is incorrect");
			//e.printStackTrace();
		}catch(MailSendException e) {
			log.error("The email is null or incorrect");
		}



		// Get the default Session object.
	}
	

}
