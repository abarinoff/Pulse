package controllers;

import models.Section;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import play.mvc.Controller;
import play.mvc.Result;
import util.Parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ParserController extends Controller {
    public static final String TEST_FILE_PATH = "data/PDRI Industrial Example - Sandra.xls";

    public static Result parse() {
        HSSFWorkbook workbook = readWorkbook(TEST_FILE_PATH);
        Parser parser = new Parser(workbook);
        List<Section> sections = parser.parse();

        StringBuilder sb = new StringBuilder();
        for (Section section : sections) {
            sb.append(section.toJson());
        }

        return ok(sb.toString()).as("application/json");
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
}
