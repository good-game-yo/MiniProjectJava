package kr.co.softcampus.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import kr.co.softcampus.beans.ContentBean;

public interface BoardMapper {
		// https://stackoverflow.com/questions/4260428/mybatis-how-to-return-the-id-of-the-inserted-object-under-postgres
		@Options(useGeneratedKeys=true, keyProperty="content_idx")
		@Insert("insert into content_table(content_subject, content_text, "+
				"content_file, content_writer_idx,content_board_idx,content_date) "+
				"values(#{content_subject},#{content_text},#{content_file},"+
				"#{content_writer_idx}, #{content_board_idx}, sysdate())")
		void addContentInfo(ContentBean writeContentBean);
		
		@Select("SELECT board_info_name "+
				"FROM board_info_table "+
				"WHERE board_info_idx=#{board_info_idx}")
		String getBoardInfoName(int board_info_idx);
		
		@Select("SELECT a1.content_idx, a1.content_subject, a2.user_name content_writer_name, "+
				"date_format(a1.content_date,'%Y-%m-%d') content_date " + 
				"FROM content_table a1, user_table a2 " + 
				"WHERE a1.content_writer_idx=a2.user_idx AND a1.content_board_idx=#{board_info_idx} ORDER BY a1.content_idx DESC")
		List<ContentBean> getContentList(int board_info_idx,RowBounds rowBounds);
		//RowBounds는 객체를 생성할때 두개의 값을 셋팅하는 데 어디서 부터 몇개를 가져오라는 정보를 셋팅한다.
		//SQL 쿼리를 이용해 전체 다 들고온뒤 RowBounds를 활용해 어디서부터 어디까지갯수를 들고오게 한다.
		
		@Select("SELECT a2.user_name content_writer_name, "+
					"date_format(a1.content_date,'%Y-%m-%d') content_date, "+
					"a1.content_subject, a1.content_text, a1.content_file, a1.content_writer_idx "+
					"FROM content_table a1, user_table a2 "+
					"WHERE a1.content_writer_idx=a2.user_idx AND content_idx=#{content_idx}")
				ContentBean getContentInfo(int content_idx);
		
		@Update("update content_table "+
				"set content_subject = #{content_subject}, content_text=#{content_text}, "+
				"content_file=#{content_file} "+
				"where content_idx=#{content_idx}")
		void modifyContentInfo(ContentBean modifyContentBean);
		
		@Delete("delete from content_table where content_idx=#{content_idx}")
		void deleteContentInfo(int content_idx);
		
		@Select("select count(*) from content_table where content_board_idx=#{content_board_idx}")
		int getContentCnt(int content_board_idx);
		
}

