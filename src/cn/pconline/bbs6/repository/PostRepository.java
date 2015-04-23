package cn.pconline.bbs6.repository;

import java.util.List;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cn.pconline.bbs6.domain.Post;
import cn.pconline.bbs6.domain.Topic;
import cn.pconline.bbs6.domain.User;
import cn.pconline.bbs6.domain.Pager;
import cn.pconline.bbs6.repository.DecodeUtils.Type;
import cn.pconline.bbs6.util.EnvUtils;

public class PostRepository extends AbstractRepository<Post> {
	static enum Field {
		postId,
		topicId,
		authorId,
		message,
		longMessage,
		createAt,
		floor,
		updateAt,
		ip,
		lastModifierId
	}

	public PostRepository() {
		super(Post.class);
	}

	public static PostRepository instance() {
		return EnvUtils.getEnv().getPostRepository();
	}
	
	@Override
	protected Post decode(String value) {
		PostDecodeHandler handler = new PostDecodeHandler();
		DecodeUtils.decode(value, handler);
		return handler.getPost();
	}

	@Override
	protected String encode(Post post) {
		EncodeBuilder eb = new EncodeBuilder();
		eb.append(Field.postId, post.getPostId());
		eb.append(Field.topicId, post.getTopicId());
		eb.append(Field.authorId, post.getAuthorId());
		eb.append(Field.message, post.getMessage() == null ? "" : post.getMessage());
		eb.append(Field.longMessage, post.isLongMessage() ? 1 : 0);
		eb.append(Field.createAt, post.getCreateAt().getTime());
		eb.append(Field.floor, post.getFloor());
		eb.append(Field.updateAt, post.getUpdateAt() == null ? 0 : post.getUpdateAt().getTime());
		eb.append(Field.ip, post.getIp());
		eb.append(Field.lastModifierId, post.getLastModifierId());
		return eb.toString();
	}

	@Override
	public Post findFromDb(long postId) {
		Post post = simpleJdbcTemplate.queryForObject(
				"select * from bbs6_post where postId = ?",
				postRowMapper, postId);
		if (post != null) {
			if (post.isLongMessage()) {
				post.setMessage(loadPostText(postId, post.getMessage()));
			}
		}
		return post;
	}

	@Override
	protected long getKey(Post post) {
		return post.getPostId();
	}

	/**
	 * 
	 */
	static ParameterizedRowMapper<Post> postRowMapper = new ParameterizedRowMapper<Post> () {
		@Override
		public Post mapRow(ResultSet rs, int index) throws SQLException {
			Post post = new Post();
			post.setPostId(rs.getLong(Field.postId.name()));
			post.setTopicId(		rs.getLong(Field.topicId.name()));
			post.setAuthorId(		rs.getLong(Field.authorId.name()));
			post.setMessage(		rs.getString(Field.message.name()));
			post.setLongMessage(	rs.getInt(Field.longMessage.name()) == 1);
			post.setCreateAt(		rs.getTimestamp(Field.createAt.name()));
			post.setFloor(			rs.getInt(Field.floor.name()));
			post.setUpdateAt(		rs.getTimestamp(Field.updateAt.name()));
			post.setIp(				rs.getString(Field.ip.name()));
			post.setLastModifierId( rs.getLong(Field.lastModifierId.name()));
			return post;
		}
	};
	
	static class PostDecodeHandler implements DecodeUtils.ItemHandler {
		Post post = new Post();
		public Post getPost() { return post; }
		
		@Override
		public void handle(String name, String value, Type type) {
			Field f = Field.valueOf(name);
			switch (f) {
			case postId:			post.setPostId(Long.parseLong(value)); break;
			case topicId:			post.setTopicId(Long.parseLong(value));  break;
			case authorId:			post.setAuthorId(Long.parseLong(value));  break;
			case message:			post.setMessage(value);  break;
			case longMessage:		post.setLongMessage("1".equals(value));  break;
			case createAt:			post.setCreateAt(new java.util.Date(Long.parseLong(value)));  break;
			case floor:				post.setFloor(Integer.parseInt(value));  break;
			case updateAt:			post.setUpdateAt(new java.util.Date(Long.parseLong(value))); break;
			case ip:				post.setIp(value); break;
			case lastModifierId:	post.setLastModifierId(Long.parseLong(value)); break;
			default: break;
			}
		}
		
	}

