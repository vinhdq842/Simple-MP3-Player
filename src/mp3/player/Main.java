package mp3.player;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Collections;
import java.util.Vector;


/**
 * An application for playing mp3
 * Created on 26/12/2019
 * Based on the idea of the same app named <b>Foobar2000</b> by <i>Peter Pawlowski</i>
 *
 * @author ServantOfEvil
 * Contact me: quangvinh0842@gmail.com
 * Any contribution is appreciated!
 */

public class Main implements ActionListener {

    /**
     * The main framework of the app
     */
    private JFrame frame;

    /**
     * The functions of the <b>File</b> option
     */
    private JMenuItem file_open;
    private JMenuItem file_addFiles;
    private JMenuItem file_addFolder;
    private JMenuItem file_newPlaylist;
    private JMenuItem file_loadPlaylist;
    private JMenuItem file_savePlaylist;
    private JMenuItem file_exit;

    /**
     * The functions of the <b>Edit</b> option
     */
    private JMenuItem edit_clear;
    private JMenuItem edit_sort_reverse;
    private JMenuItem edit_sort_byArtist;
    private JMenuItem edit_sort_byAlbum;
    private JMenuItem edit_sort_byTitle;
    private JMenuItem edit_sort_byTrackNum;
    private JMenuItem edit_rmDeadItems;
    private JMenuItem edit_rmDuplicates;

    /**
     * The functions of the <b>Playback</b> option
     */
    private JMenuItem pb_play;
    private JMenuItem pb_stop;
    private JMenuItem pb_pause;
    private JMenuItem pb_next;
    private JMenuItem pb_prev;
    private JMenuItem pb_random;
    private JCheckBoxMenuItem pb_stopAfterCurrent;
    private JMenuItem pb_order_default;
    private JMenuItem pb_order_rpTrack;
    private JMenuItem pb_order_rpPL;
    private JMenuItem pb_order_random;

    /**
     * About the app
     */
    private JMenuItem help_about;

    /**
     * The main functions
     */
    private JButton btnPlay;
    private JButton btnPause;
    private JButton btnStop;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnPlayBackRandom;

    /**
     * The playing progress
     */
    public static JSlider sliderProgress;

    /**
     * contains all current playlists. Each playlist is represented by a Tab.
     */
    private Vector<Playlist> playlists;

    private JTabbedPane tabbedPane;

    private ImageIcon icnPlay, icnPause;

    /**
     * The current played song
     */
    private static Song beingPlayedSong;

