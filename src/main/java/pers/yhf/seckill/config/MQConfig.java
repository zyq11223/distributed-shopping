package pers.yhf.seckill.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
	
	public static final String QUEUE = "queue";
	
	public static final String SECKILL_QUEUE = "seckillqueue"; 
	

	/**
	 * 直接模式
	 * @return
	 */
	@Bean
	public Queue queue(){
		return new Queue(QUEUE,true);
	}
	
	/**
	 * 主题模式
	 * @return
	 */
	/*@Bean
	public Queue topicQueue1(){
		return new Queue(TOPIC_QUEUE1,true);
	}
	
	@Bean
	public Queue topicQueue2(){
		return new Queue(TOPIC_QUEUE2,true);
	}*/
	
	/*@Bean
	public TopicExchange topicExchange(){
		return new TopicExchange(TOPIC_EXCHANGE);
	}*/
	
	
	/**
	 * Fanout模式
	 * @return
	 */
	/*@Bean
	public FanoutExchange fanoutExchange(){
		return new FanoutExchange(FANOUT_EXCHANGE);
	}*/
	
	
	/**
	 * Header模式
	 * @return
	 */
	/*@Bean
	public HeadersExchange headersExchange(){
		return new HeadersExchange(HEADERS_EXCHANGE);
	}*/
	
	/*@Bean
	public Queue headersQueue1(){
		return new Queue(HEADERS_QUEUE,true);
	}*/
	
	
	
	
	
	
	/*@Bean
	public Binding topicBinding1(){
		return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
	}
	@Bean
	public Binding topicBinding2(){
		return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
	}*/
	
	
	/**
	 * 广播模式绑定
	 * @return
	 */
	/*@Bean
	public Binding fanoutBinding1(){
		return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
	}
	@Bean
	public Binding fanoutBinding2(){
		return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
	}*/
	
	
	
	/**
	 * Headers模式绑定
	 * @return
	 *//*
	@Bean
	public Binding headersBinding(){
		Map<String,Object> map = new HashMap<String,Object>();
		  map.put("header1", "value1");
		  map.put("header2", "value2");
		return BindingBuilder.bind(headersQueue1()).to(headersExchange())
				.whereAll(map).match();
	}*/
	
	
	
}
