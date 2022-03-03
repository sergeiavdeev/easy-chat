package ru.avdeev.chat.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    @Test
    void getArrayAfterFour() {
        assertArrayEquals(new int[]{2, 3}, Application.getArrayAfterFour(new int[]{1,2,4,2,3}));
        assertArrayEquals(new int[]{}, Application.getArrayAfterFour(new int[]{4,2,4,2,4}));
        assertArrayEquals(new int[]{3}, Application.getArrayAfterFour(new int[]{4,2,4,2,4,3}));
        assertThrows(RuntimeException.class, () -> Application.getArrayAfterFour(new int[]{1,2,6,2,3}));
        assertThrows(RuntimeException.class, () -> Application.getArrayAfterFour(new int[]{}));
    }

    @Test
    void findOneFour() {
        assertTrue(Application.findOneFour(new int[] {1, 4, 3}));
        assertTrue(Application.findOneFour(new int[] {2, 4, 3}));
        assertTrue(Application.findOneFour(new int[] {1, 5, 3}));
        assertFalse(Application.findOneFour(new int[] {7, 5, 3}));
    }
}