    /**
     * The order by which the app obeys
     */
    private Order order = Order.DEFAULT;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main window = new Main();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        try {

            // setup look and feel scheme
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     *
     * @wbp.parser.entryPoint
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Simple MP3 Player");
        frame.setIconImage(new ImageIcon(getClass().getResource("/Icon/icon.png")).getImage());

        playlists = new Vector<>();

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        file_open = new JMenuItem("Open...");
        mnFile.add(file_open);
        file_open.addActionListener(this);

        file_addFiles = new JMenuItem("Add files...");
        mnFile.add(file_addFiles);
        file_addFiles.addActionListener(this);

        file_addFolder = new JMenuItem("Add folder...");
        mnFile.add(file_addFolder);
        file_addFolder.addActionListener(this);

        file_newPlaylist = new JMenuItem("New playlist");
        mnFile.add(file_newPlaylist);
        file_newPlaylist.addActionListener(this);

        file_loadPlaylist = new JMenuItem("Load playlist...");
        mnFile.add(file_loadPlaylist);
        file_loadPlaylist.addActionListener(this);

        file_savePlaylist = new JMenuItem("Save playlist...");
        mnFile.add(file_savePlaylist);
        file_savePlaylist.addActionListener(this);

        file_exit = new JMenuItem("Exit");
        mnFile.add(file_exit);
        file_exit.addActionListener(this);

        JMenu mnNewMenu = new JMenu("Edit");
        menuBar.add(mnNewMenu);

        edit_clear = new JMenuItem("Clear");
        mnNewMenu.add(edit_clear);
        edit_clear.addActionListener(this);

        JMenu edit_sort = new JMenu("Sort");
        mnNewMenu.add(edit_sort);

        edit_sort_reverse = new JMenuItem("Reverse");
        edit_sort.add(edit_sort_reverse);
        edit_sort_reverse.addActionListener(this);

        edit_sort_byArtist = new JMenuItem("By Artist");
        edit_sort.add(edit_sort_byArtist);
        edit_sort_byArtist.addActionListener(this);

        edit_sort_byAlbum = new JMenuItem("By Album");
        edit_sort.add(edit_sort_byAlbum);
        edit_sort_byAlbum.addActionListener(this);

        edit_sort_byTitle = new JMenuItem("By Title");
        edit_sort.add(edit_sort_byTitle);
        edit_sort_byTitle.addActionListener(this);

        edit_sort_byTrackNum = new JMenuItem("By Track Number");
        edit_sort.add(edit_sort_byTrackNum);
        edit_sort_byTrackNum.addActionListener(this);

        edit_rmDuplicates = new JMenuItem("Remove duplicates");
        mnNewMenu.add(edit_rmDuplicates);
        edit_rmDuplicates.addActionListener(this);

        edit_rmDeadItems = new JMenuItem("Remove dead items");
        mnNewMenu.add(edit_rmDeadItems);
        edit_rmDeadItems.addActionListener(this);

        JMenu mnPlayback = new JMenu("Playback");
        menuBar.add(mnPlayback);

        pb_stop = new JMenuItem("Stop");
        mnPlayback.add(pb_stop);
        pb_stop.addActionListener(this);

        pb_pause = new JMenuItem("Pause");
        mnPlayback.add(pb_pause);
        pb_pause.addActionListener(this);

        pb_play = new JMenuItem("Play");
        mnPlayback.add(pb_play);
        pb_play.addActionListener(this);

        pb_prev = new JMenuItem("Previous");
        mnPlayback.add(pb_prev);
        pb_prev.addActionListener(this);

        pb_next = new JMenuItem("Next");
        mnPlayback.add(pb_next);
        pb_next.addActionListener(this);

        pb_random = new JMenuItem("Random");
        mnPlayback.add(pb_random);
        pb_random.addActionListener(this);

        JMenu mnOrder = new JMenu("Order");
        mnPlayback.add(mnOrder);

        pb_order_default = new JRadioButtonMenuItem("Default");
        mnOrder.add(pb_order_default);
        pb_order_default.addActionListener(this);
        pb_order_default.setSelected(true);

        pb_order_rpPL = new JRadioButtonMenuItem("Repeat (playlist)");
        mnOrder.add(pb_order_rpPL);
        pb_order_rpPL.addActionListener(this);

        pb_order_rpTrack = new JRadioButtonMenuItem("Repeat (track)");
        mnOrder.add(pb_order_rpTrack);
        pb_order_rpTrack.addActionListener(this);

        pb_order_random = new JRadioButtonMenuItem("Random");
        mnOrder.add(pb_order_random);
        pb_order_random.addActionListener(this);

        ButtonGroup buttonGroupOrder = new ButtonGroup();
        buttonGroupOrder.add(pb_order_default);
        buttonGroupOrder.add(pb_order_rpPL);
        buttonGroupOrder.add(pb_order_rpTrack);
        buttonGroupOrder.add(pb_order_random);

        pb_stopAfterCurrent = new JCheckBoxMenuItem(" Stop after current");
        mnPlayback.add(pb_stopAfterCurrent);

        JMenu mnNewMenu_2 = new JMenu("Help");
        menuBar.add(mnNewMenu_2);

        help_about = new JMenuItem("About");
        mnNewMenu_2.add(help_about);
        help_about.addActionListener(this);

        btnStop = new JButton("");
        btnStop.setIcon(new ImageIcon(Main.class.getResource("/Icon/stop.png")));
        btnStop.addActionListener(this);
        menuBar.add(btnStop);

        btnPlay = new JButton("");
        btnPlay.setIcon(new ImageIcon(Main.class.getResource("/Icon/play.png")));
        btnPlay.addActionListener(this);
        menuBar.add(btnPlay);

        btnPause = new JButton("");
        btnPause.setIcon(new ImageIcon(Main.class.getResource("/Icon/pause.png")));
        btnPause.addActionListener(this);
        menuBar.add(btnPause);

        btnPrev = new JButton("");
        btnPrev.setIcon(new ImageIcon(Main.class.getResource("/Icon/prev.png")));
        btnPrev.addActionListener(this);
        menuBar.add(btnPrev);

        btnNext = new JButton("");
        btnNext.setIcon(new ImageIcon(Main.class.getResource("/Icon/next.png")));
        btnNext.addActionListener(this);
        menuBar.add(btnNext);

        btnPlayBackRandom = new JButton("");
        btnPlayBackRandom.setIcon(new ImageIcon(Main.class.getResource("/Icon/random.png")));
        btnPlayBackRandom.addActionListener(this);
        menuBar.add(btnPlayBackRandom);

        sliderProgress = new JSlider(0, 100, 0);
        sliderProgress.setSize(5, 5);
        sliderProgress.setEnabled(false);
        menuBar.add(sliderProgress);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addChangeListener(e -> {
            beingPlayedSong = playlists.get(tabbedPane.getSelectedIndex()).getPlayedSong();
            if (beingPlayedSong != null) sliderProgress.setValue(beingPlayedSong.getProgress());
            else sliderProgress.setValue(0);
        });
        frame.getContentPane().add(tabbedPane, BorderLayout.NORTH);
        initTabs();
        icnPause = new ImageIcon(getClass().getResource("/Icon/pause.png"));
        icnPlay = new ImageIcon(getClass().getResource("/Icon/play.png"));
    }

