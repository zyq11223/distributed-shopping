package pers.yhf.seckill.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import pers.yhf.seckill.config.SecKillConfig; 
import pers.yhf.seckill.dao.MiaoshaUserDao;
import pers.yhf.seckill.domain.MiaoshaUser;
import pers.yhf.seckill.exception.GlobalException;
import pers.yhf.seckill.redisCluster.RedisService;
import pers.yhf.seckill.redisCluster.SecKillActivityKey;
import pers.yhf.seckill.result.CodeMsg;
import pers.yhf.seckill.util.MD5Util;
import pers.yhf.seckill.util.UUIDUtil;
import pers.yhf.seckill.vo.LoginVo;

@Service
public class MiaoshaUserService {
	  

	@Autowired
	private MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private MiaoshaUserService miaoshaUserService;
	
	
	public MiaoshaUser getUserById(long id){
		  //取缓存
		MiaoshaUser user = redisService.get(SecKillActivityKey.getById, ""+id, MiaoshaUser.class);
		if(user!=null) return user;
		
		//若用户为空，则需要取数据库
		user = this.miaoshaUserDao.getUserById(id);
		if(user!=null){
			redisService.set(SecKillActivityKey.getById, ""+id, user);
		}
		return user;
	} 
	
	
	public boolean upatePassword(String token,long id,String newPassword){
		//取user
		MiaoshaUser user = getUserById(id);
		if(user == null) throw new GlobalException(CodeMsg.MOBILE_NOT_EXISTS);
		
		  //更新数据库
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(newPassword, user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		
		//处理缓存,否则会出现缓存不一致的问题
		    //删除的原因是
		redisService.delete(SecKillActivityKey.getById, ""+id);
		  //如若删除token 则无法登录，此处只需要更新token
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(SecKillActivityKey.token, token,user);
		return true;
	}

	
	@RequestMapping(value="/do_login",method=RequestMethod.GET) 
    @ResponseBody 
	public Map<String,Object> login(HttpServletResponse response,@RequestParam("nickname")String nickname,@RequestParam("password")String password) { 
	 
		//if(loginVo == null)   throw new GlobalException(CodeMsg.SERVER_ERROR);
 	  

		Map<String,Object> map =new HashMap<String,Object>();
		   
		   MiaoshaUser user = this.miaoshaUserService.getMiaoshaUserByNickName(nickname);
		     // System.out.println(user.getSalt()); 
		   
		   String realPassword = MD5Util.inputPassToDBPass(password,user.getSalt());  //加密后的密码
		    //登录成功
		   if(!realPassword.equals(user.getPassword())) map.put("loginStatus", 0);
	 	   this.miaoshaUserService.updateMiaoshaUserLoginInfo(nickname); 
	    	map.put("loginStatus",1);
	 		//System.out.println("用户输入密码123456，则真正网络上传输的串："+MD5Util.inputPassToFormPass("123456"));
	       // System.out.println(MD5Util.inputPassToDBPass("123456","1a2b3c4d")); 
		    
		    String token = UUIDUtil.uuid();
		    generateCookie(response,token,user);
		    
		    
		     LoginVo.getInstance().setUserId(user.getId()); 
		     LoginVo.getInstance().setToken(token); 
		     
		     
		    miaoshaUserService.getUserInfoByToken(response,token);
	        
		    return map;
	   }
	
	 
	
	  /**
	 * 登录信息更新
	 */
	public void updateMiaoshaUserLoginInfo(String nickname){
	    this.miaoshaUserDao.updateMiaoshaUserLoginInfo(nickname);	
	}
	 
	
	public MiaoshaUser getMiaoshaUserByNickName(String nickname){
		return this.miaoshaUserDao.getMiaoshaUserByNickName(nickname);
	}
	
	
	
	
	private void generateCookie(HttpServletResponse response,String token,MiaoshaUser user){
		//生成cookie
		  //String token = UUIDUtil.uuid();
		   //token对应的用户信息写入redis
		  redisService.set(SecKillActivityKey.token, token, user);
		  Cookie cookie = new Cookie(SecKillConfig.COOKIE_NAME_TOKEN,token); 
		  cookie.setMaxAge(SecKillActivityKey.token.expireSeconds()); //cookie有效期
		  cookie.setPath("/");
		  response.addCookie(cookie); 
	}

 
	public MiaoshaUser getUserInfoByToken(HttpServletResponse response,String token) { 
		if(StringUtils.isEmpty(token)){
			return null;
		}
		System.out.println("token:"+token);
		MiaoshaUser user = redisService.get(SecKillActivityKey.token, token, MiaoshaUser.class);
		//延长有效期 
		 if(user!=null){
			 generateCookie(response,token,user);
		 }
		 return user;
	 }
	
}
