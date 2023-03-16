package com.example.milestone2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Set;

public class PropertyAssessmentApplication extends Application {
    private ObservableList<PropertyAssessment> propertyAssessments;
    private PropertyAssessmentDAO propertyAssessmentDAO;
    private final TextField accountNumberTextField = new TextField();
    private final TextField addressTextField = new TextField();
    private final TextField neighbourhoodTextField = new TextField();
    private TextField minValueTextField = new TextField();
    private TextField maxValueTextField = new TextField();
    private ComboBox<String> sourceSelect;
    private ObservableList<String> assessmentClasses;

    ComboBox<String> assessmentClassSelect;
    Set<String> assessmentClassSet;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Property Assessments");
        HBox mainHBox = new HBox(10);
        mainHBox.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(mainHBox, 1500, 1000);
        primaryStage.setScene(scene);

        VBox tableVBox =  createTableVBox();
        tableVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.80));

        VBox selectionVBox = createDataSelectionVBox();
        selectionVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.20));

        // Ensures the data select and property finder is always the same width
        scene.widthProperty().addListener(change -> {

            double selectionBoxMultiple = 300.0/scene.getWidth();
            double tableMultiple = 1.0 - selectionBoxMultiple;

            tableVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(tableMultiple));
            selectionVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(selectionBoxMultiple));
        });

        mainHBox.getChildren().addAll(selectionVBox, tableVBox);
        primaryStage.show();
    }

    /**
     * Function called when the read data button is pressed.
     * Loads the initial data from the selected data source.
     */
    private void loadData() {
        final Source CSV = Source.CSV;
        final Source API = Source.API;

        List<PropertyAssessment> propertyAssessmentList;

        propertyAssessments.clear();
        assessmentClasses.clear();
        String source = sourceSelect.getValue();

        if(source.equals(CSV.getSource())) {
            propertyAssessmentDAO = new CsvPropertyAssessmentDAO();
        } else if(source.equals(API.getSource())) {
            propertyAssessmentDAO = new ApiPropertyAssessmentDAO();
        }

        propertyAssessmentList = propertyAssessmentDAO.getPropertyAssessments();
        assessmentClassSet = propertyAssessmentDAO.getAssessmentClasses();
        assessmentClassSet.add("");

        assessmentClasses.addAll(assessmentClassSet);

        assessmentClassSelect.setValue("");

        if (propertyAssessmentList.size() > 0) {
            propertyAssessments.addAll(propertyAssessmentList);
        }
    }

    /**
     * Gets the property assessment from the data source with the specified account number
     *
     * @return The property assessment with the specified account number or null if no such property assessment exists
     */
    private PropertyAssessment getPropertyByAccountNumber() {
        if (propertyAssessmentDAO != null) {
            try {
                return propertyAssessmentDAO.getByAccountNumber(Integer.parseInt(accountNumberTextField.getText()));
            } catch (NumberFormatException error) {
                return null;
            }
        }

        return null;
    }

    /**
     * Displays an alert with the specified message
     *
     * @param message The message to be displayed in the alert
     */
    private void showNoDataAlert(String message) {
        Alert noDataAlert = new Alert(Alert.AlertType.INFORMATION);
        noDataAlert.setTitle("Search Results");
        noDataAlert.setHeaderText(null);
        noDataAlert.setContentText(message);

        noDataAlert.initModality(Modality.APPLICATION_MODAL);

        noDataAlert.showAndWait();
    }

    /**
     * Gets the integer value in a text field
     *
     * @param textField The text field whose value is being parsed into an int
     * @return the integer value of the text field or -1 of the value couldn't be parsed
     */
    private int getIntValue(TextField textField) {
        try{
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException error) {
            return -1;
        }
    }

    /**
     * Gets a list of filtered property assessments
     *
     * @return A list of property assessments
     */
    private List<PropertyAssessment> getFilteredList() {

        String address = addressTextField.getText();
        String neighbourhood = neighbourhoodTextField.getText();
        String assessmentClass = assessmentClassSelect.getValue();
        int minimumAssessedValue = getIntValue(minValueTextField);
        int maximumAssessedValue = getIntValue(maxValueTextField);

        Filter.Builder builder = new Filter.Builder();

        if(!address.isEmpty()) {
            builder.address(address);
        }

        if(!neighbourhood.isEmpty()) {
            builder.neighbourhood(neighbourhood);
        }

        if(!assessmentClass.isEmpty()) {
            builder.assessmentClass(assessmentClass);
        }

        if(minimumAssessedValue >= 0) {
            builder.minimumAssessedValue(minimumAssessedValue);
        }

        if(maximumAssessedValue >= 0) {
            builder.maximumAssessedValue(maximumAssessedValue);
        }

        Filter filter = builder.build();

        return propertyAssessmentDAO.getPropertyAssessments(filter);
    }

    /**
     * Function called when the search button is pressed.
     *
     * Populates the table with the filtered data from the data source
     */
    private void search() {
        // Displays an alert if no data source has been selected
        if (propertyAssessmentDAO == null) {
            showNoDataAlert("No data source selected");
            return;
        }

        if(!accountNumberTextField.getText().isEmpty()) {
            // If an account number if entered then will only find the property assessment
            // with that account number and ignore other filters
            PropertyAssessment propertyAssessment = getPropertyByAccountNumber();
            if (propertyAssessment == null) {
                showNoDataAlert("Oops, did not find anything");
            } else {
                propertyAssessments.clear();
                propertyAssessments.add(propertyAssessment);
            }
        } else {

            List<PropertyAssessment> filterdList = getFilteredList();
            if(filterdList.size() == 0) {
                showNoDataAlert("Oops, did not find anything");
            } else {
                propertyAssessments.clear();
                propertyAssessments.addAll(getFilteredList());
            }
        }

    }

    /**
     * Resets the fields in the search
     */
    private void reset(){
        accountNumberTextField.setText("");
        addressTextField.setText("");
        neighbourhoodTextField.setText("");
        assessmentClassSelect.setValue("");
        minValueTextField.setText("");
        maxValueTextField.setText("");
    }

    /**
     * Creates the VBox that holds all the elements for selecting the data source
     *
     * @return The VBox that holds all the data select elements
     */
    private VBox createDataSelectVBox() {
        VBox dataSourceSelectVBox = new VBox(10);

        Source CSV = Source.CSV;
        Source API = Source.API;

        ObservableList<String> dataSources = FXCollections.observableArrayList(
                CSV.getSource(),
                API.getSource()
        );

        sourceSelect = new ComboBox<>(dataSources);

        sourceSelect.prefWidthProperty().bind(dataSourceSelectVBox.widthProperty());

        Button getDataButton = new Button("Read Data");

        getDataButton.setOnAction( event -> loadData());

        getDataButton.prefWidthProperty().bind(dataSourceSelectVBox.widthProperty());

        Label selectionBoxLabel = new Label("Select Data Source");
        selectionBoxLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        dataSourceSelectVBox.getChildren().addAll(selectionBoxLabel, sourceSelect, getDataButton);

        return dataSourceSelectVBox;
    }

    /**
     * Creates the VBox that holds the text fields for getting the min and max
     * assessed value range
     *
     * @return the VBox that contains the min and max value text fields
     */
    private VBox createAssessmentValueRangeVBox() {
        Label assessedValueRangeLabel = new Label("Assessed Value Range:");

        minValueTextField = new TextField();
        minValueTextField.setPromptText("Min Value");

        maxValueTextField = new TextField();
        maxValueTextField.setPromptText("Max Value");

        HBox minMaxHBox = new HBox(10);

        minValueTextField.prefWidthProperty().bind(minMaxHBox.widthProperty().multiply(0.5));
        maxValueTextField.prefWidthProperty().bind(minMaxHBox.widthProperty().multiply(0.5));

        minMaxHBox.getChildren().addAll(minValueTextField, maxValueTextField);

        VBox assessedValueRangeVBox = new VBox(10);

        assessedValueRangeVBox.getChildren().addAll(assessedValueRangeLabel, minMaxHBox);

        return assessedValueRangeVBox;
    }

    /**
     * Creates a VBox with the specified label and Control and matches
     * the control objects width to the new VBox
     *
     * @param label A string to give the label
     * @param control the control object in the VBoc
     * @return the new VBox with the label and control object
     */
    private VBox createControlVBox(String label, Control control) {
        Label vBoxLabel = new Label(label);
        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(vBoxLabel, control);

        control.prefWidthProperty().bind(vBox.widthProperty());

        return vBox;
    }

    /**
     * Creates the VBox that holds the assessment class combo box
     *
     * @return The VBox that contains the assessment class combo box
     */
    private VBox createAssessmentClassSelectVBox() {
        assessmentClasses = FXCollections.observableArrayList();
        assessmentClassSelect= new ComboBox<>(assessmentClasses);
        return createControlVBox("Assessment Class:", assessmentClassSelect);
    }

    /**
     * Creates the HBox that holds the search and reset buttons
     *
     * @return The HBox that holds the search and reset buttons
     */
    private HBox createButtonHBox () {
        HBox buttonHBox = new HBox(10);

        Button searchButton = new Button("Search");

        searchButton.setOnAction(event -> search());

        Button resetButton = new Button("Reset");

        resetButton.setOnAction(event -> reset());

        searchButton.prefWidthProperty().bind(buttonHBox.widthProperty().multiply(0.5));
        resetButton.prefWidthProperty().bind(buttonHBox.widthProperty().multiply(0.5));

        buttonHBox.getChildren().addAll(searchButton, resetButton);

        return buttonHBox;
    }

    /**
     * Creates the VBox that holds all the controls for filtering the property assessments
     *
     * @return The VBox that holds all the controls for filtering the property assessments
     */
    private VBox createPropertyFindVBox() {
        VBox propertyFindVBox = new VBox(10);

        VBox accountNumberVBox = createControlVBox("Account Number:", accountNumberTextField);

        VBox addressVBox = createControlVBox("Address (#suite #house street):", addressTextField);

        VBox neighbourhoodVBox = createControlVBox("Neighbourhood:", neighbourhoodTextField );

        VBox assessmentClassVBox = createAssessmentClassSelectVBox();

        VBox assessedValueRangeVBox = createAssessmentValueRangeVBox();

        Label findPropertyLabel = new Label("Find Property Assessment");
        findPropertyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        propertyFindVBox.getChildren().addAll(findPropertyLabel, accountNumberVBox, addressVBox, neighbourhoodVBox, assessmentClassVBox, assessedValueRangeVBox);

        return propertyFindVBox;
    }

    /**
     * Creates the VBox that holds all the elements responsible for getting the input from the user
     *
     * @return the VBox that holds all the elements responsible for getting the input from the user
     */
    private VBox createDataSelectionVBox() {
        VBox selectionVBox = new VBox(10);

        VBox dataSourceSelectVBox = createDataSelectVBox();

        VBox propertyFindVBox = createPropertyFindVBox();

        HBox buttonHBox = createButtonHBox();

        buttonHBox.prefWidthProperty().bind(selectionVBox.widthProperty());

        selectionVBox.getChildren().addAll(dataSourceSelectVBox, propertyFindVBox, buttonHBox);

        return selectionVBox;
    }

    /**
     * Creates and configures the components in the VBox that holds the table
     *
     * @return the VBox that holds the table
     */
    private VBox createTableVBox() {
        VBox tableVBox = new VBox(10);
        propertyAssessments = FXCollections.observableArrayList();

        TableView<PropertyAssessment> table = new TableView<>();
        table.setItems(propertyAssessments);

        TableColumn<PropertyAssessment, String> accountCol = new TableColumn<>("Account");
        accountCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
        table.getColumns().add(accountCol);

        TableColumn<PropertyAssessment, Address> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        addressCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Address address, boolean empty) {
                super.updateItem(address, empty);

                if (!empty){
                    StringBuilder addressString = new StringBuilder();

                    if(!address.getSuite().isEmpty()) {
                        addressString.append(address.getSuite()).append(" ");
                    }

                    if(!address.getHouseNumber().isEmpty()) {
                        addressString.append(address.getHouseNumber()).append(" ");
                    }

                    addressString.append(address.getStreet());

                    setText(addressString.toString());
                } else {
                    setText("");
                }

            }
        });

        addressCol.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
        table.getColumns().add(addressCol);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        TableColumn<PropertyAssessment, Integer> assessedValueCol = new TableColumn<>("Assessed Value");
        assessedValueCol.setCellValueFactory(new PropertyValueFactory<>("assessedValue"));
        assessedValueCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                currencyFormat.setMaximumFractionDigits(0);
                setText(empty ? "" : currencyFormat.format(value));
            }
        });

        assessedValueCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));
        table.getColumns().add(assessedValueCol);

        TableColumn<PropertyAssessment, List<AssessmentClass>> assessmentClassCol = new TableColumn<>("Assessment Class");
        assessmentClassCol.setCellValueFactory(new PropertyValueFactory<>("assessmentClassList"));
        assessmentClassCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(List<AssessmentClass> assessmentClassList, boolean empty) {
                super.updateItem(assessmentClassList, empty);
                if(!empty) {
                    StringBuilder assessmentClassString = new StringBuilder("[");
                    for (int i = 0; i < assessmentClassList.size(); i++) {
                        if(i > 0) {
                            assessmentClassString.append(" ");
                        }

                        assessmentClassString.append(assessmentClassList.get(i).getAssessmentClassName()).append(" ");
                        assessmentClassString.append(assessmentClassList.get(i).getAssessmentClassPercentage());
                    }

                    assessmentClassString.append("]");

                    setText(assessmentClassString.toString());
                } else {
                    setText("");
                }
            }
        });
        assessmentClassCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        table.getColumns().add(assessmentClassCol);

        TableColumn<PropertyAssessment, String> neighbourhoodCol = new TableColumn<>("Neighbourhood");
        neighbourhoodCol.setCellValueFactory(new PropertyValueFactory<>("neighbourhood"));
        table.getColumns().add(neighbourhoodCol);
        neighbourhoodCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));

        TableColumn<PropertyAssessment, Location> locationCol = new TableColumn<>("(Latitude, Longitude)");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setCellFactory(tc -> new TableCell<>() {
            protected void updateItem(Location location, boolean empty) {
                super.updateItem(location, empty);

                setText(empty ? "": "(" + location.getLatitude() + ", " + location.getLongitude() + ")");
            }
        });
        locationCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        table.getColumns().add(locationCol);

        Label tableLabel = new Label("Edmonton Property Assessments (2022)");
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        table.prefWidthProperty().bind(tableVBox.widthProperty());
        table.prefHeightProperty().bind(tableVBox.heightProperty());

        tableVBox.getChildren().addAll(tableLabel, table);

        return tableVBox;
    }

    public static void main(String[] args) {
        launch();
    }
}