    /**
     * adds a new tab to the app.
     *
     * @param title the title of the tab to be added.
     */
    private void addTab(String title) {
        title = title.trim().equals("") ? "New Playlist " + tabbedPane.getTabCount() : title;
        Playlist playlist;
        playlists.add(playlist = new Playlist(title, this));
        tabbedPane.addTab(playlist.getName(), null, playlist.getPanel(), null);
    }

    /**
     * adds a new tab from a playlist.
     *
     * @param p the playlist from which a new playlist is created.
     */
    private void addTab(Playlist p) {
        Playlist playlist;
        playlists.add(playlist = new Playlist(p.getName(), this));
        tabbedPane.addTab(playlist.getName(), null, playlist.getPanel(), null);
        playlist.setSongs(p.getSongs());
        updatePlaylist(playlist);
    }

    /** adds a tab to the framework by default */
    private void initTabs() {
        addTab("Default");
    }

    /** finds all *.mp3 files in a folder and its sub-folders then add to the playlist.
     *
     * @param file the file from which the app finds *.mp3 files
     * @param playlist the playlist to add Songs found to
     */
    private void findAndAdd(File file, Playlist playlist) {
        if (!file.isDirectory()) {
            if (file.getName().endsWith(".mp3")) playlist.add(new Song(file, playlist));
        } else {
            File[] files = file.listFiles();
            assert files != null;
            for (File file1 : files) findAndAdd(file1, playlist);
        }
    }

    /**
     * updates a Tab based on the provided playlist.
     */
    private void updatePlaylist(Playlist playlist) {
        ((DefaultTableModel) playlist.getTable().getModel()).setRowCount(0);
        for (int i = 0; i < playlist.getSongCount(); i++) {
            Song song = playlist.getSongAt(i);
            ((DefaultTableModel) playlist.getTable().getModel()).addRow(new Object[]{null, song.getArtist_album(), song.getTrack_no(), song.getTitle_trackArtist(), song.getDuration(), null});
        }
    }

