package com.movieranx.user.domain.service;

import com.movieranx.user.domain.domain.User;
import com.movieranx.user.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository repository;

    public List<User> listAllUser() {
        return repository.findAllByActiveIsTrue();
    }

    public void registerUser(User user) {
        user.setActive(true);
        user.setPassword(user.getPassword());

        try {
            repository.save(user);
        } catch (DuplicateKeyException e) {
            log.error("Could not register user, email already registered");
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Could not register user!");
            log.error(e.getMessage());
            throw e;
        }
    }

    public User findUserById(String id) {
        User user = null;

        try {
            user = repository.findByIdAndActiveIsTrue(id);

            if (user != null) {
                user.setPassword(user.getPassword());
            }
        } catch (Exception e) {
            log.error("Could not find person!");
            log.error(e.getMessage());
            throw e;
        }

        return user;
    }

    public User updateUser(User user, String id) {
        Optional<User> optional = repository.findById(id);

        if (optional.isPresent()) {
            User db = optional.get();

            db.setId(user.getId());
            db.setFirstName(user.getFirstName());
            db.setLastName(user.getLastName());
            db.setEmail(user.getEmail());
            db.setUsername(user.getUsername());

            if (!db.getPassword().equals(user.getPassword())) {
                user.setPassword(user.getPassword());
                db.setPassword(user.getPassword());
            }

            repository.save(db);

            return db;
        } else {
            throw new RuntimeException("Could not update registry");
        }
    }

    public User updatePassword(String password, String id) {
        Optional<User> optional = repository.findById(id);

        if (optional.isPresent()) {
            User db = optional.get();
            db.setPassword(password);
            repository.save(db);

            return db;
        } else {
            throw new RuntimeException("Could not update the password");
        }
    }

    public User updateUsername(String username, String id) {
        Optional<User> optional = repository.findById(id);

        if (optional.isPresent()) {
            User db = optional.get();
            db.setUsername(username);
            repository.save(db);

            return db;
        } else {
            throw new RuntimeException("Could not update the username");
        }
    }

    public void inactivate(String id) {
        Optional<User> optional = repository.findById(id);

        if (optional.isPresent()) {
            User db = optional.get();
            db.setActive(false);
            repository.save(db);
        } else {
            throw new RuntimeException("Could not inactivate user");
        }
    }

    public User reactivateUser(String id) {
        Optional<User> optional = repository.findById(id);

        if (optional.isPresent()) {
            User db = optional.get();
            db.setActive(true);
            repository.save(db);

            return db;
        } else {
            throw new RuntimeException("Could not reactivate user");
        }
    }

    public User login(String userLogin, String password) throws IllegalAccessException {
        log.info("Login service ->");
        Boolean exist = null;

        try {
            log.info("Validanting existence of user");

            if (userLogin.contains("@")) {
                exist = repository.existsByEmailAndPasswordAndActiveIsTrue(userLogin, password);
            } else {
                exist = repository.existsByUsernameAndPasswordAndActiveIsTrue(userLogin, password);
            }
        } catch (Exception e) {
            log.error("Error while validanting userLogin and password");
            log.error(e.getMessage());
            throw e;
        }

        User user = null;

        if (exist) {
            log.info("Searching person on database...");

            if (userLogin.contains("@")) {
                user = repository.findByEmail(userLogin);
            } else {
                user = repository.findByUsername(userLogin);
            }
        } else {
            log.error("User or password are incorrect");
            throw new IllegalAccessException();
        }

        return user;
    }

    public User findUserByUsername(String username) {
        User user = null;

        try {
            user = repository.findByUsername(username);
        } catch (Exception e) {
            log.error("Error while trying to find user by username");
            throw e;
        }

        return user;
    }
}
