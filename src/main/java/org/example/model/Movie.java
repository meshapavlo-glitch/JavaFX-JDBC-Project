package org.example.model;

public class Movie {
    private long id;
    private String title;
    private int releaseYear;

    public Movie() {
    }

    public Movie(long id, String title, int releaseYear) {
        this.id = id;
        setTitle(title);
        setReleaseYear(releaseYear);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        if (releaseYear < 1895 || releaseYear > 2100) {
            throw new IllegalArgumentException("Release year must be between 1895 and 2100");
        }
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s (%d)", id, title, releaseYear);
    }
}