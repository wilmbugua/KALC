/*
**    KALC POS  - Professional Point of Sale
**
**    This file is part of KALC POS Version KALC V1.5.4
**
**    Copyright (c) 2015-2023 KALC & previous KALC POS related works   
**
**    https://www.kalc.co.ke
**   
**
*/


package ke.kalc.commons.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ke.kalc.data.loader.DataRead;
import ke.kalc.data.loader.SerializerRead;

public class KeyedData {

    private final StringProperty id;
    private final StringProperty name;
    private int age = 0;

    public KeyedData(String id, String name) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
    }

    public KeyedData(Integer id, String name) {
        this.id = new SimpleStringProperty(String.valueOf(id));
        this.name = new SimpleStringProperty(name);
    }

    public KeyedData(String id, Integer age) {
        name= new SimpleStringProperty("");
        this.id = new SimpleStringProperty(id);
        this.age = age;
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public static SerializerRead getSerializerRead() {
        return (DataRead dr) -> new KeyedData(dr.getString(1), dr.getString(2));
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public Integer getIDInt() {
        return Integer.valueOf(id.get().trim());
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Integer getInt() {
        return Integer.valueOf(name.get().trim());
    }

    public String getTrimmedName() {
        return name.get().trim();
    }

    //Check if keyeddata list contains passed value
    @Override
    public boolean equals(Object object) {
        if ((this.getId() == null) || object == null || object.getClass() != getClass()) {
            return false;
        } else {
            KeyedData test = (KeyedData) object;
            if (this.getId().equals(test.getId()) && this.getName().equals(test.getName())) {
                return true;
            }
        }
        return false;
    }

}
