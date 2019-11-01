package com.example.demo.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class UserDaoService {
    private static List<User> users = new ArrayList<>();
    private static int usersCount = 4;

    static {
        users.add(new User(1, "Adam", new Date()));
        users.add(new User(2, "Sam", new Date()));
        users.add(new User(3, "John", new Date()));
        users.add(new User(4, "Mike", new Date()));
    }

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++usersCount);
        }
        users.add(user);
        return user;
    }

    public User findOne(int id) {
        for(User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public User deleteById(int id) {
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            User user = it.next();
            if (user.getId().equals(id)) {
                it.remove();
                return user;
            }
        }
        return null;
    }
}
