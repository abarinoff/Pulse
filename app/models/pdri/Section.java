package models.pdri;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Section {
    public static final int INITIAL_VERTICAL_OFFSET = 6;

    // Pattern to match the "Section" section of the worksheet
    private static final String PATTERN = "^SECTION (I){1,3} - ";

    private HSSFSheet sheet;
    private String title;

    private List<Category> categories = new LinkedList<Category>();

    private int totalScore;
    private int totalMaxScore;

    public Section(HSSFSheet sheet) {
        this.sheet = sheet;
    }

    public String parse() {

        // Look for a Section Title
        String value = "";

        // @todo Consider removing initialization
        int sectionOffset = 0;

        boolean match = false;
        for(int rowIndex = INITIAL_VERTICAL_OFFSET - 1; !match && rowIndex < sheet.getPhysicalNumberOfRows() - 1; rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            Iterator<Cell> cellIterator = row.cellIterator();
            while(!match && cellIterator!= null && cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        String val = cell.getStringCellValue();
                        if (val.matches(PATTERN)) {
                            match = true;
                            this.title = val;

                            sectionOffset = rowIndex;
                        }
                    break;
                }
            }
        }

        // We know the offset of the Category row, extract categories
        categories = Category.extractCategories(sheet, sectionOffset + 1);

        return value;
    }

  /*  private String extractSectionName(String fullSectionName) {

    }*/
}
