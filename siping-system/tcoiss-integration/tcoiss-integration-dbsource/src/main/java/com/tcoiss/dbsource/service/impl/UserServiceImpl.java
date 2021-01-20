package com.tcoiss.dbsource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.dbsource.domain.User;
import com.tcoiss.dbsource.mapper.UserMapper;
import com.tcoiss.dbsource.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public List<User> queryUserInfo() {
        return this.list();
    }
}
