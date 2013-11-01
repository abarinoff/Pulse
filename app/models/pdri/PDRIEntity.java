package models.pdri;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

public abstract class PDRIEntity {
    protected HSSFSheet sheet;
    protected HSSFWorkbook workbook;

    protected static boolean isValueInRow(Row row, String value) {
        Iterator<Cell> cellIterator = row.iterator();
        while(cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    protected static int findCellMatchIndex(Row row, String pattern) {
        int numberOfCells = row.getPhysicalNumberOfCells();
        int index = -1;

        for(int i = 0; i < numberOfCells - 1; i++) {
            Cell cell = row.getCell(i);
            if (cell.toString().matches(pattern)) {
                index = i;
                break;
            }
        }

        return index;
    }

    protected static int findNthNonEmptyColumnIndex(Row row, int n) {
        int index = -1;
        for(int i = 0; i < row.getPhysicalNumberOfCells() - 1; i++) {
            String cellContent = row.getCell(i).toString().trim();
            if (cellContent.length() > 0) {
                if (--n <= 0) {
                    index = i;
                    break;
                }
            }
        }

        return index;
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

    protected static int findSingleCellIndex(Row row) {
        int numberOfCells = row.getPhysicalNumberOfCells();
        int index = -1;

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
        return index;
    }
}
