package edu.cmu.idrift.Model;

/**
 * Created by sophiejeong on 4/2/14.
 */
public class Event {

    private long id;
    private String name;

    private String dateTime;
    private String location;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public long getId(){
        return id;
    }


    public String toString() {
        return name;
    }
}
