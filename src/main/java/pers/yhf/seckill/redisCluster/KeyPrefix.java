package pers.yhf.seckill.redisCluster;

public interface KeyPrefix {

	public int expireSeconds();
	
	public String getPrefix();
	
}
