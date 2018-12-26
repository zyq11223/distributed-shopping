package pers.yhf.seckill.redisCluster;

import pers.yhf.seckill.config.SecKillConfig;

/**
 * 商品秒杀活动前缀类
 * @author Administrator
 *
 */
public class SecKillActivityKey implements KeyPrefix {

    private int expireSeconds;
	
	private String prefix;
	
	/**
	 * 过期时间
	 */
	@Override
	public int expireSeconds() {  //默认0表示永不过期
		return expireSeconds;
	}

	@Override
	public String getPrefix() {
		String className = getClass().getSimpleName();
		return className+":"+prefix;
	}

	
	
	
	public SecKillActivityKey(int expireSeconds,String prefix) { 
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}

	
	
	//秒杀商品
    public static SecKillActivityKey getGoodsList = new SecKillActivityKey(SecKillConfig.GOODSLIST_EXPIRE,"gl");
	public static SecKillActivityKey getGoodsDetail = new SecKillActivityKey(SecKillConfig.GOODSDETAIL_EXPIRE,"gd"); 
	public static SecKillActivityKey getSecKillGoodsStock = new SecKillActivityKey(SecKillConfig.SECKILL_GOODSSTOCK_EXPIRE,"gs");
	
	
	//
    public static SecKillActivityKey isGoodsOver = new SecKillActivityKey(SecKillConfig.JUDGE_GOODSOVER_EXPIRE,"go");
	public static SecKillActivityKey getSecKillPath = new SecKillActivityKey(SecKillConfig.SECKILL_PATH_EXPIRE,"mp");  //获取的秒杀路径有效期为60秒
	public static SecKillActivityKey getSecKillVerifyCode = new SecKillActivityKey(SecKillConfig.SECKILL_VERIFYCODE_EXPIRE,"vc");
	public static SecKillActivityKey access = new SecKillActivityKey(SecKillConfig.SECKILL_ACTIVITY_EXPIRE,"access");  //5秒内最多点击5次
	
	 //登录用户前缀
    public static SecKillActivityKey token = new SecKillActivityKey(SecKillConfig.TOKEN_EXPIRE,"tk");
    public static SecKillActivityKey getById = new SecKillActivityKey(SecKillConfig.GETBYID_EXPIRE,"id"); //对象缓存希望永久
    
    
    //秒杀订单中的中的uid与gid
  	public static SecKillActivityKey getSecKillOrderByUidGid = new SecKillActivityKey(SecKillConfig.SECKILL_ORDERBYUIDGID_EXPIRE, "order(uid,gid)");
	 

	
	
}
