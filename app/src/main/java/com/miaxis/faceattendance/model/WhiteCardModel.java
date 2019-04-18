package com.miaxis.faceattendance.model;

import com.miaxis.faceattendance.manager.DaoManager;
import com.miaxis.faceattendance.model.entity.WhiteCard;
import com.miaxis.faceattendance.model.local.greenDao.gen.WhiteCardDao;

import java.util.List;

public class WhiteCardModel {

    public static void saveWhiteCard(WhiteCard whiteCard) {
        DaoManager.getInstance().getDaoSession().getWhiteCardDao().insertOrReplace(whiteCard);
    }

    public static void saveWhiteCardList(List<WhiteCard> whiteCardList) {
        DaoManager.getInstance().getDaoSession().getWhiteCardDao().insertOrReplaceInTx(whiteCardList);
    }

    public static void deleteWhileCard(WhiteCard whiteCard) {
        DaoManager.getInstance().getDaoSession().getWhiteCardDao().delete(whiteCard);
    }

    public static WhiteCard getWhiteCardByCardNumber(String cardNumber) {
        return DaoManager.getInstance().getDaoSession().getWhiteCardDao().queryBuilder()
                .where(WhiteCardDao.Properties.CardNumber.eq(cardNumber))
                .unique();
    }

    public static void deleteWhileCardList(List<String> cardNumberList) {
        for (String cardNumber : cardNumberList) {
            WhiteCard whiteCard = getWhiteCardByCardNumber(cardNumber);
            if (whiteCard != null) {
                deleteWhileCard(whiteCard);
            }
        }
    }

}
