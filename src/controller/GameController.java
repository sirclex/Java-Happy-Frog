package controller;

import java.awt.Image;
import view.GameView;
import model.Key;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Contain methods to control the game.
 *
 * @author letha
 */
public class GameController {

    private final static int FRAME_WIDTH = 600;
    private final static int FRAME_HEIGHT = 450;
    private final static int PANEL_WIDTH = 580;
    private final static int PANEL_HEIGHT = 350;

    private final static int PIPE_MIN_HEIGHT = 80;
    private final static int PIPE_GAP = 100;
    private final static int PIPE_WIDTH = 40;
    private final static int PIPE_DISTANCE = 150;

    private final static int FROG_SIZE = 40;

    private GameView view;
    private JLabel lbFrog;
    private Timer timer;
    private List<JButton> pipes;
    private int yFrog;
    private int point;
    private int passedDistance;
    private Key key;
    private boolean save;
    private boolean pause;
    private boolean firstTime;

    /**
     * Create new instance of GameController.
     *
     * @param view game interface. <Code>GameView</Code>
     */
    public GameController(GameView view) {
        this.view = view;
        initialize();
        run();
    }

    /**
     * Initialize default variables and add component to game panel.
     */
    public void initialize() {
        this.lbFrog = new JLabel();
        this.pipes = new ArrayList<>();
        this.key = new Key();
        this.yFrog = 155;
        this.point = 0;
        this.passedDistance = 119;
        this.save = false;
        this.pause = false;
        this.firstTime = true;

        this.lbFrog.addKeyListener(key);
        this.lbFrog.setSize(FROG_SIZE, FROG_SIZE);
        this.lbFrog.setLocation(60, yFrog);

        try {
            Image icon = ImageIO.read(getClass().getResource("../model/frog.png"));
            this.lbFrog.setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Can not set the frog appearance!");
        }
        this.view.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.view.setResizable(false);
        this.view.getBtPause().addKeyListener(key);
        this.view.getBtSave().addKeyListener(key);
        this.view.getPnScreen().add(lbFrog);
    }

    /**
     * Handler save button.
     */
    public void save() {
        save = true;
        try {
            FileWriter fw = new FileWriter("data.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (JButton btn : pipes) {
                int x = btn.getX();
                int y = btn.getY();
                int width = btn.getWidth();
                int height = btn.getHeight();
                bw.write(x + ";" + y + ";" + width + ";" + height);
                bw.newLine();
            }
            bw.write(passedDistance + ";" + point + ";" + yFrog);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Save failed!");
        }
    }

