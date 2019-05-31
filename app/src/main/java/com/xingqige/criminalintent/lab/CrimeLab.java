package com.xingqige.criminalintent.lab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xingqige.criminalintent.model.Crime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeDbSchema.CrimeBaseHelper;
import database.CrimeDbSchema.CrimeCursorWrapper;
import database.CrimeDbSchema.CrimeDbSchema;

public class CrimeLab {

    private static CrimeLab sCrimeLab;

//    private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
//        mCrimes = new ArrayList<>();
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToNext();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }

    }

    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void removeCrime(UUID id){
        Crime crime = getCrime(id);
//        mCrimes.remove(crime);
        mDatabase.delete(
                CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getmId().toString();
        ContentValues values = getContentValues(crime);
        int update = mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null,  // Columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
                );
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getmId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, "");

        return values;
    }

}
