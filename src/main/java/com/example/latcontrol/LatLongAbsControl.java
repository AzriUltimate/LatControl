package com.example.latcontrol;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

public abstract class LatLongAbsControl extends Group {
    private SimpleListProperty<Float> valueForUser;
    private StringBuilder stringBuilder = new StringBuilder();
    private ArrayList<Character> separators = new ArrayList<Character>();
    private ArrayList<Float> values = new ArrayList<Float>();
    private TextField inputField;
    // флаг, который отображает наличие изменений значений поля
    private boolean changeFlag = false;
    private int currentPosition = 0;

    public LatLongAbsControl(){
        separators.add('°');
        separators.add('\'');
        separators.add('\"');
        values.add(0f);
        values.add(0f);
        values.add(0f);
        inputField = new TextField(longitudeValueBuilder());
        inputField.setStyle("-fx-text-fill: black; -fx-border-color: black");
        inputField.setMinSize(40, 20);
        inputField.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 30));

        valueForUser = new SimpleListProperty<Float>(this, "valueForUser", FXCollections.observableArrayList(values));

        GridPane pane = new GridPane();
        pane.add(inputField, 0,0);
        this.getChildren().add(pane);

        TextFormatter<Object> textFormatter = new TextFormatter<>(c-> {
            if (c.isContentChange()){
                String inputChars = c.getText();
                if (inputChars.matches("[[0-9]-\\.]")){ // проверка на допустимые символы

                    // запрет ввода после последнего сепаратора
                    if (inputField.getCaretPosition() == inputField.getLength()) return null;

                    if (inputField.getSelectedText().length() == inputField.getLength()) return null;

                    // условия ввода для знака минуса
                    if (inputChars.equals("-")){
                        if (inputField.getCaretPosition() == 0 && inputField.getText().charAt(0) != '-') {
                            currentPosition = getValuePosition();
                            changeFlag = true;
                            return c;
                        }
                        if (inputField.getSelectedText().length()>0 && getValuePosition() == 0){
                            currentPosition = getValuePosition();
                            changeFlag = true;
                            return c;
                        }
                        return null;
                    }

                    // условия ввода для знака точки
                    if (inputChars.equals(".")){
                        if (getValuePosition() == 2 && dotInLastNumber()) {
                            currentPosition = getValuePosition();
                            changeFlag = true;
                            return c;
                        }
                        else return null;
                    }
                    currentPosition = getValuePosition();
                    changeFlag = true;
                    return c;
                }

                // условия для удаления
                if (c.isDeleted()){
                    char deletedChar = inputField.getCharacters().charAt(c.getCaretPosition());
                    String selectedString = inputField.getSelectedText();

                    // запрет на удаление сепаратора (без выделения)
                    if (separators.contains(deletedChar) && inputField.getCaretPosition()!=0){
                        return null;
                    }

                    // запрет на удаление выделенного текста в котором есть сепараторы
                    for (char i : selectedString.toCharArray()){
                        if (separators.contains(i)) return null;
                    }

                    // запрет на замещение значений не числами
                    if (c.getText().length()==1){
                        if (!(c.getText().matches("[0-9]"))){
                            return null;
                        }
                        else changeFlag = true;
                    }

                    currentPosition = getValuePosition();
                    changeFlag = true;
                    return c;
                }
                return null;
            }
            return c;
        });

        inputField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT){
                    if (inputField.getCaretPosition() == inputField.getLength()){
                        inputField.positionCaret(inputField.getLength()-1);
                    }
                    updateTextfield();
                    updateValuesForUser();
                    selectNextWord(currentPosition);
                }
                if (keyEvent.getCode() == KeyCode.LEFT){
                    updateTextfield();
                    updateValuesForUser();
                    selectPreviousWord(currentPosition);
                }
                if (inputField.getSelectedText().length()>0){
                    if (separators.contains(inputField.getSelectedText().charAt(0))){
                        inputField.deselect();
                    }
                }
            }
        });

        // выделение нажатием мыши
        inputField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (inputField.getCaretPosition() == inputField.getLength()){
                    inputField.positionCaret(inputField.getLength()-1);
                }
                currentPosition = getValuePosition();
                updateTextfield();
                updateValuesForUser();
                selectValueOnPosition(currentPosition);
            }
        });

        // логика, связанная с потерей фокуса на объекте
        inputField.focusedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && oldValue){
                    updateValues();
                    if (validateValues()){
                        updateTextfield();
                        updateValuesForUser();
                        inputField.setStyle("-fx-text-fill: black; -fx-border-color: black");
                    }
                    else{
                        values = new ArrayList<Float>(valueForUser.get());
                        inputField.setText(longitudeValueBuilder());
                        inputField.setStyle("-fx-text-fill: black; -fx-border-color: black");
                    }
                }
            }
        });

        inputField.setTextFormatter(textFormatter);
    }

    public ObservableList<Float> getValueForUser(){
        return valueForUser.get();
    }

    public void setValueForUser(ObservableList<Float> valueForUser){
        this.valueForUser.set(valueForUser);
    }

    public SimpleListProperty<Float> valueForUserProperty(){
        return this.valueForUser;
    }

    public Float getDegrees() {return values.get(0);}
    public Float getMinutes() {return values.get(1);}
    public Float getSeconds() {return values.get(2);}

    public abstract String getOutput();

    private String longitudeValueBuilder(){
        stringBuilder.setLength(0);
        for (int i=0;i<2;i++){
            stringBuilder.append(values.get(i).intValue());
            stringBuilder.append(separators.get(i));
        }

        if (values.get(2).intValue() - values.get(2) != 0) stringBuilder.append(values.get(2));
        else stringBuilder.append(values.get(2).intValue());
        stringBuilder.append(separators.get(2));

        return stringBuilder.toString();
    }

    // метод возвращает true, если ввод '.' допустим
    private boolean dotInLastNumber(){
        String lastNumber = getValuesWhereCaretIs();
        return (lastNumber.length() - lastNumber.replace(".", "").length()) == 0;
    }

    private int getValuePosition(){
        int position = 0;
        for (int i = 0; i<inputField.getCaretPosition(); i++){
            if (separators.contains(inputField.getText().charAt(i))) position++;
        }
        return position;
    }
    private String getValuesWhereCaretIs(){
        String textfieldValue = inputField.getText();
        stringBuilder.setLength(0);
        int separatorNumber = 0;
        String value = "";
        for (int i = 0; i<textfieldValue.length(); i++){
            if (separators.contains(textfieldValue.charAt(i))){
                if (separatorNumber == getValuePosition()){
                    value = stringBuilder.toString();
                    break;
                }
                else {
                    separatorNumber++;
                    stringBuilder.setLength(0);
                }
            }
            else stringBuilder.append(textfieldValue.charAt(i));
        }
        return value;
    }

    private void updateValues(){
        String textfieldValue = inputField.getText();
        stringBuilder.setLength(0);
        values.clear();
        int valuesOrder = 0;
        for (int i = 0; i<textfieldValue.length(); i++){
            if (separators.contains(textfieldValue.charAt(i))){
                if (stringBuilder.toString().equals("") || stringBuilder.toString().equals(".") || stringBuilder.toString().equals("-")){
                    stringBuilder.append(0);
                }
                values.add(valuesOrder, Float.valueOf(stringBuilder.toString()));
                valuesOrder++;
                stringBuilder.setLength(0);
            }
            else stringBuilder.append(textfieldValue.charAt(i));
        }
    }

    protected void updateTextfield(){
        selectValueOnPosition(currentPosition);
        if (changeFlag){
            updateValues();
            if (validateValues()) inputField.setStyle("-fx-text-fill: black; -fx-border-color: black");
            else inputField.setStyle("-fx-text-fill: red; -fx-border-color: red");
            inputField.setText(longitudeValueBuilder());
            changeFlag = false;
        }
    }

    private void updateValuesForUser(){
        if (validateValues()){
            valueForUser.set(FXCollections.observableArrayList(values));
        }
    }

    protected abstract boolean validateValues();

    private void selectValueOnPosition(int position){
        String fieldText = inputField.getText();
        int headSelectPosition = 0;
        int tailSelectPosition = 0;
        for (int i = 0; i<fieldText.length(); i++){
            tailSelectPosition = i;
            if (separators.contains(fieldText.charAt(i))){
                if (fieldText.charAt(i) == separators.get(position)){
                    break;
                }
                headSelectPosition = i+1;
                tailSelectPosition = i+1;
            }
        }
        inputField.selectRange(headSelectPosition, tailSelectPosition);
    }

    private void selectPreviousWord(int position){
        if (position > 0) {
            currentPosition = position-1;
            selectValueOnPosition(currentPosition);
        }
        else {
            currentPosition = 0;
            selectValueOnPosition(currentPosition);
        }
    }

    private void selectNextWord(int position){
        if (position<2) {
            currentPosition = position+1;
            selectValueOnPosition(currentPosition);
        }
        else {
            currentPosition = 2;
            selectValueOnPosition(currentPosition);
        }
    }
}