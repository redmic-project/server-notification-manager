package es.redmic.notificationmanager.common.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import es.redmic.brokerlib.alert.Message;
import es.redmic.notificationmanager.mail.service.EmailService;

@Controller
@KafkaListener(topics = "${broker.topic.alert}")
public class NotificationController {

	protected static Logger logger = LogManager.getLogger();

	EmailService service;

	@Autowired
	public NotificationController(EmailService service) {
		this.service = service;
	}

	@KafkaHandler
	public void listen(Message event) {

		// TODO: decidir el canal para llamar al servicio adecuado.
		service.sendSimpleMessage(event.getTo(), event.getSubject(), event.getMessage());
		logger.info("Recibida notificación -> {}: {}", event.getSubject(), event.getMessage());
	}
}
