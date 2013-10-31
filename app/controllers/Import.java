package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Import extends Controller {

    public static Result importPdri() {
        Http.MultipartFormData multipartFormData = requestAsMultipart();

        List<Http.MultipartFormData.FilePart> documents = multipartFormData.getFiles();
        Http.MultipartFormData.FilePart document = documents.get(0);

        ObjectNode result;
        try {
            result = processDocument(document.getFile());
        } catch (IOException e) {
            result = emptyJsonResponse();
        }

        return ok(result);
    }

    private static ObjectNode processDocument(File document) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        FileInputStream file = new FileInputStream(document);

        //Get the workbook instance for XLS file
        HSSFWorkbook workbook = new HSSFWorkbook(file);

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            ArrayNode sheetNode = mapper.createArrayNode();

            Iterator<Row> rowIterator = sheet.iterator();
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ArrayNode rowNode = mapper.createArrayNode();

                Iterator<Cell> cellIterator = row.cellIterator();
                while(cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch(cell.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            rowNode.add(cell.getBooleanCellValue());
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            rowNode.add(cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                            rowNode.add(cell.getStringCellValue());
                            break;
                    }
                }

                sheetNode.add(rowNode);
            }

            responseJson.put(sheet.getSheetName(), sheetNode);
        }
        file.close();

        return responseJson;
    }

    public static Http.MultipartFormData requestAsMultipart() {
        Http.RequestBody requestBody = request().body();
        return requestBody.asMultipartFormData();
    }

    public static ObjectNode emptyJsonResponse() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseJson = mapper.createObjectNode();

        return responseJson;
    }
}
