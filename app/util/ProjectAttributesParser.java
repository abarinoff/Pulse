package util;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ProjectAttributesParser extends Parser {
    public static final String PDRI_COVER_PATTERN = "^(?!)PDRI Cover\\s*";

    private static final String PROJECT_ATTRIBUTE_PATTERN = "^(?!)Project:\\s";
    private static final String PROJECT_MANAGER_ATTRIBUTE_PATTERN = "^(?!)Client:\\s";
    private static final String CLIENT_ATTRIBUTE_PATTERN = "^(?!)Project\\s+Manager:\\s";
    private static final String CLIENT_NUMBER_ATTRIBUTE_PATTERN = "^(?!)Client\\s+No\\.:\\s";
    private static final String OTHER_CONTROL_NUMBER_ATTRIBUTE_PATTERN = "^(?!)Other\\s+Control\\s+No\\.:\\s";


    public ProjectAttributesParser(HSSFWorkbook workbook) {
        super(workbook);
    }

    // @todo To be renamed to "parse()"
    public void parseAttributes() {
        HSSFSheet coverSheet = getCoverSheet();
        if (coverSheet != null) {

        }
    }

    private HSSFSheet getCoverSheet() {
        HSSFSheet coverSheet = null;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String sheetName = workbook.getSheetName(i);

            if (sheetName.matches(PDRI_COVER_PATTERN)) {
                coverSheet = workbook.getSheetAt(i);
                break;
            }
        }

        return coverSheet;
    }
}
