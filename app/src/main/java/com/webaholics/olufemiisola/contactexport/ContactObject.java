package com.webaholics.olufemiisola.contactexport;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Olufemi Isola on 26/11/17.
 */

public class ContactObject {
    private String _firstName;
    private String _lastName;
    private String _phone;
    private String _address;
    private String _email;
    private String _photoUri;
    private String _displayName;
    private String _lookUpKey;
    private String _ID;
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("ID", _ID);
            obj.put("DisplayName",_displayName);
            obj.put("FirstName", _firstName);
            obj.put("LastName",_lastName);
            obj.put("Phone",_phone);
            obj.put("Address",_address);
            obj.put("Email",_email);
        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
        }
        return obj;
    }

    public String get_ID() {
        return _ID;
    }

    public String get_lookUpKey() {
        return _lookUpKey;
    }

    public void set_lookUpKey(String _lookUpKey) {
        this._lookUpKey = _lookUpKey;
    }

    public String get_photoUri() {
        return _photoUri;
    }

    public void set_photoUri(String _photoUri) {
        this._photoUri = _photoUri;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String get_firstName() {
        return _firstName;
    }

    public void set_firstName(String _firstName) {
        this._firstName = _firstName == null?"":_firstName;
    }
    public String get_lastName() {
        return _lastName;
    }

    public void set_lastName(String _lastName) {
        this._lastName = _lastName == null?"":_lastName;
    }
    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone == null?"":_phone;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address == null?"":_address;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email =  _email == null?"":_email;
    }
    public String get_displayName() {
        return _displayName;
    }

    public void set_displayName(String _displayName) {
        this._displayName = _displayName == null?"":_displayName;
    }


}

