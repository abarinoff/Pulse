package models.pdri;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Section extends PDRIEntity {
    public static final int INITIAL_VERTICAL_OFFSET = 6;

    // Pattern to match the "Section" section of the worksheet
    private static final String PATTERN = "^SECTION (I){1,3} - .*";
    private static final String SECTION_TOTAL_PATTERN = "SECTION TOTAL";

    private String title;
    private String sectionEnd;

    private List<Category> categories = new LinkedList<Category>();

    private int totalScore;
    private int totalMaxScore;

    public int getTotalMaxScore() {
        return totalMaxScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getTitle() {
        return title;
    }

    public Section(HSSFSheet sheet, HSSFWorkbook workbook) {
        this.sheet = sheet;
        this.workbook = workbook;
    }

    public void parse() {
        int categoriesStartRow = 0, categoriesEndRow = 0;
        int rowIndex = INITIAL_VERTICAL_OFFSET - 1;

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
            int elementsStartRow = rowIndex + 1; // @todo replace with ++rowIndex
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
                int elementsEndRow = rowIndex - 1;
                category.parseEndRow(row);
                category.parseElements(sheet, elementsStartRow, elementsEndRow, workbook);
                categories.add(category);
                rowIndex++;
                break;
            }
        }
    }

    private void parseStartRow(Row row) {
        int cellIndex = indexCellMatched(row, PATTERN);
        String cellValue = row.getCell(cellIndex).toString();

        // @todo Regexp math would be better
        title = cellValue.substring(0, cellValue.indexOf("-")).trim();
    }

    private void parseEndRow(Row row) {
        String pattern = "^(?i)" + title + "(\\s)+Maximum Score.*";
        sectionEnd = indexCellMatched(row, pattern) == -1 ? " Error" : row.getCell(indexCellMatched(row, pattern)).toString();
        int sectionTotalColumnIndex = findCellMatchIndex(row, "(\\s)*(?i)" + title + "(\\s)+TOTAL(\\s)*");
        if (sectionTotalColumnIndex < 0) {
            // @todo report an error
            throw new RuntimeException("No Section Totals cell found while parsing the Section end row");
        }
        totalScore = (int) row.getCell(++sectionTotalColumnIndex).getNumericCellValue();
        totalMaxScore = (int) row.getCell(++sectionTotalColumnIndex).getNumericCellValue();
    }

    private boolean isSectionStart(Row row) {
        return indexCellMatched(row, PATTERN) == -1 ? false : true;
    }

    private boolean isSectionEnd(Row row) {
        String pattern = "^(?i)" + title + "(\\s)+Maximum Score.*";
        return row == null || indexCellMatched(row, pattern) == -1 ? false : true;
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

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /*@Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SECTION");
        sb.append("title: " + title);
        sb.append("totalScore: " + totalScore);
        sb.append("totalMaxScore: " + totalMaxScore);

        return sb.toString();
    }*/
}
