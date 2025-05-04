package org.example.melodymatch.enums;

public enum Mood {
    SAD("acoustic"),
    HAPPY("pop"),
    ENERGETIC("edm"),
    CHILL("lo-fi"),
    ANGRY("metal");

    private final String genre;

    Mood(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public static Mood fromString(String mood) {
        for (Mood m : values()) {
            if (m.name().equalsIgnoreCase(mood)) {
                return m;
            }
        }
        return HAPPY;
    }
}