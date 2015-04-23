package cn.pconline.bbs6.repository;

import cn.pconline.bbs6.domain.Topic;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author xhchen
 */
public class TopicPagerRepository extends PagerRepository<Topic> {

	@Autowired
	TopicRepository topicRepository;

	@Override
	protected String encodeResultList(List<Topic> resultList) {
		StringBuilder sb = new StringBuilder();
		for (Topic topic : resultList) {
			sb.append(topic.getTopicId()).append(',');
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	protected List<Topic> decodeResultList(String value) {
		List<Long> topicIdList = new ArrayList<Long>();
		if (StringUtils.isNotBlank(value)) {
			String[] topicIds = value.split(",");
			for (String topicId : topicIds) {
				topicIdList.add(Long.parseLong(topicId));
			}
		}
		return topicRepository.buildListBatch(topicIdList);
	}

}
