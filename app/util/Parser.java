package util;

import models.Category;
import models.Element;
import models.Section;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

public class Parser {

    protected HSSFWorkbook workbook;

    // The current sheet being parsed
    protected HSSFSheet sheet;

    // Header of the Sheet (for convenient Element parsing)
    Header header;

    FormulaEvaluator formulaEvaluator;

    private static final String categoryFields[] = new String[]{
        "(?i)Category",
        "(?i)Definition Level",
        "(?i)Level",
        "(?i)Score",
        "(?i)Max",
        "(?i)Comments"
    };

    private static final String elementFields[] = new String[]{
        "(?i)element",
        "(?i)score"
    };

    public Parser(HSSFWorkbook workbook) {
        this.workbook = workbook;
        formulaEvaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
    }

    public Section parse() {
        int index = workbook.getSheetIndex("Section I - Basis of Project 1");
        sheet = workbook.getSheetAt(index);

        // Parse header, make it a class member
        buildHeader();

        // Parse section
        int startRow = 0,
            endRow = sheet.getPhysicalNumberOfRows() - 1;
        SectionParser sectionParser = new SectionParser();
        Section section = sectionParser.parse(startRow, endRow);

        return section;
    }

    private class SectionParser {
        private static final String START_SECTION_PATTERN = "(?i)^Section (?-i)([IVX]|\\d)+\\s*-\\s*[\\w\\s]+";

        private Section section = new Section();

        public Section parse(int startRow, int endRow) {
            int categoriesStartRow = 0, categoriesEndRow = 0;
            int rowIndex = startRow;

            // Parse Section
            while(rowIndex < endRow) {
                Row row = sheet.getRow(rowIndex);
                if (!parseStart(row)) {
                    rowIndex++;
                    continue;
                }

                // Looking for the section end marker
                categoriesStartRow = ++rowIndex;
                while(rowIndex < endRow) {
                    row = sheet.getRow(rowIndex);
                    if (!parseEnd(row)) {
                        rowIndex++;
                        continue;
                    }

                    categoriesEndRow = rowIndex++ - 1;
                    CategoryParser categoryParser = new CategoryParser(section);
                    List<Category> categories = categoryParser.parse(categoriesStartRow, categoriesEndRow);

                    section.setCategories(categories);
                    break;
                }
                break;
            }

            return section;
        }

        public boolean parseStart(Row row) {
            boolean result = false;
            if (row != null) {
                int cellIndex = getMatchedCellIndex(row, START_SECTION_PATTERN);
                if (cellIndex >= 0) {
                    String cellValue = getCellAsString(row, cellIndex);

                    String title = cellValue.substring(0, cellValue.indexOf("-"));
                    title = title.trim();
                    section.setTitle(title);
                    result = true;
                }
            }

            return result;
        }

        public boolean parseEnd(Row row) {
            boolean result = false;
            if (row != null) {
                // Section's 'Max Score'
                String pattern = "(?i)^" + section.getTitle() + "\\s+Maximum Score\\s*=\\s*\\d+";
                int cellIndex = getMatchedCellIndex(row, pattern);
                if (cellIndex >=0 ) {
                    String sectionEnd = getCellAsString(row, cellIndex);
                    section.setSectionEnd(sectionEnd);

                    // Section's 'Total'
                    int sectionTotalIndex = getMatchedCellIndex(row, "(?i)\\s*" + section.getTitle() + "\\s+TOTAL\\s*");
                    if (sectionTotalIndex < 0) {
                        return false;
                    }

                    // @todo Take index from Header
                    int totalScore = getCellAsInt(row, ++sectionTotalIndex);
                    int totalMaxScore = getCellAsInt(row, ++sectionTotalIndex);

                    section.setTotalScore(totalScore);
                    section.setTotalMaxScore(totalMaxScore);

                    result = true;
                }
            }

            return result;
        }
    }

    private class CategoryParser {
        public static final String START_CATEGORY_PATTERN = "(?i)^[A-Za-z]\\..+";

        private Section section;
        private Category category;

        private CategoryParser(Section section) {
            this.section = section;
            category  = new Category(this.section);
        }

