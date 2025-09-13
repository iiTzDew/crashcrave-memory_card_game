// Step 3: Inheritance and Polymorphism with Player
public abstract class Player {
    protected String name;
    protected int score = 0;
    protected CrashCrave game;

    public Player(String name, CrashCrave game) {
        this.name = name;
        this.game = game;
    }

    public abstract void makeMove(int cardIndex);

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }
}
