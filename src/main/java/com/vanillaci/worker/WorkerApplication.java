package com.vanillaci.worker;

import com.vanillaci.worker.receivers.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;

/**
 * @author Joel Johnson
 */
@Configuration
@ComponentScan("com.vanillaci.worker")
@EnableAutoConfiguration
public class WorkerApplication {
	@Bean
	Queue queue(AppConfiguration appConfiguration) {
		return new Queue(appConfiguration.getQueueName(), false);
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange("vanilla-ci-exchange");
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange, AppConfiguration appConfiguration) {
		return BindingBuilder.bind(queue).to(exchange).with(appConfiguration.getQueueName());
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter, AppConfiguration appConfiguration) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(appConfiguration.getQueueName());
		container.setMessageListener(listenerAdapter);
		container.setMaxConcurrentConsumers(appConfiguration.getNumberConcurrentWork());
		return container;
	}

	@Bean
	WorkReceiver receiver() {
		return new WorkReceiver();
	}

	@Bean
	MessageListenerAdapter listenerAdapter(WorkReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	public static void main(String[] args) {
		SpringApplication.run(WorkerApplication.class, args);
	}
}
