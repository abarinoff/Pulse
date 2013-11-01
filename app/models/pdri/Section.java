package models.pdri;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Section extends PDRIEntity {
    public static final int INITIAL_VERTICAL_OFFSET = 6;

    // Pattern to match the "Section" section of the worksheet
    private static final String PATTERN = "^SECTION (I){1,3} - .*";

    private String title;
    private String sectionEnd;

    private List<Category> categories = new LinkedList<Category>();

    private int totalScore;
    private int totalMaxScore;

    public Section(HSSFSheet sheet) {
        this.sheet = sheet;
    }

    public String parse() {
        int categoriesStartRow = 0, categoriesEndRow = 0;
        int rowIndex = INITIAL_VERTICAL_OFFSET - 1;

        String dbg = "Error";
        while(rowIndex < sheet.getPhysicalNumberOfRows() - 1) {
            Row row = sheet.getRow(rowIndex);
            if (!isSectionStart(row)) {
                rowIndex++;
                continue;
            }
            parseStartRow(row);

            // Looking for the section end marker
            categoriesStartRow = ++rowIndex;
            while(rowIndex < sheet.getPhysicalNumberOfRows() - 1) {
                row = sheet.getRow(rowIndex);
                if (!isSectionEnd(row)) {
                    rowIndex++;
                    continue;
                }
                // Section end
                parseEndRow(row);
                categoriesEndRow = rowIndex++ - 1;
            }
            break;
        }

        // Categories
        rowIndex = categoriesStartRow;
        while(rowIndex <= categoriesEndRow) {
            Row row = sheet.getRow(rowIndex);
            if (!Category.isCategoryStart(row)) {
                rowIndex++;
                continue;
            }
            Category category = new Category();
            category.parseStartRow(row);

            // Here we have to save Elements start
            rowIndex++;
            while(rowIndex <= categoriesEndRow) {
                row = sheet.getRow(rowIndex);
                if (!Category.isCategoryEnd(row, category.getTitle())) {
                    rowIndex++;
                    continue;
                }
                category.parseEndRow(row);
                categories.add(category);
                rowIndex++;
                break;
            }
        }

        return dbg;
    }

    private void parseStartRow(Row row) {
        int cellIndex = indexCellMatched(row, PATTERN);
        String cellValue = row.getCell(cellIndex).toString();

        // @todo Regexp math would be better
        title = cellValue.substring(0, cellValue.indexOf("-"));
    }

    private void parseEndRow(Row row) {
        String pattern = "^(?i)" + title + "Maximum Score.*";
        sectionEnd = indexCellMatched(row, pattern) == -1 ? " Error" : row.getCell(indexCellMatched(row, pattern)).toString();
    }

    private boolean isSectionStart(Row row) {
        return indexCellMatched(row, PATTERN) == -1 ? false : true;
    }

    private boolean isSectionEnd(Row row) {
        String pattern = "^(?i)" + title + "Maximum Score.*";
        return indexCellMatched(row, pattern) == -1 ? false : true;
    }

    private int indexCellMatched(Row row, String pattern) {
        int match = -1;
        int index = 0;
        Iterator<Cell> cellIterator = row.cellIterator();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellContent = cell.toString();
            if (cellContent.matches(pattern)) {
                match = index;
                break;
            }
            index++;
        }
        return match;
    }
}
