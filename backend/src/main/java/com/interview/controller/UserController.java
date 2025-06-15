package com.interview.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interview.interfaces.UserInterface;
import com.interview.model.UserModel;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired 
    UserInterface userInterface;

    private boolean isUserDuplicate(UserModel user) {
        return userInterface.findByUsername(user.getUsername()) != null || userInterface.findByEmail(user.getEmail()) != null;
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        try {
            List<UserModel> users = userInterface.findAll();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(userInterface.findById(id).orElseThrow(NoSuchElementException::new), HttpStatus.OK);
        }catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/users")
    public ResponseEntity<UserModel> createUser(@RequestBody UserModel user) {
        try {
            if (this.isUserDuplicate(user)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
            UserModel _user = userInterface.save(user);
            return new ResponseEntity<>(_user, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } 
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable("id") long id,@RequestBody UserModel user) {
        try {
            UserModel _user = userInterface.findById(id);
            if (_user != null) {
                _user.setUsername(user.getUsername());
                _user.setPassword(user.getPassword());
                _user.setEmail(user.getEmail());
                return new ResponseEntity<>(userInterface.save(_user), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        try {
            userInterface.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/users")
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        try {
            userInterface.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/users/username/{username}")
    public ResponseEntity<UserModel> getUserByUsername(@PathVariable String username) {
        try {
            return new ResponseEntity<>(userInterface.findByUsername(username), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserModel> getUserByEmail(@PathVariable String email) {
        try {
            return new ResponseEntity<>(userInterface.findByEmail(email), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}