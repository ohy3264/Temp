package com.hw.oh.utility;


import com.hw.oh.model.WorkItem;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Alignment;
import jxl.write.Border;
import jxl.write.BorderLineStyle;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by oh on 2015-07-22.
 */
public class HYExcelWrite {
  public HYExcelWrite() {


  }

  public void excelWrite(String albaName, File file, Map<String, Object> header, ArrayList<WorkItem> data)
      throws IOException, WriteException {
    WorkbookSettings ws = new WorkbookSettings();
    ws.setEncoding("UTF-8");
    WritableWorkbook wb = Workbook.createWorkbook(file, ws);
    WritableSheet sh = wb.createSheet((String) header.get("title"), 0);
    sh.setColumnView(0, 10);
    sh.setColumnView(1, 10);
    sh.setColumnView(2, 10);
    sh.setColumnView(3, 10);
    sh.setColumnView(4, 10);
    sh.setColumnView(5, 10);
    sh.setColumnView(6, 10);
    sh.setColumnView(7, 10);
    sh.setColumnView(8, 10);
    sh.setColumnView(9, 10);
    sh.setColumnView(10, 10);
    sh.setColumnView(11, 10);
    sh.setColumnView(12, 10);
    sh.setColumnView(13, 10);
    sh.setColumnView(14, 10);
    sh.setColumnView(15, 10);
    sh.setColumnView(16, 10);
    sh.setColumnView(17, 10);


    //Title font
    WritableFont arial20font = new WritableFont(WritableFont.ARIAL, // 폰트이름
        20, // 폰트 크기
        WritableFont.BOLD, // 폰트 두께
        false, // 이탤릭 사용 유무
        UnderlineStyle.DOUBLE, // 밑줄 스타일
        Colour.BLACK);  // 폰트색

    //Title format
    WritableCellFormat titleFormat = new WritableCellFormat(arial20font);
    titleFormat.setAlignment(Alignment.CENTRE);
    titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

    //header format
    WritableCellFormat headerFormat = new WritableCellFormat();
    headerFormat.setAlignment(Alignment.CENTRE);
    headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
    headerFormat.setBackground(Colour.VERY_LIGHT_YELLOW);
    headerFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM);

    //Text format
    WritableCellFormat textFormat = new WritableCellFormat();
    textFormat.setAlignment(Alignment.CENTRE);
    textFormat.setBorder(Border.ALL, BorderLineStyle.THIN);


    //time format
    DateFormat timefmt = new DateFormat("hh:mm");
    WritableCellFormat timeformat = new WritableCellFormat(timefmt);
    WritableCellFormat timeformat_false = new WritableCellFormat(timefmt);

    timeformat.setAlignment(Alignment.CENTRE);
    timeformat.setBorder(Border.ALL, BorderLineStyle.THIN);
    timeformat_false.setAlignment(Alignment.CENTRE);
    timeformat_false.setBorder(Border.ALL, BorderLineStyle.THIN);
    timeformat_false.setBackground(Colour.RED);

    //Integer format
    jxl.write.NumberFormat numberFormat = new NumberFormat("###,##0");
    WritableCellFormat integerFormat = new WritableCellFormat(numberFormat);
    WritableCellFormat integerFormat_false = new WritableCellFormat(numberFormat);
    integerFormat.setAlignment(Alignment.RIGHT);
    integerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
    integerFormat_false.setAlignment(Alignment.RIGHT);
    integerFormat_false.setBorder(Border.ALL, BorderLineStyle.THIN);
    integerFormat_false.setBackground(Colour.RED);

    int titleRow = 0;
    int row = 3;
    sh.mergeCells(0, titleRow, 19, titleRow);
    sh.setRowView(titleRow, 800);
    sh.setRowView(row, 300);
    sh.addCell(new jxl.write.Label(0, titleRow, albaName, titleFormat));
    sh.addCell(new jxl.write.Label(0, row, (String) header.get("date"), headerFormat));
    sh.addCell(new jxl.write.Label(1, row, (String) header.get("startTime"), headerFormat));
    sh.addCell(new jxl.write.Label(2, row, (String) header.get("endTime"), headerFormat));
    sh.addCell(new jxl.write.Label(3, row, (String) header.get("dayTime"), headerFormat));
    sh.addCell(new jxl.write.Label(4, row, (String) header.get("addTime"), headerFormat));
    sh.addCell(new jxl.write.Label(5, row, (String) header.get("nightTime"), headerFormat));
    sh.addCell(new jxl.write.Label(6, row, (String) header.get("refreshTime"), headerFormat));
    sh.addCell(new jxl.write.Label(7, row, (String) header.get("allTime"), headerFormat));
    sh.addCell(new jxl.write.Label(8, row, (String) header.get("hourPay"), headerFormat));
    sh.addCell(new jxl.write.Label(9, row, (String) header.get("etcNum"), headerFormat));
    sh.addCell(new jxl.write.Label(10, row, (String) header.get("etcNumPay"), headerFormat));
    sh.addCell(new jxl.write.Label(11, row, (String) header.get("dayPay"), headerFormat));
    sh.addCell(new jxl.write.Label(12, row, (String) header.get("addPay"), headerFormat));
    sh.addCell(new jxl.write.Label(13, row, (String) header.get("nightPay"), headerFormat));
    sh.addCell(new jxl.write.Label(14, row, (String) header.get("etcAllPay"), headerFormat));
    sh.addCell(new jxl.write.Label(15, row, (String) header.get("weekPay"), headerFormat));
    sh.addCell(new jxl.write.Label(16, row, (String) header.get("refreshPay"), headerFormat));
    sh.addCell(new jxl.write.Label(17, row, (String) header.get("gabulPay"), headerFormat));
    sh.addCell(new jxl.write.Label(18, row, (String) header.get("allPay"), headerFormat));
    sh.addCell(new jxl.write.Label(19, row, (String) header.get("simpleMemo"), headerFormat));
    row++;

