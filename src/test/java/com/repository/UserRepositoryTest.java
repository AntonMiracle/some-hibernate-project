package com.repository;

import com.model.Address;
import com.model.Role;
import com.model.User;
import org.junit.After;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest {
    private UserRepository uRepository = new UserRepository();

    @After
    public void after() {
        for (User u : uRepository.findAllUsers()) {
            uRepository.deleteUser(u);
        }
    }

    @Test
    public void saveUniqueUser() {
        uRepository.saveUser(getUser());
        assertThat(uRepository.findAllUsers().size() == 1).isTrue();
    }

    @Test
    public void saveUserOnlyWithUniqueEmail() {
        String email = "email@gmail.com";

        uRepository.saveUser(getUser(email));
        uRepository.saveUser(getUser(email));
        uRepository.saveUser(getUser("1" + email));

        assertThat(uRepository.findAllUsers().size() == 2).isTrue();
    }

    @Test
    public void updateUser() {
        User user = getUser();
        String newName = user.getName() + "new";
        assertThat(user.getRoles().size() == 1).isTrue();
        uRepository.saveUser(user);

        user.setName(newName);
        user.getRoles().add(Role.ADMIN);
        user.getRoles().add(Role.USER);
        uRepository.saveUser(user);

        assertThat(uRepository.findByEmail(user.getEmail()).getName()).isEqualTo(newName);
        assertThat(uRepository.findByEmail(user.getEmail()).getRoles().size() == 2).isTrue();
        assertThat(uRepository.findByEmail(user.getEmail()).getRoles().contains(Role.ADMIN)).isTrue();
        assertThat(uRepository.findByEmail(user.getEmail()).getRoles().contains(Role.USER)).isTrue();
    }

    @Test
    public void updateEmailCreateNewUser() {
        String email = "some@gmail.com";
        String newEmail = "new" + email;
        User user = getUser(email);
        uRepository.saveUser(user);
        assertThat(uRepository.findAllUsers().size() == 1).isTrue();

        user.setEmail(newEmail);
        uRepository.saveUser(user);

        assertThat(uRepository.findByEmail(email)).isNotNull();
        assertThat(uRepository.findByEmail(newEmail)).isNotNull();
        assertThat(uRepository.findAllUsers().size() == 2).isTrue();
    }

    @Test
    public void findAllUser() {
        assertThat(uRepository.findAllUsers().size() == 0).isTrue();

        uRepository.saveUser(getUser("email1@gmail.com"));
        uRepository.saveUser(getUser("email2@gmail.com"));

        assertThat(uRepository.findAllUsers().size() == 2).isTrue();
    }

    @Test
    public void deleteUser() {
        User user = getUser();
        uRepository.saveUser(user);

        uRepository.deleteUser(user);

        assertThat(uRepository.findAllUsers().size() == 0).isTrue();
        assertThat(uRepository.deleteUser(null)).isFalse();
    }

    @Test
    public void findByEmail() {
        String email = "email@gmail.com";
        User user = getUser(email);
        uRepository.saveUser(user);

        assertThat(uRepository.findByEmail(email)).isEqualTo(user);
        assertThat(uRepository.findByEmail(email + "1")).isNull();
        assertThat(uRepository.findByEmail(null)).isNull();
    }

    @Test
    public void findUsersByRole() {
        User user1 = getUser("email1@com", Role.ADMIN);
        User user2 = getUser("email2@com", Role.ADMIN, Role.USER);
        User user3 = getUser("email3@com", Role.USER);
        User user4 = getUser("email4@com", Role.USER);
        uRepository.saveUser(user1);
        uRepository.saveUser(user2);
        uRepository.saveUser(user3);
        uRepository.saveUser(user4);

        assertThat(uRepository.findUsersWithRole(Role.ADMIN).size() == 2).isTrue();
        assertThat(uRepository.findUsersWithRole(Role.USER).size() == 3).isTrue();
        assertThat(uRepository.findUsersWithRole(Role.SUPER_USER).size() == 0).isTrue();
        assertThat(uRepository.findUsersWithRole(null).size() == 0).isTrue();
    }

    @Test
    public void userBirthDaySaveCorrectLocalDateTime() {
        LocalDateTime birthday = LocalDateTime.now().withNano(0).minusYears(30);
        User user = getUser(birthday);
        uRepository.saveUser(user);

        assertThat(uRepository.findByEmail(user.getEmail()).getBirthDay()).isEqualTo(birthday);
    }

    @Test
    public void convertAddress() {
        User user = getUser();
        uRepository.saveUser(user);

        assertThat(uRepository.findByEmail(user.getEmail()).getAddress()).isEqualTo(user.getAddress());
    }

    public User getUser() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        return new User("email@gmail.com", "SuperName", LocalDateTime.now(), roles, new Address("Poland", "Krakow"));
    }

    public User getUser(String email) {
        User user = getUser();
        user.setEmail(email);
        return user;
    }

    public User getUser(String email, Role... roles) {
        User user = getUser(email);
        if (roles == null || roles.length == 0) {
            return user;
        }
        Set<Role> r = Arrays.stream(roles).collect(Collectors.toSet());
        user.setRoles(r);
        return user;
    }

    public User getUser(LocalDateTime birthDay) {
        User user = getUser();
        user.setBirthDay(birthDay);
        return user;
    }
}