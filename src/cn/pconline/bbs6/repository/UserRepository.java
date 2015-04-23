package cn.pconline.bbs6.repository;

import cn.pconline.bbs6.domain.Pager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cn.pconline.bbs6.domain.User;
import cn.pconline.bbs6.repository.DecodeUtils.Type;
import cn.pconline.bbs6.util.EnvUtils;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

public class UserRepository extends AbstractRepository<User> {
	
	static enum Field {
		userId, 
		name,
		nickname,
		postCount,
		topicCount,
		createAt,
		lastPostAt
	}
	
	public static UserRepository instance() {
		return EnvUtils.getEnv().getUserRepository();
	}
	
	public UserRepository() {
		super(User.class);
	}

	@Override
	protected boolean enableFirstLevelCache() {
		return true;
	}

	public void updateUser(User user){
	    simpleJdbcTemplate.update("update bbs6_user set nickname=?, viewSign = ?, hintMessage = ?, propNum = ?, propStr = ? where userId=?",
		    user.getNickname(),
		    user.getUserId());
	    removeFromCache(user);
	}

	public void addPostCount(User user,int count){
		addPostCount(user,count,null);
	}

	public void addPostCount(User user,int count,Date updateAt){
		if(updateAt == null){
			simpleJdbcTemplate.update("update bbs6_user set postCount = postCount + ? where userId = ?",count, user.getUserId());
		}else{
			simpleJdbcTemplate.update("update bbs6_user set postCount = postCount + ?,lastPostAt = ? where userId = ?",count,updateAt, user.getUserId());
		}
	    removeFromCache(user);
	}
	
	public void addTopicCount(User user,int count){
		simpleJdbcTemplate.update("update bbs6_user set topicCount = topicCount + ? where userId = ?", count,user.getUserId());
	    removeFromCache(user);
	}

	@Override
	protected long getKey(User user) {
		return user.getUserId();
	}
	
	@Override
	public User findFromDb(long userId) {
		return EnvUtils.getEnv().getSimpleJdbcTemplate().queryForObject(
				"select * from bbs6_user where userId = ?",
				userRowMapper, userId);
	}
	
	@Override
	protected User decode(String value) { 
		UserDecodeHandler handler = new UserDecodeHandler();
		DecodeUtils.decode(value, handler);
		return handler.getUser();
	}

	@Override
	protected String encode(User user) {
		EncodeBuilder eb = new EncodeBuilder();
		eb.append(Field.userId, user.getUserId());
		eb.append(Field.postCount, user.getPostCount());
		eb.append(Field.topicCount, user.getTopicCount());
		eb.append(Field.name, user.getName());
		eb.append(Field.nickname, user.getNickname());
		eb.append(Field.createAt, user.getCreateAt().getTime());
		if(user.getLastPostAt() != null){
			eb.append(Field.lastPostAt, user.getLastPostAt().getTime());
		}

		return eb.toString();
	}
	
	static ParameterizedRowMapper<User> userRowMapper = new ParameterizedRowMapper<User> () {
		@Override
		public User mapRow(ResultSet rs, int index) throws SQLException {
			User user = new User();
			user.setUserId(		rs.getLong(Field.userId.name()));
			user.setName(		rs.getString(Field.name.name()));
			user.setNickname(	rs.getString(Field.nickname.name()));
			user.setPostCount(		rs.getInt(Field.postCount.name()));
			user.setTopicCount(		rs.getInt(Field.topicCount.name()));
			user.setCreateAt(	rs.getTimestamp(Field.createAt.name()));
			user.setLastPostAt(	rs.getTimestamp(Field.lastPostAt.name()));
			return user;
		}
	};

	static class UserDecodeHandler implements DecodeUtils.ItemHandler {
		User user = new User();
		public User getUser() { return user; }
		
		@Override
		public void handle(String name, String value, Type type) {
			Field f = Field.valueOf(name);
			switch (f) {
				case userId:	user.setUserId(Long.parseLong(value)); break;
				case name:		user.setName(value); break;
				case nickname:	user.setNickname(value); break;
				case postCount:	user.setPostCount(Integer.parseInt(value)); break;
				case topicCount:user.setTopicCount(Integer.parseInt(value)); break;
				case createAt:	user.setCreateAt(new Date(Long.parseLong(value))); break;
				case lastPostAt: user.setLastPostAt(StringUtils.isEmpty(value) ? null : new Date(Long.parseLong(value)));break;
			default: break;
			}
		}
	}

	public User findByName(String name) {
		return simpleJdbcTemplate.queryForObject("select * from bbs6_user where name = ?", userRowMapper, name);
	}
	
	public void createUser(User user) {
		
	}

	public Pager<User> pageAllUser(int pageNo, int pageSize) {
		Pager<User> pager = new Pager<User> ();
		pager.setPageNo(pageNo);
		pager.setPageSize(pageSize);
		int total = simpleJdbcTemplate.queryForInt("select count(userId) from bbs6_user");
		pager.setTotal(total);

		pager.setResultList(page("select * from bbs6_user order by userId", pageNo, pageSize));

		return pager;
	}

}
