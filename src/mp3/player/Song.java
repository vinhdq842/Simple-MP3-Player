package mp3.player;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


/**
 * represents a Song.
 *
 * @author ServantOfEvil
 */
public class Song implements Serializable {

    private boolean isPlaying = false;
    private String artist_album = "";
    private String track_no = "";
    private String title_trackArtist;
    private String duration = "00:00";
    private transient Player player;
    private String path;
    private transient FileInputStream input;
    private transient BufferedInputStream bufferedInputStream;
    private int totalLength = 1;
    private int lastPosition = 0;
    private transient Timer timer;
    private Playlist playlist;
    private String artist;
    private String title;
    private String album;

    Song(File file, Playlist playlist) {
        title_trackArtist = file.getName().substring(0, file.getName().lastIndexOf(".")) + " - Unknown";
        this.path = file.getAbsolutePath();
        this.playlist = playlist;
        try {
            Mp3File mp3File = new Mp3File(file);
            duration = toMMSSFormat(mp3File.getLengthInSeconds());
            if (mp3File.hasId3v1Tag()) {
                ID3v1 id3v1 = mp3File.getId3v1Tag();
                artist_album = (artist = testInfo(id3v1.getArtist())).concat(" - ").concat(album = testInfo(id3v1.getAlbum()));
                track_no = twoDigitsForm(id3v1.getTrack());
                title_trackArtist = (title = testInfo(id3v1.getTitle())).concat(" - ").concat(testInfo(id3v1.getArtist()));
            } else if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2 = mp3File.getId3v2Tag();
                artist_album = (artist = testInfo(id3v2.getAlbumArtist() == null ? id3v2.getArtist() : id3v2.getAlbumArtist())).concat(" - ").concat(album = testInfo(id3v2.getAlbum()));
                track_no = twoDigitsForm(id3v2.getTrack());
                title_trackArtist = (title = testInfo(id3v2.getTitle())).concat(" - ").concat(testInfo(id3v2.getArtist()));
            }

        } catch (Exception e) {
            //an exception can be thrown because of non-info files, just ignore it
            e.printStackTrace();
        }
    }
    /** seconds to mm:ss format */
    private String toMMSSFormat(long lengthInSeconds) {
        long mm = lengthInSeconds / 60;
        long ss = lengthInSeconds - mm * 60;
        return mm + ":" + twoDigitsForm(ss + "");
    }

    /**
     * tests if the provided info is null or empty.
     * @return "?" when the provided info is either null or empty, else return the tested info.
     */
    private String testInfo(String info) {
        if (info == null) return "?";
        if (info.trim().equals("")) return "?";
        return info.trim();
    }

    /**
     * turns the numeric String <b>s</b> into two-digits form.
     */
    private String twoDigitsForm(String s) {
        if (s == null) return "";
        if (s.trim().equals("")) return "";
        while (s.length() < 2) s = "0".concat(s);
        return s;
    }

    public synchronized boolean isPlaying() {
        return isPlaying;
    }

    public String getArtist_album() {
        return artist_album;
    }

    public String getTrack_no() {
        return track_no;
    }

    public String getTitle_trackArtist() {
        return title_trackArtist;
    }

    public String getDuration() {
        return duration;
    }

    public void pause() {
        if (isPlaying()) {
            try {
                lastPosition = input.available();
                player.close();
                timer.cancel();
                isPlaying = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        if (!isPlaying()) {
            try {
                input = new FileInputStream(path);
                totalLength = input.available();
                input.skip(totalLength - lastPosition);
                player = new Player(bufferedInputStream = new BufferedInputStream(input));
            } catch (IOException | JavaLayerException e) {
                e.printStackTrace();
            }
            beginPlaying();
        }
    }


    public void stop() {
        if (isPlaying()) player.close();
        isPlaying = false;
        lastPosition = totalLength;
    }


    public void play() {
        try {
            input = new FileInputStream(path);
            player = new Player(bufferedInputStream = new BufferedInputStream(input));
            this.totalLength = input.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
        beginPlaying();
    }

    @Override
    public String toString() {
        return "Song{" +
                "isPlaying=" + isPlaying +
                ", artist_album='" + artist_album + '\'' +
                ", track_no='" + track_no + '\'' +
                ", title_trackArtist='" + title_trackArtist + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }


    private void beginPlaying() {
        new Thread(() -> {
            try {
                player.play();
                isPlaying = false;
                if (player.isComplete()) {
                    if (!playlist.getMain().stopAfterCurrent()) {
                        Order order = playlist.getMain().getOrder();
                        switch (order) {
                            case DEFAULT:
                                playlist.next();
                                break;
                            case RANDOM:
                                playlist.randomPlay();
                                break;
                            case RP_TRACK:
                                playlist.play(playlist.getPlayedSongIndex());
                                break;
                            case RP_PLAYLIST:
                                if (playlist.getPlayedSongIndex() == playlist.getSongCount() - 1) {
                                    playlist.play(0);
                                } else {
                                    playlist.next();
                                }
                                break;
                        }
                    } else playlist.setPlayedSong(null);

                }
                playlist.getMain().actionPerformed(null);
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }).start();
        isPlaying = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            // update the progress bar
                while (isPlaying() && Song.this.equals(Main.getBeingPlayedSong())) {
                    Main.sliderProgress.setValue(getProgress());
                }
            }
        }, 0, 1000);

    }

    public int getProgress() {
        int curPos = lastPosition;
        try {
            curPos = bufferedInputStream.available();
        } catch (Exception e) {
            timer.cancel();
        }
        return (int) ((totalLength - curPos) * 1f / totalLength * 100);
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(artist_album, song.artist_album) &&
                Objects.equals(track_no, song.track_no) &&
                Objects.equals(title_trackArtist, song.title_trackArtist) &&
                Objects.equals(duration, song.duration) &&
                Objects.equals(path, song.path) &&
                Objects.equals(artist, song.artist) &&
                Objects.equals(title, song.title) &&
                Objects.equals(album, song.album) &&
                Objects.equals(playlist, song.playlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlist, isPlaying, artist_album, track_no, title_trackArtist, duration, player, path, input, bufferedInputStream, totalLength, lastPosition, timer, playlist, artist, title, album);
    }

}