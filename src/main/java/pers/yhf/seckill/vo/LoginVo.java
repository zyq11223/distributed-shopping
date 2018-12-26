package pers.yhf.seckill.vo;

public class LoginVo{

	Long userId;
	String token;
	
	
	//public static LoginVo loginVo = new LoginVo();
	
     //懒加载模式
	private static volatile LoginVo instance = null;
	
	private LoginVo(){} 
	
	
	public static LoginVo getInstance(){
		if(instance == null) createInstance();
		return instance;
	}
    private synchronized static LoginVo createInstance() { 
		if(instance == null) instance = new LoginVo();
		return instance;
	}
	
	
	
	
	
 //------------------------------------------------------//
	

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	 
	
	
	
}
