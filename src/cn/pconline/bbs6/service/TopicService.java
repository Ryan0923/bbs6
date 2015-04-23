package cn.pconline.bbs6.service;

import cn.pconline.bbs6.repository.*;
import cn.pconline.bbs6.domain.*;
import cn.pconline.bbs6.util.EnvUtils;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class TopicService extends EnvService {

    @Autowired
    TopicRepository topicRepository;
    @Autowired
    PostRepository postRepository;
	@Autowired
	PostPagerRepository postPagerRepository;
    @Autowired
    ForumService forumService;
    @Autowired
    UserRepository userRepository;

    static enum Operation {
        deny, // 退回
        pass, // 通过
        filter  // 过滤
    }

    public static TopicService instance() {
        return EnvUtils.getEnv().getTopicService();
    }


	/**
	 * 查询主题
	 * @param topicId
	 * @param author
	 * @param forum
	 * @param createAtMin
	 * @param createAtMax
	 * @param statusStr
	 * @param deleted
	 * @param title
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Pager<Topic> filterTopic(long topicId,User author,Forum forum,Date createAtMin,Date createAtMax,
			String statusStr,String deleted,String title,int pageNo,int pageSize){
		return filterTopic(topicId, author, forum, createAtMin, createAtMax,statusStr,deleted,title,-1,-1, pageNo, pageSize);
	}

	public Pager<Topic> filterTopic(long topicId,User author,Forum forum,Date createAtMin,Date createAtMax,
			String statusStr,String deleted,String title,int replyCountMin,int replyCountMax,int pageNo,int pageSize){
		return topicRepository.filterTopic(topicId, author, forum, createAtMin, createAtMax,statusStr,deleted,title,replyCountMin,replyCountMax, pageNo, pageSize);
	}

    /**
     * 根据ID数组获取对象
     * @param topicIds
     * @return
     */
    public List<Topic> getTopics(long[] topicIds) {
        List<Topic> list = new ArrayList<Topic>();
        for (long id : topicIds) {
            list.add(topicRepository.find(id));
        }
        return list;
    }

    /**
     * 根据ID数组获取对象
     * @param postIds
     * @return
     */
    public List<Post> getPosts(long[] postIds) {
        List<Post> list = new ArrayList<Post>();
        for (long id : postIds) {
            list.add(postRepository.find(id));
        }
        return list;
    }


	/**
	 * 从缓存中读取主题的第一页
	 * @param topic
	 * @param pageSize
	 * @return
	 */
	public Pager<Post> getTopicPage1(Topic topic,int pageSize) {
		String key = "PostPager-" + topic.getTopicId();
		Pager<Post> pager = postPagerRepository.get(key);
		if(pager == null) {
			pager = getPosts(topic,1,pageSize);
			postPagerRepository.set(key, pager);
		}
		return pager;
	}

	/**
	 * 清除主题第一页的缓存
	 * @param topic
	 */
	public void removeTopicPage1Cached(Topic topic) {
		String key = "PostPager-" + topic.getTopicId();
		postPagerRepository.delete(key);
	}

    /**
     * 按分页获取一个主题的回帖,按楼层数倒序
     * @param topic
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Pager<Post> getPosts(Topic topic, int pageNo, int pageSize) {
        return postRepository.getPostsOrderByFloor(topic, pageNo, pageSize, false);
    }

    public Pager<Post> getPosts(Topic topic, int pageNo, int pageSize, boolean desc) {
        return postRepository.getPostsOrderByFloor(topic, pageNo, pageSize, desc);
    }

    public Pager<Post> getPosts(Topic topic, User author, int pageNo, int pageSize) {
        return postRepository.getPostsOrderByFloor(topic, author, pageNo, pageSize, false);
    }

    /**
     * 判断某用户是否回复了某主题
     * @param topic
     * @param author
     * @return
     */
    public boolean hasReplyTopic(Topic topic, User author) {
        return postRepository.hasReplyTopic(topic, author);
    }

    public Topic createTopic(final Forum forum, final User author, final String type, final String title,
            final String message) {
        Topic newTopic = (Topic) getTransactionTemplate().execute(new TransactionCallback() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                try {
                    Date now = new Date();
                    Topic topic = new Topic();
                    topic.setAuthorId(author.getUserId());
                    topic.setTitle(title);
                    topic.setForumId(forum.getForumId());
                    topic.setCreateAt(now);
                    topic.setLastPostAt(now);
                    topic.setReplyCount(0);
                    topic.setFloor(1);
                    topic.setType(type);
					topic.setStatus(0);

                    long topicId = topicRepository.createTopic(topic);

                    Post post = new Post();
                    post.setAuthorId(author.getUserId());
                    post.setMessage(message);
                    post.setTopicId(topicId);
                    post.setCreateAt(now);
                    post.setFloor(1);
                    long postId = postRepository.createPost(post);
                    post.setPostId(postId);
                    topic.setPostId(postId);

                    //设置主题的第一篇帖子
                    topicRepository.setPost(topic, post);
                    return topic;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    e.printStackTrace();
                }
                return null;
            }
        });

        if (newTopic != null) {
            //非游客才加分
            if (author.getUserId() > 0) {
                userRepository.addTopicCount(author, 1);
                userRepository.addPostCount(author, 1,new Date());
            }
			forumService.removeForumPage1Cached(forum);
        }
        return newTopic;
    }

    public Topic updateTopic(final Topic topic, final User author, final String type, final String title, final String message) {
        Date now = new Date();
        topic.setTitle(title);
        topic.setType(type);
        topicRepository.updateTopic(topic);

        Post post = topic.getPost();
        post.setLastModifierId(author.getUserId());
        post.setMessage(message);
        post.setUpdateAt(now);
        postRepository.updatePost(post);
        return topic;
    }

    /**
     * 回复主题
     * @param author 发帖人
     * @param topic 被回复主题
     * @param message 回复内容
     * @return 回复生成的post
     */
    public Post replyTopic(final User author, final Topic topic, final String message) {
        Post newPost = (Post) getTransactionTemplate().execute(new TransactionCallback() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                try {
                    Date now = new Date();
                    topic.setLastPosterId(author.getUserId());
                    int floor = topicRepository.replyTopic(topic);

                    Post post = new Post();
                    post.setAuthorId(author.getUserId());
                    post.setMessage(message);
                    post.setTopicId(topic.getTopicId());
                    post.setCreateAt(now);
                    post.setFloor(floor);
                    long postId = postRepository.createPost(post);
                    post.setPostId(postId);

                    return post;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    e.printStackTrace();
                }
                return null;
            }
        });

        Forum forum = topic.getForum();
        if (author.getUserId() > 0) {
            userRepository.addPostCount(author, 1,new Date());
        }
        forumService.updateForumPostInfo(forum, author.getUserId(), 1, 0, newPost.getPostId());
		removeTopicPage1Cached(topic);
		forumService.removeForumPage1Cached(forum);
        return newPost;
    }

    public void updatePost(final User modifier, final Post post, final String message) {
        post.setUpdateAt(new Date());
        post.setLastModifierId(modifier.getUserId());
        post.setMessage(message);
        postRepository.updatePost(post);
    }

}
