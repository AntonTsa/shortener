package com.anton.tsarenko.shortener;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

/**
 * Test class for AppLauncher.
 */
public class AppLauncherTest {
    @Test
    void main_invokesSpringApplicationRun_withArgs() {
        String[] args = {"--test.arg=value"};
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(AppLauncher.class, args)).thenReturn(null);

            AppLauncher.main(args);

            mocked.verify(() -> SpringApplication.run(AppLauncher.class, args), Mockito.times(1));
        }
    }

    @Test
    void main_invokesSpringApplicationRun_withEmptyArgs() {
        String[] args = new String[0];
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(AppLauncher.class, args)).thenReturn(null);

            AppLauncher.main(args);

            mocked.verify(() -> SpringApplication.run(AppLauncher.class, args), Mockito.times(1));
        }
    }
}
