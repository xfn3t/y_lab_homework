package ru.homework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.homework.DTO.Conference;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class ConferenceTest {

    @Test
    void testFindIndexById_validId() {
        List<Conference> conferences = List.of(new Conference(1L, "Test", new Date(), new Date(), "user", 1L));

        Long index = App.findIndexById(conferences, 1L);

        assertThat(index).isEqualTo(0L);
    }

    @Test
    void testFindIndexById_invalidId() {
        List<Conference> conferences = List.of(new Conference(1L, "Test", new Date(), new Date(), "user", 1L));

        assertThatThrownBy(() -> App.findIndexById(conferences, 2L)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void testFindIndexById_nonExistentId() {
        List<Conference> conferences = List.of(new Conference(1L, "Test", new Date(), new Date(), "user", 1L));

        Long index = App.findIndexById(conferences, 1L);

        assertThat(index).isEqualTo(0L);
    }

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testErrorMessage() {
        Logger.errorMessage("Test error message");
        assertEquals("ERROR\n!!! TEST ERROR MESSAGE !!!\n\n", outContent.toString());
    }

    @Test
    public void testInfoMessage() {
        Logger.infoMessage("Test info message");
        assertEquals("INFO\n+++ Test info message +++\n", outContent.toString());
    }

    @Test
    public void testErrorMessageWithTitle() {
        Logger.errorMessage("Title", "Test error message");
        assertEquals("Title\n!!! TEST ERROR MESSAGE !!!\n\n", outContent.toString());
    }

    @Test
    public void testInfoMessageWithTitle() {
        Logger.infoMessage("Title", "Test info message");
        assertEquals("Title\n+++ Test info message +++\n", outContent.toString());
    }

}

