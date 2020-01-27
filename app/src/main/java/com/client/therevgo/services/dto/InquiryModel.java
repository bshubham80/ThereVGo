package com.client.therevgo.services.dto;

/**
 * Created by shubham on 20/11/17.
 */

public class InquiryModel {

    private int id;
    private int con_id;
    private String cus_name;
    private String cus_email_id;
    private String cus_phne_no;
    private String v_phone_no;
    private String v_bus_name;
    private String v_address;
    private String creation_date;
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCon_id() {
        return con_id;
    }

    public void setCon_id(int con_id) {
        this.con_id = con_id;
    }

    public String getName() {
        return cus_name;
    }

    public void setCus_name(String cus_name) {
        this.cus_name = cus_name;
    }

    public String getEmail() {
        return cus_email_id;
    }

    public void setCus_email_id(String cus_email_id) {
        this.cus_email_id = cus_email_id;
    }

    public String getContact() {
        return cus_phne_no;
    }

    public void setCus_phne_no(String cus_phne_no) {
        this.cus_phne_no = cus_phne_no;
    }

    public String getV_phone_no() {
        return v_phone_no;
    }

    public void setV_phone_no(String v_phone_no) {
        this.v_phone_no = v_phone_no;
    }

    public String getV_bus_name() {
        return v_bus_name;
    }

    public void setV_bus_name(String v_bus_name) {
        this.v_bus_name = v_bus_name;
    }

    public String getV_address() {
        return v_address;
    }

    public void setV_address(String v_address) {
        this.v_address = v_address;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
