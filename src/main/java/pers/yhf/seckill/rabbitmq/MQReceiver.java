package pers.yhf.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.yhf.seckill.config.MQConfig;
import pers.yhf.seckill.domain.MiaoshaOrder;
import pers.yhf.seckill.domain.MiaoshaUser;
import pers.yhf.seckill.redisCluster.RedisService;
import pers.yhf.seckill.service.GoodsService;
import pers.yhf.seckill.service.MiaoshaService;
import pers.yhf.seckill.service.OrderService;
import pers.yhf.seckill.vo.GoodsVo;
 

@Service
public class MQReceiver {

	private Logger log = LoggerFactory.getLogger(MQReceiver.class);
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MiaoshaService miaoshaService;
	
	/**
	 * Direct模式
	 * @param message
	 */
	@SuppressWarnings("static-access")
	@RabbitListener(queues=MQConfig.SECKILL_QUEUE)
	public void receive(String message){
		log.info("receive message: "+message);
		MiaoshaMessage mm = redisService.stringToBean(message, MiaoshaMessage.class);
	     MiaoshaUser user = mm.getUser();
	     long goodsId = mm.getGoodsId();
	     
	     GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
	     int stock = goods.getStockCount();
	     if(stock<=0) return; 
	     
	   //判断是否秒杀到了
	        //这里可做的优化：可以不用查数据库，当生成订单时 ，我们把订单写入缓存里了，故只需要查缓存即可
	     MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(),goodsId);
	     if(order != null) return ; 
	     
	   //减库存    下订单  写入秒杀订单
	     miaoshaService.miaosha(user,goods);
	   
	     
	}
	
	/*
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
	public void receiveTopic1(String message){
		log.info("topic queue1 message: "+message);
	}
	
	
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
	public void receiveTopic2(String message){
		log.info("topic queue2 message: "+message);
	}
	
	
	@RabbitListener(queues=MQConfig.HEADERS_QUEUE)
	public void receiveHeaderQueue(byte[] message){
		log.info("header queue message: "+new String(message));
	}*/
	
	
}
