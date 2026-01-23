package application.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFService {

    public static <T> void exportTableViewToPDF(TableView<T> tableView, String title, Stage stage) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"));
            fileChooser.setInitialFileName(title.replace(" ", "_") + "_" +timestamp + ".pdf");
            
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                
                
                DeviceRgb brandColor = new DeviceRgb(0, 150, 136);
                DeviceRgb lightGray = new DeviceRgb(245, 245, 245);


                
                document.add(new Paragraph("PHARMA SYS")
                        .setFontSize(24)
                        .setBold()
                        .setFontColor(brandColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5));

                document.add(new Paragraph("Rapport: " + title.toUpperCase())
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(2));

                document.add(new Paragraph("Généré le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")))
                        .setFontSize(10)
                        .setFontColor(ColorConstants.GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20));


                
                int numCols = tableView.getColumns().size();
                Table table = new Table(UnitValue.createPercentArray(numCols)).useAllAvailableWidth();


                
                for (TableColumn<T, ?> col : tableView.getColumns()) {
                    Cell headerCell = new Cell();
                    headerCell.add(new Paragraph(col.getText()).setBold());

                    
                    
                    
                    headerCell.setBackgroundColor(brandColor);
                    headerCell.setFontColor(ColorConstants.WHITE);
                    headerCell.setTextAlignment(TextAlignment.CENTER);
                    headerCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    //headerCell.setPadding(2);
                    
                    table.addHeaderCell(headerCell);
                }


                
                
                ObservableList<T> items = tableView.getItems();
                boolean isEven = false;

                for (T item : items) {
                    for (TableColumn<T, ?> col : tableView.getColumns()) {
                        Object cellData = col.getCellData(item);
                        String value = (cellData == null) ? "-" : cellData.toString();
                        
                        Cell cell = new Cell();
                        cell.add(new Paragraph(value).setFontSize(10));
                        cell.setPadding(5);
                        cell.setTextAlignment(TextAlignment.CENTER);
                        

                        
                        if (isEven) {
                            cell.setBackgroundColor(lightGray);
                        }
                        
                        
                        
                        cell.setBorderTop(Border.NO_BORDER);
                        cell.setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
                        
                        table.addCell(cell);
                    }
                    isEven = !isEven;
                }

                document.add(table);
                

                document.add(new Paragraph("\n--- FIN DOCUMENT ---")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.GRAY)
                        .setMarginTop(20));

                document.close();
                
                System.out.println("PDF exporté avec succès: " + file.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}