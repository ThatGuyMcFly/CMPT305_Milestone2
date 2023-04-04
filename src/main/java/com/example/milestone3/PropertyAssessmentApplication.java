package com.example.milestone3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
    private LineChart<String, Float> graph= overviewGraph();
    private LineChart<String, Float> graph2= specificGraph();
    private Font dataFont;
    private Font headingFont;
    private Font titleFont;
    private ComboBox<String> assessmentClassSelect;
    private Scene mainScene;
    private Stage primaryStage;

    private final Text minValueText = new Text();
    private final Text maxValueText = new Text();
    private final Text averageValueText = new Text();
    private final Text minLotSizeText = new Text();
    private final Text maxLotSizeText = new Text();
    private final Text averageLotSizeText = new Text();

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        //primaryStage.setTitle("Property Assessments");
        HBox mainHBox = new HBox(10);
        mainHBox.setPadding(new Insets(10, 10, 10, 10));
        mainScene = new Scene(mainHBox, 1500, 1000);
        mainHBox.setStyle("-fx-background-color: #FFF5EB");
        this.primaryStage.setScene(mainScene);

        dataFont = Font.font("Arial", 20);
        headingFont = Font.font("Arial", FontWeight.BOLD, 20);
        titleFont = Font.font("Arial", FontWeight.BOLD, 30);

        VBox tabVBox = createTabVBox();
        tabVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.80));

        VBox selectionVBox = createDataSelectionVBox();
        selectionVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.20));

        // Ensures the data select and property finder is always the same width
        mainScene.widthProperty().addListener(change -> {

            double selectionBoxMultiple = 300.0/mainScene.getWidth();
            double tableMultiple = 1.0 - selectionBoxMultiple;

            tabVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(tableMultiple));
            selectionVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(selectionBoxMultiple));
        });

        mainHBox.getChildren().addAll(selectionVBox, tabVBox);
        primaryStage.show();
    }


    /**
     * Sets up a VBox that has a title and text box to be filled in later
     * @param text The text box that will be populated later
     * @param title The title to be put in the VBox
     * @return a new VBox with a title and text box
     */
    private VBox setupTextVBox(Text text, String title){
        Text textTitle = new Text(title);
        textTitle.setFont(dataFont);
        text.setFont(dataFont);
        VBox textVBox = new VBox(textTitle, text);
        textVBox.setPadding(new Insets(0, 0, 10, 0));

        return textVBox;
    }

    /**
     * Creates a VBox that holds the assessed value information for property assessments
     * @return a VBox that holds the assessed value information for property assessments
     */
    private VBox createValuesVBox() {
        VBox minValueVBox = setupTextVBox(minValueText, "Minimum Value");
        VBox maxValueVBox = setupTextVBox(maxValueText, "Maximum Value");
        VBox averageValueVBox = setupTextVBox(averageValueText, "Average Value");

        return new VBox(minValueVBox, maxValueVBox, averageValueVBox);
    }

    /**
     * Creates a VBox that holds the lot size info of property assessments
     * @return a VBox that holds the lot size info of property assessments
     */
    private VBox createLotSizeVBox() {
        VBox minLotSizeVBox = setupTextVBox(minLotSizeText, "Minimum Lot Size");
        VBox maxLotSizeVBox = setupTextVBox(maxLotSizeText, "Maximum Lot Size");
        VBox averageLotSize = setupTextVBox(averageLotSizeText, "Average Lot Size");

        return new VBox(minLotSizeVBox, maxLotSizeVBox, averageLotSize);
    }

    /**
     * Creates an HBox that holds the Assessed Values and lot size information of the property assessments
     * @return An HBox that holds the details of the property values
     */
    private HBox createDetailsHBox() {
        VBox valuesVBox = createValuesVBox();
        VBox lotSizeVBox = createLotSizeVBox();

        HBox detailsHBox = new HBox(valuesVBox, lotSizeVBox);

        valuesVBox.prefWidthProperty().bind(detailsHBox.widthProperty().multiply(.5));

        return detailsHBox;
    }

    /**
     * Creates a VBox that holds the overview information
     * @return A VBox with the overview information
     */
    private VBox createOverviewVBox() {
        HBox detailsHBox = createDetailsHBox();
        Label overViewLabel = createLabel("Property Assessments Overview", titleFont);
        //overViewVBox.prefHeightProperty().bind(primaryStage.heightProperty().multiply(31.80));
        return new VBox(overViewLabel, detailsHBox, graph);
    }

    /**
     * Builds the VBox that holds the tabs
     * @return a VBox containing the tabs
     */
    private VBox createTabVBox() {
        VBox tableVBox =  createTableVBox();
        VBox overviewVBox = createOverviewVBox();
        Tab overviewTab = new Tab("Overview", overviewVBox);
        Tab tableTab = new Tab("Property Assessment List", tableVBox);

        overviewTab.setClosable(false);
        tableTab.setClosable(false);

        TabPane tabPane = new TabPane(overviewTab, tableTab);

        Label tabLabel = new Label("Property Assessments");
        tabLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        VBox tabVBox = new VBox(tabLabel, tabPane);

        tableVBox.prefHeightProperty().bind(tabVBox.heightProperty());

        return tabVBox;
    }

    /**
     * Builds a filter based on the values in the search boxes
     * @return a populated Filter
     */
    private Filter buildFilter() {

        if (this.assessmentClassSelect == null) return null;

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

        return builder.build();
    }

    /**
     * Returns a currency formatted string of an integer if greater than or equal to 0, or the string 'No Data Found'
     * @param value The integer to be converted to a string
     * @return the string value of an integer or 'No Data Found' if value less than 0
     */
    private String valueString(int value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.CANADA);
        currencyFormat.setRoundingMode(RoundingMode.HALF_UP);
        currencyFormat.setMaximumFractionDigits(0);

        return value >= 0 ? currencyFormat.format(value) : "No Data Found";
    }

    /**
     * Return a formatted string of an integer representing a property's lot size
     * @param value The property's lot size (in sq meters)
     * @return A formatted string of the property's lot size (comma separated), followed by m^2
     */
    private String lotSizeString(int value) {
        if (value < 0) {
            return "No Data Found";
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value) + " m²";
    }

    /**
     * Populates the text fields in the overview tab
     */
    private void populateOverview() {
        Filter filter = buildFilter();

        String minValueString = valueString((int) propertyAssessmentDAO.min(filter));
        String maxValueString = valueString((int) propertyAssessmentDAO.max(filter));
        String averageValueString = valueString((int) propertyAssessmentDAO.average(filter));

        String minLotSizeString = lotSizeString(propertyAssessmentDAO.minLotSize(filter));
        String maxLotSizeString = lotSizeString(propertyAssessmentDAO.maxLotSize(filter));
        String averageLotSizeString = lotSizeString(propertyAssessmentDAO.averageLotSize(filter));

        minValueText.setText(minValueString);
        maxValueText.setText(maxValueString);
        averageValueText.setText(averageValueString);

        minLotSizeText.setText(minLotSizeString);
        maxLotSizeText.setText(maxLotSizeString);
        averageLotSizeText.setText(averageLotSizeString);

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
        Set<String> assessmentClassSet = propertyAssessmentDAO.getAssessmentClasses();
        assessmentClassSet.add("");

        assessmentClasses.addAll(assessmentClassSet);

        assessmentClassSelect.setValue("");

        if (propertyAssessmentList.size() > 0) {
            propertyAssessments.addAll(propertyAssessmentList);
        }

        populateOverview();
        overviewGUpdate();
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
        return propertyAssessmentDAO.getPropertyAssessments(buildFilter());
    }

    /**
     * Function called when the search button is pressed.
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
                populateOverview();
                overviewGUpdate();
            }
        } else {

            List<PropertyAssessment> filterdList = getFilteredList();
            if(filterdList.size() == 0) {
                showNoDataAlert("Oops, did not find anything");
            } else {
                propertyAssessments.clear();
                propertyAssessments.addAll(getFilteredList());
                populateOverview();
                overviewGUpdate();
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
     * Sets up a button going to the main scene
     * @return a button for returning to main scene
     */
    private Button setupBackButton() {
        Button backButton = new Button("Back");

        backButton.setOnAction(event -> primaryStage.setScene(mainScene));

        return backButton;
    }

    /**
     * Creates a VBox that will have title and some data
     * @param title The title of the VBox
     * @param value The value to be displayed in the VBox
     * @return a VBox with a title and some data
     */
    private VBox createDataVBox (String title, String value){
        Text textTitle = new Text(title);
        Text textValue = new Text(value);
        textTitle.setFont(dataFont);
        textValue.setFont(dataFont);
        VBox dataVBox = new VBox(textTitle, textValue);
        dataVBox.setPadding(new Insets(0, 0, 10, 0));

        return dataVBox;
    }

    /**
     * Creates and populates a VBox that will hold the address of a property assessment
     * @param address the address object whose data will be used to populate the VBox
     * @return A VBox with address data in it
     */
    private VBox createAddressVBox(Address address) {
        StringBuilder addressString = new StringBuilder();

        String suite = address.suite();
        String houseNumber = address.houseNumber();
        String street = address.street();

        if(!suite.equals("")){
            addressString.append(suite).append(" ");
        }

        if(!houseNumber.equals("")) {
            addressString.append(houseNumber).append(" ");
        }

        if(!street.equals("")) {
            addressString.append(street);
        }

        return createDataVBox("Address:", addressString.toString());
    }

    /**
     * Creates a label with specified label text and font
     * @param labelText The text to put in the Label
     * @param font The font to be applied to the label
     * @return A Label with specified text and font
     */
    private Label createLabel(String labelText, Font font){
        Label label = new Label(labelText);
        label.setFont(font);
        label.setPadding(new Insets(0, 0, 10, 0));

        return label;
    }

    /**
     * Creates the VBox that holds the specifics of a property assessment
     * @param propertyAssessment The property assessment whose data will be displayed in the VBox
     * @return An VBox that has data of a property assessment
     */
    private VBox createSpecificsHBox(PropertyAssessment propertyAssessment) {
        Label specificsLabel = createLabel("Property Specifics", headingFont);

        VBox accountNumberVBox = createDataVBox("Account Number:", Integer.toString(propertyAssessment.getAccountNumber()));

        VBox addressVBox = createAddressVBox(propertyAssessment.getAddress());

        VBox neighbourhoodVBox = createDataVBox("Neighbourhood", propertyAssessment.getNeighbourhood());

        return new VBox(specificsLabel, accountNumberVBox, addressVBox, neighbourhoodVBox);
    }

    /**
     * A VBox that holds the current data of a property assessment
     * @param propertyAssessment The property assessment whose current data will be displayed in the VBox
     * @return A VBox with a property assessment's current data
     */
    private VBox createCurrentDataVBox(PropertyAssessment propertyAssessment) {
        Label currentDataLabel = createLabel("Current Data", headingFont);

        String formattedValue = valueString(propertyAssessment.getAssessedValue());
        VBox currentValueVBox = createDataVBox("Current Value", formattedValue);

        return new VBox(currentDataLabel, currentValueVBox);
    }

    /**
     * Creates the VBox for displaying the current assessment data
     * @param propertyAssessment the property assessment whose data is to be displayed
     * @return the VBox containing a property assessment's current assessment data
     */
    private HBox createPropertyAssessmentCurrentInformationHBox(PropertyAssessment propertyAssessment) {
        VBox specificsVBox = createSpecificsHBox(propertyAssessment);
        VBox currentDataHBox = createCurrentDataVBox(propertyAssessment);

        HBox propertyAssessmentCurrentInformationHBox = new HBox();

        specificsVBox.prefWidthProperty().bind(propertyAssessmentCurrentInformationHBox.widthProperty().multiply(.5));
        currentDataHBox.prefWidthProperty().bind(propertyAssessmentCurrentInformationHBox.widthProperty().multiply(.5));

        specificsVBox.setPadding(new Insets(0, 0, 0, 50));

        propertyAssessmentCurrentInformationHBox.getChildren().addAll(specificsVBox, currentDataHBox);

        return propertyAssessmentCurrentInformationHBox;
    }

    /**
     * Creates the header for the property assessment scene
     * @return an HBox containing the header of the property assessment scene
     */
    HBox createPropertyAssessmentHeader() {
        Button backButton = setupBackButton();
        Label propertyAssessmentTitle = new Label("Property Assessment");
        propertyAssessmentTitle.setFont(titleFont);

        HBox headerHBox = new HBox(backButton, propertyAssessmentTitle);

        propertyAssessmentTitle.prefWidthProperty().bind(headerHBox.widthProperty().multiply(.95));
        backButton.prefWidthProperty().bind(headerHBox.widthProperty().multiply(.05));

        propertyAssessmentTitle.setAlignment(Pos.CENTER);

        return headerHBox;
    }

    /**
     * creates a VBox for displaying a property's assessment data
     * @param propertyAssessment the property assessment whose data is being displayed
     * @return a VBox containing a property assessment's data and a back button
     */
    private VBox createPropertyAssessmentVBox(PropertyAssessment propertyAssessment) {
        HBox propertyAssessmentHeader = createPropertyAssessmentHeader();
        HBox currentInformationVBox = createPropertyAssessmentCurrentInformationHBox(propertyAssessment);

        currentInformationVBox.setPadding(new Insets(30, 10, 10, 10));

        VBox propertyAssessmentVBox = new VBox();

        currentInformationVBox.prefWidthProperty().bind(propertyAssessmentVBox.widthProperty());

        CompletableFuture<List<Integer>> historicalValues = propertyAssessmentDAO.getHistoricalPropertyValuesByAccountNumber(propertyAssessment.getAccountNumber());

        specificGUpdate(historicalValues);

        return new VBox(propertyAssessmentHeader, currentInformationVBox, graph2);
    }

    /**
     * Creates and displays a scene to show a single property assessment's data
     * @param propertyAssessment The property assessment to be displayed
     */
    private void showProperty(PropertyAssessment propertyAssessment) {
        VBox propertyAssessmentVBox = createPropertyAssessmentVBox(propertyAssessment);
        Scene propertyAssessmentScene = new Scene(propertyAssessmentVBox, mainScene.getWidth(), mainScene.getHeight());
        propertyAssessmentVBox.setStyle("-fx-background-color: #FFF5EB");
        primaryStage.setScene(propertyAssessmentScene);
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

                    if(!address.suite().isEmpty()) {
                        addressString.append(address.suite()).append(" ");
                    }

                    if(!address.houseNumber().isEmpty()) {
                        addressString.append(address.houseNumber()).append(" ");
                    }

                    addressString.append(address.street());

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

                        assessmentClassString.append(assessmentClassList.get(i).assessmentClassName()).append(" ");
                        assessmentClassString.append(assessmentClassList.get(i).assessmentClassPercentage());
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

        // Set up a listener to listen for a click on a row in table
        table.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            if(t1.intValue() > -1) {
                // show the property that was clicked on
                showProperty(table.getItems().get(t1.intValue()));
            }
        });

        tableVBox.getChildren().addAll(tableLabel, table);

        return tableVBox;
    }


    /**
     *
     * update overview graph with new info on new search
     */
    private void overviewGUpdate(){
        this.graph.getData().clear();

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Average Value");

        Filter filter = buildFilter();
        CompletableFuture<List<Integer>> futureAvgs = propertyAssessmentDAO.getAvgHistoricalValues(filter);

        futureAvgs.thenAccept( vals -> Platform.runLater(() -> {
            for(int i=0; i < 11; i++){
                series1.getData().add(
                        new XYChart.Data(Integer.toString((2012+i)), (((float)vals.get(i)/(float)vals.get(0))-1)*100)
                );
            }
        }));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Minimum Value");

        CompletableFuture<List<Integer>> futureMins = propertyAssessmentDAO.getAvgHistoricalMin(filter);
        futureMins.thenAccept( vals -> Platform.runLater(() -> {
            for(int i=0; i < 11; i++){
                series2.getData().add(
                        new XYChart.Data(Integer.toString((2012+i)), (((float)vals.get(i)/(float)vals.get(0))-1)*100)
                );
            }
        }));

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Maximum Value");
        CompletableFuture<List<Integer>> futureMaxs = propertyAssessmentDAO.getAvgHistoricalMax(filter);
        futureMaxs.thenAccept( vals -> {
            Platform.runLater(() -> {
                for(int i=0; i < 11; i++){
                    series3.getData().add(
                            new XYChart.Data(Integer.toString((2012+i)), (((float)vals.get(i)/(float)vals.get(0))-1)*100)
                    );
                }
            });
        });

        this.graph.getData().addAll(series1,series2,series3);
    }

    /**
     * update specific graph on page open
     * @param historicalVals a promise that resolves to a list of property values used to poopulate the graph
     */
    private void specificGUpdate(CompletableFuture<List<Integer>> historicalVals){
        this.graph2.getData().clear();

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Value");

        historicalVals.thenAccept( vals -> Platform.runLater(() -> {
            for(int i=0; i < 11; i++)
                series1.getData().add(new XYChart.Data(Integer.toString((2012+i)), (float)vals.get(i)));
        }));

        this.graph2.getData().addAll(series1);
    }

    /**
     * Initialize graph on overview tab
     * @return Line chart
     */
    private LineChart<String,Float> overviewGraph(){

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Year");
        yAxis.setLabel("Percent Change(%)");
        LineChart<String,Float> lineChart =
                new LineChart(xAxis,yAxis);
        lineChart.setPrefHeight(700);
        lineChart.setTitle("10 Year Growth");

        return lineChart;
    }

    /**
     * Initialize graph on specific property page
     */
    private LineChart<String,Float> specificGraph(){

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Year");
        yAxis.setLabel("Value($)");
        LineChart<String,Float> lineChart =
                new LineChart(xAxis,yAxis);
        lineChart.setPrefHeight(700);

        return lineChart;
    }


    public static void main(String[] args) {
        launch();
    }
}