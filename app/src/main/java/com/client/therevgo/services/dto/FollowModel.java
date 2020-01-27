package com.client.therevgo.services.dto;

/**
 * Created by shubham on 17/7/16.
 */
public class FollowModel {

            /*"id": 34,
            "userid": 47,
            "type_of_user": "Customer",
            "name": "Mr. Shubham",
            "mobile_no": "9138916815",
            "email_id": "bshubham102@gmail.com",
            "description": "Hello ",
            "follow_up_date": "17 Jul 2016",
            "area": "Railway Station ",
            "city": "Rewari",
            "creation_date": "2016-07-17T14:25:55.543",
            "created_by": 47,
            "modification_date": "2016-07-17T14:25:55.543",
            "modified_by": 47*/
    int id , userId ;
    String name , contact , email , description , followData , area , city ;
    boolean isSelected ;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFollowData() {
        return followData;
    }

    public void setFollowData(String followData) {
        this.followData = followData;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
