# online-tictactoe

Welcome to the Online Tic Tac Toe game! This project allows two players on separate devices to enjoy a game of Tic Tac Toe and chat over a same network. One device will act as both the server and a client, while the other device will only run the client.

## How to Play

1. **Set Up the Server:**
   - Run the `Server.java` file on the device that will act as both the server and a client.
   - Make a note of the server's IP address.

2. **Connect Client:**
   - Run the `Client.java` file on the player's device.
   - Open the `Client.java` file and replace the placeholder `IP_ADDRESS` with the actual IP address of the server.

3. **Start the Game:**
   - With the server and client running on one device and the client running on the other, the game is ready to begin.
   - Use the chat functionality to communicate with your opponent.

## Chat Functionality

- Players can communicate with each other using the integrated chat feature.
- Simply type your message in the chat box and press 'Enter' to send.

## Important Note

- Ensure that all devices are connected to the same network for the game and chat to function properly.
- The server IP address needs to be correctly set in the `Client.java` file for successful connection.

## Technologies Used

- Java
- Socket Programming
