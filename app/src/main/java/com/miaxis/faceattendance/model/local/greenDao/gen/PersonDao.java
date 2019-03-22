package com.miaxis.faceattendance.model.local.greenDao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.miaxis.faceattendance.model.entity.Person;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PERSON".
*/
public class PersonDao extends AbstractDao<Person, Long> {

    public static final String TABLENAME = "PERSON";

    /**
     * Properties of entity Person.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Sex = new Property(2, String.class, "sex", false, "SEX");
        public final static Property CardNumber = new Property(3, String.class, "cardNumber", false, "CARD_NUMBER");
        public final static Property Nation = new Property(4, String.class, "nation", false, "NATION");
        public final static Property Address = new Property(5, String.class, "address", false, "ADDRESS");
        public final static Property ValidateStart = new Property(6, String.class, "validateStart", false, "VALIDATE_START");
        public final static Property ValidateEnd = new Property(7, String.class, "validateEnd", false, "VALIDATE_END");
        public final static Property IssuingAuthority = new Property(8, String.class, "issuingAuthority", false, "ISSUING_AUTHORITY");
        public final static Property Birthday = new Property(9, String.class, "birthday", false, "BIRTHDAY");
        public final static Property CardId = new Property(10, String.class, "cardId", false, "CARD_ID");
        public final static Property Feature = new Property(11, String.class, "feature", false, "FEATURE");
        public final static Property FacePicturePath = new Property(12, String.class, "facePicturePath", false, "FACE_PICTURE_PATH");
    }


    public PersonDao(DaoConfig config) {
        super(config);
    }
    
    public PersonDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PERSON\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"SEX\" TEXT," + // 2: sex
                "\"CARD_NUMBER\" TEXT," + // 3: cardNumber
                "\"NATION\" TEXT," + // 4: nation
                "\"ADDRESS\" TEXT," + // 5: address
                "\"VALIDATE_START\" TEXT," + // 6: validateStart
                "\"VALIDATE_END\" TEXT," + // 7: validateEnd
                "\"ISSUING_AUTHORITY\" TEXT," + // 8: issuingAuthority
                "\"BIRTHDAY\" TEXT," + // 9: birthday
                "\"CARD_ID\" TEXT," + // 10: cardId
                "\"FEATURE\" TEXT," + // 11: feature
                "\"FACE_PICTURE_PATH\" TEXT);"); // 12: facePicturePath
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PERSON\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Person entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(3, sex);
        }
 
        String cardNumber = entity.getCardNumber();
        if (cardNumber != null) {
            stmt.bindString(4, cardNumber);
        }
 
        String nation = entity.getNation();
        if (nation != null) {
            stmt.bindString(5, nation);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(6, address);
        }
 
        String validateStart = entity.getValidateStart();
        if (validateStart != null) {
            stmt.bindString(7, validateStart);
        }
 
        String validateEnd = entity.getValidateEnd();
        if (validateEnd != null) {
            stmt.bindString(8, validateEnd);
        }
 
        String issuingAuthority = entity.getIssuingAuthority();
        if (issuingAuthority != null) {
            stmt.bindString(9, issuingAuthority);
        }
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(10, birthday);
        }
 
        String cardId = entity.getCardId();
        if (cardId != null) {
            stmt.bindString(11, cardId);
        }
 
        String feature = entity.getFeature();
        if (feature != null) {
            stmt.bindString(12, feature);
        }
 
        String facePicturePath = entity.getFacePicturePath();
        if (facePicturePath != null) {
            stmt.bindString(13, facePicturePath);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Person entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(3, sex);
        }
 
        String cardNumber = entity.getCardNumber();
        if (cardNumber != null) {
            stmt.bindString(4, cardNumber);
        }
 
        String nation = entity.getNation();
        if (nation != null) {
            stmt.bindString(5, nation);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(6, address);
        }
 
        String validateStart = entity.getValidateStart();
        if (validateStart != null) {
            stmt.bindString(7, validateStart);
        }
 
        String validateEnd = entity.getValidateEnd();
        if (validateEnd != null) {
            stmt.bindString(8, validateEnd);
        }
 
        String issuingAuthority = entity.getIssuingAuthority();
        if (issuingAuthority != null) {
            stmt.bindString(9, issuingAuthority);
        }
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(10, birthday);
        }
 
        String cardId = entity.getCardId();
        if (cardId != null) {
            stmt.bindString(11, cardId);
        }
 
        String feature = entity.getFeature();
        if (feature != null) {
            stmt.bindString(12, feature);
        }
 
        String facePicturePath = entity.getFacePicturePath();
        if (facePicturePath != null) {
            stmt.bindString(13, facePicturePath);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Person readEntity(Cursor cursor, int offset) {
        Person entity = new Person( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // sex
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // cardNumber
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // nation
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // address
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // validateStart
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // validateEnd
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // issuingAuthority
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // birthday
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // cardId
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // feature
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // facePicturePath
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Person entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSex(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCardNumber(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNation(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAddress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setValidateStart(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setValidateEnd(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIssuingAuthority(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setBirthday(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCardId(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setFeature(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setFacePicturePath(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Person entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Person entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Person entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
