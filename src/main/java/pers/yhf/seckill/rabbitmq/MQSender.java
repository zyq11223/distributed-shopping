package pers.yhf.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.yhf.seckill.config.MQConfig;
import pers.yhf.seckill.redisCluster.RedisService; 

@Service
public class MQSender {
	
	private Logger log = LoggerFactory.getLogger(MQSender.class);
	
	@Autowired
	AmqpTemplate amqpTemplate;

	/*public void send(Object message){
		String msg = RedisService.beanToString(message);
		log.info("send message: "+msg);  
		amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
	}
	
	
	public void sendTopic(Object message){
		String msg = RedisService.beanToString(message);
		log.info("send topic message: "+msg);  
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1",msg+"1"); 
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2",msg+"2");
	}
	
	public void sendFanout(Object message){
		String msg = RedisService.beanToString(message);
		log.info("send fanout message: "+msg);  
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "",msg); 
		//amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "",msg+"2");
	}
	
	
	public void sendHeader(Object message){
		String msg = RedisService.beanToString(message);
		log.info("send header message: "+msg);  
		MessageProperties properties = new MessageProperties();
		 properties.setHeader("header1", "value1"); 
		 properties.setHeader("header2", "value2");
		Message obj = new Message(msg.getBytes(),properties);
		amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "",obj);  
	}
*/

	 public void sendMiaoshaMessage(MiaoshaMessage message) {
		 String msg = RedisService.beanToString(message);
			log.info("send message: "+msg);  
			amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
	 }
	
	
	
	
	
	
	
	
	
}
