import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            GamePanel gamePanel = new GamePanel();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800);
            frame.setLocationRelativeTo(null);
            frame.add(gamePanel);
            frame.setVisible(true);
            gamePanel.requestFocusInWindow();
        });
    }
}

class GamePanel extends JPanel {
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 30;
    private static final int BOARD_SIZE = GRID_SIZE * CELL_SIZE;
    private static final int TITLE_Y = 48;
    private static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
    private static final Color GRID_COLOR = new Color(85, 85, 85);
    private static final Color SNAKE_COLOR = Color.GREEN;
    private static final Color FOOD_COLOR = Color.RED;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color EYE_COLOR = Color.WHITE;
    private static final Color PUPIL_COLOR = Color.BLACK;
    private static final Color TONGUE_COLOR = new Color(255, 90, 90);
    private static final int TICK_DELAY_MS = 150;

    private final List<GridCell> snake = new ArrayList<>();
    private final Timer movementTimer;
    private final Random random = new Random();
    private final JButton startButton;

    private Direction currentDirection = Direction.RIGHT;
    private GridCell food;
    private int score;
    private boolean gameOver;
    private boolean directionChangedThisFrame;
    private boolean isPlaying;

    public GamePanel() {
        setPreferredSize(new Dimension(900, 800));
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);
        setLayout(null);

        startButton = new JButton("Start Game");
        startButton.addActionListener(event -> startGame());
        add(startButton);

        configureKeyControls();
        resetGame();

