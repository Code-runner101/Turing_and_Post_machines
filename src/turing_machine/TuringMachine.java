package turing_machine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuringMachine {
    private String tape;
    private int headPosition;
    private int step;
    private String currentState;
    private Map<String, Command> program;
    private List<Character> alphabet;

    public TuringMachine(String alphabetFile, String programFile, String tapeFile) throws IOException {
        alphabet = new ArrayList<>();
        program = new HashMap<>();
        loadAlphabet(alphabetFile);
        loadProgram(programFile);
        loadTape(tapeFile);
        currentState = "q1";
        headPosition = 0;
        step = 1;
    }

    public void run() {
        try (PrintWriter output = new PrintWriter("output.txt")) {
            output.println("Начальное состояние ленты: " + tape);

            while (!currentState.equals("q_final")) {  // Завершаем, когда достигнем конечного состояния q_final
                char currentSymbol = tape.charAt(headPosition);
                String key = currentState + currentSymbol;

                if (!program.containsKey(key)) {
                    System.err.println("Ошибка: команда для состояния " + currentState + " и символа " + currentSymbol + " не найдена.");
                    break;
                }

                Command cmd = program.get(key);
                StringBuilder tapeBuilder = new StringBuilder(tape);
                tapeBuilder.setCharAt(headPosition, cmd.writeSymbol);
                tape = tapeBuilder.toString();

                StringBuilder caretLine = new StringBuilder(" ".repeat(tape.length()));
                caretLine.setCharAt(headPosition, '^');

                output.println("Шаг №" + step++ + ": ");
                output.println("Лента: " + tape);
                output.println("       " + caretLine);
                output.println("Команда: " + currentState + " " + currentSymbol + " -> " + cmd.nextState + " " + cmd.writeSymbol + " " + cmd.direction);

                currentState = cmd.nextState;
                if (cmd.direction == '>') {
                    headPosition++;
                    if (headPosition >= tape.length()) {
                        tape += "_";
                    }
                } else if (cmd.direction == '<') {
                    headPosition--;
                    if (headPosition < 0) {
                        tape = "_" + tape;
                        headPosition = 0;
                    }
                }

                if (headPosition < 0 || headPosition >= tape.length()) {
                    System.err.println("Ошибка: каретка вышла за пределы ленты.");
                    break;
                }
            }

            output.println("Программа завершена. Финальное состояние ленты: " + tape);
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл output.txt: " + e.getMessage());
        }
    }

    private void loadAlphabet(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int symbol;
            while ((symbol = reader.read()) != -1) {
                alphabet.add((char) symbol);
            }
        }
    }

    private void loadProgram(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String currentState = parts[0];
                char readSymbol = parts[1].charAt(0);
                String nextState = parts[2];
                char writeSymbol = parts[3].charAt(0);
                char direction = parts[4].charAt(0);
                String key = currentState + readSymbol;
                program.put(key, new Command(nextState, writeSymbol, direction));
            }
        }
    }

    private void loadTape(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            tape = reader.readLine();
        }
    }

    public static void main(String[] args) {
        try {
            TuringMachine machine = new TuringMachine("alphabet.txt", "program.txt", "tape.txt");
            machine.run();
            System.out.println("Результат выполнения записан в файл output.txt.");
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
