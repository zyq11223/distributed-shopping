package pers.yhf.seckill.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pers.yhf.seckill.dao.OrderDao;
import pers.yhf.seckill.domain.MiaoshaOrder;
import pers.yhf.seckill.domain.MiaoshaUser;
import pers.yhf.seckill.domain.OrderInfo;
import pers.yhf.seckill.redisCluster.RedisService;
import pers.yhf.seckill.redisCluster.SecKillActivityKey;
import pers.yhf.seckill.vo.GoodsVo;

@Service
public class OrderService {

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private RedisService redisService;
	
	public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(Long userId, long goodsId) {
	    // return this.orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId,goodsId);
		return redisService.get(SecKillActivityKey.getSecKillOrderByUidGid, ""+userId+"_"+goodsId, MiaoshaOrder.class);
	}

	@Transactional
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) { 
		
		OrderInfo orderInfo = new OrderInfo();
		
		
		orderInfo.setCreateDate(new Date()); 
		orderInfo.setDeliveryAddrId(0L); 
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName()); 
		
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId()); 
		
		orderDao.insert(orderInfo);
		
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderInfo.getId());
		miaoshaOrder.setUserId(user.getId());
		
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		//生成订单后，需要写入缓存中
		redisService.set(SecKillActivityKey.getSecKillOrderByUidGid, ""+user.getId()+"_"+goods.getId(), miaoshaOrder);
		
		return orderInfo;
	}
 
	public OrderInfo getOrderById(long orderId) { 
		 return orderDao.getOrderById(orderId);
	}

	
	
}
