package com.miaxis.faceattendance.model.local.greenDao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.miaxis.faceattendance.model.entity.converter.DateConverter;

import com.miaxis.faceattendance.model.entity.Record;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RECORD".
*/
public class RecordDao extends AbstractDao<Record, Long> {

    public static final String TABLENAME = "RECORD";

    /**
     * Properties of entity Record.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CardNumber = new Property(1, String.class, "cardNumber", false, "CARD_NUMBER");
        public final static Property FacePicture = new Property(2, String.class, "facePicture", false, "FACE_PICTURE");
        public final static Property Latitude = new Property(3, String.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(4, String.class, "longitude", false, "LONGITUDE");
        public final static Property Location = new Property(5, String.class, "location", false, "LOCATION");
        public final static Property Sex = new Property(6, String.class, "sex", false, "SEX");
        public final static Property Name = new Property(7, String.class, "name", false, "NAME");
        public final static Property VerifyTime = new Property(8, Long.class, "verifyTime", false, "VERIFY_TIME");
        public final static Property Score = new Property(9, String.class, "score", false, "SCORE");
        public final static Property Upload = new Property(10, Boolean.class, "upload", false, "UPLOAD");
        public final static Property CategoryId = new Property(11, long.class, "categoryId", false, "CATEGORY_ID");
    }

    private final DateConverter verifyTimeConverter = new DateConverter();

    public RecordDao(DaoConfig config) {
        super(config);
    }
    
    public RecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CARD_NUMBER\" TEXT," + // 1: cardNumber
                "\"FACE_PICTURE\" TEXT," + // 2: facePicture
                "\"LATITUDE\" TEXT," + // 3: latitude
                "\"LONGITUDE\" TEXT," + // 4: longitude
                "\"LOCATION\" TEXT," + // 5: location
                "\"SEX\" TEXT," + // 6: sex
                "\"NAME\" TEXT," + // 7: name
                "\"VERIFY_TIME\" INTEGER," + // 8: verifyTime
                "\"SCORE\" TEXT," + // 9: score
                "\"UPLOAD\" INTEGER," + // 10: upload
                "\"CATEGORY_ID\" INTEGER NOT NULL );"); // 11: categoryId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Record entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cardNumber = entity.getCardNumber();
        if (cardNumber != null) {
            stmt.bindString(2, cardNumber);
        }
 
        String facePicture = entity.getFacePicture();
        if (facePicture != null) {
            stmt.bindString(3, facePicture);
        }
 
        String latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindString(4, latitude);
        }
 
        String longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindString(5, longitude);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(6, location);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(7, sex);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(8, name);
        }
 
        String verifyTime = entity.getVerifyTime();
        if (verifyTime != null) {
            stmt.bindLong(9, verifyTimeConverter.convertToDatabaseValue(verifyTime));
        }
 
        String score = entity.getScore();
        if (score != null) {
            stmt.bindString(10, score);
        }
 
        Boolean upload = entity.getUpload();
        if (upload != null) {
            stmt.bindLong(11, upload ? 1L: 0L);
        }
        stmt.bindLong(12, entity.getCategoryId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Record entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cardNumber = entity.getCardNumber();
        if (cardNumber != null) {
            stmt.bindString(2, cardNumber);
        }
 
        String facePicture = entity.getFacePicture();
        if (facePicture != null) {
            stmt.bindString(3, facePicture);
        }
 
        String latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindString(4, latitude);
        }
 
        String longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindString(5, longitude);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(6, location);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(7, sex);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(8, name);
        }
 
        String verifyTime = entity.getVerifyTime();
        if (verifyTime != null) {
            stmt.bindLong(9, verifyTimeConverter.convertToDatabaseValue(verifyTime));
        }
 
        String score = entity.getScore();
        if (score != null) {
            stmt.bindString(10, score);
        }
 
        Boolean upload = entity.getUpload();
        if (upload != null) {
            stmt.bindLong(11, upload ? 1L: 0L);
        }
        stmt.bindLong(12, entity.getCategoryId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Record readEntity(Cursor cursor, int offset) {
        Record entity = new Record( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // cardNumber
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // facePicture
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // latitude
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // longitude
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // location
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // sex
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // name
            cursor.isNull(offset + 8) ? null : verifyTimeConverter.convertToEntityProperty(cursor.getLong(offset + 8)), // verifyTime
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // score
            cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0, // upload
            cursor.getLong(offset + 11) // categoryId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Record entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCardNumber(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFacePicture(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLatitude(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLongitude(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLocation(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSex(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setVerifyTime(cursor.isNull(offset + 8) ? null : verifyTimeConverter.convertToEntityProperty(cursor.getLong(offset + 8)));
        entity.setScore(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setUpload(cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0);
        entity.setCategoryId(cursor.getLong(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Record entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Record entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Record entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
