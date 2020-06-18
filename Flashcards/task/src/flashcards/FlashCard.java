package flashcards;

import java.util.Objects;

public class FlashCard {

    String term;
    String definition;
    int mistakes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlashCard flashCard = (FlashCard) o;
        return mistakes == flashCard.mistakes &&
                Objects.equals(term, flashCard.term) &&
                Objects.equals(definition, flashCard.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, definition, mistakes);
    }

    public FlashCard(String term, String definition, int mistakes) {
        this.term = term;
        this.definition = definition;
        this.mistakes = mistakes;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }
}
