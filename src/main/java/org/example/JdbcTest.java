package org.example;

import org.example.model.Movie;
import org.example.model.Actor;
import org.example.service.DatabaseService;

import java.util.List;

public class JdbcTest {
    public static void main(String[] args) {
        System.out.println("Full database crash test");

        try {
            DatabaseService dbService = new DatabaseService();

            System.out.println("\nAdding a test movie and actor");
            dbService.addMovie("Test Crash Movie", 2026);
            dbService.addActor("Test Crash Actor");

            List<Movie> movies = dbService.searchMovies("Test Crash Movie", "2026");
            Movie testMovie = movies.get(movies.size() - 1);

            List<Actor> actors = dbService.getAllActors();
            Actor testActor = actors.get(actors.size() - 1);

            System.out.println("Created Movie ID: " + testMovie.getId());
            System.out.println("Created Actor ID: " + testActor.getActorId());

            System.out.println("\nLinking the actor to the movie");
            dbService.addActorToMovie(testMovie.getId(), testActor.getActorId(), 1);
            System.out.println("Successfully linked");

            System.out.println("\nChecking the cast list for this movie");
            List<Actor> cast = dbService.getActorsByMovieId(testMovie.getId());
            for (Actor a : cast) {
                System.out.println(" -> " + a.getName() + " in role: " + a.getRole());
            }

            System.out.println("\nCleaning up test data");
            dbService.removeActorFromMovie(testMovie.getId(), testActor.getActorId());
            dbService.deleteActor(testActor.getActorId());
            dbService.deleteMovie(testMovie.getId());
            System.out.println("Database is clean again");

            System.out.println("\nAll tests passed successfully");

        } catch (Exception e) {
            System.err.println("\nAn error occurred during testing");
            e.printStackTrace();
            System.err.println("Hint: Make sure there is at least one record in the Role table with role_id = 1");
        }
    }
}