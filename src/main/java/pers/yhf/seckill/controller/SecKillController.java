package pers.yhf.seckill.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.yhf.seckill.domain.MiaoshaOrder;
import pers.yhf.seckill.domain.MiaoshaUser;
import pers.yhf.seckill.rabbitmq.MQSender;
import pers.yhf.seckill.rabbitmq.MiaoshaMessage;
import pers.yhf.seckill.redisCluster.RedisService;
import pers.yhf.seckill.redisCluster.SecKillActivityKey;
import pers.yhf.seckill.result.CodeMsg;
import pers.yhf.seckill.result.Result;
import pers.yhf.seckill.service.GoodsService;
import pers.yhf.seckill.service.MiaoshaService;
import pers.yhf.seckill.service.OrderService;
import pers.yhf.seckill.vo.GoodsVo;
 

@Controller
@RequestMapping("/miaosha")
public class SecKillController implements InitializingBean{

	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MiaoshaService miaoshaService;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private MQSender sender;
	
	private Map<Long,Boolean> isSecKillOverMap = new HashMap<Long,Boolean>();
	
	/**
	 * 系统初始化
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		 List<GoodsVo> goodsList = goodsService.getGoodsVoList();
		 if(goodsList==null) return;
		//1 将商品数量加载到缓存中
		 for(GoodsVo goods:goodsList){
			 redisService.set(SecKillActivityKey.getSecKillGoodsStock, ""+goods.getId(), goods.getStockCount());
			 isSecKillOverMap.put(goods.getId(), false);
		 }
	}
	
	
	
	
	@RequestMapping(value="/{path}/do_miaosha",method = RequestMethod.POST)
	@ResponseBody
    public Result<Integer> doMiaosha(Model model,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId,@PathVariable("path")String path) {
	    model.addAttribute("user",user); 
	    
	     if(user == null){ 
	    	  return Result.error(CodeMsg.SESSION_ERROR);
	     }
	     
	     //验证path
	     boolean isPathValid = miaoshaService.checkPath(user,goodsId,path);
	     if(!isPathValid){
	    	 return Result.error(CodeMsg.REQUEST_ILLEGAL);
	     }
	      
	     //此处内存标记，目的在于减少对于Redis的访问
	     boolean over = isSecKillOverMap.get(goodsId);
	     if(over){  
	    	 return Result.error(CodeMsg.SECKILL_OVER);
	     }
	     
	     //假如有10个商品，某用户同时发出两个请求 req1, req2
	     //req1判断库存 可以购买，req2同样OK
	     //两个请求再同时判断是否秒杀到了，显然两个请求都没有秒杀到
	     //结果创建了两个订单，也就是说一个用户秒杀了两件商品
	      
	     
	     //2 预减库存
	     long stock = redisService.decr(SecKillActivityKey.getSecKillGoodsStock, ""+goodsId);
	        System.out.println("id="+goodsId+"  stock="+stock); 
	       if(stock<0){
	    	   isSecKillOverMap.put(goodsId, true);  
	    	   return Result.error(CodeMsg.SECKILL_OVER);
	       }
	       
	     //3判断是否秒杀到了
	     MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
	      if(order!=null){  
	    	  return Result.error(CodeMsg.REPEAT_SECKILL);
	      }
	     
	     //4 入队
	      MiaoshaMessage mm = new MiaoshaMessage();
	      mm.setUser(user);
	      mm.setGoodsId(goodsId); 
	      sender.sendMiaoshaMessage(mm);
	    
	      return Result.success(0); //排队中
	 }
	
	
	
	/**
	 * 
	 * 秒杀成功，返回orderId
	 * 秒杀失败，返回-1
	 * 排队中， 0
	 */
	@RequestMapping(value="/result",method = RequestMethod.GET)
	@ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
	    model.addAttribute("user",user);
	     if(user == null)return Result.error(CodeMsg.SESSION_ERROR);
	     long result = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
	     return Result.success(result);
	}
	
	
	
	
	@RequestMapping(value="/path",method = RequestMethod.GET)
	@ResponseBody
    public Result<String> getSecKillPath(HttpServletRequest request,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId,
    		@RequestParam("verifyCode")int verifyCode) { 
	    
	     if(user == null){ 
	    	  return Result.error(CodeMsg.SESSION_ERROR);
	     }
	     
	     
	      //查询访问次数
	     String uri = request.getRequestURI();
	     String key = uri+"_"+user.getId();
	     Integer count = redisService.get(SecKillActivityKey.access, key, Integer.class);
	      if(count == null){
	    	  redisService.set(SecKillActivityKey.access, key, 1);  
	      }
	      else if(count<5) redisService.incr(SecKillActivityKey.access, key);
	      else return Result.error(CodeMsg.ACCESS_LIMIT);
	      
	     
	      //访问太频繁的话，直接返回错误信息，以下任务都不执行
	     boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
	     if(!check){
	    	 //未通过验证
	    	 return Result.error(CodeMsg.REQUEST_ILLEGAL);  //非法请求 
	     }
	     
	     String path = miaoshaService.createSecKillPath(user,goodsId);
	      
	     return Result.success(path); 
	}
	
	
	//生成图片验证码
	@RequestMapping(value="/verifyCode",method = RequestMethod.GET)
	@ResponseBody
    public Result<String> generateVerifyCode(HttpServletResponse response,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) { 
	    
	     if(user == null){ 
	    	  return Result.error(CodeMsg.SESSION_ERROR);
	     }
	      
	     try {
	    		BufferedImage image  = miaoshaService.createSecKillverifyCode(user, goodsId);
	    		OutputStream out = response.getOutputStream();
	    		ImageIO.write(image, "JPEG", out);
	    		out.flush();
	    		out.close();
	    		return null;
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    		return Result.error(CodeMsg.MIAOSHA_FAIL);
	    	} 
	}
	

	
	
}
