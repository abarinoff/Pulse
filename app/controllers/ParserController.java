package controllers;

import models.Section;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import play.mvc.Controller;
import play.mvc.Result;
import util.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ParserController extends Controller {
    public static final String TEST_FILE_PATH = "data/PDRI Industrial Example - Sandra.xls";

    public static Result parse() {
        HSSFWorkbook workbook = readWorkbook(TEST_FILE_PATH);
        Parser parser = new Parser(workbook);
        Section section = parser.parse();

        return ok(section.toJson()).as("application/json");
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

    /*private static List<HSSFSheet> getMatchingSheets(HSSFWorkbook workbook, String namePattern) {
        List<HSSFSheet> sheets = new LinkedList<HSSFSheet>();

        int count = workbook.getNumberOfSheets();
        for(int i = 0; i < count; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().matches(namePattern)) {
                sheets.add(sheet);
            }
        }

        return sheets;
    }*/
}
