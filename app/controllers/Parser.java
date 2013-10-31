package controllers;

import models.pdri.Section;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Parser extends Controller {
    public static final String SHEET_NAME_PATTERN = "^Section (I){1,3} - [^(Unweighted)].*";

    public static final String TEST_FILE_PATH = "data/PDRI Industrial Example - Sandra.xls";

    public static Result parse() {
        HSSFWorkbook workbook = readWorkbook(TEST_FILE_PATH);
        List<HSSFSheet> sheets = getMatchingSheets(workbook, SHEET_NAME_PATTERN);

        // So far we parse only the very first sheet (for now we are guaranteed to have elements in the list)
        HSSFSheet sheet = sheets.get(0);

        Section section = new Section(sheet);
        String result = sheet.getSheetName() + " " + section.parse();

        /*StringBuilder sb = new StringBuilder();
        for (HSSFSheet sheet : sheets) {
            sb.append(sheet.getSheetName() + "\r\n");
        }*/

        return ok(result);
    }

    private static HSSFWorkbook readWorkbook(String path) {
        HSSFWorkbook workbook = null;

        try {
            FileInputStream file = new FileInputStream(new File(path));
            workbook = new HSSFWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workbook;
    }

    private static List<HSSFSheet> getMatchingSheets(HSSFWorkbook workbook, String namePattern) {
        List<HSSFSheet> sheets = new LinkedList<HSSFSheet>();

        int count = workbook.getNumberOfSheets();
        for(int i = 0; i < count; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().matches(namePattern)) {
                sheets.add(sheet);
            }
        }

        return sheets;
    }
}
