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

    /*Category(String fullTitle) {
        // @todo Handle situations when the dot symbol has not been found
        this.title = fullTitle.substring(0, fullTitle.indexOf("."));
        this.description = fullTitle.substring(fullTitle.indexOf("."));
    }*/

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
    public String getCategoryTotalToken() {
        return StringUtils.join(CATEGORY_TOTAL_TOKEN.split(" "), " " + title + " ");
    }

    public void parseCategoryTotals(Row row) {

    }

    public void parseCategoryTotalRow(Row row) {

    }
}