    /**
     * handles actions from the user.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTable currentJTable = (JTable) ((JScrollPane) ((JPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex())).getComponent(0)).getViewport().getView();
        DefaultTableModel tableModel = (DefaultTableModel) currentJTable.getModel();
        Playlist currentPlaylist = playlists.elementAt(tabbedPane.getSelectedIndex());
        int index = currentJTable.getSelectedRow();

        if (e != null) {
            if (e.getSource() instanceof JMenuItem) {
                JMenuItem action = (JMenuItem) e.getSource();
                if (action == file_open) {
                    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        Song song = new Song(fileChooser.getSelectedFile(), currentPlaylist);
                        currentPlaylist.add(song);
                        tableModel.addRow(new Object[]{null, song.getArtist_album(), song.getTrack_no(), song.getTitle_trackArtist(), song.getDuration(), null});
                    }
                } else if (action == file_addFiles) {
                    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    fileChooser.setMultiSelectionEnabled(true);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File[] files = fileChooser.getSelectedFiles();
                        for (File file : files) {
                            Song song = new Song(file, currentPlaylist);
                            currentPlaylist.add(song);
                            tableModel.addRow(new Object[]{null, song.getArtist_album(), song.getTrack_no(), song.getTitle_trackArtist(), song.getDuration(), null});
                        }
                    }
                } else if (action == file_addFolder) {
                    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        findAndAdd(file, currentPlaylist);
                        updatePlaylist(currentPlaylist);
                    }
                } else if (action == file_newPlaylist) {
                    addTab(JOptionPane.showInputDialog("Enter the Playlist's name:"));
                } else if (action == file_loadPlaylist) {
                    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    fileChooser.setMultiSelectionEnabled(true);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        if (!file.getName().endsWith(".soe")) return;
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            ObjectInputStream ois = new ObjectInputStream(fis);
                            Playlist p = (Playlist) ois.readObject();
                            addTab(p);
                            ois.close();
                            fis.close();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error loading playlist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (action == file_savePlaylist) {
                    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    fileChooser.setMultiSelectionEnabled(true);
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + "/" + currentPlaylist.getName() + ".soe");
                        try {
                            if (file.exists()) file.delete();
                            FileOutputStream fos = new FileOutputStream(file);
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(currentPlaylist);
                            oos.close();
                            fos.close();
                            JOptionPane.showMessageDialog(null, "Successfully saved: " + file.getAbsolutePath());
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "Error saving playlist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    }
                } else if (action == file_exit) {
                    System.exit(0);
                } else if (action == pb_stop) {
                    e.setSource(btnStop);
                    actionPerformed(e);
                } else if (action == pb_play) {
                    e.setSource(btnPlay);
                    actionPerformed(e);
                } else if (action == pb_pause) {
                    e.setSource(btnPause);
                    actionPerformed(e);
                } else if (action == pb_prev) {
                    e.setSource(btnPrev);
                    actionPerformed(e);
                } else if (action == pb_next) {
                    e.setSource(btnNext);
                    actionPerformed(e);
                } else if (action == pb_random) {
                    e.setSource(btnPlayBackRandom);
                    actionPerformed(e);
                } else if (action == help_about) {
                    JOptionPane.showMessageDialog(frame, "---Simple MP3 Player---\n Developed by:\n Đặng Quang Vinh(ServantOfEvil)\n Ngô Quang Vinh\n Nguyễn Công Hậu", "About", JOptionPane.INFORMATION_MESSAGE);
                } else if (action == pb_order_default) {
                    order = Order.DEFAULT;
                } else if (action == pb_order_rpPL) {
                    order = Order.RP_PLAYLIST;
                } else if (action == pb_order_rpTrack) {
                    order = Order.RP_TRACK;
                } else if (action == pb_order_random) {
                    order = Order.RANDOM;
                } else if (action == edit_clear) {
                    e.setSource(btnStop);
                    actionPerformed(e);
                    currentPlaylist.getSongs().removeAllElements();
                    updatePlaylist(currentPlaylist);
                } else if (action == edit_sort_byArtist) {
                    currentPlaylist.getSongs().sort(new Sort(Type.BY_ARTIST));
                    updatePlaylist(currentPlaylist);
                    currentPlaylist.updatePlayedSongIndex();
                } else if (action == edit_sort_byAlbum) {
                    currentPlaylist.getSongs().sort(new Sort(Type.BY_ALBUM));
                    updatePlaylist(currentPlaylist);
                    currentPlaylist.updatePlayedSongIndex();
                } else if (action == edit_sort_byTitle) {
                    currentPlaylist.getSongs().sort(new Sort(Type.BY_TITLE));
                    updatePlaylist(currentPlaylist);
                    currentPlaylist.updatePlayedSongIndex();
                } else if (action == edit_sort_byTrackNum) {
                    currentPlaylist.getSongs().sort(new Sort(Type.BY_TRACK_NUM));
                    updatePlaylist(currentPlaylist);
                    currentPlaylist.updatePlayedSongIndex();
                } else if (action == edit_sort_reverse) {
                    Collections.reverse(currentPlaylist.getSongs());
                    updatePlaylist(currentPlaylist);
                    currentPlaylist.updatePlayedSongIndex();
                } else if (action == edit_rmDuplicates) {
                    currentPlaylist.removeDuplicates();
                    updatePlaylist(currentPlaylist);
                    currentPlaylist.updatePlayedSongIndex();
                } else if (action == edit_rmDeadItems) {
                    currentPlaylist.removeDeadItems();
                    updatePlaylist(currentPlaylist);
                    currentPlaylist.updatePlayedSongIndex();
                }
            } else if (e.getSource() instanceof JButton) {
                JButton action = (JButton) e.getSource();

                if (action == btnPlay) {
                    currentPlaylist.play(index);
                } else if (action == btnNext) {
                    currentPlaylist.next();
                } else if (action == btnPrev) {
                    currentPlaylist.prev();
                } else if (action == btnPlayBackRandom) {
                    currentPlaylist.randomPlay();
                } else if (action == btnPause) {
                    if (currentPlaylist.getPlayedSong() != null) {
                        if (currentPlaylist.getPlayedSong().isPlaying()) {
                            currentPlaylist.getPlayedSong().pause();
                        } else {
                            currentPlaylist.getPlayedSong().resume();
                        }
                    }
                } else if (action == btnStop) {
                    if (currentPlaylist.getPlayedSong() != null) {
                        currentPlaylist.getPlayedSong().stop();
                        currentPlaylist.setPlayedSong(null);
                    }
                }

            }
        }

        // marks the played song in its table
        currentJTable.getSelectionModel().setSelectionInterval(currentPlaylist.getPlayedSongIndex(), currentPlaylist.getPlayedSongIndex());
        for (int i = 0; i < tableModel.getRowCount(); i++) tableModel.setValueAt(null, i, 0);
        if (currentPlaylist.getSongCount() > 0) {
            if (currentPlaylist.getPlayedSong() != null) {
                if (currentPlaylist.getPlayedSong().isPlaying())
                    tableModel.setValueAt(icnPlay, currentPlaylist.getPlayedSongIndex(), 0);
                else tableModel.setValueAt(icnPause, currentPlaylist.getPlayedSongIndex(), 0);
            } else tableModel.setValueAt(null, currentPlaylist.getPlayedSongIndex(), 0);
        }
        beingPlayedSong = currentPlaylist.getPlayedSong();

        // sets the progress of the played song
        if (beingPlayedSong != null) sliderProgress.setValue(beingPlayedSong.getProgress());
        else sliderProgress.setValue(0);
    }

    public static Song getBeingPlayedSong() {
        return beingPlayedSong;
    }

    public Order getOrder() {
        return order;
    }

    public boolean stopAfterCurrent() {
        return pb_stopAfterCurrent.isSelected();
    }
}

enum Order {
    DEFAULT, RP_PLAYLIST, RP_TRACK, RANDOM
}