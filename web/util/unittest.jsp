<%@ page session="false" contentType="text/html; charset=UTF-8"%><%@
page import="cn.pconline.bbs6.domain.*,cn.pconline.bbs6.util.*,cn.pconline.bbs6.service.*,cn.pconline.bbs6.repository.*" %><%@
page import="org.apache.commons.lang.math.NumberUtils,org.apache.commons.lang.StringUtils" %><%@
page import="java.util.*,org.springframework.jdbc.core.*, org.springframework.jdbc.core.simple.SimpleJdbcCall,
org.springframework.jdbc.core.namedparam.MapSqlParameterSource,java.sql.*" %>
<%

TopicService topicService = EnvUtils.getEnv().getTopicService();
//topicService.updatePost(User.find(1), Post.find(8), "xhchen");
				final PostRepository postRepository = EnvUtils.getEnv().getPostRepository();
				final Post post = Post.find(8);
				post.setUpdateAt(new java.util.Date());
				post.setLastModifier(User.find(1));
				post.setMessage("xhchen");
EnvUtils.getEnv().getTransactionTemplate().execute(
		new org.springframework.transaction.support.TransactionCallback() {
            @Override
            public Object doInTransaction(org.springframework.transaction.TransactionStatus status) {
				postRepository.updatePost(post);
				return null;
			}
		}
);
/*
for (int i = 18; i < 119; i ++) {
	for (int j = 0; j < 100; j ++) {
		Topic topic = Topic.find(i);
		User user = User.find(j + 1);
		topicService.replyTopic(user, topic, "宝马论坛又称宝马车友会、宝马俱乐部，是太平洋汽车论坛下属版块。宝马车友可以在这个社区交流和分享购车、驾车心得。");
	}
}
 */
	//topicService.createTopic(Forum.find(5), User.find(1), "test" + i, "topic" + i,
	//		" topic topic topic topic topic topic topic topic topic topic topic topic topic topic topic ");

%>
