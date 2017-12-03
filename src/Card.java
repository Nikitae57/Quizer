import java.io.Serializable;

/**
 * Created by Nikita on 30.04.2017.
 */
public class Card implements Serializable {
    private String question, answer;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
