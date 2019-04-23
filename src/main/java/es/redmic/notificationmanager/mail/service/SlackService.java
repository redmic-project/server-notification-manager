package es.redmic.notificationmanager.mail.service;

import java.io.IOException;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.webhook.Payload;

@Service
public class SlackService {

	@Value("${slack.webhook.url}")
	String url;
	
	@Value("${slack.webhook.username}")
	String username;
	
	@Value("${slack.webhook.channel}")
	String channel;

	public void sendMessage(String subject, String text) {

		Payload payload = Payload.builder()
				.channel(channel)
				.username(username)
				.text(subject + ": " + text)
					.build();

		Slack slack = Slack.getInstance();
		try {
			slack.send(url, payload);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
