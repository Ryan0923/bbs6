package cn.pconline.bbs6.repository;

import cn.pconline.bbs6.domain.Post;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author xhchen
 */
public class PostPagerRepository extends PagerRepository<Post> {

	@Autowired
	PostRepository postRepository;

	@Override
	protected String encodeResultList(List<Post> resultList) {
		StringBuilder sb = new StringBuilder();
		for (Post post : resultList) {
			sb.append(post.getPostId()).append(',');
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	protected List<Post> decodeResultList(String value) {
		List<Long> postIdList = new ArrayList<Long>();
		if (StringUtils.isNotBlank(value)) {
			String[] postIds = value.split(",");
			for (String postId : postIds) {
				postIdList.add(Long.parseLong(postId));
			}
		}
		return postRepository.buildListBatch(postIdList);
	}

}
