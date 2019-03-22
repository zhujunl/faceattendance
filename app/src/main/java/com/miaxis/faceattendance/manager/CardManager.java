package com.miaxis.faceattendance.manager;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Base64;

import com.miaxis.faceattendance.event.CardEvent;
import com.miaxis.faceattendance.model.entity.IDCardRecord;
import com.miaxis.faceattendance.util.ValueUtil;
import com.zkteco.android.IDReader.WLTService;

import org.greenrobot.eventbus.EventBus;
import org.zz.idcard_hid_driver.IdCardDriver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class CardManager {

    private CardManager() {
    }

    public static CardManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CardManager instance = new CardManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public static boolean run = true;
    public static boolean noCardFlag = true;

    private IdCardDriver idCardDriver;

    private byte[] lastCardId = null;

    public void startReadCard(Application application) {
        run = true;
        idCardDriver = new IdCardDriver(application);
        new Thread(new ReadIdCardThread()).start();
    }

    public void closeReadCard() {
        run = false;
        idCardDriver = null;
    }

    /* 解析身份证id 字符串 */
    private String getCardIdStr(byte[] cardId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardId.length; i = i + 2) {
            if (cardId[i] == (byte) 0x90 && cardId[i + 1] == (byte) 0x00)
                break;
            sb.append(String.format("%02x", cardId[i]));
            sb.append(String.format("%02x", cardId[i + 1]));
        }
        return sb.toString();
    }

    private void readCard(String curCardId) throws Exception {
        IDCardRecord idCardRecord;
        byte[] bCardInfo = new byte[256 + 1024];
        int re = idCardDriver.mxReadCardInfo(bCardInfo);
        if (re == 0) {
            idCardRecord = analysisIdCardInfo(bCardInfo, curCardId);
        } else {
            throw new Exception("读卡失败");
        }
        if (checkIsOutValidate(idCardRecord)) {
            EventBus.getDefault().post(new CardEvent(CardEvent.OVERDUE));
        } else {
            EventBus.getDefault().post(new CardEvent(idCardRecord));
        }
    }

    private void readCardFull(String curCardId) throws Exception {
        IDCardRecord idCardRecord;
        byte[] bCardFullInfo = new byte[256 + 1024 + 1024];
        int re = idCardDriver.mxReadCardFullInfo(bCardFullInfo);
        if (re == 1) {
            idCardRecord = analysisIdCardInfo(bCardFullInfo, curCardId);
        } else if (re == 0) {
            idCardRecord = analysisIdCardInfo(bCardFullInfo, curCardId);
            byte[] bFingerData0 = new byte[ValueUtil.mFingerDataSize];
            byte[] bFingerData1 = new byte[ValueUtil.mFingerDataSize];
            int iLen = 256 + 1024;
            System.arraycopy(bCardFullInfo, iLen, bFingerData0, 0, bFingerData0.length);
            iLen += 512;
            System.arraycopy(bCardFullInfo, iLen, bFingerData1, 0, bFingerData1.length);
            idCardRecord.setFingerprintPosition0(ValueUtil.fingerPositionCovert(bFingerData0[5]));
            idCardRecord.setFingerprint0(Base64.encodeToString(bFingerData0, Base64.NO_WRAP));
            idCardRecord.setFingerprintPosition1(ValueUtil.fingerPositionCovert(bFingerData1[5]));
            idCardRecord.setFingerprint1(Base64.encodeToString(bFingerData1, Base64.NO_WRAP));
        } else {
            throw new Exception("读卡失败");
        }
        if (checkIsOutValidate(idCardRecord)) {
            EventBus.getDefault().post(new CardEvent(CardEvent.OVERDUE));
        } else {
            EventBus.getDefault().post(new CardEvent(idCardRecord));
        }
    }

    /* 解析身份证信息 */
    private IDCardRecord analysisIdCardInfo(byte[] bCardInfo, String cardId) {
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 民族
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] _RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
        idCardRecord.setCardType("");
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        idCardRecord.setName(ValueUtil.unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            idCardRecord.setSex("男");
        } else {
            idCardRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_Rev, 0, id_Rev.length);
        iLen = iLen + id_Rev.length;
        int iRev = Integer.parseInt(ValueUtil.unicode2String(id_Rev));
        idCardRecord.setNation(ValueUtil.FOLK[iRev - 1]);

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        idCardRecord.setBirthday(ValueUtil.unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        idCardRecord.setAddress(ValueUtil.unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        idCardRecord.setCardNumber(ValueUtil.unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, _RegOrg, 0, _RegOrg.length);
        iLen = iLen + _RegOrg.length;
        idCardRecord.setIssuingAuthority(ValueUtil.unicode2String(_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = ValueUtil.unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = ValueUtil.unicode2String(id_ValidPeriodEnd).trim();
        idCardRecord.setValidateStart(validateStart);
        idCardRecord.setValidateEnd(validateEnd);

        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;
        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap(id_pImage);
        if (bitmap != null) {
            idCardRecord.setCardBitmap(bitmap);
        }
        return idCardRecord;
    }

    private Bitmap getBitmap(byte[] wlt){
        byte[] buffer = new byte[38556];
        int result = WLTService.wlt2Bmp(wlt, buffer);
        if (result==1) {
            return Bgr2Bitmap(buffer);
        }
        return null;
    }

    private Bitmap Bgr2Bitmap(byte[] bgrbuf) {
        int width = WLTService.imgWidth;
        int height = WLTService.imgHeight;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int row = 0, col = width-1;
        for (int i = bgrbuf.length-1; i >= 3; i -= 3) {
            int color = bgrbuf[i] & 0xFF;
            color += (bgrbuf[i-1] << 8) & 0xFF00;
            color += ((bgrbuf[i-2]) << 16) & 0xFF0000;
            bmp.setPixel(col--, row, color);
            if (col < 0) {
                col = width-1;
                row++;
            }
        }
        return bmp;
    }

    /**
     * 检查身份证是否已经过期
     * @return true - 已过期 false - 未过期
     */
    private boolean checkIsOutValidate(IDCardRecord idCardRecord) {
        try {
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
            Date validEndDate = myFmt.parse(idCardRecord.getValidateEnd());
            return validEndDate.getTime() < System.currentTimeMillis();
        } catch (ParseException e) {
            return false;
        }
    }

    private class ReadIdCardThread extends Thread {

        @Override
        public void run() {
            byte[] curCardId;
            int re;
            while (run) {
                curCardId = new byte[64];
                re = idCardDriver.mxReadCardId(curCardId);
                switch (re) {
                    case ValueUtil.GET_CARD_ID:
                        noCardFlag = false;
                        if (!Arrays.equals(lastCardId, curCardId)) {
                            EventBus.getDefault().post(new CardEvent(CardEvent.FIND_CARD));
                            try {
                                readCard(getCardIdStr(curCardId));
                            } catch (Exception e) {
                                continue;
                            }
                        }
                        lastCardId = curCardId;
                        break;
                    case ValueUtil.NO_CARD:
                        noCardFlag = true;
                        lastCardId = null;
                        EventBus.getDefault().post(new CardEvent(CardEvent.NO_CARD));
                        break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
