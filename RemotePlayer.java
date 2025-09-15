public class RemotePlayer extends Player {
    public RemotePlayer(String name, CrashCrave game) {
        super(name, game);
    }

    /**
     * RemotePlayer represents the opponent player in a two-player multiplayer game.
     * When a RemotePlayer makes a move, it means we received a move from the remote
     * opponent via network. We should execute the move locally but NOT send it back
     * over the network to avoid infinite loops.
     */
    @Override
    public void makeMove(int cardIndex) {
        // Execute the move locally for the remote player
        // This is called when we receive a move from the opponent
        System.out.println("RemotePlayer " + name + " making move at index: " + cardIndex);
        game.flipCard(cardIndex);
        // NOTE: We do NOT call game.sendMoveToRemote() here because this move
        // already came from the remote player - sending it back would create an infinite loop
    }
}
