package com.lzh.bi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.bi.mapper.UserMapper;
import com.lzh.bi.pojo.entity.User;
import com.lzh.bi.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author lzh
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

}




