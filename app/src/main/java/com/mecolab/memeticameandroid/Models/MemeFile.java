package com.mecolab.memeticameandroid.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tristan on 21-09-16.
 */

@Table(name="Files")
public class MemeFile extends Model {
    @Column(name="Url")
    public String mUrl;
    @Column(name="MimeType")
    public String mMime_type;

    public MemeFile() { super();}

    public MemeFile(String url, String mime_type) {
        mUrl = url;
        mMime_type = mime_type;
    }

    public static MemeFile from_JsonObject(JSONObject jsonObject) throws JSONException{
        String url = jsonObject.getString("url");
        String mime_type = jsonObject.getString("mime_type");
        MemeFile file = new Select().from(MemeFile.class)
                    .where("Url = ?", url)
                    .executeSingle();
        if (file == null) {
            file = new MemeFile(
                    url,
                    mime_type
            );
            file.save();
        }
        return file;
    }

}
