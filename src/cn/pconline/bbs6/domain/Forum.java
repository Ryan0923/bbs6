package cn.pconline.bbs6.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pconline.bbs6.repository.ForumRepository;
import org.springframework.dao.EmptyResultDataAccessException;

public class Forum {
	long forumId;
	long parentId;
	int seq;
	String name;
	Date createAt;
	int topicCount;
	int postCount;
	int todayPostCount;
	long lastPostById;
	long lastPostId;
	Date lastPostAt;

	boolean hasChildren;

	transient Forum parent;
    transient User lastPoster;
	transient Post lastPost;
	
	public static Forum find(long forumId) {
        return ForumRepository.instance().find(forumId);
	}
	
	public static Forum getRootForum() {
		return find(1);
	}

	public long getForumId() { return forumId; }
	public void setForumId(long forumId) { this.forumId = forumId; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getSeq() { return seq; }
	public void setSeq(int seq) { this.seq = seq; }
	public Date getCreateAt() { return createAt; }
	public void setCreateAt(Date createAt) { this.createAt = createAt; }
	public int getPostCount() { return postCount; }
	public void setPostCount(int postCount) { this.postCount = postCount; } 
	public int getTopicCount() { return topicCount; } 
	public void setTopicCount(int topicCount) { this.topicCount = topicCount; }
	public long getParentId() {	return parentId; }
	public void setParentId(long parentId) {	
		if (parentId == forumId) {
			throw new DataIntegrityException("Parent is self, forumId:" + forumId);
		}
		this.parentId = parentId;
	}
	public boolean getHasChildren() {return hasChildren; }
	public void setHasChildren(boolean hasChildren) { this.hasChildren = hasChildren; }

	public Date getLastPostAt() { return lastPostAt; }
	public void setLastPostAt(Date lastPostAt) { this.lastPostAt = lastPostAt; }
	public long getLastPostById() { return lastPostById; }
	public void setLastPostById(long lastPostById) { this.lastPostById = lastPostById; }
	public int getTodayPostCount() { return todayPostCount; }
	public void setTodayPostCount(int todayPostCount) { this.todayPostCount = todayPostCount; }
	public long getLastPostId() { return lastPostId; }
	public void setLastPostId(long lastPostId) { this.lastPostId = lastPostId; }

	public Forum getParent() {
		if (parentId > 0 && parent == null) {
			if (parentId == forumId) {
				throw new DataIntegrityException("Parent is self, forumId:" + forumId);
			}
			try {
				parent = find(parentId);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("Parent(Forum) not found, forumId:" + parentId + " in forum:" + forumId, e);
			}
		}
		return parent;
	}
	
	public List<Forum> getRoute(){
		List<Forum> list = new ArrayList<Forum>();
		for (Forum tmp = this; tmp != null; tmp = tmp.getParent()) {
			// 避免死循环
			for (int i = 0, c = list.size(); i < c; ++i) {
				if (list.get(i).getForumId() == tmp.getForumId()) {
					throw new DataIntegrityException("Forum route dead loop, forumId:" + forumId);
				}
			}
			list.add(0, tmp);
		}
		return list;
	}

	public List<Forum> getChildForums(){
		return ForumRepository.instance().listChildForums(forumId);
	}

    public User getLastPoster() {
		if (lastPoster == null && lastPostById > 0) {
			try {
				lastPoster = User.find(lastPostById);
			} catch (EmptyResultDataAccessException e) {
				throw new DataIntegrityException("LastPoster(User) not found, userId:" + lastPostById + " in topic:" + forumId, e);
			}
		}
		return lastPoster;
	}

	public Post getLastPost(){
		if(lastPost == null && lastPostId > 0){
			try {
				lastPost = Post.find(lastPostId);
			} catch (EmptyResultDataAccessException ex) {
				// 有可能被删除了，直接忽略。
			}
		}
		return lastPost;
	}

}
