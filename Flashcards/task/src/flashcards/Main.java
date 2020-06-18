package flashcards;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        Map<String, FlashCard> cards = new HashMap<>();
        List<String> logList = new ArrayList<>();  //log input and output
        boolean isMenu = true;
        String fileToImport = null;
        String fileToExport = null;
        boolean exportAtExit = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-import")) {
                fileToImport = args[i + 1];
                importCards(scanner, cards, fileToImport);
                System.out.println();
            }
            if (args[i].equals("-export")) {
                fileToExport = args[i + 1];
                exportAtExit = true;
            }
        }


        while (isMenu) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            switch (scanner.nextLine()) {
                case "add":
                    addCard(scanner, cards, logList);
                    break;
                case "remove":
                    removeCard(scanner, cards);
                    break;
                case "import":
                    importCards(scanner, cards, "");
                    break;
                case "export":
                    export(scanner, cards, "");
                    break;
                case "ask":
                    ask(scanner, cards, logList);
                    break;
                case "exit":
                    System.out.println("Bye bye!");
                    if (exportAtExit) {
                        export(scanner, cards, fileToExport);
                    }
                    isMenu = false;
                    break;
                case "log":
                    logSave(scanner, logList);
                    break;
                case "hardest card":
                    hardestCard(cards);
                    break;
                case "reset stats":
                    resetStats(cards);
                    break;
            }
        }

    }

    public static void logSave(Scanner scanner, List<String> logList) {
        System.out.println("File name:");
        String filename = scanner.nextLine();
        try (PrintWriter writer = new PrintWriter(filename)) {
            logList.forEach(writer::write);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The log has been saved.");
    }

    public static void resetStats(Map<String, FlashCard> cards) {
        for (FlashCard fc : cards.values()) {
            fc.setMistakes(0);
        }
        System.out.println("Card statistics has been reset.");
    }

    public static void hardestCard(Map<String, FlashCard> cards) {
        int mostMistakes = 0;
        List<String> hardCards = new ArrayList<>();  // to store if multiple cards have most mistakes

        // add highest mistakes count cards to list
        for (FlashCard fc : cards.values()) {
            if (fc.getMistakes() > mostMistakes) {
                hardCards.clear();
                hardCards.add(fc.term);
                mostMistakes = fc.getMistakes();
            }  else if (fc.getMistakes() == mostMistakes) {
                hardCards.add(fc.term);
            }
        }

        if (mostMistakes == 0) {
            System.out.println("There are no cards with errors");
        } else if (hardCards.size() == 1) {
            System.out.println("The hardest card is \"" + hardCards.get(0) + "\".  You have " + mostMistakes + " errors answering it.");
        } else {
            System.out.print("The hardest cards are ");
            for (int i = 0; i < hardCards.size(); i++) {
                if (i != hardCards.size() - 1) {
                    System.out.print("\"" + hardCards.get(i) + "\", ");
                } else {
                    System.out.println("\"" + hardCards.get(i) + "\" with " + mostMistakes + " mistakes.");

                }
            }
        }
    }


    public static void addCard(Scanner scanner, Map<String, FlashCard> cards, List<String> logList) {
        System.out.println("The card:");
        String input = scanner.nextLine();
        logList.add(input);
        if (cards.containsKey(input)) {
            System.out.println("The card \"" + input + "\" already exists.");
        } else {
            System.out.println("The definition of the card: ");
            String definition = scanner.nextLine();
                    cards.put(input, new FlashCard(input, definition, 0));
                    System.out.println("The pair (\"" + input + "\":\"" + definition + "\") has been added.");
        }
    }


    public static void removeCard(Scanner scanner, Map<String, FlashCard> cards) {
        System.out.print("The card:\n> ");
        String input = scanner.nextLine();
        if (cards.containsKey(input)) {
            cards.remove(input);
            System.out.println("The card has been removed.");
        } else {
            System.out.println("Can't remove \"" + input + "\": there is no such card.");
        }
    }

    public static void importCards(Scanner scanner, Map<String, FlashCard> cards, String filename) throws Exception {
        String inFile = null;
        if (!filename.isEmpty()) {
            inFile = filename;
        } else {
            System.out.println("File name:");
            inFile = scanner.nextLine();
        }

        int loadedCards = 0;

        String line = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            while ((line = reader.readLine()) != null) {
                String term = line;
                String definition = reader.readLine();
                int mistakes = Integer.valueOf(reader.readLine());

                FlashCard fc = new FlashCard(term, definition, mistakes);

                cards.put(line, fc);
                loadedCards++;
            }
            System.out.println(loadedCards + " cards have been loaded.");
        } catch (IOException e) {
            System.out.println("File Not found.");
        }
    }

    public static void export(Scanner scanner, Map<String, FlashCard> cards, String filename) {
        if (filename.isEmpty()) {
            System.out.println("File name:");
            filename = scanner.nextLine();
        }
        int numberOfCards = cards.size();

        try (FileWriter writer = new FileWriter(filename)) {
            for (FlashCard fc : cards.values()) {
                writer.write(fc.getTerm() + "\n" + fc.getDefinition() + "\n" + fc.getMistakes() + "\n");
            }
        } catch (IOException e) {
            System.out.println("An exception in exporting ...");
        }
        System.out.println(numberOfCards + " cards have been saved.");
    }


    public static void ask(Scanner scanner, Map<String, FlashCard> cards, List<String> logList) {
        System.out.println("How many times to ask?");
        int count = Integer.valueOf(scanner.nextLine());
        String otherDefinition = "";

        while (count > 0) {
            String key = randomEntry(cards);
            System.out.println("Print the definition of \"" + key + "\":");
            String guess = scanner.nextLine();
            if (guess.equals(cards.get(key).getDefinition())) {     //Correct
                System.out.println("Correct answer");
            } else if (containsDefinition(guess, cards)) {    //Different answer
                for (String k : cards.keySet()) {
                    if (cards.get(k).getDefinition().equals(guess)) {
                        otherDefinition = k;
                        break;
                    }
                }
                System.out.println("Wrong answer. The correct one is \"" + cards.get(key).getDefinition() +
                        "\", you've just written the definition of \"" + otherDefinition + "\".");
                cards.get(key).setMistakes(cards.get(key).getMistakes() + 1);
            } else {    //Just wrong
                System.out.println("Wrong Answer. The correct one is \"" + cards.get(key).getDefinition() + "\".");
                cards.get(key).setMistakes(cards.get(key).getMistakes() + 1);
            }
            count--;
        }
    }

    // check if Map contains the guess in all definitions
    public static boolean containsDefinition(String guess, Map<String, FlashCard> cards) {
        for (FlashCard fc : cards.values()) {
            if (fc.getDefinition().equals(guess)) {
                return true;
            }
        }
        return false;
    }

    // Get a random entry from the HashMap.
    public static String randomEntry(Map<String, FlashCard> cards) {
        Object[] keys = cards.keySet().toArray();
        Object key = keys[new Random().nextInt(keys.length)];
        return (String) key;
    }
}
