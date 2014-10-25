package com.vanillaci.worker;

import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

/**
 * @author Joel Johnson
 */
@Controller
public class MyController {
	@Autowired
	private AppConfiguration appConfiguration;

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	RabbitTemplate rabbitTemplate;

	@RequestMapping("/blah")
	@ResponseBody
	public String doSomething(@RequestBody String message) {
		System.out.println("Sending a new message.");

		rabbitTemplate.convertAndSend(appConfiguration.getQueueName(), message);

		return "sent: " + message;
	}
}