        public List<Category> parse(int startRow, int endRow) {
            List<Category> categories = new LinkedList<Category>();
            int rowIndex = startRow;
            while(rowIndex <= endRow) {
                Row row = sheet.getRow(rowIndex);
                if (!parseStart(row)) {
                    rowIndex++;
                    continue;
                }
                int elementsStartRow = rowIndex + 1;

                // Here we have to save Elements start
                rowIndex++;
                while(rowIndex <= endRow) {
                    row = sheet.getRow(rowIndex);
                    if (!parseEnd(row)) {
                        rowIndex++;
                        continue;
                    }
                    int elementsEndRow = rowIndex - 1;
                    categories.add(category);

                    ElementParser elementParser = new ElementParser(category);
                    List<Element> elements = elementParser.parse(elementsStartRow, elementsEndRow);
                    category.setElements(elements);

                    rowIndex++;
                    break;
                }
            }

            return categories;
        }

        private boolean parseStart(Row row) {
            boolean result = false;

            int cellIndex = findSingleCellIndex(row);
            if (cellIndex >= 0) {
                String cellContent = getCellAsString(row, cellIndex);
                if (!cellContent.matches(START_CATEGORY_PATTERN)) {
                    return false;
                }
                category = new Category(section);

                String title = cellContent.substring(0, cellContent.indexOf("."));
                category.setTitle(title);

                String description = cellContent.substring(cellContent.indexOf(".") + 1);
                category.setDescription(description);

                result = true;
            }

            return result;
        }

        private boolean parseEnd(Row row) {
            String pattern = "(?i)\\s*CATEGORY\\s+" + category.getTitle() + "\\s+TOTAL\\s*";
            int cellIndex = getMatchedCellIndex(row, pattern);
            boolean result = false;

            if (cellIndex >= 0) {
                int totalScore = getCellAsInt(row, ++cellIndex);
                category.setTotalScore(totalScore);

                int totalMaxScore = getCellAsInt(row, ++cellIndex);
                category.setTotalMaxScore(totalMaxScore);

                result = true;
            }

            return result;
        }
    }

    private class ElementParser {
        Category category;

        public ElementParser(Category category) {
            this.category = category;
        }

        public List<Element> parse(int startRow, int endRow) {
            List<Element> elements = new LinkedList<Element>();
            int rowIndex = startRow;
            for (rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                // @todo check if row can be parsed into Element
                Element element = new Element(category);

                // Title
                String title = getCellAsString(row, header.elementsOffset);
                element.setTitle(title);

                // Definition Levels
                Map<String, Integer> definitionLevels = new HashMap<String, Integer>();
                for (Map.Entry<String, Integer> entry : header.levelsOffsets.entrySet()) {
                    definitionLevels.put(entry.getKey(), getCellAsInt(row, entry.getValue()));
                }
                element.setDefinitionLevels(definitionLevels);

                // Level
                int level = getCellAsInt(row, header.levelOffset);
                element.setLevel(level);

                // Score
                int score = getCellAsInt(row, header.scoreOffset);
                element.setScore(score);

                // Max Score
                int maxScore = getEvaluatedCellAsInt(row.getCell(header.maxScoreOffset));
                element.setMaxScore(maxScore);

                // Comments
                String comment = getEvaluatedCellAsString(row.getCell(header.commentsOffset));
                element.setComments(comment);

                elements.add(element);
            }

            return elements;
        }

    }

    private class Header {
        private int categoryOffset;
        private int elementsOffset;
        private int defLevelOffset;
        private Map<String, Integer> levelsOffsets;
        private int levelOffset;
        private int scoreOffset;
        private int maxScoreOffset;
        private int commentsOffset;

        public int getDefLevelOffset() {
            return defLevelOffset;
        }

        public Map<String, Integer> getLevelsOffsets() {
            return levelsOffsets;
        }

        public int getSpecificLevelOffset(String level) {
            return levelsOffsets.get(level);
        }

        public int getLevelOffset() {
            return levelOffset;
        }

        private int getScoreOffset() {
            return scoreOffset;
        }

        private int getMaxScoreOffset() {
            return maxScoreOffset;
        }

        private int getCommentsOffset() {
            return commentsOffset;
        }
    }


