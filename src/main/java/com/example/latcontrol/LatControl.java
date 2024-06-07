package com.example.latcontrol;

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

public class LatControl extends Group {
    StringBuilder stringBuilder = new StringBuilder();
    ArrayList<Character> separators = new ArrayList<Character>();
    ArrayList<Float> values = new ArrayList<Float>();
    TextField latitudeField = null;
    // флаг, который отображает наличие изменений значений поля
    boolean changeFlag = false;

    public LatControl(){
        separators.add('°');
        separators.add('\'');
        separators.add('\"');
        values.add(0.0f);
        values.add(0.0f);
        values.add(0.0f);
        latitudeField = new TextField(latitudeValueBuilder());
        latitudeField.setStyle(".text-field,.text-field-focused");
        latitudeField.setMinSize(100, 40);
        latitudeField.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 30));

        GridPane pane = new GridPane();
        pane.add(latitudeField, 0,0);
        this.getChildren().add(pane);

        // форматер текста
        TextFormatter textFormatter = new TextFormatter<>(c-> {
            if (c.isContentChange()) {

                //позволяет вводить только числа и точку
                if (c.getText().matches("[0-9]") || c.getText().matches("\\.") || c.getText().matches("-")) {
                    String selectedString = latitudeField.getSelectedText();
                    for (char i : separators) {
                        if (selectedString.indexOf(i) != -1) {
                            return null;
                        }
                    }

                    // запрет ввода более друх точек
                    if (c.getText().matches("\\.")){
                        String currentValue = getValuesWhereCaretIs(separators);
                        if ((currentValue.length() - currentValue.replace(".", "").length())>0){
                            return null;
                        }
                    }

                    //проверка на то, чтобы знак минуса добавлялся лишь в начале значения
                    if (c.getText().matches("-")){
                        int carPos = latitudeField.getCaretPosition();
                        if (carPos == 0) return c;
                        if (separators.contains(latitudeField.getCharacters().charAt(carPos-1))) return c;
                        if (latitudeField.getSelectedText().length()>0) return c;
                        return null;
                    }

                    changeFlag = true;
                    return c;
                }

                if (c.isDeleted()) {
                    char deletedChar = latitudeField.getCharacters().charAt(c.getCaretPosition());
                    //запрет удаления значений, являющихся сепаратором
                    if (separators.contains(deletedChar)){
                        return null;
                    }
                    String selectedString = latitudeField.getSelectedText();
                    for (char i : separators){
                        if (selectedString.indexOf(i) != -1) return null;
                    }
                    //запрет на ввод не чисел при замещении выделенного
                    if (c.getText().length()==1){
                        if (!(c.getText().matches("[0-9]") || c.getText().matches("-"))){
                            return null;
                        }
                        else changeFlag = true;
                    }
                    changeFlag = true;
                    return c;
                }

                return null;
            }

            return c;
        });

        latitudeField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectWhereCaretIs(separators);
                updateValues();
                if (changeFlag){
                    int caretPos = latitudeField.getCaretPosition();
                    updateTextfield();
                    latitudeField.positionCaret(caretPos);
                    changeFlag = false;
                }
            }
        });

        latitudeField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT){
                    if (latitudeField.getCaretPosition() == latitudeField.getLength()){
                        latitudeField.positionCaret(latitudeField.getLength()-1);
                    }
                    updateValues();
                    if (changeFlag){
                        int caretPos = latitudeField.getCaretPosition();
                        updateTextfield();
                        latitudeField.positionCaret(caretPos);
                        changeFlag = false;
                    }
                    selectWhereCaretIs(separators);
                }
                if (keyEvent.getCode() == KeyCode.LEFT){
                    latitudeField.positionCaret(latitudeField.getSelection().getStart()-1);
                    updateValues();
                    if (changeFlag){
                        int caretPos = latitudeField.getCaretPosition();
                        updateTextfield();
                        latitudeField.positionCaret(caretPos);
                        changeFlag = false;
                    }
                    selectWhereCaretIs(separators);
                }
                if (latitudeField.getSelectedText().length()>0){
                    if (separators.contains(latitudeField.getSelectedText().charAt(0))){
                        latitudeField.deselect();
                    }
                }
            }
        });
        latitudeField.setTextFormatter(textFormatter);
    }

    ////////////////////////////////////////
    private String latitudeValueBuilder(){
        stringBuilder.setLength(0);
        for (int i=0;i<3;i++){
            stringBuilder.append(values.get(i));
            stringBuilder.append(separators.get(i));
        }
        return stringBuilder.toString();
    }

    private void selectWhereCaretIs(ArrayList<Character> separators){
        int caretPosition = latitudeField.getCaretPosition();
        String fieldText = latitudeField.getText();
        if (caretPosition >= 0){
            int sep1, sep2 = -1;
            for (int i = 0; i<fieldText.length(); i++){
                if (separators.contains(fieldText.charAt(i))){
                    sep1 = sep2;
                    sep2 = i;
                    if (caretPosition > sep1 && caretPosition < sep2){
                        latitudeField.selectRange(sep1+1, sep2);
                    }
                }
            }
            if (caretPosition > sep2 && caretPosition < fieldText.length()){
                latitudeField.selectRange(sep2+1, fieldText.length());
            }
        }
    }

    private String getValuesWhereCaretIs(ArrayList<Character> separators){
        int caretPosition = latitudeField.getCaretPosition();
        stringBuilder.setLength(0);
        char iterChar = ' ';
        String value = "";
        for (int i = 0; i<caretPosition;i++){
            iterChar = latitudeField.getText().charAt(i);
            if (separators.contains(iterChar)) stringBuilder.setLength(0);
            else stringBuilder.append(iterChar);
        }
        value = stringBuilder.toString();
        return value;
    }

    private void updateValues(){
        String textfieldValue = latitudeField.getText();
        stringBuilder.setLength(0);
        values.clear();
        int valuesOrder = 0;
        for (int i = 0; i<textfieldValue.length(); i++){
            if (separators.contains(textfieldValue.charAt(i))){
                if (stringBuilder.toString().equals("")){
                    stringBuilder.append(0);
                }
                values.add(valuesOrder, Float.valueOf(stringBuilder.toString()));
                valuesOrder++;
                stringBuilder.setLength(0);
            }
            else{
                stringBuilder.append(textfieldValue.charAt(i));
            }
        }
        System.out.println(values);
    }

    private void updateTextfield(){
        latitudeField.setText(latitudeValueBuilder());
        System.out.println("updated! " + latitudeValueBuilder());
    }
}