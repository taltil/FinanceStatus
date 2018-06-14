import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;
import java.util.List;

public class Main extends Application {

    private static CategoryInfoModel incomesInfo = new CategoryInfoModel();
    private static CategoryInfoModel outcomesInfo = new CategoryInfoModel();
    private static CategoryInfoModel depositInfo = new CategoryInfoModel();
    private static CategoryInfoModel coinInfo = new CategoryInfoModel();
    private static CategoryInfoModel stockInfo = new CategoryInfoModel();
    private static Double baseAccount = Utils.getAccountBase();
    private static double totalAmount;
    private static Label totalAmountLabel;

    public static void main(String[] args) {
        launch(args);
    }

    private static void tablesInit() {
        incomesInfo.setList(Utils.getTable(CategoryTypes.INCOME));
        outcomesInfo.setList(Utils.getTable(CategoryTypes.OUTCOME));
        depositInfo.setList(Utils.getTable(CategoryTypes.DEPOSIT));
        coinInfo.setList(Utils.getTable(CategoryTypes.COIN));
        stockInfo.setList(Utils.getTable(CategoryTypes.STOCK));
        incomesInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) incomesInfo.getList()));
        outcomesInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) outcomesInfo.getList()));
        depositInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) depositInfo.getList()));
        coinInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) coinInfo.getList()));
        stockInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) stockInfo.getList()));
        TableView<CategoryData> table;
        table = createTable(incomesInfo.getObservableList(), false);
        incomesInfo.setTable(table);
        table = createTable(outcomesInfo.getObservableList(), false);
        outcomesInfo.setTable(table);
        table = createTable(depositInfo.getObservableList(), false);
        depositInfo.setTable(table);
        table = createTable(coinInfo.getObservableList(), true);
        coinInfo.setTable(table);
        table = createTable(stockInfo.getObservableList(), true);
        stockInfo.setTable(table);
        incomesInfo.setTotal(Utils.calculateTotal(CategoryTypes.INCOME, incomesInfo.getList())
                - Utils.calculateTotal(CategoryTypes.OUTCOME, outcomesInfo.getList()));
        depositInfo.setTotal(Utils.calculateTotal(CategoryTypes.DEPOSIT, depositInfo.getList()));
        coinInfo.setTotal(Utils.calculateTotal(CategoryTypes.COIN, coinInfo.getList()));
        stockInfo.setTotal(Utils.calculateTotal(CategoryTypes.STOCK, stockInfo.getList()));
        totalAmount = baseAccount + incomesInfo.getTotal() + depositInfo.getTotal() + coinInfo.getTotal() + stockInfo.getTotal();
    }

    private static void deleteAllTable() {
        incomesInfo.getList().removeAll(incomesInfo.getList());
        outcomesInfo.getList().removeAll(outcomesInfo.getList());
        depositInfo.getList().removeAll(depositInfo.getList());
        coinInfo.getList().removeAll(coinInfo.getList());
        stockInfo.getList().removeAll(stockInfo.getList());
        incomesInfo.getObservableList().removeAll(incomesInfo.getObservableList());
        outcomesInfo.getObservableList().removeAll(outcomesInfo.getObservableList());
        depositInfo.getObservableList().removeAll(depositInfo.getObservableList());
        coinInfo.getObservableList().removeAll(coinInfo.getObservableList());
        stockInfo.getObservableList().removeAll(stockInfo.getObservableList());
        TableView<CategoryData> table;
        table = createTable(incomesInfo.getObservableList(), false);
        incomesInfo.setTable(table);
        table = createTable(outcomesInfo.getObservableList(), false);
        outcomesInfo.setTable(table);
        table = createTable(depositInfo.getObservableList(), false);
        depositInfo.setTable(table);
        table = createTable(coinInfo.getObservableList(), true);
        coinInfo.setTable(table);
        table = createTable(stockInfo.getObservableList(), true);
        stockInfo.setTable(table);
        baseAccount = 0.0;
        incomesInfo.setTotal(0.0);
        incomesInfo.getLabel().setText("Account total: " + String.format("%.2f", incomesInfo.getTotal()));
        depositInfo.setTotal(0.0);
        depositInfo.getLabel().setText("Account total: " + String.format("%.2f", depositInfo.getTotal()));
        coinInfo.setTotal(0.0);
        coinInfo.getLabel().setText("Account total: " + String.format("%.2f", coinInfo.getTotal()));
        stockInfo.setTotal(0.0);
        stockInfo.getLabel().setText("Account total: " + String.format("%.2f", stockInfo.getTotal()));
        totalAmount = 0.0;
        totalAmountLabel.setText("Total amount: " + String.format("%.2f", totalAmount));
    }

    private static TableView<CategoryData> createTable(ObservableList<CategoryData> list, boolean needPriceValue) {
        TableView<CategoryData> table = new TableView<CategoryData>();
        table.setEditable(true);
        if (needPriceValue) {
            table.setMaxWidth(300);
        } else {
            table.setMaxWidth(200);
        }

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<CategoryData, String>("name"));

        TableColumn valueCol = new TableColumn("Value");
        valueCol.setMinWidth(100);
        valueCol.setCellValueFactory(
                new PropertyValueFactory<CategoryData, String>("value"));
        if (needPriceValue) {
            TableColumn priceCol = new TableColumn("Price");
            priceCol.setMinWidth(100);
            priceCol.setCellValueFactory(
                    new PropertyValueFactory<CategoryData, String>("valuePrice"));
            table.setItems(list);
            table.getColumns().addAll(nameCol, valueCol, priceCol);
        } else {
            table.setItems(list);
            table.getColumns().addAll(nameCol, valueCol);
        }
        table.setRowFactory(tv -> {
            TableRow<CategoryData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {
                    CategoryData clickedRow = row.getItem();
                    updateWindow(clickedRow);
                }
            });
            return row;
        });
        return table;
    }

    private static void refreshTable(CategoryTypes type) {
        TableView<CategoryData> table;
        switch (type) {
            case INCOME:
                incomesInfo.getObservableList().removeAll(incomesInfo.getObservableList());
                incomesInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) incomesInfo.getList()));
                table = incomesInfo.getTable();
                table.setItems(incomesInfo.getObservableList());
                incomesInfo.setTable(table);
                incomesInfo.setTotal(Utils.calculateTotal(CategoryTypes.INCOME, incomesInfo.getList())
                        - Utils.calculateTotal(CategoryTypes.OUTCOME, outcomesInfo.getList()));
                incomesInfo.getLabel().setText("Account total: " + String.format("%.2f", incomesInfo.getTotal()));
                break;
            case OUTCOME:
                //Total and label of income and outcome are in incomesInfo
                outcomesInfo.getObservableList().removeAll(outcomesInfo.getObservableList());
                outcomesInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) outcomesInfo.getList()));
                table = outcomesInfo.getTable();
                table.setItems(outcomesInfo.getObservableList());
                outcomesInfo.setTable(table);
                incomesInfo.setTotal(Utils.calculateTotal(CategoryTypes.INCOME, incomesInfo.getList())
                        - Utils.calculateTotal(CategoryTypes.OUTCOME, outcomesInfo.getList()));
                incomesInfo.getLabel().setText("Account total: " + String.format("%.2f", incomesInfo.getTotal()));
                break;
            case DEPOSIT:
                depositInfo.getObservableList().removeAll(depositInfo.getObservableList());
                depositInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) depositInfo.getList()));
                table = depositInfo.getTable();
                table.setItems(depositInfo.getObservableList());
                depositInfo.setTable(table);
                depositInfo.setTotal(Utils.calculateTotal(type, depositInfo.getList()));
                depositInfo.getLabel().setText("Account total: " + String.format("%.2f", depositInfo.getTotal()));
                break;
            case COIN:
                coinInfo.getObservableList().removeAll(coinInfo.getObservableList());
                coinInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) coinInfo.getList()));
                table = coinInfo.getTable();
                table.setItems(coinInfo.getObservableList());
                coinInfo.setTable(table);
                coinInfo.setTotal(Utils.calculateTotal(type, coinInfo.getList()));
                coinInfo.getLabel().setText("Account total: " + String.format("%.2f", coinInfo.getTotal()));
                break;
            case STOCK:
                stockInfo.getObservableList().removeAll(stockInfo.getObservableList());
                stockInfo.setObservableList(FXCollections.observableArrayList((List<CategoryData>) stockInfo.getList()));
                table = stockInfo.getTable();
                table.setItems(stockInfo.getObservableList());
                stockInfo.setTable(table);
                stockInfo.setTotal(Utils.calculateTotal(type, stockInfo.getList()));
                stockInfo.getLabel().setText("Account total: " + String.format("%.2f", stockInfo.getTotal()));
                break;
        }
        totalAmount = baseAccount + incomesInfo.getTotal() + depositInfo.getTotal() + coinInfo.getTotal() + stockInfo.getTotal();
        totalAmountLabel.setText("Total amount: " + String.format("%.2f", totalAmount));
    }

    private static void getAccountBaseWindow(boolean isItFirstTime) {
        Label valueLabel = new Label();
        valueLabel.setText("Current Account Value: ");
        TextField valueField = new TextField();
        Button addButton = new Button();
        addButton.setText("Add");

        HBox buttonHbox = new HBox(10);
        buttonHbox.setAlignment(Pos.BOTTOM_CENTER);
        buttonHbox.getChildren().add(addButton);
        GridPane updateGrid = new GridPane();
        updateGrid.add(valueLabel, 0, 0);
        updateGrid.add(valueField, 1, 0);

        updateGrid.add(buttonHbox, 0, 2, 2, 1);
        updateGrid.setHgap(10);
        updateGrid.setVgap(10);
        updateGrid.setPadding(new Insets(25, 25, 25, 25));
        Scene addScene = new Scene(updateGrid, 350, 100);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setAlwaysOnTop(true);
        newWindow.setTitle("Set current account");
        newWindow.setScene(addScene);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Double value = Utils.valuesValidation(valueField.getText());
                if (value != null) {
                    Utils.updateAccountBase(value);
                    baseAccount = value;
                    totalAmount = baseAccount + incomesInfo.getTotal() + depositInfo.getTotal() + coinInfo.getTotal() + stockInfo.getTotal();
                    totalAmountLabel.setText("Total amount: " + String.format("%.2f", totalAmount));
                    newWindow.close();
                } else {
                    errorValidation();
                }
            }
        });
        newWindow.show();
    }

    private static void addWindow(CategoryTypes type) {
        Label nameLabel = new Label();
        nameLabel.setText("Name: ");
        Label valueLabel = new Label();
        valueLabel.setText("Value: ");
        TextField nameField = new TextField();
        TextField valueField = new TextField();
        Button addButton = new Button();
        addButton.setText("ADD");
        addButton.setMaxWidth(Double.MAX_VALUE);

        HBox buttonHbox = new HBox(10);
        buttonHbox.setAlignment(Pos.BOTTOM_CENTER);
        buttonHbox.getChildren().add(addButton);
        GridPane updateGrid = new GridPane();
        updateGrid.add(nameLabel, 0, 0);
        updateGrid.add(nameField, 1, 0);
        updateGrid.add(valueLabel, 0, 1);
        updateGrid.add(valueField, 1, 1);

        updateGrid.add(buttonHbox, 0, 2, 2, 1);
        updateGrid.setHgap(10);
        updateGrid.setVgap(10);
        updateGrid.setPadding(new Insets(25, 25, 25, 25));
        Scene updateScene = new Scene(updateGrid, 250, 150);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Add new data");
        newWindow.setScene(updateScene);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String name = nameField.getText();
                Double value = Utils.valuesValidation(valueField.getText());
                CategoryData categoryData = Utils.createCategoryData(type, name, value);
                if (categoryData != null && Utils.nameValidation(type, name)) {
                    switch (type) {
                        case INCOME:
                            incomesInfo.getList().add(categoryData);
                            break;
                        case OUTCOME:
                            outcomesInfo.getList().add(categoryData);
                            break;
                        case DEPOSIT:
                            depositInfo.getList().add(categoryData);
                            break;
                        case COIN:
                            coinInfo.getList().add(categoryData);
                            break;
                        case STOCK:
                            stockInfo.getList().add(categoryData);
                            break;
                    }
                    refreshTable(type);
                    newWindow.close();
                    Utils.addData(categoryData);
                } else {
                    errorValidation();
                }
            }
        });
        newWindow.show();
    }

    private static void updateWindow(CategoryData clickedRow) {
        Label nameLabel = new Label();
        nameLabel.setText("Name: ");
        Label valueLabel = new Label();
        valueLabel.setText("Value: ");
        TextField nameField = new TextField();
        nameField.setText(clickedRow.getName());
        TextField valueField = new TextField();
        valueField.setText(Double.toString(clickedRow.getValue()));
        Button updateButton = new Button();
        updateButton.setText("Update");
        updateButton.setMaxWidth(Double.MAX_VALUE);

        Button deleteButton = new Button();
        deleteButton.setText("Delete");
        deleteButton.setMaxWidth(Double.MAX_VALUE);

        HBox buttonHbox = new HBox(10);
        buttonHbox.setAlignment(Pos.BOTTOM_CENTER);
        buttonHbox.getChildren().addAll(updateButton, deleteButton);
        GridPane updateGrid = new GridPane();
        updateGrid.add(nameLabel, 0, 0);
        updateGrid.add(nameField, 1, 0);
        updateGrid.add(valueLabel, 0, 1);
        updateGrid.add(valueField, 1, 1);

        updateGrid.add(buttonHbox, 0, 2, 2, 1);
        updateGrid.setHgap(10);
        updateGrid.setVgap(10);
        updateGrid.setPadding(new Insets(25, 25, 25, 25));
        Scene updateScene = new Scene(updateGrid, 250, 150);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Update data");
        newWindow.setScene(updateScene);

        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (clickedRow.getType()) {
                    case INCOME:
                        incomesInfo.getList().removeIf(item -> item.getId() == clickedRow.getId());
                        break;
                    case OUTCOME:
                        outcomesInfo.getList().removeIf(item -> item.getId() == clickedRow.getId());
                        break;
                    case DEPOSIT:
                        depositInfo.getList().removeIf(item -> item.getId() == clickedRow.getId());
                        break;
                    case COIN:
                        coinInfo.getList().removeIf(item -> item.getId() == clickedRow.getId());
                        break;
                    case STOCK:
                        stockInfo.getList().removeIf(item -> item.getId() == clickedRow.getId());
                        break;
                }
                if (clickedRow.getType() == CategoryTypes.COIN || clickedRow.getType() == CategoryTypes.STOCK ||
                        clickedRow.getType() == CategoryTypes.DEPOSIT) {
                    moveMoneyToAccount(clickedRow);
                }
                refreshTable(clickedRow.getType());
                Utils.deleteData(clickedRow);
                newWindow.close();
            }
        });
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String name = nameField.getText();
                Double value = Utils.valuesValidation(valueField.getText());
                if (clickedRow.getType() == CategoryTypes.COIN || clickedRow.getType() == CategoryTypes.STOCK ||
                        clickedRow.getType() == CategoryTypes.DEPOSIT) {
                    moveMoneyToAccount(clickedRow);
                }
                if (value == null || !Utils.nameValidation(clickedRow.getType(), name)) {
                    errorValidation();
                } else {
                    switch (clickedRow.getType()) {
                        case INCOME:
                            for (CategoryData item : incomesInfo.getList()) {
                                if (item.getId() == clickedRow.getId()) {
                                    item.setName(name);
                                    item.setValue(value);
                                    refreshTable(clickedRow.getType());
                                    Utils.updateData(item.getId(), name, value);
                                    newWindow.close();
                                    break;
                                }
                            }
                            break;
                        case OUTCOME:
                            for (CategoryData item : outcomesInfo.getList()) {
                                if (item.getId() == clickedRow.getId()) {
                                    item.setName(name);
                                    item.setValue(value);
                                    refreshTable(clickedRow.getType());
                                    Utils.updateData(item.getId(), name, value);
                                    newWindow.close();
                                    break;
                                }
                            }
                            break;
                        case DEPOSIT:
                            for (CategoryData item : depositInfo.getList()) {
                                if (item.getId() == clickedRow.getId()) {
                                    item.setName(name);
                                    item.setValue(value);
                                    refreshTable(clickedRow.getType());
                                    Utils.updateData(item.getId(), name, value);
                                    newWindow.close();
                                    break;
                                }
                            }
                            break;
                        case COIN:
                            for (CategoryData item : coinInfo.getList()) {
                                if (item.getId() == clickedRow.getId()) {
                                    item.setName(name);
                                    item.setValue(value);
                                    refreshTable(clickedRow.getType());
                                    Utils.updateData(item.getId(), name, value);
                                    newWindow.close();
                                    break;
                                }
                            }
                            break;
                        case STOCK:
                            for (CategoryData item : stockInfo.getList()) {
                                if (item.getId() == clickedRow.getId()) {
                                    item.setName(name);
                                    item.setValue(value);
                                    refreshTable(clickedRow.getType());
                                    Utils.updateData(item.getId(), name, value);
                                    newWindow.close();
                                    break;
                                }
                            }
                            break;
                    }
                }
            }
        });
        newWindow.show();
    }

    private static void moveMoneyToAccount(CategoryData categoryData) {
        Double value;
        if (categoryData.getType() == CategoryTypes.DEPOSIT) {
            value = categoryData.getValue();
        } else {
            value = categoryData.getValuePrice();
        }
        TextInputDialog dialog = new TextInputDialog(Double.toString(value));
        dialog.setTitle("Move money into account?");
        dialog.setHeaderText("Should we add this money to account?");
        dialog.setContentText("Please change the value if needed:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            value = Utils.valuesValidation(result.get());
            if (value != null) {
                Utils.updateAccountBase(value);
                baseAccount += value;
                totalAmount = baseAccount + incomesInfo.getTotal() + depositInfo.getTotal() + coinInfo.getTotal() + stockInfo.getTotal();
                totalAmountLabel.setText("Total amount: " + String.format("%.2f", totalAmount));
            } else {
                errorValidation();
            }
        }
    }

    private static void previousMonthsView() {
        List<String> choices = Utils.getMonths();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Previous month view");
        dialog.setHeaderText("Choose a month");
        dialog.setContentText("Please enter the wanted month:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            ObservableList<CategoryData> income = FXCollections.observableArrayList((List<CategoryData>) Utils.getSpecificMonthData(CategoryTypes.INCOME, s));
            ObservableList<CategoryData> outcome = FXCollections.observableArrayList((List<CategoryData>) Utils.getSpecificMonthData(CategoryTypes.OUTCOME, s));

            GridPane updateGrid = new GridPane();
            GridPane.setHalignment(incomesInfo.getLabel(), HPos.CENTER);
            updateGrid.setHgap(10);
            updateGrid.setVgap(10);
            updateGrid.setPadding(new Insets(25, 25, 25, 25));
            Scene updateScene = new Scene(updateGrid);

            final Label inLabel = new Label("Incomes");
            inLabel.setFont(new Font("Arial", 16));
            TableView<CategoryData> incomeTable = createTable(income, false);

            final VBox inVbox = new VBox();
            inVbox.setSpacing(5);
            inVbox.setPadding(new Insets(10, 0, 0, 10));
            inVbox.getChildren().addAll(inLabel, incomeTable);
            updateGrid.add(inVbox, 0, 0);

            //outcome table
            final Label outLabel = new Label("Outcomes");
            outLabel.setFont(new Font("Arial", 16));

            TableView<CategoryData> outcomeTable = createTable(outcome, false);

            final VBox outVbox = new VBox();
            outVbox.setSpacing(5);
            outVbox.setPadding(new Insets(10, 0, 0, 10));
            outVbox.getChildren().addAll(outLabel, outcomeTable);
            updateGrid.add(outVbox, 1, 0);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Date for " + s);
            newWindow.setScene(updateScene);
            newWindow.show();
        });
    }

    private static void deleteAllTableConfirmation() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to delete all data?");
        alert.setContentText("You can not retrieve this data ");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Utils.deleteAllData();
            deleteAllTable();
        }
    }

    private static void errorValidation() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("An Error Occur");
        alert.setHeaderText("Please verify the name and value are valid");
        String errorExplain = "Value should be a positive number without any additional characters, for example: 3.0 or 3\n" +
                "For coins and stocks, name may not been found in database.";
        alert.setContentText(errorExplain);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        tablesInit();
        Utils.createAssetTable();
        Utils.needUpdate();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 10, 25, 10));
        BorderPane root = new BorderPane();

        primaryStage.setTitle("TotalFinance");

        baseAccount = Utils.getAccountBase();
        if (baseAccount == 0) {
            getAccountBaseWindow(true);
        }

        totalAmountLabel = new Label("Total amount: " + String.format("%.2f", totalAmount));
        totalAmountLabel.setFont(new Font("Arial", 24));
        grid.add(totalAmountLabel, 0, 0, 4, 1);

        //menu
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        MenuItem deleteAllItem = new MenuItem("Delete all");
        deleteAllItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteAllTableConfirmation();
            }
        });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        menuFile.getItems().addAll(deleteAllItem, exitItem);

        Menu menuEdit = new Menu("Edit");
        MenuItem updateBaseAccountItem = new MenuItem("Update current Account");
        updateBaseAccountItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getAccountBaseWindow(false);

            }
        });
        menuEdit.getItems().add(updateBaseAccountItem);

        Menu menuView = new Menu("View");
        MenuItem previousMonthsItem = new MenuItem("Choose previous months view");
        previousMonthsItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                previousMonthsView();
            }
        });
        menuView.getItems().add(previousMonthsItem);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

        //income table
        final Label inLabel = new Label("Incomes");
        inLabel.setFont(new Font("Arial", 16));

        final VBox inVbox = new VBox();
        inVbox.setSpacing(5);
        inVbox.setPadding(new Insets(10, 0, 0, 10));
        inVbox.getChildren().addAll(inLabel, incomesInfo.getTable());
        grid.add(inVbox, 0, 1);

        Button inAdd = new Button();
        inAdd.setText("Add");
        inAdd.setMaxWidth(Double.MAX_VALUE);
        inAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addWindow(CategoryTypes.INCOME);
            }
        });
        HBox inHbox = new HBox(10);
        inHbox.setAlignment(Pos.BOTTOM_CENTER);
        inHbox.getChildren().add(inAdd);
        grid.add(inHbox, 0, 2);

        //outcome table
        final Label outLabel = new Label("Outcomes");
        outLabel.setFont(new Font("Arial", 16));

        final VBox outVbox = new VBox();
        outVbox.setSpacing(5);
        outVbox.setPadding(new Insets(10, 0, 0, 10));
        outVbox.getChildren().addAll(outLabel, outcomesInfo.getTable());
        grid.add(outVbox, 1, 1);

        Button outAdd = new Button();
        outAdd.setText("Add");
        outAdd.setMaxWidth(Double.MAX_VALUE);
        outAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addWindow(CategoryTypes.OUTCOME);
            }
        });
        HBox outHbox = new HBox(10);
        outHbox.setAlignment(Pos.BOTTOM_CENTER);
        outHbox.getChildren().add(outAdd);
        grid.add(outHbox, 1, 2);

        //total account (income and outcome)
        //Total and label of income and outcome are in incomesInfo
        incomesInfo.setLabel(new Label("Account total: " + String.format("%.2f", incomesInfo.getTotal())));
        incomesInfo.getLabel().setFont(new Font("Arial", 16));
        grid.add(incomesInfo.getLabel(), 0, 3, 2, 1);
        GridPane.setHalignment(incomesInfo.getLabel(), HPos.CENTER);

        //deposits table
        final Label depLabel = new Label("Deposits");
        depLabel.setFont(new Font("Arial", 16));

        final VBox depVbox = new VBox();
        depVbox.setSpacing(5);
        depVbox.setPadding(new Insets(10, 0, 0, 10));
        depVbox.getChildren().addAll(depLabel, depositInfo.getTable());
        grid.add(depVbox, 2, 1);

        Button depAdd = new Button();
        depAdd.setText("Add");
        depAdd.setMaxWidth(Double.MAX_VALUE);
        depAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addWindow(CategoryTypes.DEPOSIT);
            }
        });
        HBox depHbox = new HBox(10);
        depHbox.setAlignment(Pos.BOTTOM_CENTER);
        depHbox.getChildren().add(depAdd);
        grid.add(depHbox, 2, 2);

        //total deposits
        depositInfo.setLabel(new Label("Deposits total: " + String.format("%.2f", depositInfo.getTotal())));
        depositInfo.getLabel().setFont(new Font("Arial", 16));
        grid.add(depositInfo.getLabel(), 2, 3);
        GridPane.setHalignment(depositInfo.getLabel(), HPos.CENTER);

        //coins table
        final Label coinLabel = new Label("Cryptocurrency: ");
        coinLabel.setFont(new Font("Arial", 16));

        final VBox coinVbox = new VBox();
        coinVbox.setSpacing(5);
        coinVbox.setPadding(new Insets(10, 0, 0, 10));
        coinVbox.getChildren().addAll(coinLabel, coinInfo.getTable());
        grid.add(coinVbox, 3, 1);

        Button coinAdd = new Button();
        coinAdd.setText("Add");
        coinAdd.setMaxWidth(Double.MAX_VALUE);
        coinAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addWindow(CategoryTypes.COIN);
            }
        });
        HBox coinHbox = new HBox(10);
        coinHbox.setAlignment(Pos.BOTTOM_CENTER);
        coinHbox.setSpacing(25);
        coinHbox.getChildren().add(coinAdd);
        grid.add(coinHbox, 3, 2);

        //total crypto coins
        coinInfo.setLabel(new Label("Cryptocurrency total: " + String.format("%.2f", coinInfo.getTotal())));
        coinInfo.getLabel().setFont(new Font("Arial", 16));
        grid.add(coinInfo.getLabel(), 3, 3);
        GridPane.setHalignment(coinInfo.getLabel(), HPos.CENTER);

        //stocks table
        final Label stockLabel = new Label("Stocks");
        stockLabel.setFont(new Font("Arial", 16));


        final VBox stockVbox = new VBox();
        stockVbox.setSpacing(5);
        stockVbox.setPadding(new Insets(10, 0, 0, 10));
        stockVbox.getChildren().addAll(stockLabel, stockInfo.getTable());
        grid.add(stockVbox, 4, 1);

        HBox stockHbox = new HBox(10);
        Button stockAdd = new Button();
        stockAdd.setText("Add");
        stockAdd.setMaxWidth(Double.MAX_VALUE);
        stockAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addWindow(CategoryTypes.STOCK);
            }
        });
        stockHbox.setAlignment(Pos.BOTTOM_CENTER);
        stockHbox.setSpacing(25);
        stockHbox.getChildren().add(stockAdd);
        grid.add(stockHbox, 4, 2);

        //total stocks
        stockInfo.setLabel(new Label("Stocks total: " + String.format("%.2f", stockInfo.getTotal())));
        stockInfo.getLabel().setFont(new Font("Arial", 16));
        grid.add(stockInfo.getLabel(), 4, 3);
        GridPane.setHalignment(stockInfo.getLabel(), HPos.CENTER);

        //show
        root.setTop(menuBar);
        root.setCenter(grid);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
