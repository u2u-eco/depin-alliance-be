package xyz.telegram.depinalliance.common.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author holden on 12-Sep-2024
 */
public class QuizResponse {
  public long index;
  public String question;
  public Boolean isMultiple = false;
  public List<Answer> answers = new ArrayList<>();

  public static class Answer {
    public long index;
    public String text;
    public Boolean correct;

    public boolean isCorrect() {
      return correct != null && correct;
    }

    public Answer(long index, String text, boolean correct) {
      this.index = index;
      this.text = text;
      this.correct = correct;
    }

    public Answer() {
    }
  }
}