    private void buildHeader() {
        for(int i = 0; i < sheet.getPhysicalNumberOfRows() - 1; i++) {
            Row row = sheet.getRow(i);
            if (row != null && hasAllValues(row, categoryFields)) {
                header = new Header();
                header.categoryOffset = getMatchedCellIndex(row, categoryFields[0]);
                header.defLevelOffset = getMatchedCellIndex(row, categoryFields[1]);
                header.levelOffset = getMatchedCellIndex(row, categoryFields[2]);
                header.scoreOffset = getMatchedCellIndex(row, categoryFields[3]);
                header.maxScoreOffset = getMatchedCellIndex(row, categoryFields[4]);
                header.commentsOffset = getMatchedCellIndex(row, categoryFields[5]);

                // check for 'Elements' part of a Header
                row = sheet.getRow(i + 1);
                if (row != null && hasAllValues(row, elementFields)) {
                    Map<String, Integer> defLevels = new HashMap<String, Integer>();
                    for (int levelIndex = header.defLevelOffset; levelIndex < header.levelOffset; levelIndex++) {
                        String key = getCellAsString(row, levelIndex);
                        defLevels.put(key, levelIndex);
                    }
                    header.levelsOffsets = defLevels;
                    break;
                }

                // 'Elements' part of a Header was not found, Header should not be created
                header = null;
            }
        }
    }

    // @todo Consider moving to a helper class
    protected static int findSingleCellIndex(Row row) {
        int numberOfCells = row.getPhysicalNumberOfCells();
        int index = -1;

        if (row != null) {
            for(int i = 0; i < numberOfCells - 1; i++) {
                Cell cell = row.getCell(i);
                // @todo Investigate the ways to determine the empty cell and/or strip whitespaces
                if (!cell.toString().equals("")) {
                    if (index > 0) {
                        return -1;
                    }
                    index = i;
                }
            }
        }

        return index;
    }

    protected static int findNthNonEmptyColumnIndex(Row row, int n) {
        int index = -1;
        if (row != null) {
            for(int i = 0; i < row.getPhysicalNumberOfCells() - 1; i++) {
                String cellContent = row.getCell(i).toString().trim();
                if (cellContent.length() > 0) {
                    if (--n <= 0) {
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }

    protected static int getMatchedCellIndex(Row row, String pattern) {
        int numberOfCells = row.getPhysicalNumberOfCells();
        int index = -1;

        if (row != null) {
            for(int i = 0; i < numberOfCells; i++) {
                Cell cell = row.getCell(i);
                if (cell != null && cell.toString().trim().matches(pattern)) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    protected static boolean hasValue(Row row, String value) {
        boolean result = false;
        if (row != null) {
            Iterator<Cell> cellIterator = row.iterator();
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().equals(value)) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    protected static boolean hasAllValues(Row row, String[] patterns) {
        boolean hasAll = true;
        if (row == null) {
            return false;
        }
        for (String pattern : patterns) {
            if (getMatchedCellIndex(row, pattern) == -1) {
                hasAll = false;
                break;
            }
        }

        return hasAll;
    }

    protected static boolean isFirstFilledCell(Row row, String value) {
        boolean isFirstFilled = false;
        if (row != null) {
            int firstNonEmptyColIndex = findNthNonEmptyColumnIndex(row, 1);
            if (row.getCell(firstNonEmptyColIndex).equals(value)) {
                isFirstFilled = true;
            }
        }

        return isFirstFilled;
    }

    protected static Object getValueAt(Row row, int position, HSSFWorkbook workbook1) {
        Object value = null;

        // @todo consider other cell types
        Cell cell = row.getCell(position);
        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_FORMULA:
                FormulaEvaluator evaluator = workbook1.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                value = cellValue.getNumberValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = cell.getNumericCellValue();
                break;
        }

        return value;
    }

    private static int getCellAsInt(Row row, int index) {
        return (int)row.getCell(index).getNumericCellValue();
    }

    private static String getCellAsString(Row row, int index) {
        return row.getCell(index).toString().trim();
    }

    private int getEvaluatedCellAsInt(Cell cell) {
        CellValue cellValue = formulaEvaluator.evaluate(cell);

        return (int) cellValue.getNumberValue();
    }

    private String getEvaluatedCellAsString(Cell cell) {
        CellValue cellValue = formulaEvaluator.evaluate(cell);

        return cellValue.getStringValue();
    }
}
