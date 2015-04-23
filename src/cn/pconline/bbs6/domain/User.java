package cn.pconline.bbs6.domain;

import java.util.Date;

import cn.pconline.bbs6.repository.UserRepository;
import org.apache.commons.lang.StringUtils;

public class User {
    public static int SIGN_STATUS_NULL = 0;
    public static int SIGN_STATUS_NORMAL = 1;
    public static int SIGN_STATUS_DENY = -1;
    long userId;
    String name;
    String nickname;
    Date createAt;
    int postCount;
    int topicCount;
    Date lastPostAt;

    public final static User ANONYMOUS = new User();

    static {
        ANONYMOUS.setName("游客");
        ANONYMOUS.setNickname("游客");
    }

    public static User find(long userId) {
        return UserRepository.instance().find(userId);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return StringUtils.isEmpty(nickname) ? name : nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getLastPostAt() {
        return lastPostAt;
    }

    public void setLastPostAt(Date lastPostAt) {
        this.lastPostAt = lastPostAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User object:{");
        sb.append("userId=").append(userId);
        sb.append(",name=").append(name);
        sb.append(",nickname=").append(nickname);
        sb.append("}");
        return sb.toString();
    }
}
