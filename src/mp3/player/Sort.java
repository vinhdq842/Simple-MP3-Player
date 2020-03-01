package mp3.player;

import java.util.Comparator;

/**
 * used to sort a playlist.
 *
 * @author ServantOfEvil
 */
public class Sort implements Comparator<Song> {
    private Type type;

    Sort(Type type) {
        this.type = type;
    }


    @Override
    public int compare(Song o1, Song o2) {
        switch (type) {
            case BY_ARTIST:
                return o1.getArtist().compareTo(o2.getArtist());
            case BY_ALBUM:
                return o1.getAlbum().compareTo(o2.getAlbum());
            case BY_TITLE:
                return o1.getTitle().compareTo(o2.getTitle());
            case BY_TRACK_NUM:
                return o1.getTrack_no().compareTo(o2.getTrack_no());
        }
        return 0;
    }
}

enum Type {
     BY_ARTIST, BY_ALBUM, BY_TITLE, BY_TRACK_NUM
}
