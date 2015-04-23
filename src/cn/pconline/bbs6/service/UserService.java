package cn.pconline.bbs6.service;

import cn.pconline.bbs6.domain.Pager;
import cn.pconline.bbs6.domain.User;
import cn.pconline.bbs6.repository.SqlPageBuilder;
import cn.pconline.bbs6.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

public class UserService extends EnvService {

    @Autowired
    UserRepository userRepository;

    @Autowired
	SqlPageBuilder sqlPageBuilder;

    /**
     * 修改用户信息
     * @param user
     */
    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

	/**
	 * 根据用户名查找用户
	 * @param username
	 * @return
	 */
	public User findByName(String username){
		return userRepository.findByName(username);
	}

	public void createUser(User user) {
		userRepository.createUser(user);
	}

	public Pager<User> pageAllUser(int pageNo, int pageSize) {
		return userRepository.pageAllUser(pageNo, pageSize);
	}
}
