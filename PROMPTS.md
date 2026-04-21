## Prompt 1 (seed prompt):
Prompt used: I'm starting a Java game project in VS Code using Swing. Create a single file called SnakeGame.java with a main method that opens a JFrame window (600x600 pixels) titled "Snake". Add a JPanel subclass called GamePanel inside the frame. No game logic yet — just get the window to open.

Result: This prompt made the program open a window when the game was run. No game code yet.

Fixes: No fixes needed for this code. AI produced exactly what I requested. 

Observation: AI added the "// UI setup goes here later." comment to the GamePanel class. I think this is a good addition because it will guide AI later on when it makes changes to the file. 

## Prompt 2 (seed prompt):
Prompt used: Add a 20x20 grid to GamePanel. Represent the snake as a sequence of grid cells and start it with 3 segments near the center, facing right. Each cell should be drawn as a 30x30 pixel square. Draw the snake in green and the background in dark gray.

Result: This added a grid to the window with a gray background. It also added an unmoving, green snake to the middle of the grid, which took up three squares. 

Fixes: No fixes needed for this code.

Observation: AI added a bunch of constants. I like this because it allows us to change important values in one place if we ever need to. 

## Prompt 3 (seed prompt):
Prompt used: Make the snake move automatically using a Swing timer that ticks every 150 milliseconds — the snake should advance one cell per tick in its current direction. Add arrow key controls so the player can steer, but don't allow the snake to reverse direction. For now, have the snake wrap around the edges instead of dying. Make sure the panel can receive keyboard input.

Result: This prompt made the snake able to move and change direction based on arrow key presses. It made the snake wrap around the edges of the board and prevented reversing direction. 

Fixes: I needed to add prevention of multiple direction changes in a single frame, which made it possible to reverse direction. This was done in prompt 5.

Observation: AI created an enum for directions. I wonder why it chose to design the code this way, worth asking it to explain. 

## Prompt 4 (seed prompt):
Prompt used: Add a food pellet that spawns at a random empty cell. When the snake eats it, grow by one segment and spawn new food. Add collision detection: hitting a wall or the snake's own body should end the game, stop movement, and show a "Game Over" message with the final score in the center of the screen. Display the current score in the top-left corner during play. When the game is over, let the player press R to reset everything and play again.

Result: This prompt added all remaining game logic. It added spawning food, the capability for the snake to grow after eating, a score, rules for ending the game, and a reset capability.

Fixes: No fixes needed for this code. 

Observation: AI thought of a lot of edge cases! For example, it added code to configureKeyControls() to prevent arrow keys from having any effects if a game is not ongoing.

## Prompt 5:
Prompt used: There is a bug in snake movement. I am able to reverse direction by clicking a non-reverse direction arrow key and then clicking the reverse direction arrow key after. To fix this bug, don't allow two direction changes within the same movement frame.

Result: AI added the boolean directionChangedThisFrame, which is set to true when the snake changes direction and set to false when the snake moves. If it is true, changing direction is not allowed. 

Fixes: No fixes needed for this code.

Observation: AI set the boolean to false near the end of the advanceSnake() method but before the repaint() method call in it. Worth asking why it chose that placement. 

## Prompt 6:
Prompt used: Change the first and last blocks that the snake occupies into a head and a tail to improve the visual style and make it more clear where the snake is going. Add a glow animation to the snake after it eats to make it look like it "leveled up".

Result: This prompt did not work like I expected it to. AI changed the color of the head and tail squares rather than changing their shape. Also, the glow animation did not look as cool as I thought it would look and didn't really add to the game experience. 

Fixes: I decided to undo the changes AI made after this prompt and start fresh with a new prompt with better instructions (prompt 7).

Observation: AI sometimes cannot accurately determine what changes I want it to make, especially when I do not provide enough detail. I wonder what prompt-engineering techniques would be useful for better communicating with AI and whether sharing a picture of a design with AI would have been helpful for this prompt.

## Prompt 7:
Prompt used: Change the first and last blocks that the snake occupies into a head and tail to improve the visual style and make it more clear where the snake is going. Do not change the color of the head or the tail blocks, but change their shape to look more like a head and a tail. Add eyes and a tongue to the head.

Result: This prompt changed the first and last blocks of the snake into a head and a tail, with the head having eyes and a tongue.

Fixes: No fixes needed for this code.

Observation: In the switch statement in the drawHead method, AI make some calculation take the CELL_SIZE constant into account, while others don't. Worth asking why that is and how this will affect responsiveness to changes in CELL_SIZE.

## Prompt 8: 
Prompt used: Make the game grid take up part of the screen rather than the full screen. It should be in the middle of the screen. Add the title of the game ("Snake") to the screen. Add a start button for starting the game rather than having it automatically start. The button should go away while the game is being played and reappear when it ends to be able to restart the game.

Result: This prompt changed the screen so that the game did not take up the full screen, as well as added a title and start button, making it look more like a real game. 

Fixes: AI initially removed the "press R to restart" capability after this prompt, so I manually added it back, changing the reset action to call the new startGame() method introduced by AI after this prompt. The title was also very small, so I made a follow up prompt to make it bigger and green. 

Observation: I wonder why AI removed the "press R to restart" functionality. It is possible that it inferred that I did not want it anymore because of the new button to start and restart the game. In future prompts, I should specify to leave features as they are unless it is specifically requested to change them.

## Prompt 9: 
Prompt used: Make the title bigger and green to match the snake.

Result: Made the title bigger and green.

Fixes: No fixes needed for this code.

Observation: AI changed the text color to SNAKE_COLOR, rather than defining a new color contant such as TITLE_COLOR. It is worth asking why it chose this approach.

## Prompt 10:
Prompt used: Add a high score that persists across all games. Add it outside the game board, directly above the current score. When the high score is broken, add "NEW HIGH SCORE!" on a new line in the game over screen. 

Result: This prompt added a high score to the top right of the page and a message to the game over screen when the high score is broken. 

Fixes: AI changed the positioning of the current score, which I did not want. After AI made its changes, I manually reverted the change to the position of the current score text and made the high score text a little higher up on the page. 

Observation: Something in my prompt likely caused AI to move the current score text up. Worth asking what that was and adjusting my future prompts. 