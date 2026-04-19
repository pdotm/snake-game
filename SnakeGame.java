import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new GamePanel());
            frame.setVisible(true);
        });
    }
}

class GamePanel extends JPanel {
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 30;
    private static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
    private static final Color GRID_COLOR = new Color(85, 85, 85);
    private static final Color SNAKE_COLOR = Color.GREEN;

    private final List<GridCell> snake = new ArrayList<>();

    public GamePanel() {
        setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        setBackground(BACKGROUND_COLOR);
        initializeSnake();
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

    private static final class GridCell {
        private final int row;
        private final int column;

        private GridCell(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
}
