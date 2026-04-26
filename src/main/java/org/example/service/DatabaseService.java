package org.example.service;

import org.example.model.Movie;
import org.example.model.Actor;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseService {
    private final Properties props = new Properties();

    public DatabaseService() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("db.properties file not found in resources");
            }
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM MOVIE ORDER BY movie_id";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getLong("movie_id"),
                        rs.getString("title"),
                        rs.getInt("release_year")));
            }
        }
        return movies;
    }

    public void addMovie(String title, int year) throws SQLException {
        String sql = "INSERT INTO MOVIE (title, release_year) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, year);
            pstmt.executeUpdate();
        }
    }

    public void updateMovie(Movie movie) throws SQLException {
        String sql = "UPDATE MOVIE SET title = ?, release_year = ? WHERE movie_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setInt(2, movie.getReleaseYear());
            pstmt.setLong(3, movie.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteMovie(long id) throws SQLException {
        String sql = "DELETE FROM MOVIE WHERE movie_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Movie> searchMovies(String title, String yearStr) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM MOVIE WHERE 1=1");

        if (title != null && !title.isEmpty())
            sql.append(" AND title ILIKE ?");
        if (yearStr != null && !yearStr.isEmpty())
            sql.append(" AND release_year = ?");

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (title != null && !title.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + title + "%");
            }
            if (yearStr != null && !yearStr.isEmpty()) {
                pstmt.setInt(paramIndex, Integer.parseInt(yearStr));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(new Movie(
                            rs.getLong("movie_id"),
                            rs.getString("title"),
                            rs.getInt("release_year")));
                }
            }
        }
        return movies;
    }

    public List<Actor> getAllActors() throws SQLException {
        List<Actor> actors = new ArrayList<>();
        String sql = "SELECT * FROM ACTOR ORDER BY actor_id";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Actor actor = new Actor();
                actor.setActorId(rs.getLong("actor_id"));
                actor.setName(rs.getString("full_name"));
                actors.add(actor);
            }
        }
        return actors;
    }

    public void addActor(String fullName) throws SQLException {
        String sql = "INSERT INTO ACTOR (full_name) VALUES (?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.executeUpdate();
        }
    }

    public void updateActor(Actor actor) throws SQLException {
        String sql = "UPDATE ACTOR SET full_name = ? WHERE actor_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, actor.getName());
            pstmt.setLong(2, actor.getActorId());
            pstmt.executeUpdate();
        }
    }

    public void deleteActor(long id) throws SQLException {
        String sql = "DELETE FROM ACTOR WHERE actor_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Actor> getActorsByMovieId(long movieId) throws SQLException {
        List<Actor> actors = new ArrayList<>();
        String sql = "SELECT a.actor_id, a.full_name, r.role_title " +
                "FROM MOVIE_CAST mc " +
                "JOIN ACTOR a ON mc.actor_id = a.actor_id " +
                "JOIN ROLE r ON mc.role_id = r.role_id " +
                "WHERE mc.movie_id = ?";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Actor actor = new Actor(
                            rs.getString("full_name"),
                            rs.getString("role_title"));
                    actor.setActorId(rs.getLong("actor_id"));
                    actor.setMovieId(movieId);
                    actors.add(actor);
                }
            }
        }
        return actors;
    }

    public void addActorToMovie(long movieId, long actorId, long roleId) throws SQLException {
        String sql = "INSERT INTO MOVIE_CAST (movie_id, actor_id, role_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, movieId);
            pstmt.setLong(2, actorId);
            pstmt.setLong(3, roleId);
            pstmt.executeUpdate();
        }
    }

    public void removeActorFromMovie(long movieId, long actorId) throws SQLException {
        String sql = "DELETE FROM MOVIE_CAST WHERE movie_id = ? AND actor_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, movieId);
            pstmt.setLong(2, actorId);
            pstmt.executeUpdate();
        }
    }
}