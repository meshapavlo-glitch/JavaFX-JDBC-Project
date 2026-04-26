package org.example.model;

public class Actor {
    private long actorId;
    private String name;
    private String role;
    private long movieId;

    public Actor() {
    }

    public Actor(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public long getActorId() {
        return actorId;
    }

    public void setActorId(long actorId) {
        this.actorId = actorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    @Override
    public String toString() {
        return String.format("Actor{name='%s', role='%s'}", name, role);
    }
}