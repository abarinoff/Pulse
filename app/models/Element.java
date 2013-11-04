package models;

import java.util.Map;

public class Element extends SheetItem {
    Category category;
    String title;
    Map<String, Integer> definitionLevels;
    int level;
    int score;
    int maxScore;
    String comments;

    public Element(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Integer> getDefinitionLevels() {
        return definitionLevels;
    }

    public Integer getDefinitionLevel(String level) {
        return definitionLevels.get(level);
    }

    public void setDefinitionLevels(Map<String, Integer> definitionLevels) {
        this.definitionLevels = definitionLevels;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
