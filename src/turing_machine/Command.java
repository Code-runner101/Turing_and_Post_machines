package turing_machine;

public class Command {
    String nextState;
    char writeSymbol;
    char direction;

    public Command(String nextState, char writeSymbol, char direction) {
        this.nextState = nextState;
        this.writeSymbol = writeSymbol;
        this.direction = direction;
    }
}