	public Pager<Post> getPostsOrderByFloor(Topic topic,User author, int pageNo, int pageSize, boolean desc){
		Pager<Post> pager = new Pager<Post>();
		pager.setPageNo(pageNo);
		pager.setPageSize(pageSize);
		String countSql = null;
		String sql = null;
		if(author == null){
			sql = "select postId from bbs6_post where topicId=? order by floor " + (desc ? "desc" : "asc");
			countSql = "select count(*) from bbs6_post where topicId=?";
			pager.setResultList(page(sql, pageNo, pageSize, topic.getTopicId()));
			pager.setTotal(count(countSql, topic.getTopicId()));
		}else{
			sql = "select postId from bbs6_post where topicId=? and authorId=? order by floor asc";
			countSql = "select count(*) from bbs6_post where topicId=? and authorId=?";
			pager.setResultList(page(sql, pageNo, pageSize, topic.getTopicId(),author.getUserId()));
			pager.setTotal(count(countSql, topic.getTopicId(),author.getUserId()));
		}
		return pager;
	}

	public Pager<Post> getPostsOrderByFloor(Topic topic, int pageNo, int pageSize, boolean desc){
		return getPostsOrderByFloor(topic,null,pageNo,pageSize, desc);
	}
	
	/**
	 * 判断某用户是否回复了某主题
	 * @param topic
	 * @param author
	 * @return
	 */
	public boolean hasReplyTopic(Topic topic,User author){
		return count("select count(*) from bbs6_post where topicId=? and authorId=?",topic.getTopicId(),author.getUserId()) > 0;
	}

	public long createPost(final Post post) {
		final long postId = idGenerator.generate("bbs6_post", Field.postId.name());
		String message = post.getMessage();
		int longMessage = 0;
		if (message.length() > 125) {
			longMessage = 1;
			message = post.getMessage().substring(0, 125);
		}
		simpleJdbcTemplate.update("insert into bbs6_post " +
				" (postId,topicId,authorId,message,longmessage,createAt,updateAt,floor,version,lastModifierId)" +
				" values (?,?,?,?,?,?,?,?,?,?)",
				postId,
				post.getTopicId(),
				post.getAuthorId(),
				message,
				longMessage,
				post.getCreateAt(),
				post.getCreateAt(),
				post.getFloor(),
				1,
				post.getLastModifierId());

		if (longMessage == 1) {
			createPostText(postId, post.getMessage(), 1);
		}

		return postId;
	}
	
	public void deletePost(Post post,String reason){
		removeFromCache(post);
		simpleJdbcTemplate.update("delete from bbs6_post where postId = ?", post.getPostId());
		deletePostText(post.getPostId());
	}


	public void deleteForTopic(Topic topic,String reason){
		List<Post> postList = list("select postId from bbs6_post where topicId = ?" , topic.getTopicId());
		for(Post post:postList){
			deletePost(post,reason);
		}
	}
	
	/**
	 * 更新post时要重新审核(状态改为未审核,审核人id为0,审核时间为null)
	 * @param post
	 */
	public void updatePost(final Post post){
		String message = post.getMessage();
		int longMessage = 0;
		boolean oldLongMessage = post.isLongMessage();
		if (message.length() > 125) {
			longMessage = 1;
			message = post.getMessage().substring(0, 125);
		}
		simpleJdbcTemplate.update("update bbs6_post set message = ?, longMessage = ?, updateAt = ?, lastModifierId = ? where postId = ?",
				message,
				longMessage,
				new Timestamp(post.getUpdateAt().getTime()),
				post.getLastModifierId(),
				post.getPostId());
		if (longMessage == 1) {
			updatePostText(post.getPostId(), post.getMessage(), 1);
		}
		if (oldLongMessage && longMessage == 0) {
			deletePostText(post.getPostId());
		}

		removeFromCache(post);
	}

	private String loadPostText(long postId, String messagePart) {
		final StringBuilder sb = new StringBuilder(messagePart);
		simpleJdbcTemplate.getJdbcOperations().query("select val from bbs6_post_text where postId = ? order by seq", 
				new Long[]{postId},
				new ResultSetExtractor() {
					public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
						while (rs.next()) {
							sb.append(rs.getString("val"));
						}
						return null;
					}
				});
		return sb.toString();
	}

	private void updatePostText(long postId, String message, long logId) {
		deletePostText(postId);
		createPostText(postId, message, logId);
	}

	private void deletePostText(long postId) {
		simpleJdbcTemplate.update("delete bbs6_post_text where postId = ?", postId);
	}

	private void createPostText(long postId, String message, long logId) {
		if (message.length() <= 125) return;
		for (int i = 125, c = message.length(), n = 1; i < c; i += 125, n ++) {
			int e =Math.min(c, i + 125);
			simpleJdbcTemplate.update(
					"insert into bbs6_post_text(postId, seq, val, logId) values (?,?,?,?)", 
					postId, n, message.substring(i, e), logId);
		}
	}

}
