package ar.com.strellis.ampflower.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity(tableName = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{
    @PrimaryKey
    private int id;
    private String username;
    private String auth;
    private String email;
    private int access;
    private String fullname_public;
    private String validation;
    private int disabled;
    private long create_date;
    private long last_seen;
    private String website;
    private String state;
    private String city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getFullname_public() {
        return fullname_public;
    }

    public void setFullname_public(String fullname_public) {
        this.fullname_public = fullname_public;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public int getDisabled() {
        return disabled;
    }

    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
