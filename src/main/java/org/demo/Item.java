package org.demo;

import javafx.beans.property.*;

public class Item {

    private final StringProperty sku;
    private final StringProperty descr;
    private final FloatProperty price;
    private final BooleanProperty taxable;

    public Item(String sku, String descr, float price, boolean taxable) {
        this.sku = new SimpleStringProperty(sku);
        this.descr = new SimpleStringProperty(descr);
        this.price = new SimpleFloatProperty(price);
        this.taxable = new SimpleBooleanProperty(taxable);
    }

    // SKU getter and setter
    public String getSku() {
        return sku.get();
    }

    public void setSku(String sku) {
        this.sku.set(sku);
    }

    public StringProperty skuProperty() {
        return sku;
    }

    // Description getter and setter
    public String getDescr() {
        return descr.get();
    }

    public void setDescr(String descr) {
        this.descr.set(descr);
    }

    public StringProperty descrProperty() {
        return descr;
    }

    // Price getter and setter
    public float getPrice() {
        return price.get();
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public FloatProperty priceProperty() {
        return price;
    }

    // Taxable getter and setter
    public boolean isTaxable() {
        return taxable.get();
    }

    public void setTaxable(boolean taxable) {
        this.taxable.set(taxable);
    }

    public BooleanProperty taxableProperty() {
        return taxable;
    }
}
