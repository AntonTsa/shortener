package com.anton.tsarenko.shortener;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

/**
 * Test class for AppLauncher.
 */
public class AppLauncherTest {
    @Test
    void main_printsAppIsRunningMessage() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            AppLauncher.main();
        } finally {
            System.setOut(original);
        }

        String output = out.toString();
        assertTrue(output.contains("App is running!"), "Expected message was not printed");
    }
}
