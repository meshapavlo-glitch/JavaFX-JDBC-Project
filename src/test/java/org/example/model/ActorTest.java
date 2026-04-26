package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    @Test
    void testActorCreationAndGetters() {
        Actor actor = new Actor("Keanu Reeves", "Neo");
        actor.setActorId(10L);
        actor.setMovieId(5L);

        assertEquals(10L, actor.getActorId());
        assertEquals("Keanu Reeves", actor.getName());
        assertEquals("Neo", actor.getRole());
        assertEquals(5L, actor.getMovieId());
    }

    @Test
    void testToStringFormat() {
        Actor actor = new Actor("Carrie-Anne Moss", "Trinity");
        assertEquals("Actor{name='Carrie-Anne Moss', role='Trinity'}", actor.toString());
    }
}