worker
======

Worker using RabbitMQ to receive work messages.

How To Use
==========

- Download, install, and run RabbitMQ. http://www.rabbitmq.com/download.html
- Compile project and run com.vanillaci.worker.WorkerApplication
- Add messages to the queue defined in application.properties as a json string that can be deserialzed as the WorkMessage class (or use the separate API project).
 
Eventually the implementations of WorkStep and WorkStepInteceptor will be dynamically loaded.
