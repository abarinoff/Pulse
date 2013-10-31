package models.pdri;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

public abstract class PDRIEntity {
    protected HSSFSheet sheet;

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
