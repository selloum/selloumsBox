package com.mana.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mana.miaosha.dao.UserDao;
import com.mana.miaosha.domin.User;

@Service
public class UserService {
	@Autowired
	UserDao userDao;
	
	public User getById(int id) {
		return userDao.getById(id);
	}
}
