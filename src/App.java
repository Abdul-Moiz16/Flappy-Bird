import javax.swing.*;


public class App {
    public static void main(String[] args) throws Exception {

        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);                // Set the size of the window
        frame.setLocationRelativeTo(null);                   // Place the window at center of the screen
        frame.setResizable(false);                   // Prevent the window from being resized
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit the application when the window is closed
        
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);     // Add the FlappyBird panel to the frame
        frame.pack();              // sizes frame to match panel.getPreferredSize()
        flappyBird.requestFocus(); // Request focus for key events
        frame.setVisible(true);  // Make the window visible
    
    } 
}
