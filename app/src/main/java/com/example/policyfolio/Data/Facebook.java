package com.example.policyfolio.Data;


public class Facebook {
    private long id;
    private String email;
    private String name;
    private String birthday;
    private String gender;
    private Location location;

    public Facebook(long id, String email, String name, String birthday, String gender, long locationId, String locationName){
        this.id = id;
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.location = new Location(locationId,locationName);
    }

    private class Location{
        long id;
        String name;

        public Location(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public long getLocationId() {
        return location.getId();
    }

    public void setLocationId(long id) {
        this.location.setId(id);
    }

    public String getLocationName() {
        return location.getName();
    }

    public void setLocationName(String name) {
        this.location.setName(name);
    }
}
