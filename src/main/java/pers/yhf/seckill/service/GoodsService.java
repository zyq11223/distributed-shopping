package pers.yhf.seckill.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.yhf.seckill.dao.GoodsDao;
import pers.yhf.seckill.domain.Goods;
import pers.yhf.seckill.domain.MiaoshaGoods;
import pers.yhf.seckill.vo.GoodsVo;

@Service
public class GoodsService {

	@Autowired
	private GoodsDao goodsDao;
	
	public List<GoodsVo> getGoodsVoList(){
		return this.goodsDao.getGoodsVoList(); 
	}

	
	
	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
	  return this.goodsDao.getGoodsVoByGoodsId(goodsId);
	}



	public boolean reduceStock(GoodsVo goods) {
	 MiaoshaGoods miaoshagoods = new MiaoshaGoods();
	 miaoshagoods.setGoodsId(goods.getId()); 
	  int ret = goodsDao.reduceStock(miaoshagoods);
	  return ret>0;
	}
	 

/*
	// @Transactional
	public boolean tx() { 
		User u1 = new User();
		u1.setId(2);
		u1.setName("222"); 
		userDao.insert(u1);
		
		User u2 = new User();
		u2.setId(1);
		u2.setName("1111"); 
		userDao.insert(u2);
		return true;
	}*/
	
}
