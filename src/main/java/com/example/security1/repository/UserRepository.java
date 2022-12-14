package com.example.security1.repository;

import com.example.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// CRUD 메서드를 JpaReposiroty가 들고 있음.
// @Repository 어노테이션 없어도 IoC 빈 등록이 됨. (JpaRepository 상속했기 때문)
public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByUsername(String username); // select * from user where username = 'username'

}
