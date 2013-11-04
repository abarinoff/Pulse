package models;

import java.util.LinkedList;
import java.util.List;

public class Category extends SheetItem {
    private String title;

    private String description;

    private List<Element> elements = new LinkedList<Element>();

    private int totalScore;
    private int totalMaxScore;

    private Section section;

    public Category(Section section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalMaxScore() {
        return totalMaxScore;
    }

    public void setTotalMaxScore(int totalMaxScore) {
        this.totalMaxScore = totalMaxScore;
    }
}
