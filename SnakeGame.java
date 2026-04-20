import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            GamePanel gamePanel = new GamePanel();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
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
    private static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
    private static final Color GRID_COLOR = new Color(85, 85, 85);
    private static final Color SNAKE_COLOR = Color.GREEN;
    private static final int TICK_DELAY_MS = 150;

    private final List<GridCell> snake = new ArrayList<>();
    private final Timer movementTimer;

    private Direction currentDirection = Direction.RIGHT;

    public GamePanel() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);

        initializeSnake();
        configureKeyControls();

        movementTimer = new Timer(TICK_DELAY_MS, event -> advanceSnake());
        movementTimer.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);

        graphics.setColor(GRID_COLOR);
        for (int index = 0; index <= GRID_SIZE; index++) {
            int position = index * CELL_SIZE;
            graphics.drawLine(position, 0, position, GRID_SIZE * CELL_SIZE);
            graphics.drawLine(0, position, GRID_SIZE * CELL_SIZE, position);
        }

        graphics.setColor(SNAKE_COLOR);
        for (GridCell segment : snake) {
            graphics.fillRect(segment.column * CELL_SIZE, segment.row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    private void initializeSnake() {
        int centerRow = GRID_SIZE / 2;
        int centerColumn = GRID_SIZE / 2;

        snake.add(new GridCell(centerRow, centerColumn - 2));
        snake.add(new GridCell(centerRow, centerColumn - 1));
        snake.add(new GridCell(centerRow, centerColumn));
    }

    private void configureKeyControls() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
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
        if (!currentDirection.isOpposite(nextDirection)) {
            currentDirection = nextDirection;
        }
    }

    private void advanceSnake() {
        GridCell head = snake.get(snake.size() - 1);
        int nextRow = wrapCoordinate(head.row + currentDirection.rowDelta);
        int nextColumn = wrapCoordinate(head.column + currentDirection.columnDelta);

        snake.add(new GridCell(nextRow, nextColumn));
        snake.remove(0);
        repaint();
    }

    private int wrapCoordinate(int value) {
        return (value + GRID_SIZE) % GRID_SIZE;
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
    }
}
