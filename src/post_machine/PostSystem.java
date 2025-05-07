package post_machine;

import java.io.*;
import java.util.*;

public class PostSystem {
    public static void main(String[] args) {
        try {
            // Чтение входного файла
            BufferedReader reader = new BufferedReader(new FileReader("post_input.txt"));
            Map<String, String> data = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        data.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
            reader.close();

            // Инициализация алфавита, переменных, аксиомы и правил
            String entryString = data.get("ES");
            String alphabet = data.get("A");
            String variables = data.get("X");
            String axiom = data.get("A1").replace("{", "").replace("}", "");
            String[] rules = data.get("R").replace("{", "").replace("}", "").split(";");


            // Преобразование правил в список
            List<Rule> ruleList = new ArrayList<>();
            for (String rule : rules) {
                String[] parts = rule.split("->");
                if (parts.length == 2) {
                    String[] mutatedRuleParts = new String[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        mutatedRuleParts[i] = parts[i].replaceAll("x",axiom);
                    }
                    ruleList.add(new Rule(mutatedRuleParts[0].trim(), mutatedRuleParts[1].trim()));
                }
            }

            // Постоянное применение правил, пока возможно
            String currentString = entryString;
            List<String> ruleStrings = new ArrayList<>();
            boolean ruleApplied;
            do {
                ruleApplied = false; // Сбрасываем флаг перед началом нового прохода
                for (Rule rule : ruleList) {
                    if (currentString.contains(rule.match)) { // Проверяем применимость правила
                        ruleStrings.add("Исходная строка: " + currentString + "\n");
                        currentString = currentString.replaceFirst(rule.match, rule.result);
                        ruleStrings.add("Применено правило: " + rules[0] + "\n");
                        ruleStrings.add("Результат применения правила: " + currentString + "\n\n");
                        ruleApplied = true; // Устанавливаем флаг, чтобы продолжить цикл
                        break; // Прерываем текущий проход, чтобы начать заново с первого правила
                    }
                }
            } while (ruleApplied); // Продолжаем, пока хоть одно правило применимо

            // Запись результата в выходной файл
            BufferedWriter writer = new BufferedWriter(new FileWriter("post_output.txt"));
            for (String ruleStr: ruleStrings) {
                writer.write(ruleStr);
            }
            writer.close();

            System.out.println("Результат записан в post_output.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Класс для хранения правил
    static class Rule {
        String match;
        String result;

        Rule(String match, String result) {
            this.match = match;
            this.result = result;
        }
    }
}