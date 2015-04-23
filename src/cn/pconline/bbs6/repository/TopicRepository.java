package cn.pconline.bbs6.repository;

import cn.pconline.bbs6.domain.Forum;
import cn.pconline.bbs6.domain.Pager;
import cn.pconline.bbs6.domain.Post;
import cn.pconline.bbs6.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cn.pconline.bbs6.domain.Topic;
import cn.pconline.bbs6.repository.DecodeUtils.Type;
import cn.pconline.bbs6.util.Env;
import cn.pconline.bbs6.util.EnvUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicRepository extends AbstractRepository<Topic> {

	public Pager<Topic> filterTopic(long topicId, User author, Forum forum, Date createAtMin, Date createAtMax, String statusStr, String deleted, String title, int replyCountMin, int replyCountMax, int pageNo, int pageSize) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	static enum Field {
		topicId,
		title,
		forumId,
		postId,
		replyCount,
		authorId,
		createAt,
		lastPosterId,
		lastPostAt,
		floor,
		type,
		status
	}
	
	public TopicRepository() {
		super(Topic.class);
	}

	public static TopicRepository instance() {
		return EnvUtils.getEnv().getTopicRepository();
	}
	
	@Override
	protected String encode(Topic topic) {
		EncodeBuilder eb = new EncodeBuilder();
		eb.append(Field.topicId, topic.getTopicId());
		eb.append(Field.title, topic.getTitle());
		eb.append(Field.forumId, topic.getForumId());
		eb.append(Field.postId, topic.getPostId());
		eb.append(Field.replyCount, topic.getReplyCount());
		eb.append(Field.authorId, topic.getAuthorId());
		eb.append(Field.createAt, topic.getCreateAt().getTime());
		eb.append(Field.lastPosterId, topic.getLastPosterId());
		eb.append(Field.floor, topic.getFloor());
		eb.append(Field.type, topic.getType());
		if (topic.getLastPostAt() != null)
			eb.append(Field.lastPostAt, topic.getLastPostAt().getTime());
		eb.append(Field.status, topic.getStatus());

		return eb.toString();
	}
	
	@Override
	protected Topic decode(String value) {
		TopicDecodeHandler handler = new TopicDecodeHandler();
		DecodeUtils.decode(value, handler);
		return handler.getTopic();
	}

	@Override
	protected long getKey(Topic obj) {
		return obj.getTopicId();
	}

	@Override
	public Topic findFromDb(long topicId) {
		return simpleJdbcTemplate.queryForObject(
				"select * from bbs6_topic where topicId = ?",
				topicRowMapper, topicId);
	}

	static ParameterizedRowMapper<Topic> topicRowMapper = new ParameterizedRowMapper<Topic> () {
		@Override
		public Topic mapRow(ResultSet rs, int index) throws SQLException {
			Topic topic = new Topic();
			topic.setTopicId(		rs.getLong(Field.topicId.name()));
			topic.setTitle(			rs.getString(Field.title.name()));
			topic.setForumId(		rs.getLong(Field.forumId.name()));
			topic.setPostId(		rs.getLong(Field.postId.name()));
			topic.setReplyCount(	rs.getInt(Field.replyCount.name()));
			topic.setCreateAt(		rs.getTimestamp(Field.createAt.name()));
			topic.setAuthorId(		rs.getLong(Field.authorId.name()));
			topic.setLastPosterId(	rs.getLong(Field.lastPosterId.name()));
			topic.setLastPostAt(	rs.getTimestamp(Field.lastPostAt.name()));
			topic.setFloor( 		rs.getInt(Field.floor.name()));
			topic.setType( 			rs.getString(Field.type.name()));
			topic.setStatus(        rs.getInt(Field.status.name()));

			return topic;
		}
	};
	
	static class TopicDecodeHandler implements DecodeUtils.ItemHandler {
		Topic topic = new Topic();
		public Topic getTopic() { return topic; }
		
		@Override
		public void handle(String name, String value, Type type) {
			Field f = Field.valueOf(name);
			switch (f) {
			case topicId:		topic.setTopicId(Long.parseLong(value)); break;
			case title:			topic.setTitle(value); break;
			case forumId:		topic.setForumId(Long.parseLong(value)); break;
			case postId:		topic.setPostId(Long.parseLong(value)); break;
			case replyCount:	topic.setReplyCount(Integer.parseInt(value)); break;
			case authorId:		topic.setAuthorId(Long.parseLong(value)); break;
			case createAt:		topic.setCreateAt(new java.util.Date(Long.parseLong(value))); break;
			case lastPosterId:	topic.setLastPosterId(Long.parseLong(value)); break;
			case lastPostAt:	topic.setLastPostAt(new java.util.Date(Long.parseLong(value))); break;
			case floor:			topic.setFloor(Integer.parseInt(value)); break;
			case type:			topic.setType(value); break;
			case status:		topic.setStatus(Integer.valueOf(value)); break;
			default: break;
			}
		}
	}

	public long createTopic(Topic topic) {
		long topicId = idGenerator.generate("bbs6_topic", Field.topicId.name());
		topic.setTopicId(topicId);
		simpleJdbcTemplate.update("insert into bbs6_topic (" +
				"topicId,status,title,forumId,postId,replyCount,authorId,createAt,lastPosterId,lastPostAt,floor,type,version)" +
				" values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
				topicId,
				topic.getStatus(),
				topic.getTitle(),
				topic.getForumId(),
				topic.getPostId(),
				topic.getReplyCount(),
				topic.getAuthorId(),
				topic.getCreateAt(),
				topic.getLastPosterId(),
				topic.getLastPostAt(),
				topic.getFloor(),
				topic.getType(),
				1);
		return topicId;
	}
	
	public void setPost(Topic topic,Post post){
		simpleJdbcTemplate.update("update bbs6_topic set postId=? where topicId=?", post.getPostId(),topic.getTopicId());
	}

	public void deleteTopic(Topic topic,String reason){
		simpleJdbcTemplate.update("delete from bbs6_topic where topicId = ?", topic.getTopicId());
		removeFromCache(topic);
	}

	public void updateTopic(Topic topic) {
		simpleJdbcTemplate.update("update bbs6_topic set title=?, forumId=?,containImage=?, hasHiddenContent=?, type=?,tags=?,version=version+1,updateAt=SYSDATE,logId=?, status = ? where topicId=?",
				topic.getTitle(),
				topic.getForumId(),
				topic.getType(),
				topic.getStatus(),
				topic.getTopicId());
		removeFromCache(topic);
	}

	public int replyTopic(Topic topic) {
		simpleJdbcTemplate.update("update bbs6_topic set replyCount=replyCount + 1,  lastPosterId=?, lastPostAt=?, floor=floor + 1 where topicId=?",
				topic.getLastPosterId(),
				topic.getLastPostAt(),
				topic.getTopicId());
		removeFromCache(topic);
		return simpleJdbcTemplate.queryForInt("select floor from bbs6_topic where topicId = ?", topic.getTopicId());
	}

	public void preloadRelativeObject(List<Topic> topicList) {
		Set<Long> idSet = new HashSet<Long>(topicList.size());

		List<Long> counterIdList = new ArrayList<Long>();
		for (Topic topic : topicList) {
			idSet.add(topic.getAuthorId());
			idSet.add(topic.getLastPosterId());
			counterIdList.add(topic.getTopicId());
		}

		Env env = EnvUtils.getEnv();

		idSet.remove(0L);
		List<Long> idList = new ArrayList<Long>(idSet.size());
		for (Long uid : idSet) {
			idList.add(uid);
		}
		env.getUserRepository().preload(idList);

	}

}
