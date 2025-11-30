package it.unibo.oop.lab.streams;

import static java.util.stream.Collectors.summingDouble;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
                .map(Song::getSongName)
                .sorted(String::compareTo);
    }

    @Override
    public Stream<String> albumNames() {
        return albums.entrySet().stream().map(Map.Entry::getKey);
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream()
                .filter(y -> y.getValue() == year)
                .map(Map.Entry::getKey);
    }

    @Override
    public int countSongs(final String albumName) {
        return Math.toIntExact(this.songs.stream()
                .filter(s -> albumName.equals(s.getAlbumName().orElse("")))
                .count());
    }

    @Override
    public int countSongsInNoAlbum() {
        return Math.toIntExact(this.songs.stream()
                .filter(s -> !s.getAlbumName().isPresent())
                .count());
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
                .filter(s -> albumName.equals(s.getAlbumName().orElse("")))
                .mapToDouble(Song::getDuration)
                .average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
                .max(Comparator.comparingDouble(Song::getDuration))
                .map(Song::getSongName);
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.songs.stream()
                .collect(Collectors.groupingBy(Song::getAlbumName, summingDouble(Song::getDuration)))
                .entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry<Optional<String>, Double>::getKey).get();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
