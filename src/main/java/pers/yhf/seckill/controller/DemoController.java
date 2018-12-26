package pers.yhf.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.yhf.seckill.domain.User;
import pers.yhf.seckill.redisCluster.RedisService;
import pers.yhf.seckill.result.CodeMsg;
import pers.yhf.seckill.result.Result;
import pers.yhf.seckill.service.UserService;
 

@Controller 
@RequestMapping("/demo")
public class DemoController {
	 
	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisService redisService;
	
	 
	 
	
	 	@RequestMapping("/")
	    @ResponseBody
	    String home() {
	        return "Hello World!";
	    }
	 	//1.rest api json输出 2.页面          默认处理
	 	@RequestMapping("/hello")
	    @ResponseBody
	    public Result<String> hello() {
	 		return Result.success("hello,imooc");
	       // return new Result(0, "success", "hello,imooc");
	    }
	 	
	 	@RequestMapping("/helloError")
	    @ResponseBody
	    public Result<String> helloError() {
	 		return Result.error(CodeMsg.SERVER_ERROR);
	 		//return new Result(500102, "XXX");
	    }
	 	
	 	@RequestMapping("/thymeleaf")
	    public String  thymeleaf(Model model) {
	 		model.addAttribute("name", "Joshua");
	 		return "hello";
	    }
	 	
	 	@RequestMapping("/db/get")
	 	@ResponseBody
	 	public Result<User> dbGet(){
	 		User user = userService.getUserById(1);
	 	    return Result.success(user);
	 	}
	 	
	 	
	 	@RequestMapping("/db/tx")
	 	@ResponseBody
	 	public Result<Boolean> dbTx(){
	 		   userService.tx();
	 	    return Result.success(true);
	 	}
	 	
	 	
	 	
	 	
	 	
	 	
}
