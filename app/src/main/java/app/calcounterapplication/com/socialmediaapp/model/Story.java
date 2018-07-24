package app.calcounterapplication.com.socialmediaapp.model;

/**
 * Created by idoed on 07/05/2018.
 */

public class Story {

    public String uid;
    public String time;
    public String date;
    public String story;

    public Story(){

    }

    public Story(String uid, String time, String date, String story, String fullName) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.story = story;
        this.fullName = fullName;
    }

    public String fullName;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
