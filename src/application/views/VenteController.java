package application.views;

import application.dao.clientDAO;
import application.dao.stockDAO;
import application.dao.venteDAO;
import application.modeles.*;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class VenteController {

    // catalog
    @FXML private TextField txtSearch;
    @FXML private TableView<Stock> tableProduits;
    @FXML private TableColumn<Stock, String> colProdNom, colProdPrix;
    @FXML private TableColumn<Stock, Integer> colProdStock;
    @FXML private TableColumn<Stock, Void> colProdAction;

    // carte/client
    @FXML private ComboBox<Client> cmbClient;
    @FXML private TableView<LigneVente> tablePanier;
    @FXML private TableColumn<LigneVente, String> colPanierNom, colPanierTotal;
    @FXML private TableColumn<LigneVente, Integer> colPanierQte;
    @FXML private TableColumn<LigneVente, Void> colPanierAction;
    
    @FXML private Label lblTotal;
    @FXML private Button btnValider;

    // données
    private ObservableList<LigneVente> panier = FXCollections.observableArrayList();
    private FilteredList<Stock> filteredStock;

    @FXML
    public void initialize() {
        setupProductTable();
        setupPanierTable();
        setupClientSection();
        setupSearch();
    }


    private void setupClientSection() {
        cmbClient.setItems(DataService.getClients());
        

        cmbClient.setConverter(new StringConverter<Client>() {
            @Override public String toString(Client c) { return c == null ? "" : c.getNom(); }
            @Override public Client fromString(String string) { return null; }
        });


        DataService.getClients().stream()
            .filter(c -> c.getNom().equalsIgnoreCase("Anonyme"))
            .findFirst()
            .ifPresent(cmbClient::setValue);
    }

    @FXML
    private void handleAjoutClient(ActionEvent event) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Client");
        dialog.setHeaderText("Ajouter un client");


        TextField txtNom = new TextField(); 
        txtNom.setPromptText("Nom & Prénom");
        
        TextField txtTel = new TextField(); 
        txtTel.setPromptText("Téléphone (8 chiffres)");
        

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
        
        grid.add(new Label("Nom :"), 0, 0); 
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Tél :"), 0, 1); 
        grid.add(txtTel, 1, 1);
        grid.add(lblError, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        final Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        
        btnOk.addEventFilter(ActionEvent.ACTION, ae -> {
            String name = txtNom.getText().trim();
            String phone = txtTel.getText().trim();
            
            txtNom.setStyle("");
            txtTel.setStyle("");
            lblError.setText("");

            // verifier nom
            if (name.isEmpty()) {
                txtNom.setStyle("-fx-border-color: red;");
                lblError.setText("Le nom est obligatoire.");
                ae.consume();
                return;
            }
            
            // verifier tele
            if (!phone.matches("\\d{8}")) {
                txtTel.setStyle("-fx-border-color: red;");
                lblError.setText("Le téléphone doit contenir 8 chiffres.");
                ae.consume(); 
                return;
            }
        });


        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                int nextId = DataService.getClients().stream().mapToInt(Client::getId).max().orElse(0) + 1;
                return new Client(nextId, txtNom.getText().trim(), "", txtTel.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(tempClient -> {
            try {
                // 1. SAVE TO DB
                clientDAO dao = new clientDAO(application.resources.DatabaseConnection.getConnection());
                int newId = dao.save(tempClient);
                
                // 2. UPDATE OBJECT WITH REAL ID
                Client realClient = new Client(newId, tempClient.getNom(), "", tempClient.getTelephone());
                
                // 3. ADD TO MEMORY
                DataService.getClients().add(realClient);
                cmbClient.setValue(realClient);
                
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible de sauvegarder le client dans la base de données.");
                alert.show();
            }
        });
    }

    // table produits
    private void setupProductTable() {
        filteredStock = new FilteredList<>(DataService.getStockGlobal(), p -> true);
        tableProduits.setItems(filteredStock);

        colProdNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colProdStock.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));
        colProdPrix.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f TND", cell.getValue().getProduit().getPrixVente())));

        colProdAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnAdd = new Button("+");
            {
                btnAdd.setStyle("-fx-background-color: #0d9488; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btnAdd.setOnAction(e -> addToCart(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnAdd);
            }
        });
    }

    // panier
    private void setupPanierTable() {
        tablePanier.setItems(panier);
        colPanierNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colPanierQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPanierTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.1f", cell.getValue().getSousTotal())));

        colPanierAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnRem = new Button("✖");
            {
                btnRem.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-font-weight: bold; -fx-cursor: hand;");
                btnRem.setOnAction(e -> removeFromCart(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnRem);
            }
        });
    }


    private void addToCart(Stock stockItem) {
        if (stockItem.getQuantiteDisponible() <= 0) return;


        for (LigneVente lv : panier) {
            if (lv.getProduit().getId() == stockItem.getProduit().getId()) {
                if (lv.getQuantite() < stockItem.getQuantiteDisponible()) {
                    lv.setQuantite(lv.getQuantite() + 1);
                    tablePanier.refresh();
                    updateTotal();
                }
                return;
            }
        }

        int tempId = (int) (System.currentTimeMillis() & 0xfffffff); 
        
        LigneVente newLine = new LigneVente(
            tempId, stockItem.getProduit(), 1
        );
        panier.add(newLine);
        updateTotal();
    }

    private void removeFromCart(LigneVente line) {
        panier.remove(line);
        updateTotal();
    }

    private void updateTotal() {
        double total = panier.stream().mapToDouble(LigneVente::getSousTotal).sum();
        lblTotal.setText(String.format("%.3f TND", total));
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, old, newVal) -> {
            filteredStock.setPredicate(s -> newVal == null || newVal.isEmpty() || 
                s.getProduit().getNom().toLowerCase().contains(newVal.toLowerCase()));
        });
    }

    @FXML
    private void handleValider(ActionEvent event) {
        if (panier.isEmpty()) return;

        try {
            Client client = cmbClient.getValue();
            if (client == null && !DataService.getClients().isEmpty()) {
                 client = DataService.getClients().get(0);
            }


            Vente vente = new Vente(0, LocalDateTime.now(), client.getId(), UI_Controller.getUtilisateur().getId(), 0.0);
            for (LigneVente lv : panier) {
                vente.addLigne(lv); 
            }


            venteDAO vDao = new venteDAO(application.resources.DatabaseConnection.getConnection());
            vDao.save(vente);
            

            DataService.getHistoriqueVentes().setAll(vDao.getAllVentes());


            stockDAO sDao = new application.dao.stockDAO(application.resources.DatabaseConnection.getConnection());
            DataService.getStockGlobal().setAll(sDao.getAllStocks());
            

            panier.clear();
            updateTotal();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vente validée et sauvegardée !");
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur: " + e.getMessage());
            alert.show();
        }
    }
    
    @FXML private void handleAnnuler(ActionEvent e) { panier.clear(); updateTotal(); }
}