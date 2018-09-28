package es.redmic.notificationmanager.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import es.redmic.brokerlib.alert.Message;
import es.redmic.notificationmanager.mail.service.EmailService;

@Controller
@KafkaListener(topics = "${broker.topic.alert}")
public class NotificationController {

	EmailService service;
	
	@Autowired
	public NotificationController(EmailService service) {
		this.service = service;
	}
	
	@KafkaHandler
	public void listen(Message event) {
		
		//TODO: si el type no es email, llamar al servicio adecuado.
		if (!event.getTo().contains("test")) {
			service.sendSimpleMessage(event.getTo(), event.getSubject(), event.getMessage());
		}
	}
}
