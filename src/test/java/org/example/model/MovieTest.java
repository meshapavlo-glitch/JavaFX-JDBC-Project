package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    @Test
    void testValidMovieCreation() {
        Movie movie = new Movie(1L, "Inception", 2010);
        assertEquals(1L, movie.getId());
        assertEquals("Inception", movie.getTitle());
        assertEquals(2010, movie.getReleaseYear());
    }

    @Test
    void testBoundaryReleaseYears() {
        assertDoesNotThrow(() -> new Movie(2L, "First Movie", 1895));
        assertDoesNotThrow(() -> new Movie(3L, "Future Movie", 2100));
    }

    @Test
    void testInvalidReleaseYearThrowsException() {
        Movie movie = new Movie();

        Exception tooOld = assertThrows(IllegalArgumentException.class, () -> movie.setReleaseYear(1894));
        assertTrue(tooOld.getMessage().contains("between 1895 and 2100"));

        Exception tooFarFuture = assertThrows(IllegalArgumentException.class, () -> movie.setReleaseYear(2101));
        assertTrue(tooFarFuture.getMessage().contains("between 1895 and 2100"));
    }

    @Test
    void testInvalidTitleThrowsException() {
        Movie movie = new Movie();

        Exception nullTitle = assertThrows(IllegalArgumentException.class, () -> movie.setTitle(null));
        assertEquals("Movie title cannot be empty", nullTitle.getMessage());

        Exception blankTitle = assertThrows(IllegalArgumentException.class, () -> movie.setTitle("   "));
        assertEquals("Movie title cannot be empty", blankTitle.getMessage());
    }

    @Test
    void testToStringFormat() {
        Movie movie = new Movie(5L, "The Matrix", 1999);
        assertEquals("ID: 5 | The Matrix (1999)", movie.toString());
    }
}