    /**
     * Check if frog is intersected with panel or column.
     *
     * @return <Code>boolean</Code> datatype
     */
    public boolean isIntersect() {
        if (lbFrog.getY() <= 0 || lbFrog.getY() >= (PANEL_HEIGHT - FROG_SIZE)) {
            return true;
        }
        Rectangle bf = new Rectangle(lbFrog.getX(), lbFrog.getY(), lbFrog.getWidth(), lbFrog.getHeight());
        for (JButton btn : pipes) {
            Rectangle b = new Rectangle(btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight());
            if (bf.intersects(b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add Pipe, <Code>JButton</Code> object, into game panel.
     */
    public void addPipe() {
        Random r = new Random();
        JButton topPipe = new JButton();
        int range = PANEL_HEIGHT - (2 * PIPE_MIN_HEIGHT) - PIPE_GAP;
        int hup = r.nextInt(range) + PIPE_MIN_HEIGHT;
        topPipe.setBounds(PANEL_WIDTH, 0, PIPE_WIDTH, hup);
        JButton botPipe = new JButton();

        int hdown = PANEL_HEIGHT - PIPE_GAP - hup;
        botPipe.setBounds(PANEL_WIDTH, PANEL_HEIGHT - hdown, PIPE_WIDTH, hdown);

        topPipe.addKeyListener(key);
        botPipe.addKeyListener(key);

        pipes.add(topPipe);
        pipes.add(botPipe);
        view.getPnScreen().add(topPipe);
        view.getPnScreen().add(botPipe);
    }

    /**
     * Restart game, set variables to default.
     */
    public void restartGame() {
        view.getPnScreen().removeAll();
        view.getPnScreen().repaint();
        pipes.clear();
        point = 0;
        passedDistance = 119;
        view.getLbPoint().setText("Point: " + point);
        yFrog = 40;
        view.getPnScreen().add(lbFrog);
        timer.restart();
    }

    /**
     * Get medal according to point.
     *
     * @return <Code>String</Code> datatype
     */
    public String getMedals() {
        int finalPoint = point / 2;
        if (finalPoint < 10) {
            return "no medal";
        } else if (finalPoint >= 10 && finalPoint < 20) {
            return "a Bronze Medal";
        } else if (finalPoint >= 20 && finalPoint < 30) {
            return "a Silver Medal";
        } else if (finalPoint >= 30 && finalPoint < 40) {
            return "a Gold Medal";
        } else {
            return "a Platinum Medal";
        }
    }

    /**
     * End the game and show message dialog depending on save. No save, then new
     * game. Save, then continue.
     */
    public void endGame() {
        if (save == false) {
            Object mes[] = {"New Game", "Exit"};
            String content = "You got " + getMedals() + ".\nDo you want to continue?";
            int option = JOptionPane.showOptionDialog(null, content,
                    "Notice!",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, mes, mes[0]);
            if (option == 0) {
                restartGame();
            } else if (option == 1 || option == -1) {
                System.exit(0);
            }
        } else {
            Object mes[] = {"New Game", "Continue", "Exit"};
            String content = "You got " + getMedals() + ".\nDo you want to continue?";
            int option = JOptionPane.showOptionDialog(null, content,
                    "Notice!",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, mes, mes[0]);
            if (option == 0) {
                restartGame();
            }
            if (option == 1) {
                view.getPnScreen().removeAll();
                view.getPnScreen().repaint();
                pipes.clear();
                try {
                    FileReader fr = new FileReader("data.txt");
                    BufferedReader br = new BufferedReader(fr);
                    String line = "";
                    do {
                        line = br.readLine().trim();
                        if (line == null) {
                            break;
                        }
                        JButton btn = new JButton();
                        String txt[] = line.split(";");
                        int arr[] = new int[txt.length];
                        for (int i = 0; i < arr.length; i++) {
                            arr[i] = Integer.parseInt(txt[i]);
                        }
                        if (arr.length == 4) {
                            btn.setBounds(arr[0], arr[1], arr[2], arr[3]);
                            pipes.add(btn);
                            view.getPnScreen().add(btn);
                        } else {
                            passedDistance = arr[0];
                            point = arr[1];
                            view.getLbPoint().setText("Point: " + point / 2);
                            yFrog = arr[2];
                        }
                    } while (true);
                    br.close();
                } catch (Exception e) {
                }
                view.getPnScreen().add(lbFrog);
                timer.restart();
            }
            if (option == 2 || option == -1) {
                System.exit(0);
            }
        }
    }

    /**
     * Handle pause button.
     */
    public void pause() {
        if (pause == false) {
            timer.stop();
            pause = true;
            view.getBtPause().setText("Continue");
        } else {
            timer.restart();
            pause = false;
            view.getBtPause().setText("Pause");
        }
    }

    /**
     * Add point for each passed column.
     */
    public void addPoint() {
        for (JButton btn : pipes) {
            if (lbFrog.getX() == btn.getX()) {
                point++;
                view.getLbPoint().setText("Point: " + point / 2);
            }
        }
    }

    /**
     * Thread to make game running. Column moving and frog falling.
     */
    public void run() {
        timer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (firstTime) {
                    if (key.isPress()) {
                        yFrog = yFrog - 30;
                        key.setPress(false);
                        firstTime = false;
                    }
                } else {
                    if (key.isPress()) {
                        yFrog = yFrog - 20;
                        key.setPress(false);
                    }
                    yFrog++;
                    lbFrog.setBounds(60, yFrog, 40, 40);
                    for (int i = 0; i < pipes.size(); i++) {
                        int x = pipes.get(i).getBounds().x - 1;
                        int y = pipes.get(i).getBounds().y;
                        pipes.get(i).setLocation(x, y);
                        if (x <= -40) {
                            pipes.remove(i);
                            i--;
                        }
                    }
                    addPoint();
                    passedDistance++;
                    if (passedDistance == PIPE_DISTANCE) {
                        addPipe();
                        passedDistance = 0;
                    }
                    if (isIntersect()) {
                        timer.stop();
                        endGame();
                    }
                }
            }
        });
        timer.start();
    }

    /**
     * Get the game interface.
     *
     * @return object of <Code>GameView</Code>
     */
    public GameView getView() {
        return view;
    }

    /**
     * Set the game interface.
     *
     * @param view object of <Code>GameView</Code>
     */
    public void setView(GameView view) {
        this.view = view;
    }

    /**
     * Get the frog.
     *
     * @return object of <Code>JLabel</Code>
     */
    public JLabel getLbFrog() {
        return lbFrog;
    }

    /**
     * Set the frog.
     *
     * @param btFrog object of <Code>JLabel</Code>
     */
    public void setLbFrog(JLabel btFrog) {
        this.lbFrog = btFrog;
    }

    /**
     * Get the thread that make game run.
     *
     * @return object of <Code>Timer</Code>
     */
    public Timer getTimer() {
        return timer;
    }

    /**
     * Set the thread that make game run.
     *
     * @param timer object of <Code>Timer</Code>
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * Get the list of pipe.
     *
     * @return object of <Code>List</Code>
     */
    public List<JButton> getPipes() {
        return pipes;
    }

    /**
     * Set the list of pipe.
     *
     * @param pipes object of <Code>List</Code>
     */
    public void setPipes(List<JButton> pipes) {
        this.pipes = pipes;
    }

    /**
     * Get y coordinates of frog.
     *
     * @return <Code>int</Code> datatype
     */
    public int getyFrog() {
        return yFrog;
    }

    /**
     * Set y coordinates of frog.
     *
     * @param yFrog <Code>int</Code> datatype
     */
    public void setyFrog(int yFrog) {
        this.yFrog = yFrog;
    }

    /**
     * Get the game point.
     *
     * @return <Code>int</Code> datatype
     */
    public int getPoint() {
        return point;
    }

    /**
     * Set the game point.
     *
     * @param point <Code>int</Code> datatype
     */
    public void setPoint(int point) {
        this.point = point;
    }

    /**
     * Get the distance that frog passed.
     *
     * @return <Code>int</Code> datatype
     */
    public int getPassedDistance() {
        return passedDistance;
    }

    /**
     * Set the distance that frog passed.
     *
     * @param passedDistance <Code>int</Code> datatype
     */
    public void setPassedDistance(int passedDistance) {
        this.passedDistance = passedDistance;
    }

    /**
     * Get the key listener.
     *
     * @return object of <Code>Key</Code>
     */
    public Key getKey() {
        return key;
    }

    /**
     * Set the key listener.
     *
     * @param key object of <Code>Key</Code>
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Check if this is first time game run.
     *
     * @return <Code>boolean</Code> datatype
     */
    public boolean isFirstTime() {
        return firstTime;
    }

    /**
     * Set the first time game run.
     *
     * @param firstTime <Code>boolean</Code> datatype
     */
    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    /**
     * Check if user want to save.
     *
     * @return <Code>boolean</Code> datatype
     */
    public boolean isSave() {
        return save;
    }

    /**
     * Set save status.
     *
     * @param save <Code>boolean</Code> datatype
     */
    public void setSave(boolean save) {
        this.save = save;
    }

    /**
     * Check if user want to pause.
     *
     * @return <Code>boolean</Code> datatype
     */
    public boolean isPause() {
        return pause;
    }

    /**
     * Set pause status.
     *
     * @param pause <Code>boolean</Code> datatype
     */
    public void setPause(boolean pause) {
        this.pause = pause;
    }

}
