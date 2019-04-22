package es.redmic.notificationmanager.common.controller;

/*-
 * #%L
 * Notification manager
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
