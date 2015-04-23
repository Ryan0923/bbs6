
package cn.pconline.bbs6.domain;
import java.util.Date;

import cn.pconline.bbs6.repository.TopicRepository;
import cn.pconline.bbs6.repository.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;

public class Topic {
	long topicId;
	long forumId;
	long postId;
	String title;
	long authorId;
	Date createAt;
	long lastPosterId;
	Date lastPostAt;
	/** 回复数 */
	int replyCount;
	int floor;
	String type;
	int version;
	int status; 

	transient User author;
	transient User lastPoster;
	transient Post post;
	transient Forum forum;

	public static Topic find(long topicId) {
		return TopicRepository.instance().find(topicId);
	}
	
	public long getTopicId() { return topicId; }
	public void setTopicId(long topicId) { this.topicId = topicId; }
	public long getForumId() { return forumId; }
	public void setForumId(long forumId) { this.forumId = forumId; }
	public long getPostId() { return postId; }
	public void setPostId(long postId) { this.postId = postId; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public Date getCreateAt() { return createAt; }
	public void setCreateAt(Date createAt) { this.createAt = createAt; }
	public Date getLastPostAt() { return lastPostAt; }
	public void setLastPostAt(Date lastPostAt) { this.lastPostAt = lastPostAt; }
	public int getReplyCount() { return replyCount; }
	public void setReplyCount(int replyCount) { this.replyCount = replyCount; }
	public long getAuthorId() { return authorId; }
	public void setAuthorId(long authorId) { this.authorId = authorId; }
	public long getLastPosterId() { return lastPosterId; }
	public void setLastPosterId(long lastPosterId) { this.lastPosterId = lastPosterId; }
	public int getFloor() { return floor; }
	public void setFloor(int floor) { this.floor = floor; }
	public void setAuthor(User author) { this.author = author; }
	public void setLastPoster(User lastPoster) { this.lastPoster = lastPoster; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
	public int getVersion() { return version; }
	public void setVersion(int version) { this.version = version; }
	public int getStatus() { return status; }
	public void setStatus(int status) { this.status = status; }

	public String getStatusDesc() { return status == 0 ? "未审" : "已审"; }

	public User getAuthor() {
		if (author == null && authorId > 0) {
			try {
				author = UserRepository.instance().find(authorId);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("Author(User) not found, userId:" + authorId + " in topic:" + topicId, e);
			}
		} else if (authorId == 0){
			author = User.ANONYMOUS;
		}
		return author;
	}
	public User getLastPoster() {
		if (lastPoster == null && lastPosterId > 0) {
			try {
				lastPoster = UserRepository.instance().find(lastPosterId);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("LastPoster(User) not found, userId:" + lastPosterId + " in topic:" + topicId, e);
			}
		}
		return lastPoster;
	}
	
	public Forum getForum(){
		if(forum == null){
			try {
				forum = Forum.find(forumId);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("Forum not found, forumId:" + forumId + " in topic:" + topicId, e);
			}
		}
		return forum;
	}

	public Post getPost(){
		if(post == null){
			try{
				post = Post.find(postId);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("Post not found, postId:" + postId + " in topic:" + topicId, e);
			}
		}
		return post;
	}

	/** 判断是否为新主题 */
	public boolean isNew(){
		long hours = 4;
		if(createAt.getTime() > System.currentTimeMillis() - hours*3600000){
			return true;
		}
		return false;
	}

	public boolean isHot(){
		return replyCount > 30;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (topicId ^ (topicId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Topic other = (Topic) obj;
		if (topicId != other.topicId)
			return false;
		return true;
	}

}
