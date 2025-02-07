package org.demo;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;  // 确保导入的是 JavaFX 的 Button


public class TableSelectApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        TableView<Item> tblItems = new TableView<>();
        tblItems.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox.setVgrow(tblItems, Priority.ALWAYS );

        TableColumn<Item, String> colSKU = new TableColumn<>("SKU");
        TableColumn<Item, String> colDescr = new TableColumn<>("Item");
        TableColumn<Item, Float> colPrice = new TableColumn<>("Price");
        TableColumn<Item, Boolean> colTaxable = new TableColumn<>("Tax");

        colSKU.setCellValueFactory( new PropertyValueFactory<>("sku") );
        colDescr.setCellValueFactory( new PropertyValueFactory<>("descr") );
        colPrice.setCellValueFactory( new PropertyValueFactory<>("price") );
        colTaxable.setCellValueFactory( new PropertyValueFactory<>("taxable") );

        tblItems.getColumns().addAll(
                colSKU, colDescr, colPrice, colTaxable
        );

        tblItems.getItems().addAll(
                new Item("KBD-0455892", "Mechanical Keyboard", 100.0f, true),
                new Item( "145256", "Product Docs", 0.0f, false ),
                new Item( "OR-198975", "O-Ring (100)", 10.0f, true)
        );

        Button btnInventory = new Button("Inventory");
        Button btnCalcTax = new Button("Tax");

        btnInventory.disableProperty().bind(
                tblItems.getSelectionModel().selectedItemProperty().isNull()
        );

        btnCalcTax.disableProperty().bind(
                tblItems.getSelectionModel().selectedItemProperty().isNull().or(
                        Bindings.select(
                                tblItems.getSelectionModel().selectedItemProperty(),
                                "taxable"
                        ).isEqualTo(false)
                )
        );

        HBox buttonHBox = new HBox( btnInventory, btnCalcTax );
        buttonHBox.setSpacing( 8 );

        VBox vbox = new VBox( tblItems, buttonHBox );
        vbox.setPadding( new Insets(10) );
        vbox.setSpacing( 10 );

        Scene scene = new Scene(vbox);

        primaryStage.setTitle("TableSelectApp");
        primaryStage.setScene( scene );
        primaryStage.setHeight( 376 );
        primaryStage.setWidth( 667 );
        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}