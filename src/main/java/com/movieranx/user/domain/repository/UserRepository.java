package com.movieranx.user.domain.repository;

import com.movieranx.user.domain.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /*
    Return all active users on database
     */
    List<User> findAllByActiveIsTrue();

    /*
    Param id
    Return specific user by his id
     */
    User findByIdAndActiveIsTrue(String id);

    /*
    Param username
    Return specific user by his username
     */
    User findByUsername(String username);

    /*
    Param email
    Return specific user by his email
     */
    User findByEmail(String email);

    /*
    Param username
    Param password
    Return true if there is a user with these params or false if there isn't
     */
    Boolean existsByUsernameAndPasswordAndActiveIsTrue(String username, String password);

    /*
    Param email
    Param password
    Return true if there is a user with these params or false if there isn't
     */
    Boolean existsByEmailAndPasswordAndActiveIsTrue(String email, String password);

}
