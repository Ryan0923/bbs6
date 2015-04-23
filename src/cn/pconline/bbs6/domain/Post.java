package cn.pconline.bbs6.domain;

import java.util.Date;

import cn.pconline.bbs6.repository.PostRepository;
import org.springframework.dao.EmptyResultDataAccessException;

public class Post {
	long postId;
	long topicId;
	long authorId;
	Date createAt;
	String message;
	boolean longMessage;
	int floor;
	Date updateAt;
	long lastModifierId;
	String ip;
	
	transient Topic topic;
	transient Forum forum;
	transient User author;
	transient User lastModifier;
	
	public static Post find(long postId){
		return PostRepository.instance().find(postId);
	}
	
	public long getPostId() { return postId; }
	public void setPostId(long postId) { this.postId = postId; }
	public long getTopicId() { return topicId; }
	public void setTopicId(long topicId) { this.topicId = topicId; }
	public Date getCreateAt() { return createAt; }
	public void setCreateAt(Date createAt) { this.createAt = createAt; }
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	public boolean isLongMessage() { return longMessage; } 
	public void setLongMessage(boolean longMessage) { this.longMessage = longMessage; }
	public int getFloor() { return floor; }
	public void setFloor(int floor) { this.floor = floor; }
	public Date getUpdateAt() { return updateAt; }
	public void setUpdateAt(Date updateAt) { this.updateAt = updateAt; }
	public long getLastModifierId() { return lastModifierId; }
	public void setLastModifierId(long lastModifierId) { this.lastModifierId = lastModifierId; }
	public long getAuthorId() {return authorId; }
	public void setAuthorId(long authorId) { this.authorId = authorId; }
	public String getIp() { return ip;}
	public void setIp(String ip){ this.ip = ip;}

	public void setLastModifier(User lastModifier) { this.lastModifier = lastModifier; }
	public User getLastModifier(){
		if(lastModifier == null && lastModifierId > 0) {
			try {
				lastModifier = User.find(lastModifierId);
			} catch(EmptyResultDataAccessException e) {
				throw new DataIntegrityException("LastModifier(User) not found, userId:" + lastModifierId + " in post:" + postId, e);
			}
		}
		return lastModifier;
	}
	
	public void setAuthor(User author) { this.author = author; }
	public User getAuthor() {
		if (author == null && authorId > 0) {
			try {
				author = User.find(authorId);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("Author(User) not found, userId:" + authorId + " in post:" + postId, e);
			}
		} else if(authorId == 0) {
			author = User.ANONYMOUS;
		}
		return author; 
	}

	public Topic getTopic() {
		if (topic == null) {
			try {
				topic = Topic.find(topicId);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("Topic not found, topicId:" + topicId + " in post:" + postId, e);
			}
		}
		return topic;
	}

	public Forum getForum() {
		if (forum == null) {
			forum = getTopic().getForum();
		}
		return forum;
	}
	
}
