package models.pdri;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

public class Element extends PDRIEntity {
    private static final int LEVELS_NUMBER = 6;

    private int[] levels = new int[LEVELS_NUMBER];
    private int level;
    private int score;
    private int maxScore;
    private String comments;

    public int[] getLevels() {
        return levels;
    }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public String getComments() {
        return comments;
    }

    public void setLevels(int[] levels) {
        this.levels = levels;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    private Element() {}

    public static Element createElement(Row row, HSSFWorkbook workbook1) {
        Element element = null;
        int dataOffset = findNthNonEmptyColumnIndex(row, 2);

        if (dataOffset > -1) {
            element = new Element();

            // Get definition levels
            int levels[] = new int[LEVELS_NUMBER];
            for(int i = 0; i < LEVELS_NUMBER; i++) {
                levels[i] = ((Double)(getValueAt(row, dataOffset++, workbook1))).intValue();
            }
            element.setLevels(levels);

            element.setLevel(((Double)(getValueAt(row, dataOffset++, workbook1))).intValue());

            // Score (Formula)
            int score = ((Double)(getValueAt(row, dataOffset++, workbook1))).intValue();
            element.setScore(score);

            int maxScore = ((Double)(getValueAt(row, dataOffset++, workbook1))).intValue();
            element.setMaxScore(maxScore);

            /*String comments = (String)getValueAt(row, dataOffset++, workbook1);
            element.setComments(comments);*/
        }

        return element;
    }
}
