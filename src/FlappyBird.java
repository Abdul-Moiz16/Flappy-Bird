import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardheight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    //Starting Position
    int birdX = boardWidth / 8;  // One-eighth of the board width
    int birdY = boardheight / 2; // Half of the board height
    int birdWidth = 34; // Width of the bird image
    int birdHeight = 24; // Height of the bird image

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image Img;

        Bird(Image img) {
            this.Img = img;
           
        }
    }
    //Pipes
    int pipeX = boardWidth; // Start pipes on right side of the board
    int pipeY = 0;  // Start form the top of the screen
    int pipeWidth = 64; // Scaled by 1/6
    int pipeHeight = 512; 

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image Img;
        boolean passed = false; // check if bird has passed the pipe

        Pipe(Image img) {
            this.Img = img;
        }
    }

    //Game logic
    Bird bird;
    int velocityX = -4; // Move pipes to the left speed (simulates bird moving right)
    int velocityY = 0;  // bird will fall down initially when the game starts
    int gravity = 1;    // Gravity pulls the bird down

    ArrayList<Pipe> pipes; // Store the pipes
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {

        // Simple Swing JPanel that defines the Flappy Bird game area and its preferred size
        setPreferredSize(new Dimension(boardWidth, boardheight));
        //setBackground(Color.cyan);
        setFocusable(true); // makes component eligible to receive keyboard events via key listener
        addKeyListener(this);

        //Load Images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        
        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() { // call place pipes evey 1.5 s
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }    
        });
        placePipesTimer.start();
 
        //game timer
        gameLoop = new Timer(1000/60, this);  // 1000 ms / 60 FPS = 16.67 ms
        gameLoop.start();
    }

    public void placePipes() {
        // (0-1) * pipeHeight / 2 -> (0-256)
        //128
        //0 - 128 - (0-256) --> 1/4 pipeHeight -> 3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardheight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y +  pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardheight, null);

        // bird
        g.drawImage(bird.Img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.Img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        if(!gameOver) {
            g.drawString("Score: " + (int)score, 10, 30);
        }else{
            g.drawString("Game Over: " + String.valueOf( (int) score), 10, 30);
        }
    }

    public void move() {
        //bird 
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y,0);  // Keep the bird within the upper bound, bird will not escape the screen

        //pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;  // 0.5 because there are 2 pipes! so 1 score for each set of pipes
            }

            if(collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if(bird.y > boardheight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&  // a top left corner doesnt reach b top right corner
               a.x + a.width > b.x &&  // a top right corner reaches b top left corner
               a.y < b.y + b.height &&  // a top left corner doesnt reach b bottom left corner
               a.y + a.height > b.y;    // a bottom left corner reaches b top left corner
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        move();
        // Repaint the panel to reflect changes
        repaint();
        if(gameOver) {
            gameLoop.stop();
            placePipesTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {  // if key pressed is space
            velocityY = -9; // Move the bird up by setting a negative velocity

            if(gameOver) {
                // Restart the game by reseting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }
        
    @Override
    public void keyReleased(KeyEvent e) {}
        
    @Override
    public void keyTyped(KeyEvent e) {}


}

