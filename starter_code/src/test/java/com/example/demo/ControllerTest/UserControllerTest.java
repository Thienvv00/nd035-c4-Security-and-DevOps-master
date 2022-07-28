package com.example.demo.ControllerTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private final static String MOCK_USERNAME = "thien";
    private final static String MOCK_PASSWORD = "thienpassword";

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObject(userController,"userRepository",userRepo);
        TestUtils.injectObject(userController,"cartRepository",cartRepo);
        TestUtils.injectObject(userController,"bCryptPasswordEncoder",encoder);
    }

    @Test
    public void create_user_with_Happy_pass() throws  Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest cur = new CreateUserRequest();
        cur.setUsername("test");
        cur.setPassword("testPassword");
        cur.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(cur);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        User user = response.getBody();

        assertEquals(0,user.getId());
        assertEquals("test",user.getUsername());
        assertEquals("thisIsHashed",user.getPassword());
    }
    @Test
    public void create_user_with_weak_pass() throws  Exception{
//        if password.length() < 7 -> weak password -> status 400
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest cur = new CreateUserRequest();
        cur.setUsername("test");
        cur.setPassword("1");
        cur.setConfirmPassword("1");
        ResponseEntity<User> response = userController.createUser(cur);

        assertNotNull(response);
        assertEquals(400,response.getStatusCodeValue());

    }
    @Test
    public void create_user_with_Wrong_ConfirmPassword() throws  Exception{

        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest cur = new CreateUserRequest();
        cur.setUsername("test");
        cur.setPassword("testPassword");
        cur.setConfirmPassword("testPassword1");
        ResponseEntity<User> response = userController.createUser(cur);

        assertNotNull(response);
        assertEquals(400,response.getStatusCodeValue());

    }
    @Test
    public void findByUsername() throws  Exception{

        User mockUser = new User();
        mockUser.setUsername(MOCK_USERNAME);
        when(userRepo.findByUsername(MOCK_USERNAME)).thenReturn(mockUser);

        ResponseEntity<User> response = userController.findByUserName(MOCK_USERNAME);
        User user = response.getBody();

        Assert.assertNotNull(user);
        Assert.assertEquals( HttpStatus.OK.value(),response.getStatusCodeValue());
        Assert.assertEquals(MOCK_USERNAME, user.getUsername());

    }
    @Test
    public void findByNotExistUsername() throws  Exception{
    String NotExistUsername ="doraemon";
        User mockUser = new User();
        mockUser.setUsername(MOCK_USERNAME);
        when(userRepo.findByUsername(MOCK_USERNAME)).thenReturn(mockUser);

        ResponseEntity<User> response = userController.findByUserName(NotExistUsername);
        User user = response.getBody();

        Assert.assertNull(user);


    }


}
