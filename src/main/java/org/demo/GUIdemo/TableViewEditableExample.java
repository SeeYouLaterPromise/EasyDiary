package org.demo.GUIdemo;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TableViewEditableExample extends Application {

    private TableView<Person> tableView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // 创建ObservableList，用于存储表格的数据
        ObservableList<Person> data = FXCollections.observableArrayList(
                new Person("John", "Doe"),
                new Person("Jane", "Smith"),
                new Person("Mark", "Johnson")
        );

        // 创建 TableView
        tableView = new TableView<>();

        // 创建并配置 "First Name" 列
        TableColumn<Person, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());  // 允许单元格编辑
        firstNameColumn.setEditable(true); // 设置列可编辑
        firstNameColumn.setOnEditCommit(event -> {
            // 更新列表中的数据
            event.getRowValue().setFirstName(event.getNewValue());
        });

        // 创建并配置 "Last Name" 列
        TableColumn<Person, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());  // 允许单元格编辑
        lastNameColumn.setEditable(true); // 设置列可编辑
        lastNameColumn.setOnEditCommit(event -> {
            // 更新列表中的数据
            event.getRowValue().setLastName(event.getNewValue());
        });

        // 将列添加到 TableView
        tableView.getColumns().addAll(firstNameColumn, lastNameColumn);
        tableView.setItems(data);  // 设置表格的数据源
        tableView.setEditable(true); // 启用表格的编辑功能

        // 创建根容器并添加 TableView
        StackPane root = new StackPane();
        root.getChildren().add(tableView);

        // 创建并设置场景
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Editable TableView Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Person 类
    public static class Person {
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;

        public Person(String firstName, String lastName) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String firstName) {
            this.firstName.set(firstName);
        }

        public SimpleStringProperty firstNameProperty() {
            return firstName;
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String lastName) {
            this.lastName.set(lastName);
        }

        public SimpleStringProperty lastNameProperty() {
            return lastName;
        }
    }
}

