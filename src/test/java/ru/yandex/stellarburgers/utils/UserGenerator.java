package ru.yandex.stellarburgers.utils;

import ru.yandex.stellarburgers.model.User;

import java.util.Random;

public class UserGenerator {
    private static final Random random = new Random();

    public static User generateRandomUser() {
        String randomString = String.valueOf(System.currentTimeMillis());
        return new User(
            "test_" + randomString + "@test.com",
            "password" + randomString,
            "TestUser" + randomString
        );
    }

    public static User createUserWithoutEmail() {
        String randomString = String.valueOf(System.currentTimeMillis());
        User user = new User();
        user.setPassword("password" + randomString);
        user.setName("TestUser" + randomString);
        return user;
    }

    public static User createUserWithoutPassword() {
        String randomString = String.valueOf(System.currentTimeMillis());
        User user = new User();
        user.setEmail("test_" + randomString + "@test.com");
        user.setName("TestUser" + randomString);
        return user;
    }

    public static User createUserWithoutName() {
        String randomString = String.valueOf(System.currentTimeMillis());
        User user = new User();
        user.setEmail("test_" + randomString + "@test.com");
        user.setPassword("password" + randomString);
        return user;
    }
}
