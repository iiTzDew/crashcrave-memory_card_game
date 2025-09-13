public class RemotePlayer extends Player {
    public RemotePlayer(String name, CrashCrave game) {
        super(name, game);
    }

    @Override
    public void makeMove(int cardIndex) {
        game.flipCard(cardIndex);
    }
}
