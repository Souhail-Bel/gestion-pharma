package application.services;

import com.itextpdf.io.exceptions.IOException;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class PDFService {
	
	// soit par list + col exclu(s)
	// soit par list + total + col exclu(s)
	

	public static <T> void exportTableViewToPDF(TableView<T> tableView, String title, Stage stage, String... colExclus) {
		exportTableViewToPDFTotal(tableView, title, stage, null, colExclus);
	}
	
    public static <T> void exportTableViewToPDFTotal(TableView<T> tableView, String title, Stage stage, String totalInfo, String... colExclus) {
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
                
                DeviceRgb accent = new DeviceRgb(0, 150, 150);
                DeviceRgb evenLighterGrayForPrinters = new DeviceRgb(245, 245, 245);
                
                
                document.add(new Paragraph("PHARMA SYS")
                        .setFontSize(24)
                        .setBold()
                        .setFontColor(accent)
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


                
                // tous
                //int numCols = tableView.getColumns().size();
                
                
                List<String> listExclusion = Arrays.asList(colExclus);
                
                int numCols = 0;
                
                for(TableColumn<T, ?> col : tableView.getColumns())
                	if(!listExclusion.contains(col.getText()))
                		numCols++;
                
                Table table = new Table(UnitValue.createPercentArray(numCols)).useAllAvailableWidth();


                
                for (TableColumn<T, ?> col : tableView.getColumns()) {
                	
                	if(listExclusion.contains(col.getText())) continue;
                	
                    Cell headerCell = new Cell();
                    headerCell.add(new Paragraph(col.getText()).setBold());
                    
                    headerCell.setBackgroundColor(accent);
                    headerCell.setFontColor(ColorConstants.WHITE);
                    headerCell.setTextAlignment(TextAlignment.CENTER);
                    headerCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    //headerCell.setPadding(2);
                    headerCell.setBold();
                    
                    table.addHeaderCell(headerCell);
                }


                
                
                ObservableList<T> items = tableView.getItems();
                boolean isEven = false;

                for (T item : items) {
                    for (TableColumn<T, ?> col : tableView.getColumns()) {
                    	
                    	if(listExclusion.contains(col.getText())) continue;
                    	
                        Object cellData = col.getCellData(item);
                        String value = (cellData == null) ? "-" : cellData.toString();
                        
                        Cell cell = new Cell();
                        cell.add(new Paragraph(value).setFontSize(10));
                        cell.setPadding(5);
                        cell.setTextAlignment(TextAlignment.CENTER);
                        

                        
                        if (isEven)
                            cell.setBackgroundColor(evenLighterGrayForPrinters);
                        
                        cell.setBorderTop(Border.NO_BORDER);
                        cell.setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
                        
                        table.addCell(cell);
                    }
                    isEven = !isEven;
                }

                document.add(table);
                
                
                
                if(totalInfo != null && !totalInfo.isEmpty()) {
                	document.add(new Paragraph("\n"));
                	
                	document.add(new Paragraph(totalInfo)
                			.setTextAlignment(TextAlignment.RIGHT)
                			.setFontSize(14)
                			.setBold()
                			.setBorderBottom(new SolidBorder(accent, 1))
                	);
                	
                }
                
                
                
                

                document.add(new Paragraph("\n--- FIN DOCUMENT ---")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.GRAY)
                        .setMarginTop(20));

                document.close();
                
                System.out.println("PDF exporté avec succès: " + file.getAbsolutePath());
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous ouvrir le PDF maintenant?",
                		ButtonType.YES, ButtonType.NO);
                alert.setTitle("Fin export");
                alert.setHeaderText("Succès sauvegarde de "+title+" !");
                
                if(alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                	// separer JavaFX et AWT threads
                	// resoudre interblocage sur Linux
                	new Thread(() -> {
                		try {
	                		if(Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
	                	} catch(java.io.IOException ex) {
	                		ex.printStackTrace();
	                	}
                	}).start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}