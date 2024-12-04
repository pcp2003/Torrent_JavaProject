import java.io.Serializable;

public class WordSearchMessage implements Serializable {
    private String word;
    WordSearchMessage(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "WordSearchMessage{" +
                "word='" + word + '\'' +
                '}';
    }

    public String getWord() {return word;}
}
