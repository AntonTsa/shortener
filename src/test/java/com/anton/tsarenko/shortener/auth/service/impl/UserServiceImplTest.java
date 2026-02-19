package com.anton.tsarenko.shortener.auth.service.impl;

import static com.anton.tsarenko.shortener.auth.service.impl.UserServiceImplFixture.EXISTING_USER;
import static com.anton.tsarenko.shortener.auth.service.impl.UserServiceImplFixture.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.anton.tsarenko.shortener.auth.entity.User;
import com.anton.tsarenko.shortener.auth.repo.UsersRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link UserServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("""
            GIVEN existing user id
            WHEN getUserById is called
            THEN returns found user
            """)
    void getUserByIdWhenUserExists() {
        // GIVEN
        given(usersRepository.findById(USER_ID)).willReturn(Optional.of(EXISTING_USER));

        // WHEN
        User actualUser = userService.getUserById(USER_ID);

        // THEN
        assertThat(actualUser).isEqualTo(EXISTING_USER);
    }

    @Test
    @DisplayName("""
            GIVEN unknown user id
            WHEN getUserById is called
            THEN returns null
            """)
    void getUserByIdWhenUserNotFound() {
        // GIVEN
        given(usersRepository.findById(USER_ID)).willReturn(Optional.empty());

        // WHEN
        User actualUser = userService.getUserById(USER_ID);

        // THEN
        assertThat(actualUser).isNull();
    }
}
