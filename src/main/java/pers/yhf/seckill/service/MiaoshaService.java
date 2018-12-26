package pers.yhf.seckill.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pers.yhf.seckill.domain.MiaoshaOrder;
import pers.yhf.seckill.domain.MiaoshaUser;
import pers.yhf.seckill.domain.OrderInfo;
import pers.yhf.seckill.redisCluster.RedisService;
import pers.yhf.seckill.redisCluster.SecKillActivityKey;
import pers.yhf.seckill.util.MD5Util;
import pers.yhf.seckill.util.UUIDUtil;
import pers.yhf.seckill.vo.GoodsVo;

@Service
public class MiaoshaService {

	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private RedisService redisService;
	
	
	
	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {  
	   //减库存  下订单  写入秒杀订单
		boolean success = goodsService.reduceStock(goods);  //这里 减库存有可能失败
		//减库存成功才需要下订单
		if(success){
		return orderService.createOrder(user,goods);
		}
		else{
			setGoodsOver(goods.getId()); //商品秒杀完了
			return null;
		}
	}

	

	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
		 if(order!=null){  //秒杀成功
			 return order.getOrderId();
		 }else{
			 boolean isOver = getGoodsOver(goodsId);
			 if(isOver){
				 return -1;
			 }
			 else{  //没有卖完，继续轮询
				 return 0;
			 }
		 }
		 
	}

	
	private void setGoodsOver(Long goodsId) {
		redisService.set(SecKillActivityKey.isGoodsOver, ""+goodsId, true);
	}
	
	
	private boolean getGoodsOver(long goodsId) { 
		return redisService.exists(SecKillActivityKey.isGoodsOver, ""+goodsId); 
	}



	public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
		if(user == null || path== null) return false;
	   String pathBefore = redisService.get(SecKillActivityKey.getSecKillPath, ""+user.getId()+"_"+goodsId, String.class);
		return path.equals(pathBefore); 
	}


 
	public String createSecKillPath(MiaoshaUser user, long goodsId) {
		
		if(user == null || goodsId<=0) return null;
		
		String str = MD5Util.md5(UUIDUtil.uuid()+"123456");  
	    redisService.set(SecKillActivityKey.getSecKillPath, ""+user.getId()+"_"+goodsId, str);
	    return str;
	}

 
      //生成秒杀验证码
	public BufferedImage createSecKillverifyCode(MiaoshaUser user, long goodsId) {
		if(user == null || goodsId<=0) return null;
		int width = 80;
		int height = 32;
		//create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// 生成验证码
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		//把验证码存到redis中
		int rnd = calc(verifyCode);
		redisService.set(SecKillActivityKey.getSecKillVerifyCode, user.getId()+","+goodsId, rnd);
		//输出图片	
		return image;

	} 
	
	
	
	public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
		if(user == null || goodsId <=0) {
			return false;
		}
		Integer codeOld = redisService.get(SecKillActivityKey.getSecKillVerifyCode, user.getId()+","+goodsId, Integer.class);
		if(codeOld == null || codeOld - verifyCode != 0 ) {
			return false;
		}
		redisService.delete(SecKillActivityKey.getSecKillVerifyCode, user.getId()+","+goodsId);
		return true;
	}
	
	
	private static int calc(String exp) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return (Integer)engine.eval(exp);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	
	private static char[] ops = new char[] {'+', '-', '*'};
	/**
	 * + - * 
	 * */
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
	    int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = ""+ num1 + op1 + num2 + op2 + num3;
		return exp;
	}
	
	
	

	
	
}
