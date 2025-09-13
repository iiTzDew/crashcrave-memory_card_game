public class LocalPlayer extends Player {
    public LocalPlayer(String name, CrashCrave game) {
        super(name, game);
    }

    @Override
    public void makeMove(int cardIndex) {
        game.flipCard(cardIndex);
        game.sendMoveToRemote(cardIndex);
    }
}
