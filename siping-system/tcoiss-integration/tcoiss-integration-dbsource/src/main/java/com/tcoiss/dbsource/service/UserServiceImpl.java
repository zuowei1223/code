package com.tcoiss.dbsource.service;

import com.tcoiss.dbsource.domain.User;
import com.tcoiss.dbsource.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> queryUserInfo() {
        return userMapper.queryUserInfo();
    }
}
