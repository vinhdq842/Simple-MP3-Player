package mp3.player;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

/**
 * A playlist to contain songs. Displayed as a Tab.
 *
 * @author ServantOfEvil
 */
public class Playlist implements Serializable {

    private Vector<Song> songs;
    private transient JTable table;
    private Song playedSong;
    private int playedSongIndex = 0;
    private String name;
    private transient JPanel panel;
    private transient Main main;

    Playlist(String title, Main main) {
        songs = new Vector<>();
        this.main = main;
        name = title;
        initInterface();
    }

    private void initInterface() {
        JScrollPane scrollPane = new JScrollPane();
        panel = new JPanel();
        panel.setLayout(new CardLayout(0, 0));

        table = new JTable();
        scrollPane.setViewportView(table);
        table.setModel(new DefaultTableModel(
                new Object[][]{
                },
                new String[]{
                        "Playing", "Artist/album", "Track no", "Title/track artist", "Duration", ""
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                return super.getColumnClass(columnIndex);
            }
        });
        table.setSelectionBackground(Color.LIGHT_GRAY);
        table.setSelectionForeground(Color.RED);
        panel.add(scrollPane, 0);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(205);
    }

    public void add(Song song) {
        songs.addElement(song);
    }

    public Song getSongAt(int index) {
        return songs.elementAt(index);
    }

    public Vector<Song> getSongs() {
        return songs;
    }

    public void setSongs(Vector<Song> songs) {
        this.songs = songs;
        for (Song song : songs) song.setPlaylist(this);
    }

    public JTable getTable() {
        return table;
    }

    public Song getPlayedSong() {
        return playedSong;
    }

    public int getPlayedSongIndex() {
        return playedSongIndex;
    }

    public void next() {
        if (playedSongIndex < songs.size() - 1) {
            playedSongIndex++;
            play(playedSongIndex);
        }
    }

    public void prev() {
        if (playedSongIndex > 0) {
            playedSongIndex--;
            play(playedSongIndex);
        }
    }

    public void randomPlay() {
        if (songs.size() < 1) return;
        Random s1 = new Random();
        playedSongIndex = s1.nextInt(songs.size());
        play(playedSongIndex);
    }

    public void play(int index) {
        if (index == -1) index = 0;
        if (playedSong != null) playedSong.stop();
        if (index < 0 || index >= songs.size()) return;
        this.playedSongIndex = index;
        playedSong = getSongAt(index);
        playedSong.play();
    }


    public void setPlayedSong(Song playedSong) {
        this.playedSong = playedSong;
    }

    public int getSongCount() {
        return songs.size();
    }

    public String getName() {
        return name;
    }

    public JPanel getPanel() {
        return panel;
    }


    public Main getMain() {
        return main;
    }

    public void updatePlayedSongIndex() {
        playedSongIndex = (songs.indexOf(playedSong) == -1 ? 0 : songs.indexOf(playedSong));
    }

    public void removeDuplicates() {
        Vector<Song> songs1 = new Vector<>();
        for (Song song : songs) if (!songs1.contains(song)) songs1.addElement(song);
        this.songs = songs1;
    }

    public void removeDeadItems() {
        if (getSongCount() < 1) return;
        File checker;

        for (int i = 0; i < getSongCount(); i++) {
            checker = new File(songs.elementAt(i).getPath());
            if (!checker.exists()) songs.elementAt(i).setPath("");
        }
        for (int i = 0; i < getSongCount(); i++) if (songs.elementAt(i).getPath().equals("")) songs.removeElementAt(i);
    }
}