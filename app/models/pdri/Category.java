package models.pdri;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Category extends PDRIEntity {
    public static final String CATEGORY_TOKEN = "Category";
    public static final String ELEMENT_TOKEN = "Element";
    public static final String CATEGORY_TOTAL_TOKEN = "CATEGORY TOTAL";

    public static final String CATEGORY_START_PATTERN = "^[A-Za-z]{1}\\..*";
    public static final String CATEGORY_END_PATTERN = "(\\s)*CATEGORY TOTAL(\\s)*";

    /**
     * Title of the category (part of the Category cell's content up to the first dot ".")
     */
    private String title;

    /**
     * Description of the category (part of the Category cell's content after the first dot)
     */
    private String description;

    private List<Element> elements = new LinkedList<Element>();

    private int totalScore;
    private int totalMaxScore;

    public String getTitle() {
        return this.title;
    }

    /*public static List<Category> extractCategories(HSSFSheet sheet, int offset) throws Exception {
        List<Category> categories = new LinkedList<Category>();
        int elementOffset = offset + 1;

        Row row = sheet.getRow(offset);
        if (isValueInRow(row, CATEGORY_TOKEN)) {
            row = sheet.getRow(elementOffset);
            if (isValueInRow(row, ELEMENT_TOKEN)) {

                int categoryOffset = elementOffset + 1;
                while (categoryOffset < sheet.getPhysicalNumberOfRows()) {
                    // There are Category and Element tokens following the Section row which means we have correct header
                    row = sheet.getRow(categoryOffset);
                    int categoryTitleIndex = findSingleCellIndex(row);

                    if (categoryTitleIndex < 0) {
                        throw new Exception("Category title was not found");
                    }
                    String title = row.getCell(categoryTitleIndex).getStringCellValue();
                    Category category = new Category(title);
                    String categoryTotalToken = category.getCategoryTotalToken();

                    while (true) {
                        row = sheet.getRow(i);
                        if (!isValueInRow(row, categoryTotalToken)) {
                            // @todo Must be appended to the list of Elements
                        }
                        else {
                            category.parseCategoryTotals(row);
                        }
                    }
                }
            }
        }

        return categories;
    }
*/
    public void parseStartRow(Row row) {
        int cellIndex = findSingleCellIndex(row);
        if (cellIndex < 0) {
            // @todo Report error
            throw new RuntimeException("Invalid Category start row");
        }
        String cellContent = row.getCell(cellIndex).toString();
        title = cellContent.substring(0, cellContent.indexOf("."));
        description = cellContent.substring(cellContent.indexOf(".") + 1);
    }

    public void parseEndRow(Row row) {
        String pattern = getCategoryTotalToken(title);
        int cellIndex = findCellMatchIndex(row, pattern);
        if (cellIndex <= 0) {
            // @todo Report an Error
            throw new RuntimeException("Couldn't find the category end prefix while parsing the Category closing row");
        }
        totalScore = (int) row.getCell(++cellIndex).getNumericCellValue();
        totalMaxScore = (int) row.getCell(++cellIndex).getNumericCellValue();
    }

    public static boolean isCategoryStart(Row row) {
        boolean isStart = false;

        int cellIndex = findSingleCellIndex(row);
        if (cellIndex >= 0) {
            String cellContent = row.getCell(cellIndex).toString();
            isStart = cellContent.matches(CATEGORY_START_PATTERN);
        }

        return isStart;
    }

    public static boolean isCategoryEnd(Row row, String categoryTitle) {
        String pattern = getCategoryTotalToken(categoryTitle);
        int cellIndex = findCellMatchIndex(row, pattern);

        return cellIndex > 0 ? true : false;
    }

    private static String getCategoryTotalToken(String title) {
        return StringUtils.join(CATEGORY_END_PATTERN.split(" "), " " + title + " ");
    }

    public void parseCategoryTotals(Row row) {

    }

    public void parseCategoryTotalRow(Row row) {

    }
}
