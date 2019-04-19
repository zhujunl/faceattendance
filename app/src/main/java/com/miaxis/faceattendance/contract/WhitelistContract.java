package com.miaxis.faceattendance.contract;

import com.miaxis.faceattendance.model.entity.WhiteCard;

import java.util.List;

public interface WhitelistContract {
    interface View extends BaseContract.View {
        void loadWhitelistCallback(List<WhiteCard> whiteCardList);
        void addWhiteCardCallback(boolean result);
        void deleteWhiteCardCallback(WhiteCard whiteCard, boolean result);
        void getWhiteCardByCardNumberCallback(WhiteCard whiteCard);
    }

    interface Presenter extends BaseContract.Presenter {
        void loadWhitelist(int pageNum, int pageSize);
        void addWhiteCard(String name, String cardNumber);
        void deleteWhiteCard(WhiteCard whiteCard);
        void getWhiteCardByCardNumber(String cardNumber);
    }
}
