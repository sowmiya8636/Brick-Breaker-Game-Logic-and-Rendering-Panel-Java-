import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int level = 1;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310;

    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballXDir = -2;
    private int ballYDir = -3;

    private MapGenerator map;

    private boolean powerUpActive = false;
    private int powerUpDuration = 0;
    private Color ballColor = Color.yellow;

    public GamePanel() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 792, 692);

        // Drawing map
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 692);
        g.fillRect(0, 0, 792, 3);
        g.fillRect(791, 0, 3, 692);

        // Scores
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score + "  Level: " + level, 590, 30);

        // Paddle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        // Ball
        g.setColor(ballColor);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // Game Over
        if (ballPosY > 670) {
            play = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Scores: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        // Victory
        if (totalBricks == 0) {
            level++;
            play = false;
            ballXDir = 0;
            ballYDir = 0;
            g.setColor(Color.green);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Level Up!", 300, 300);

            map = new MapGenerator(3 + level, 7 + level);
            totalBricks = (3 + level) * (7 + level);
            ballPosX = 120;
            ballPosY = 350;
            ballXDir = -2 - (level - 1);
            ballYDir = -3 - (level - 1);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Continue", 250, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        if (play) {
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYDir = -ballYDir;
            }

            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.hitBrick(i, j);
                            totalBricks--;
                            score += 5;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }

                            // Activate power-up
                            if (Math.random() < 0.1) {
                                activatePowerUp();
                            }

                            break A;
                        }
                    }
                }
            }

            ballPosX += ballXDir;
            ballPosY += ballYDir;

            if (ballPosX < 0) {
                ballXDir = -ballXDir;
            }
            if (ballPosY < 0) {
                ballYDir = -ballYDir;
            }
            if (ballPosX > 770) {
                ballXDir = -ballXDir;
            }

            if (powerUpActive) {
                powerUpDuration++;
                if (powerUpDuration > 100) {
                    deactivatePowerUp();
                }
            }
        }

        repaint();
    }

    private void activatePowerUp() {
        powerUpActive = true;
        powerUpDuration = 0;
        ballColor = Color.red;
        ballXDir *= 2;
        ballYDir *= 2;
    }

    private void deactivatePowerUp() {
        powerUpActive = false;
        ballColor = Color.yellow;
        ballXDir /= 2;
        ballYDir /= 2;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 700) {
                playerX = 700;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballPosX = 120;
                ballPosY = 350;
                ballXDir = -2 - (level - 1);
                ballYDir = -3 - (level - 1);
                playerX = 310;
                score = 0;
                totalBricks = (3 + level) * (7 + level);
                map = new MapGenerator(3 + level, 7 + level);

                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}