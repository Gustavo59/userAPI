package com.movieranx.user.application.api;

import com.movieranx.user.domain.domain.User;
import com.movieranx.user.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService service;

    @CrossOrigin("*")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid User body) {
        log.info("Register user started ->");

        /*User user = User.builder()
                    .firstName(body.getFirstName())
                    .lastName(body.getLastName())
                    .email(body.getEmail())
                    .password(body.getPassword())
                    .username(body.getUsername())
                    .id(body.getId())
                    .active(body.getActive())
                    .build();*/

        try {
            service.registerUser(body);
        } catch (DuplicateKeyException e) {
            log.error("Could not register user, email already registered");
            log.error(e.getMessage());
            if (Objects.requireNonNull(e.getMessage().contains("username"))) {
                return new ResponseEntity<>("USERNAME_ALREADY_REGISTERED", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>("EMAIL_ALREADY_REGISTERED", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Could not register the user");
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not register the user", HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @CrossOrigin("*")
    @GetMapping("/listalluser")
    public ResponseEntity<?> listAllUser(){
        List<User> users;

        try {
            users = service.listAllUser();
        }catch (Exception e){
            return new ResponseEntity<>("Could not retrieve list of users", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @GetMapping("/findbyid/{id}")
    public ResponseEntity<?> findUserById(@PathVariable String id){
        log.info("Finding user by id....");

        User user;

        try{
            user = service.findUserById(id);
        }catch (Exception e){
            log.error("Error while finding user with ID "+ id, e.getCause());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (user == null){
            return new ResponseEntity<>("Could not find user", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @PutMapping("/updateuser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody @Valid User body){
        log.info("Updating user.....");

        body.setId(id);

        User response;

        try {
            response = service.updateUser(body, id);
        } catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not update user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response == null){
            return new ResponseEntity<>("Could not find user", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @PutMapping("/updatepassword/{id}")
    public ResponseEntity<?> updatePassword(@RequestHeader @Valid String password, @PathVariable String id){
        log.info("Updating user password....");

        if (password == null || password.equals("")){
            return new ResponseEntity<>("Password must not be null", HttpStatus.BAD_REQUEST);
        }

        if (password.length()<6){
            return new ResponseEntity<>("Password must be at least 6 characters long", HttpStatus.BAD_REQUEST);
        }

        User response;

        try {
            response = service.updatePassword(password, id);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not update password", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response == null){
            return new ResponseEntity<>("Could not find password from ID: " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @PutMapping("/updateusername/{id}")
    public ResponseEntity<?> updateUsername(@RequestHeader @Valid String username, @PathVariable String id){
        log.info("Updating user username....");

        if (username == null || username.equals("")){
            return new ResponseEntity<>("Username must not be null", HttpStatus.BAD_REQUEST);
        }

        if (username.length()<3){
            return new ResponseEntity<>("Username must be at least 3 characters long", HttpStatus.BAD_REQUEST);
        }

        User response;

        try {
            response = service.updateUsername(username, id);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not update username", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response == null){
            return new ResponseEntity<>("Could not find username from ID: " + id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUSer(@PathVariable String id){
        try {
            service.inactivate(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not delete user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @CrossOrigin("*")
    @PutMapping("/reactivateuser/{id}")
    public ResponseEntity<?> reactivatePerson(@PathVariable String id) {
        User response;

        try {
            response = service.reactivateUser(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Could not reactivate user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestHeader String userLogin, @RequestHeader String password) throws IllegalAccessException{
        log.info("Login started...");

        User user;

        try {
            user = service.login(userLogin, password);
        } catch (IllegalAccessException e) {
            log.error("User or password are incorrect");
            return new ResponseEntity<>("User or password are incorrect", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error while validating userLogin and password");
            log.error(e.getMessage());
            return new ResponseEntity<>("Error while userLogin and password", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @CrossOrigin("*")
    @GetMapping("/findbycpf")
    public ResponseEntity<?> findPersonByUsername(@PathVariable String username){
        log.info("Finding user by username...");

        User user = null;

        try {
            user = service.findUserByUsername(username);

            if (user == null){
                return new ResponseEntity<>("Could not find person with Username: " + username, HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e){
            log.error("Error while trying to find person with username");
            return new ResponseEntity<>("Error while trying to find person with Username", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