    for (WorkItem tem : data) {
      sh.addCell(new jxl.write.Label(0, row, HYStringUtil.dateFunction(tem.getYear(), tem.getMonth(), tem.getDay()), textFormat));
      sh.addCell(new jxl.write.DateTime(1, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getStartTimeHour()), Integer.parseInt(tem.getStartTimeMin())), timeformat));
      sh.addCell(new jxl.write.DateTime(2, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getEndTimeHour()), Integer.parseInt(tem.getEndTimeMin())), timeformat));
      sh.addCell(new jxl.write.DateTime(3, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getDayTimeHour()), Integer.parseInt(tem.getDayTimeMin())), timeformat));
      sh.addCell(new jxl.write.DateTime(7, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getWorkTimeHour()), Integer.parseInt(tem.getWorkTimeMin())), timeformat));
      sh.addCell(new jxl.write.Number(8, row, tem.getHourMoney(), integerFormat));
      sh.addCell(new jxl.write.Number(9, row, Integer.parseInt(tem.getWorkEtcNum()), integerFormat));
      sh.addCell(new jxl.write.Number(10, row, Integer.parseInt(tem.getWorkEtcMoney()), integerFormat));
      sh.addCell(new jxl.write.Number(11, row, Double.parseDouble(tem.getWorkPayDay()), integerFormat));
      sh.addCell(new jxl.write.Number(18, row, tem.getTotalMoney(), integerFormat));
      sh.addCell(new jxl.write.Label(19, row, tem.getSimpleMemo(), textFormat));


      if (Boolean.parseBoolean(tem.getWorkAdd())) {
        sh.addCell(new jxl.write.Number(12, row, Double.parseDouble(tem.getWorkPayAdd()), integerFormat));
        sh.addCell(new jxl.write.DateTime(4, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getAddTimeHour()), Integer.parseInt(tem.getAddTimeMin())), timeformat));
      } else {
        sh.addCell(new jxl.write.Number(12, row, Double.parseDouble(tem.getWorkPayAdd()), integerFormat_false));
        sh.addCell(new jxl.write.DateTime(4, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getAddTimeHour()), Integer.parseInt(tem.getAddTimeMin())), timeformat_false));
      }
      if (Boolean.parseBoolean(tem.getWorkNight())) {
        sh.addCell(new jxl.write.Number(13, row, Double.parseDouble(tem.getWorkPayNight()), integerFormat));
        sh.addCell(new jxl.write.DateTime(5, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getNightTimeHour()), Integer.parseInt(tem.getNightTimeMin())), timeformat));
      } else {
        sh.addCell(new jxl.write.Number(13, row, Double.parseDouble(tem.getWorkPayNight()), integerFormat_false));
        sh.addCell(new jxl.write.DateTime(5, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getNightTimeHour()), Integer.parseInt(tem.getNightTimeMin())), timeformat_false));
      }
      if (Boolean.parseBoolean(tem.getWorkEtc())) {
        sh.addCell(new jxl.write.Number(14, row, Integer.parseInt(tem.getWorkEtcNum()) * Integer.parseInt(tem.getWorkEtcMoney()), integerFormat));
      } else {
        sh.addCell(new jxl.write.Number(14, row, Integer.parseInt(tem.getWorkEtcNum()) * Integer.parseInt(tem.getWorkEtcMoney()), integerFormat_false));
      }
      if (Boolean.parseBoolean(tem.getWorkWeek())) {
        sh.addCell(new jxl.write.Number(15, row, Double.parseDouble(tem.getWorkPayWeek()), integerFormat));
      } else {
        sh.addCell(new jxl.write.Number(15, row, Double.parseDouble(tem.getWorkPayWeek()), integerFormat_false));
      }
      if (Boolean.parseBoolean(tem.getWorkRefresh())) {
        sh.addCell(new jxl.write.Number(16, row, Double.parseDouble(tem.getWorkPayRefresh()), integerFormat));
        sh.addCell(new jxl.write.DateTime(6, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getRefreshTimeHour()), Integer.parseInt(tem.getRefreshTimeMin())), timeformat));
      } else {
        sh.addCell(new jxl.write.Number(16, row, Double.parseDouble(tem.getWorkPayRefresh()), integerFormat_false));
        sh.addCell(new jxl.write.DateTime(6, row, HYTimeUtill.timeReturn(Integer.parseInt(tem.getRefreshTimeHour()), Integer.parseInt(tem.getRefreshTimeMin())), timeformat_false));
      }
      if (Boolean.parseBoolean(tem.getWorkPayGabul())) {
        sh.addCell(new jxl.write.Number(17, row, Double.parseDouble(tem.getWorkPayGabulValue()), integerFormat));
      } else {
        sh.addCell(new jxl.write.Number(17, row, Double.parseDouble(tem.getWorkPayGabulValue()), integerFormat_false));
      }
      row++;
    }
    wb.write();
    wb.close();

  }

}
