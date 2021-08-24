package kr.co.softcampus.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import kr.co.softcampus.beans.UserBean;

public interface UserMapper {
	@Select("SELECT user_name "+
			"FROM user_table "+
			"WHERE user_id= #{user_id}")
	String checkUserIdExist(String user_id);

	@Insert("insert into user_table(user_name,user_id,user_pw) "+"values(#{user_name},#{user_id},#{user_pw})")
	void addUserInfo(UserBean joinUserBean);
	
	@Select("select user_idx,user_name "+
			"from user_table "+
			"where user_id=#{user_id} and user_pw =#{user_pw}")
	UserBean getLoginUserInfo(UserBean tempLoginUserBean);
}