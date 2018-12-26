package pers.yhf.seckill.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pers.yhf.seckill.domain.MiaoshaUser;
 
@Mapper
public interface MiaoshaUserDao {

	@Select("select * from miaosha_user where id = #{id}")
	public MiaoshaUser getUserById(@Param("id")long id);

	@Update(" upate miaosha_user set password= #{password} where id = #{id} ")
	public void update(MiaoshaUser toBeUpdate);

    @Update("update miaosha_user set login_count=login_count+1 where nickname = #{nickname}")
    public void updateMiaoshaUserLoginInfo(@Param("nickname")String nickname);

    
    @Select("select * from miaosha_user where nickname = #{nickname}")
	public MiaoshaUser getMiaoshaUserByNickName(@Param("nickname")String nickname);
	
}
