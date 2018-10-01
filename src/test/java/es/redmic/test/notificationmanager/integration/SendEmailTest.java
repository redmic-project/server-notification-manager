package es.redmic.test.notificationmanager.integration;

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
import org.springframework.kafka.test.rule.KafkaEmbedded;
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
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { NotificationManagerApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "schema.registry.port=18084" })
public class SendEmailTest extends KafkaBaseIntegrationTest {

	static String alertTopic = "alert";

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, alertTopic);

	@Autowired
	private KafkaTemplate<String, Message> kafkaTemplate;

	@MockBean
	EmailService service;

	@PostConstruct
	public void SendEmailTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@Test
	public void sentAlertMessage_SendEmail_IfTypeIsEmail() throws InterruptedException {

		String subjectDefault = "[ERROR][TEST] ", subject = subjectDefault + "SendMailTest", to = "info@redmic.es",
				content = "Esto es una prueba";
		Message message = new Message(to, subject, content, AlertType.ERROR.name());

		ListenableFuture<SendResult<String, Message>> future = kafkaTemplate.send(alertTopic, message);
		future.addCallback(new SendListener());

		Thread.sleep(4000);

		verify(service).sendSimpleMessage(to, subject, content);
	}
}