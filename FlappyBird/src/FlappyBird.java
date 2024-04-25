/*
    Saya Tattha Maharany Yasmin Akbar dengan NIM 2201805 mengerjakan soal Latihan 7
    dalam Praktikum mata kuliah Desain dan Pemrograman Berbasis Objek, untuk keberkahan-Nya
    maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamin.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private int score = 0;
    private JLabel gameOverLabel;

    int frameWidth = 360;
    int frameHeight = 640;

    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;

    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;
    Player player;

    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer pipesCooldown;
    int gravity = 1;

    public FlappyBird(){
        // Tampilkan background game terlebih dahulu
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        gameOverLabel = new JLabel();

        // Tampilkan dialog pilihan saat aplikasi dimulai
        int choice = JOptionPane.showOptionDialog(null, "Welcome to Flappy Bird!\nWould you like to start the game?", "Start Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(choice == JOptionPane.YES_OPTION) {
            // Jika pemain memilih untuk memulai permainan, panggil metode startGame
            startGame();
        } else {
            // Jika tidak, keluar dari aplikasi
            System.exit(0);
        }
    }

    // Method untuk memulai permainan
    private void startGame() {
        // Setup game setelah pemain memilih untuk memulai
        setFocusable(true);
        addKeyListener(this);

        birdImage = new ImageIcon(getClass().getResource("assets/bintang.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        gameOverLabel = new JLabel();
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gameOverLabel.setForeground(Color.WHITE);

        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        pipesCooldown.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
        paintScore(g);
    }

    private void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);

        g.drawImage(player.getImage(), player.getPosX(), player.getPosY(), player.getWidth(), player.getHeight(), null);

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }
    }

    public void move(){
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

            // Deteksi tabrakan dan burung jatuh
            if ((player.getPosX() <= pipe.getPosX() + pipe.getWidth() &&
                    player.getPosX() + player.getWidth() >= pipe.getPosX() &&
                    player.getPosY() <= pipe.getPosY() + pipe.getHeight() &&
                    player.getPosY() + player.getHeight() >= pipe.getPosY()) || player.getPosY() >= frameHeight) {
                // Tabrakan terjadi/burung jatuh, hentikan permainan
                gameLoop.stop();
                // Tampilkan pesan bahwa permainan berakhir
                gameOver();
            }
            else if (!pipe.isPassed() && pipe.getPosX() < player.getPosX()) { // jika berhasil melewati pipa
                pipe.setPassed(true);
                increaseScore(); // tambahkan skor
            }
        }
    }

    public void placePipes(){
        int randomPipePosY = (int) (pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = frameHeight/3;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPipePosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPipePosY + pipeHeight + openingSpace, pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    @Override
    public void keyReleased(KeyEvent e){

    }

    @Override
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_SPACE){ // untuk move
            player.setVelocityY(-10);
        }
        else if(e.getKeyCode() == KeyEvent.VK_R){ // untuk restart
            restartGame();
        }
    }

    // Method untuk restart game
    private void restartGame() {
        // Reset posisi pemain
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);

        // Hapus semua pipa
        pipes.clear();
        resetScore();
        // Mulai kembali penempatan pipa
        pipesCooldown.restart();
        gameLoop.start();

        // Sembunyikan pesan game over
        gameOverLabel.setVisible(false);
        // Hapus label dari panel
        remove(gameOverLabel);

        // Mulai kembali penempatan pipa dan game loop
        pipesCooldown.restart();
        gameLoop.start();
    }
    // Method game over
    private void gameOver() {
        gameLoop.stop();
        gameOverLabel.setVisible(true);
        gameOverLabel.setText("<html><div style='text-align: center;'>GAME OVER!<br>Press R to restart<br>Your score: " + score/2 + "</div></html>");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setHorizontalAlignment(JLabel.CENTER);
        gameOverLabel.setVerticalAlignment(JLabel.CENTER);
        gameOverLabel.setBounds((frameWidth - 300) / 2, (frameHeight - 40) / 2, 300, 130);
        add(gameOverLabel);
    }
    // Method nambah score
    private void increaseScore() {
        score++;
    }
    // Method reset score
    private void resetScore() {
        score = 0;
    }
    // Method untuk menampilkan score
    private void paintScore(Graphics g) {
        // Gambar skor di layar permainan
        g.drawString("Score: " + score/2, 10, 20);
    }
}
