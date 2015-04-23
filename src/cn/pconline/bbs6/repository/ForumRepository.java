package cn.pconline.bbs6.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cn.pconline.bbs6.domain.Forum;
import cn.pconline.bbs6.domain.Pager;
import cn.pconline.bbs6.repository.DecodeUtils.Type;
import cn.pconline.bbs6.util.EnvUtils;
import java.util.List;

public class ForumRepository extends AbstractRepository<Forum> {
	
	static enum Field {
		forumId,
		parentId,
		name,
		seq,
		topicCount,
		postCount,
		todayPostCount,
		hasChildren,
		createAt,
		lastPostById,
		lastPostId,
		lastPostAt
	}
	
	public static ForumRepository instance() {
		return EnvUtils.getEnv().getForumRepository();
	}

	public ForumRepository() {
		super(Forum.class);
	}

	@Override
	protected boolean enableFirstLevelCache() {
		return true;
	}

	@Override
	protected long getKey(Forum forum) {
		return forum.getForumId();
	}
	
	@Override
	public Forum findFromDb(long forumId) {
		Forum forum = simpleJdbcTemplate.queryForObject(
				"select * from bbs6_forum where forumId = ?",
				forumRowMapper, forumId);

		return forum;
	}

	@Override
	protected Forum decode(String value) {
		ForumDecodeHandler handler = new ForumDecodeHandler();
		DecodeUtils.decode(value, handler);
		return handler.getForum();
	}

	
	@Override
	protected String encode(Forum forum) {
		EncodeBuilder eb = new EncodeBuilder();
		eb.append(Field.forumId, forum.getForumId());
		eb.append(Field.parentId, forum.getParentId());
		eb.append(Field.name, forum.getName());
		eb.append(Field.seq, forum.getSeq());
		eb.append(Field.topicCount,forum.getTopicCount());
		eb.append(Field.postCount,forum.getPostCount());
		eb.append(Field.todayPostCount,forum.getTodayPostCount());
		eb.append(Field.hasChildren, forum.getHasChildren() ? 1 : 0);
		eb.append(Field.createAt,forum.getCreateAt().getTime());
		eb.append(Field.lastPostById, forum.getLastPostById());
		eb.append(Field.lastPostId, forum.getLastPostId());
		if (forum.getLastPostAt() != null)
			eb.append(Field.lastPostAt, forum.getLastPostAt().getTime());
		return eb.toString();
	}
	
	static ParameterizedRowMapper<Forum> forumRowMapper = new ParameterizedRowMapper<Forum> () {
		@Override
		public Forum mapRow(ResultSet rs, int index) throws SQLException {
			Forum forum = new Forum();
			forum.setForumId(rs.getLong(Field.forumId.name()));
			forum.setParentId(rs.getLong(Field.parentId.name()));
			forum.setName(rs.getString(Field.name.name()));
			forum.setSeq(rs.getInt(Field.seq.name()));
			forum.setTopicCount(rs.getInt(Field.topicCount.name()));
			forum.setPostCount(rs.getInt(Field.postCount.name()));
			forum.setTodayPostCount(rs.getInt(Field.todayPostCount.name()));
			forum.setHasChildren(rs.getBoolean(Field.hasChildren.name()));
			forum.setCreateAt(rs.getTimestamp(Field.createAt.name()));
			forum.setLastPostById(rs.getLong(Field.lastPostById.name()));
			forum.setLastPostId(rs.getLong(Field.lastPostId.name()));
			forum.setLastPostAt(rs.getTimestamp(Field.lastPostAt.name()));

			return forum;
		}
	};
	
	static class ForumDecodeHandler implements DecodeUtils.ItemHandler {
		Forum forum = new Forum();
		public Forum getForum() {return forum; }
		
		@Override
		public void handle(String name, String value, Type type) {
			Field f = Field.valueOf(name);
			switch (f) {
				case forumId:		forum.setForumId(Long.parseLong(value));	break;
				case parentId:		forum.setParentId(Long.parseLong(value));	break;
				case name:			forum.setName(value); break;
				case seq:			forum.setSeq(Integer.parseInt(value)); break;
				case topicCount:	forum.setTopicCount(Integer.parseInt(value)); break;
				case postCount:		forum.setPostCount(Integer.parseInt(value)); break;
				case todayPostCount:forum.setTodayPostCount(Integer.parseInt(value)); break;
				case hasChildren:	forum.setHasChildren(Integer.parseInt(value) == 1); break;
				case createAt:		forum.setCreateAt(new java.util.Date(Long.parseLong(value))); break;
				case lastPostById:  forum.setLastPostById(Long.parseLong(value)); break;
				case lastPostAt:	forum.setLastPostAt(new java.util.Date(Long.parseLong(value))); break;
				case lastPostId:	forum.setLastPostId(Long.parseLong(value)); break;
				default: break;
			}
		}
	}
	
	public Pager<Forum> pageAllForum(int pageNo, int pageSize) {
		Pager<Forum> pager = new Pager<Forum> ();
		pager.setPageNo(pageNo);
		pager.setPageSize(pageSize);
		int total = simpleJdbcTemplate.queryForInt("select count(forumId) from bbs6_forum");
		pager.setTotal(total);
		
		pager.setResultList(page("select * from bbs6_forum order by forumId", pageNo, pageSize));
		
		return pager;
	}
	
	public Pager<Forum> pageChildForums(int pageNo, int pageSize, long forumId){
		Pager<Forum> pager = new Pager<Forum>();
		pager.setPageNo(pageNo);
		pager.setPageSize(pageSize);
		pager.setResultList(page("select * from bbs6_forum where parentId=? order by seq, createAt",pageNo,pageSize,forumId));
		pager.setTotal(count("select count(*) from bbs6_forum where parentId=?",forumId));
		return pager;
	}

	public List<Forum> listChildForums(long forumId){
		return list("select * from bbs6_forum where parentId=? order by seq, createAt", forumId);
	}


}
