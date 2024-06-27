import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class BrickBreakerGame extends JPanel implements ActionListener, KeyListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Timer timer;
    private boolean playing;
    private int score;
    private int bricksLeft;

    private Paddle paddle;
    private Ball ball;
    private ArrayList<Brick> bricks;

    public BrickBreakerGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(5, this);
        playing = false;

        initializeGame();
    }

    private void initializeGame() {
        score = 0;
        bricksLeft = 0;

        paddle = new Paddle(WIDTH / 2 - Paddle.WIDTH / 2, HEIGHT - 50);
        ball = new Ball(WIDTH / 2, HEIGHT - 100, -1, -2);

        bricks = new ArrayList<>();
        generateBricks();
    }

    private void generateBricks() {
        int brickRows = 5;
        int brickCols = 10;
        int brickWidth = 75;
        int brickHeight = 20;
        int padding = 10;
        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN};

        for (int row = 0; row < brickRows; row++) {
            for (int col = 0; col < brickCols; col++) {
                int x = col * (brickWidth + padding) + padding;
                int y = row * (brickHeight + padding) + padding + 50;
                Color color = colors[row];
                bricks.add(new Brick(x, y, brickWidth, brickHeight, color));
                bricksLeft++;
            }
        }
    }

  
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (playing) {
            paddle.draw(g);
            ball.draw(g);

            for (Brick brick : bricks) {
                brick.draw(g);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 20, 30);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Brick Breaker", WIDTH / 2 - 150, HEIGHT / 2 - 100);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press Enter to Start", WIDTH / 2 - 100, HEIGHT / 2);
        }
    }

    public void startGame() {
        playing = true;
        timer.start();
    }

  
    public void actionPerformed(ActionEvent e) {
        if (playing) {
            paddle.move();
            ball.move();

            checkPaddleCollision();
            checkBrickCollision();
            checkWallCollision();
            checkGameOver();

            repaint();
        }
    }

    private void checkPaddleCollision() {
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.reverseYDirection();
        }
    }

    private void checkBrickCollision() {
        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            if (ball.getBounds().intersects(brick.getBounds())) {
                bricks.remove(brick);
                ball.reverseYDirection();
                score += 10;
                bricksLeft--;
                break;
            }
        }
    }

    private void checkWallCollision() {
        if (ball.getX() <= 0 || ball.getX() >= WIDTH - Ball.DIAMETER) {
            ball.reverseXDirection();
        }
        if (ball.getY() <= 0) {
            ball.reverseYDirection();
        }
    }

    private void checkGameOver() {
        if (ball.getY() >= HEIGHT) {
            playing = false;
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score, "Game Over", JOptionPane.PLAIN_MESSAGE);
            initializeGame();
        } else if (bricksLeft == 0) {
            playing = false;
            timer.stop();
            JOptionPane.showMessageDialog(this, "Congratulations! You won! Your score: " + score, "You Won!", JOptionPane.PLAIN_MESSAGE);
            initializeGame();
        }
    }

  
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            paddle.setLeft(true);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            paddle.setRight(true);
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (!playing) {
                startGame();
            }
        }
    }

  
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            paddle.setLeft(false);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            paddle.setRight(false);
        }
    }

  
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker Game");
        BrickBreakerGame game = new BrickBreakerGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class Paddle {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 10;
    private static final int SPEED = 10;

    private int x, y;
    private boolean left, right;

    public Paddle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

    public void move() {
        if (left && x > 0) {
            x -= SPEED;
        }
        if (right && x < BrickBreakerGame.WIDTH - WIDTH) {
            x += SPEED;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}

class Ball {

    public static final int DIAMETER = 20;
    private int x, y;
    private int xSpeed, ySpeed;

    public Ball(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, DIAMETER, DIAMETER);
    }

    public void move() {
        x += xSpeed;
        y += ySpeed;
    }

    public void reverseXDirection() {
        xSpeed = -xSpeed;
    }

    public void reverseYDirection() {
        ySpeed = -ySpeed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, DIAMETER, DIAMETER);
    }
}

class Brick {

    private int x, y, width, height;
    private Color color;

    public Brick(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
