package es.redmic.test.notificationmanager.integration;

import static org.mockito.Mockito.times;

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

import static org.mockito.Mockito.verify;

import javax.annotation.PostConstruct;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.alert.AlertType;
import es.redmic.brokerlib.alert.Message;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.notificationmanager.NotificationManagerApplication;
import es.redmic.notificationmanager.mail.service.EmailService;
import es.redmic.notificationmanager.mail.service.SlackService;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { NotificationManagerApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "schema.registry.port=18084" })
public class SendSlackAlertTest extends KafkaBaseIntegrationTest {

	static String alertTopic = "alert";

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(3, true, 3, alertTopic);

	@Autowired
	private KafkaTemplate<String, Message> kafkaTemplate;

	@MockBean
	EmailService emailService;
	
	@MockBean
	SlackService slackService;

	@PostConstruct
	public void SendEmailTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}
	
	@Test
	public void sentAlertMessage_SendSlackAlert_IfTypeIsRealTime() throws InterruptedException {

		String subjectDefault = "[ERROR][TEST] ",
				subject = subjectDefault + "SendSlackAlertTest",
				content = "Esto es una prueba";
		
		Message message = new Message(subject, content, AlertType.NIFI_ACTIVITY_MONITOR.name());

		ListenableFuture<SendResult<String, Message>> future = kafkaTemplate.send(alertTopic, message);
		future.addCallback(new SendListener());

		Thread.sleep(4000);
		verify(slackService, times(1)).sendMessage(subject, content);
	}
	
	@Test
	public void sentAlertMessage_NoSendSlackAlert_IfTypeIsNotRealTime() throws InterruptedException {

		String subjectDefault = "[ERROR][TEST] ",
				subject = subjectDefault + "SendSlackAlertTest",
				content = "Esto es una prueba";
		
		Message message = new Message(subject, content, AlertType.ERROR.name());

		ListenableFuture<SendResult<String, Message>> future = kafkaTemplate.send(alertTopic, message);
		future.addCallback(new SendListener());

		Thread.sleep(4000);
		verify(slackService, times(0)).sendMessage(subject, content);
	}
}
