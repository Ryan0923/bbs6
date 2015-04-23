package cn.pconline.bbs6.service;

import cn.pconline.bbs6.domain.Forum;
import cn.pconline.bbs6.domain.Topic;
import cn.pconline.bbs6.domain.Pager;
import cn.pconline.bbs6.repository.ForumRepository;
import cn.pconline.bbs6.repository.TopicRepository;
import cn.pconline.bbs6.repository.PostRepository;
import cn.pconline.bbs6.repository.TopicPagerRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ForumService extends EnvService {
	public final static String TOPIC_FILTER_TYPE = "type";
	public final static String TOPIC_FILTER_VOTING = "voting";
	public final static String TOPIC_FILTER_REWARD = "reward";
	public final static String TOPIC_FILTER_PICK = "pick";
	public final static String TOPIC_FILTER_IMAGE = "image";
	public final static String TOPIC_FILTER_TIME = "time";
	public final static String TOPIC_ORDERBY_REPLY_TIME = "replyat";
	public final static String TOPIC_ORDERBY_POST_TIME = "postat";

    @Autowired
    ForumRepository forumRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    TopicPagerRepository topicPagerRepository;
    @Autowired
    PostRepository postRepository;
	@Autowired
	UserService userServcie;

	public Pager<Forum> pageAllForums(int pageNo, int pageSize) {
		return forumRepository.pageAllForum(pageNo, pageSize);
	}

	public Pager<Forum> pageChildForums(int pageNo, int pageSize, long forumId) {
		return forumRepository.pageChildForums(pageNo, pageSize, forumId);
	}

	public List<Forum> listChildForums(long forumId){
		return forumRepository.listChildForums(forumId);
	}

	/**
	 * 从缓存读取版块第一页的主题列表
	 * @param forum
	 * @param pageSize
	 * @return
	 */
	public Pager<Topic> getForumPage1(Forum forum, int pageSize) {
		String key = "TopicPager-" + forum.getForumId();
		Pager<Topic> pager = topicPagerRepository.get(key);
		if (pager == null) {
			pager = listTopics(forum,null,null,null,1,pageSize);
			topicPagerRepository.set(key, pager);
		}
		return pager;
	}

	/**
	 * 清除版块第一页的主题列表缓存
	 * @param forum
	 */
	public void removeForumPage1Cached(Forum forum) {
		String key = "TopicPager-" + forum.getForumId();
		topicPagerRepository.delete(key);
	}

    /**
     * 显示版块的主题
     * @param forum 版块对象
     * @param filter 过滤的内容，可使用Forum.TOPIC_FILTER_***
     * @param filterValue 过滤值
     * @param orderBy 排序方式，可使用Forum.TOPIC_ORDERBY_****,默认可由系统配置，如果都没有则用Forum.TOPIC_ORDERBY_REPLY_TIME
     * @param pageNo 页面序号
     * @param pageSize 页面显示主题数
     * @return Pager<Topic>
     */
    public Pager<Topic> listTopics(Forum forum, String filter, String filterValue, String orderBy, int pageNo, int pageSize) {
        Pager<Topic> pager = new Pager<Topic>();
        pager.setPageNo(pageNo);
        pager.setPageSize(pageSize);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("forumId", forum.getForumId());
        String sql = "select topicId from bbs6_topic where forumId = :forumId";
        String countSql = "select count(topicId) from bbs6_topic where forumId = :forumId";
        Date filterTime = null;
        if (TOPIC_FILTER_REWARD.equals(filter)) {
            sql += " and rewardAmount > 0";
            countSql += " and rewardAmount > 0";
        } else if (TOPIC_FILTER_PICK.equals(filter)) {
            sql += " and pick > 0";
            countSql += " and pick > 0";
            if (!StringUtils.isEmpty(filterValue)) {
                int typeId = 0;
                try {
                    typeId = Integer.parseInt(filterValue);
                } catch (Exception e) {
                }
                sql += " and pickTypeId = :pickTypeId";
                countSql += " and pickTypeId = :pickTypeId";
                params.put("pickTypeId", typeId);
            }
        } else if (TOPIC_FILTER_TYPE.equals(filter)) {
            sql += " and type = :type";
            countSql += " and type = :type";
            params.put("type", filterValue);
        } else if (TOPIC_FILTER_VOTING.equals(filter)) {
            sql += " and voting = 1";
            countSql += " and voting = 1";
        } else if (TOPIC_FILTER_IMAGE.equals(filter)) {
            sql += " and containImage = 1";
            countSql += " and containImage  = 1";
        } else if (TOPIC_FILTER_TIME.equals(filter)) {
            long second = 0l;
            try {
                second = Long.parseLong(filterValue);
            } catch (Exception e) {
            }
            if (second > 0) {
                filterTime = new Date(System.currentTimeMillis() - second * 1000);
            }
        }

		String orderbySeq = orderbySeq = "desc";
		//获取板块配置中的默认主题排序字段,如果为空则用lastPostAt
		String orderbyField = orderbyField = "lastPostAt";
        if (TOPIC_ORDERBY_POST_TIME.equals(orderBy)) {
            if (filterTime != null) {
                sql += " and createAt > :createAt";
                countSql += " and createAt > :createAt";
                params.put("createAt", filterTime);
            }
            sql += " order by createAt " + orderbySeq;
        } else if (TOPIC_ORDERBY_REPLY_TIME.equals(orderBy)) {
            if (filterTime != null) {
                sql += " and lastPostAt > :lastPostAt";
                countSql += " and lastPostAt > :lastPostAt";
                params.put("lastPostAt", filterTime);
            }
            sql += " order by lastPostAt " + orderbySeq;
        } else {
            if (filterTime != null) {
                sql += " and lastPostAt > :lastPostAt";
                countSql += " and lastPostAt > :lastPostAt";
                params.put("lastPostAt", filterTime);
            }
            sql += " order by "+ orderbyField +" " + orderbySeq;
        }
        pager.setResultList(topicRepository.page(sql, pageNo, pageSize, params));
        pager.setTotal(topicRepository.count(countSql, params));
        return pager;
    }

	void updateForumPostInfo(Forum forum, long userId, int i, int i0, long postId) {
		//throw new UnsupportedOperationException("Not yet implemented");
	}

}
