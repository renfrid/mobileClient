package org.sacids.android.database;

/**
 * Created by Renfrid-Sacids on 3/15/2016.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.sacids.android.models.Feedback;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "odk_db";

    // Feedback table name
    private static final String TABLE_FEEDBACK = "feedback";

    // Feedback Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FEEDBACK_ID = "feedback_id";
    private static final String KEY_FORM_ID = "form_id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATE_CREATED = "date_created";

    private Feedback feedback = null;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FEEDBACK_TABLE = "CREATE TABLE "
                + TABLE_FEEDBACK + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FEEDBACK_ID + "INTEGER,"
                + KEY_FORM_ID + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_DATE_CREATED + " TEXT" + ")";
        db.execSQL(CREATE_FEEDBACK_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FEEDBACK_ID, feedback.getId()); // Feedback id
        values.put(KEY_FORM_ID, feedback.getFormId()); // Form id
        values.put(KEY_MESSAGE, feedback.getMessage()); //Message
        values.put(KEY_DATE_CREATED, feedback.getDateCreated()); //date created

        // Inserting Row
        db.insert(TABLE_FEEDBACK, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Feedback
    public Feedback getFeedback(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FEEDBACK, new String[] { KEY_ID,
                        KEY_FEEDBACK_ID,KEY_FORM_ID, KEY_MESSAGE, KEY_DATE_CREATED }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Feedback feedback = new Feedback(Long.parseLong(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3));
        // return feedback
        return feedback;
    }

    public Feedback getLastFeedback(){
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //check if cursor not null
        if(cursor != null && cursor.moveToFirst()) {
            //feedback constructor
            feedback = new Feedback(Long.parseLong(cursor.getString(1)),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        // return feedback
        return feedback;
    }

    // Getting All Feedback
    public List<Feedback> getAllFeedback() {

        List<Feedback> feedbackList = new ArrayList<Feedback>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback();
                feedback.setId(Integer.parseInt(cursor.getString(1)));
                feedback.setFormId(cursor.getString(2));
                feedback.setMessage(cursor.getString(3));
                feedback.setDateCreated(cursor.getString(4));

                // Adding feedback to list
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
        }

        // return feedback list
        return feedbackList;
    }



    // Updating single feedback
    public int updateFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FEEDBACK_ID, feedback.getId()); // Form id
        values.put(KEY_FORM_ID, feedback.getFormId()); // Form id
        values.put(KEY_MESSAGE, feedback.getMessage()); //Message
        values.put(KEY_DATE_CREATED, feedback.getDateCreated()); //date created

        // updating row
        return db.update(TABLE_FEEDBACK, values, KEY_ID + " = ?",
                new String[] { String.valueOf(feedback.getId())});
    }

    // Deleting single feedback
    public void deleteFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEEDBACK, KEY_ID + " = ?",
                new String[] { String.valueOf(feedback.getId()) });
        db.close();
    }


    // Getting feedback Count
    public int getFeedbackCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FEEDBACK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}