        movementTimer = new Timer(TICK_DELAY_MS, event -> advanceSnake());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Rectangle boardBounds = getBoardBounds();
        int boardX = boardBounds.x;
        int boardY = boardBounds.y;

        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, getWidth(), getHeight());

        drawTitle(graphics);

        graphics.setColor(Color.BLACK);
        graphics.fillRoundRect(boardX - 10, boardY - 10, BOARD_SIZE + 20, BOARD_SIZE + 20, 16, 16);

        graphics.setColor(GRID_COLOR);
        for (int index = 0; index <= GRID_SIZE; index++) {
            int position = index * CELL_SIZE;
            graphics.drawLine(boardX + position, boardY, boardX + position, boardY + BOARD_SIZE);
            graphics.drawLine(boardX, boardY + position, boardX + BOARD_SIZE, boardY + position);
        }

        if (food != null) {
            graphics.setColor(FOOD_COLOR);
            graphics.fillOval(boardX + food.column * CELL_SIZE, boardY + food.row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        drawSnake(graphics, boardX, boardY);

        drawScore(graphics, boardX, boardY);

        if (gameOver) {
            drawGameOver(graphics, boardX, boardY);
        } else if (!isPlaying) {
            drawReadyMessage(graphics, boardX, boardY);
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        Rectangle boardBounds = getBoardBounds();
        int buttonWidth = 140;
        int buttonHeight = 36;
        int buttonX = (getWidth() - buttonWidth) / 2;
        int buttonY = boardBounds.y + BOARD_SIZE + 25;
        startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    private void initializeSnake() {
        snake.clear();

        int centerRow = GRID_SIZE / 2;
        int centerColumn = GRID_SIZE / 2;

        snake.add(new GridCell(centerRow, centerColumn - 2));
        snake.add(new GridCell(centerRow, centerColumn - 1));
        snake.add(new GridCell(centerRow, centerColumn));
    }

    private Rectangle getBoardBounds() {
        int boardX = (getWidth() - BOARD_SIZE) / 2;
        int boardY = (getHeight() - BOARD_SIZE) / 2;
        return new Rectangle(boardX, boardY, BOARD_SIZE, BOARD_SIZE);
    }

    private void drawTitle(Graphics graphics) {
        String title = "Snake";
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int titleX = (getWidth() - fontMetrics.stringWidth(title)) / 2;

        graphics.setColor(TEXT_COLOR);
        graphics.drawString(title, titleX, TITLE_Y);
    }

    private void drawScore(Graphics graphics, int boardX, int boardY) {
        graphics.setColor(TEXT_COLOR);
        graphics.drawString("Score: " + score, boardX + 10, boardY + 20);
    }

    private void drawSnake(Graphics graphics, int boardX, int boardY) {
        if (snake.isEmpty()) {
            return;
        }

        graphics.setColor(SNAKE_COLOR);

        if (snake.size() == 1) {
            GridCell onlySegment = snake.get(0);
            graphics.fillRoundRect(
                boardX + onlySegment.column * CELL_SIZE + 2,
                boardY + onlySegment.row * CELL_SIZE + 2,
                CELL_SIZE - 4,
                CELL_SIZE - 4,
                12,
                12
            );
            return;
        }

        for (int index = 1; index < snake.size() - 1; index++) {
            GridCell segment = snake.get(index);
            graphics.fillRect(boardX + segment.column * CELL_SIZE, boardY + segment.row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        drawTail(graphics, snake.get(0), snake.get(1), boardX, boardY);
        drawHead(graphics, snake.get(snake.size() - 1), boardX, boardY);
    }

    private void drawTail(Graphics graphics, GridCell tail, GridCell nextSegment, int boardX, int boardY) {
        int x = boardX + tail.column * CELL_SIZE;
        int y = boardY + tail.row * CELL_SIZE;
        int deltaRow = tail.row - nextSegment.row;
        int deltaColumn = tail.column - nextSegment.column;

        graphics.setColor(SNAKE_COLOR);
        graphics.fillRoundRect(x + 4, y + 4, CELL_SIZE - 8, CELL_SIZE - 8, 12, 12);

        Polygon tailTip = new Polygon();
        if (deltaColumn == 1) {
            tailTip.addPoint(x + CELL_SIZE, y + CELL_SIZE / 2);
            tailTip.addPoint(x + CELL_SIZE - 8, y + CELL_SIZE / 2 - 6);
            tailTip.addPoint(x + CELL_SIZE - 8, y + CELL_SIZE / 2 + 6);
        } else if (deltaColumn == -1) {
            tailTip.addPoint(x, y + CELL_SIZE / 2);
            tailTip.addPoint(x + 8, y + CELL_SIZE / 2 - 6);
            tailTip.addPoint(x + 8, y + CELL_SIZE / 2 + 6);
        } else if (deltaRow == 1) {
            tailTip.addPoint(x + CELL_SIZE / 2, y + CELL_SIZE);
            tailTip.addPoint(x + CELL_SIZE / 2 - 6, y + CELL_SIZE - 8);
            tailTip.addPoint(x + CELL_SIZE / 2 + 6, y + CELL_SIZE - 8);
        } else {
            tailTip.addPoint(x + CELL_SIZE / 2, y);
            tailTip.addPoint(x + CELL_SIZE / 2 - 6, y + 8);
            tailTip.addPoint(x + CELL_SIZE / 2 + 6, y + 8);
        }
        graphics.fillPolygon(tailTip);
    }

    private void drawHead(Graphics graphics, GridCell head, int boardX, int boardY) {
        Graphics2D graphics2d = (Graphics2D) graphics.create();

        int x = boardX + head.column * CELL_SIZE;
        int y = boardY + head.row * CELL_SIZE;

        graphics2d.setColor(SNAKE_COLOR);
        graphics2d.fillRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 14, 14);

        int eyeSize = 5;
        int pupilSize = 2;

        int firstEyeX = x + 7;
        int firstEyeY = y + 7;
        int secondEyeX = x + 18;
        int secondEyeY = y + 7;
        int tongueBaseX = x + CELL_SIZE / 2;
        int tongueBaseY = y + CELL_SIZE / 2;
        int tongueTipX = tongueBaseX;
        int tongueTipY = tongueBaseY;

        switch (currentDirection) {
            case UP:
                firstEyeX = x + 8;
                firstEyeY = y + 6;
                secondEyeX = x + 17;
                secondEyeY = y + 6;
                tongueBaseX = x + CELL_SIZE / 2;
                tongueBaseY = y + 4;
                tongueTipX = tongueBaseX;
                tongueTipY = y - 6;
                break;
            case DOWN:
                firstEyeX = x + 8;
                firstEyeY = y + 19;
                secondEyeX = x + 17;
                secondEyeY = y + 19;
                tongueBaseX = x + CELL_SIZE / 2;
                tongueBaseY = y + CELL_SIZE - 4;
                tongueTipX = tongueBaseX;
                tongueTipY = y + CELL_SIZE + 6;
                break;
            case LEFT:
                firstEyeX = x + 6;
                firstEyeY = y + 8;
                secondEyeX = x + 6;
                secondEyeY = y + 17;
                tongueBaseX = x + 4;
                tongueBaseY = y + CELL_SIZE / 2;
                tongueTipX = x - 6;
                tongueTipY = tongueBaseY;
                break;
            case RIGHT:
                firstEyeX = x + 19;
                firstEyeY = y + 8;
                secondEyeX = x + 19;
                secondEyeY = y + 17;
                tongueBaseX = x + CELL_SIZE - 4;
                tongueBaseY = y + CELL_SIZE / 2;
                tongueTipX = x + CELL_SIZE + 6;
                tongueTipY = tongueBaseY;
                break;
            default:
                break;
        }

        graphics2d.setColor(EYE_COLOR);
        graphics2d.fillOval(firstEyeX, firstEyeY, eyeSize, eyeSize);
        graphics2d.fillOval(secondEyeX, secondEyeY, eyeSize, eyeSize);

        graphics2d.setColor(PUPIL_COLOR);
        graphics2d.fillOval(firstEyeX + 1, firstEyeY + 1, pupilSize, pupilSize);
        graphics2d.fillOval(secondEyeX + 1, secondEyeY + 1, pupilSize, pupilSize);

        graphics2d.setColor(TONGUE_COLOR);
        graphics2d.drawLine(tongueBaseX, tongueBaseY, tongueTipX, tongueTipY);
        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
            graphics2d.drawLine(tongueTipX, tongueTipY, tongueTipX - 3, tongueTipY + (currentDirection == Direction.UP ? -3 : 3));
            graphics2d.drawLine(tongueTipX, tongueTipY, tongueTipX + 3, tongueTipY + (currentDirection == Direction.UP ? -3 : 3));
        } else {
            graphics2d.drawLine(tongueTipX, tongueTipY, tongueTipX + (currentDirection == Direction.LEFT ? -3 : 3), tongueTipY - 3);
            graphics2d.drawLine(tongueTipX, tongueTipY, tongueTipX + (currentDirection == Direction.LEFT ? -3 : 3), tongueTipY + 3);
        }

        graphics2d.dispose();
    }

    private void drawGameOver(Graphics graphics, int boardX, int boardY) {
        String gameOverMessage = "Game Over";
        String scoreMessage = "Final Score: " + score;
        String resetMessage = "Press R to restart";
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int centerX = boardX + BOARD_SIZE / 2;
        int centerY = boardY + BOARD_SIZE / 2;

        graphics.setColor(TEXT_COLOR);
        graphics.drawString(gameOverMessage, centerX - fontMetrics.stringWidth(gameOverMessage) / 2, centerY - 20);
        graphics.drawString(scoreMessage, centerX - fontMetrics.stringWidth(scoreMessage) / 2, centerY + 5);
        graphics.drawString(resetMessage, centerX - fontMetrics.stringWidth(resetMessage) / 2, centerY + 30);
    }

    private void drawReadyMessage(Graphics graphics, int boardX, int boardY) {
        String readyMessage = "Click Start Game";
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int centerX = boardX + BOARD_SIZE / 2;
        int centerY = boardY + BOARD_SIZE / 2;

        graphics.setColor(TEXT_COLOR);
        graphics.drawString(readyMessage, centerX - fontMetrics.stringWidth(readyMessage) / 2, centerY);
    }

    private void configureKeyControls() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_R && gameOver) {
                    startGame();
                    return;
                }

                if (gameOver) {
                    return;
                }

                switch (event.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        updateDirection(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        updateDirection(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        updateDirection(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        updateDirection(Direction.RIGHT);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void updateDirection(Direction nextDirection) {
        if (directionChangedThisFrame) {
            return;
        }

        if (!currentDirection.isOpposite(nextDirection)) {
            currentDirection = nextDirection;
            directionChangedThisFrame = true;
        }
    }

    private void advanceSnake() {
        if (!isPlaying || gameOver) {
            return;
        }

        GridCell head = snake.get(snake.size() - 1);
        int nextRow = head.row + currentDirection.rowDelta;
        int nextColumn = head.column + currentDirection.columnDelta;
        GridCell nextHead = new GridCell(nextRow, nextColumn);

        if (isWallCollision(nextHead) || isSelfCollision(nextHead)) {
            gameOver = true;
            isPlaying = false;
            movementTimer.stop();
            startButton.setVisible(true);
            repaint();
            return;
        }

        snake.add(nextHead);
        if (nextHead.equals(food)) {
            score++;
            food = spawnFood();
        } else {
            snake.remove(0);
        }

        directionChangedThisFrame = false;
        repaint();
    }

    private boolean isWallCollision(GridCell cell) {
        return cell.row < 0 || cell.row >= GRID_SIZE || cell.column < 0 || cell.column >= GRID_SIZE;
    }

    private boolean isSelfCollision(GridCell nextHead) {
        int startIndex = nextHead.equals(food) ? 0 : 1;

        for (int index = startIndex; index < snake.size(); index++) {
            if (snake.get(index).equals(nextHead)) {
                return true;
            }
        }

        return false;
    }

    private GridCell spawnFood() {
        List<GridCell> emptyCells = new ArrayList<>();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                GridCell candidate = new GridCell(row, column);
                if (!snake.contains(candidate)) {
                    emptyCells.add(candidate);
                }
            }
        }

        if (emptyCells.isEmpty()) {
            return null;
        }

        return emptyCells.get(random.nextInt(emptyCells.size()));
    }

    private void resetGame() {
        score = 0;
        gameOver = false;
        isPlaying = false;
        currentDirection = Direction.RIGHT;
        directionChangedThisFrame = false;
        initializeSnake();
        food = spawnFood();
    }

    private void startGame() {
        resetGame();
        isPlaying = true;
        startButton.setVisible(false);
        movementTimer.start();
        requestFocusInWindow();
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    private enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

        private final int rowDelta;
        private final int columnDelta;

        Direction(int rowDelta, int columnDelta) {
            this.rowDelta = rowDelta;
            this.columnDelta = columnDelta;
        }

        private boolean isOpposite(Direction other) {
            return rowDelta + other.rowDelta == 0 && columnDelta + other.columnDelta == 0;
        }
    }

    private static final class GridCell {
        private final int row;
        private final int column;

        private GridCell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GridCell)) {
                return false;
            }

            GridCell gridCell = (GridCell) other;
            return row == gridCell.row && column == gridCell.column;
        }

        @Override
        public int hashCode() {
            return 31 * row + column;
        }
    }
}
