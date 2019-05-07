package com.miaxis.faceattendance.manager;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

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

    public static boolean noCardFlag = true;
    private Application application;
    private IdCardDriver idCardDriver;
    private byte[] lastCardId = null;
    private volatile boolean running = true;
    private volatile boolean needReadCard = true;

    public void init(Application application) {
        this.application = application;
        idCardDriver = new IdCardDriver(application);
    }

    public void startReadCard() {
        running = true;
        needReadCard = true;
        new Thread(new ReadIdCardThread()).start();
    }

    public void closeReadCard() {
        running = false;
    }

    public synchronized void setNeedReadCard(boolean needReadCard) {
        this.needReadCard = needReadCard;
    }

    /* 解析身份证id 字符串 */
    private String getCardIdStr(byte[] cardId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardId.length; i++) {
            sb.append(String.format("%02x", cardId[i]));
        }
        String data = sb.toString();
        String cardIdStr = data.substring(0, 16);
        String errorCode = data.substring(16, 20);
        if (TextUtils.equals(errorCode, "9000")) {
            return cardIdStr;
        } else {
            return "";
        }
    }

    private void readCard(String curCardId) throws Exception {
        IDCardRecord idCardRecord = null;
        byte[] bCardInfo = new byte[256 + 1024];
        int re = idCardDriver.mxReadCardInfo(bCardInfo);
        String type = isGreenCard(bCardInfo);
        if (re == 0) {
            if ("".equals(type)) {
                idCardRecord = analysisIdCardInfo(bCardInfo, curCardId);
            } else if ("I".equals(type)) {
                idCardRecord = analysisGreenCard(bCardInfo, curCardId);
            } else if ("J".equals(type)) {
                idCardRecord = analysiGATCardInfo(bCardInfo, curCardId);
            }
        } else {
            throw new Exception("读卡失败");
        }
        if (idCardRecord != null) {
            if (checkIsOutValidate(idCardRecord)) {
                EventBus.getDefault().post(new CardEvent(CardEvent.OVERDUE));
            } else {
                EventBus.getDefault().post(new CardEvent(idCardRecord));
            }
        }
    }

    private void readCardFull(String curCardId) throws Exception {
        IDCardRecord idCardRecord = null;
        byte[] bCardFullInfo = new byte[256 + 1024 + 1024];
        int re = idCardDriver.mxReadCardFullInfo(bCardFullInfo);
        String type = isGreenCard(bCardFullInfo);
        if (re == 1) {
            if ("".equals(type)) {
                idCardRecord = analysisIdCardInfo(bCardFullInfo, curCardId);
            } else if ("I".equals(type)) {
                idCardRecord = analysisGreenCard(bCardFullInfo, curCardId);
            } else if ("J".equals(type)) {
                idCardRecord = analysiGATCardInfo(bCardFullInfo, curCardId);
            }
        } else if (re == 0) {
            if ("".equals(type)) {
                idCardRecord = analysisIdCardInfo(bCardFullInfo, curCardId);
            } else if ("I".equals(type)) {
                idCardRecord = analysisGreenCard(bCardFullInfo, curCardId);
            } else if ("J".equals(type)) {
                idCardRecord = analysiGATCardInfo(bCardFullInfo, curCardId);
            }
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
        if (idCardRecord != null) {
            if (checkIsOutValidate(idCardRecord)) {
                EventBus.getDefault().post(new CardEvent(CardEvent.OVERDUE));
            } else {
                EventBus.getDefault().post(new CardEvent(idCardRecord));
            }
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

    /* 解析港澳台通行证信息 */
    public IDCardRecord analysiGATCardInfo(byte[] bCardInfo, String cardId) {
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 预留区
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] id_RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
//        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_PassNum = new byte[18]; //通行证号码
        byte[] id_IssueNum = new byte[4]; //签发次数
        byte[] id_NewAddr = new byte[14]; //
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
        idCardRecord.setCardType("J");

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
//        int iRev = Integer.parseInt(MyUtil.unicode2String(id_Rev));
        idCardRecord.setNation("");

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        idCardRecord.setBirthday(ValueUtil.unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        idCardRecord.setAddress(ValueUtil.unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        idCardRecord.setCardNumber(ValueUtil.unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, id_RegOrg, 0, id_RegOrg.length);
        iLen = iLen + id_RegOrg.length;
        idCardRecord.setIssuingAuthority(ValueUtil.unicode2String(id_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = ValueUtil.unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = ValueUtil.unicode2String(id_ValidPeriodEnd).trim();
        idCardRecord.setValidateStart(validateStart);
        idCardRecord.setValidateEnd(validateEnd);

        System.arraycopy(bCardInfo, iLen, id_PassNum, 0, id_PassNum.length);
        iLen = iLen + id_PassNum.length;
        idCardRecord.setPassNumber(ValueUtil.unicode2String(id_PassNum).trim());

        System.arraycopy(bCardInfo, iLen, id_IssueNum, 0, id_IssueNum.length);
        iLen = iLen + id_IssueNum.length;
        idCardRecord.setIssueCount(ValueUtil.unicode2String(id_IssueNum).trim());

        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;

        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap(id_pImage);
        if (bitmap != null) {
            idCardRecord.setCardBitmap(bitmap);
        }
        return idCardRecord;
    }

    /* 解析外国人永久居留证信息 */
    public IDCardRecord analysisGreenCard(byte[] bCardInfo, String cardId) {
        IDCardRecord idCardRecord = new IDCardRecord();
        idCardRecord.setCardId(cardId);
        byte[] id_Name = new byte[120];    // 姓名
        byte[] id_Sex = new byte[2];      // 性别 1为男 其他为女
        byte[] id_cardNo = new byte[30];     // 永久居留证号码
        byte[] id_nation = new byte[6];      // 国籍或所在地区代码
        byte[] id_chinese_name = new byte[30];     // 中文姓名
        byte[] id_start_date = new byte[16];     // 证件签发日期
        byte[] id_end_date = new byte[16];     // 证件终止日期
        byte[] id_birthday = new byte[16];     // 出生日期
        byte[] id_version = new byte[4];      // 证件版本号
        byte[] id_reg_org = new byte[8];      // 当前申请受理机关代码
        byte[] id_type = new byte[2];      // 证件类型标识
        byte[] id_remark = new byte[6];      // 预留项
        byte[] id_pImage = new byte[1024];   // 照片
        int iLen = 0;
        idCardRecord.setCardType("I");

        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        idCardRecord.setName(ValueUtil.unicode2String(id_Name));

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;
        if (id_Sex[0] == '1') {
            idCardRecord.setSex("男");
        } else {
            idCardRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_cardNo, 0, id_cardNo.length);
        iLen += id_cardNo.length;
        idCardRecord.setCardNumber(ValueUtil.unicode2String(id_cardNo));

        System.arraycopy(bCardInfo, iLen, id_nation, 0, id_nation.length);
        iLen += id_nation.length;
        idCardRecord.setNation(ValueUtil.unicode2String(id_nation));

        System.arraycopy(bCardInfo, iLen, id_chinese_name, 0, id_chinese_name.length);
        iLen = iLen + id_chinese_name.length;
        idCardRecord.setChineseName(ValueUtil.unicode2String(id_chinese_name));

        System.arraycopy(bCardInfo, iLen, id_start_date, 0, id_start_date.length);
        iLen = iLen + id_start_date.length;
        System.arraycopy(bCardInfo, iLen, id_end_date, 0, id_end_date.length);
        iLen = iLen + id_end_date.length;
        String validateStart = ValueUtil.unicode2String(id_start_date).trim();
        String validateEnd = ValueUtil.unicode2String(id_end_date).trim();
        idCardRecord.setValidateStart(validateStart);
        idCardRecord.setValidateEnd(validateEnd);

        System.arraycopy(bCardInfo, iLen, id_birthday, 0, id_birthday.length);
        iLen = iLen + id_birthday.length;
        idCardRecord.setBirthday(ValueUtil.unicode2String(id_birthday));

        System.arraycopy(bCardInfo, iLen, id_version, 0, id_version.length);
        iLen = iLen + id_version.length;
        idCardRecord.setVersion(ValueUtil.unicode2String(id_version));
//        curId.setVersion(CommonUtil.unicode2String(id_version));

        System.arraycopy(bCardInfo, iLen, id_reg_org, 0, id_reg_org.length);
        iLen += id_reg_org.length;
        idCardRecord.setIssuingAuthority(ValueUtil.unicode2String(id_reg_org));

        System.arraycopy(bCardInfo, iLen, id_type, 0, id_type.length);
        iLen += id_type.length;
        idCardRecord.setVersion(ValueUtil.unicode2String(id_type));

        System.arraycopy(bCardInfo, iLen, id_remark, 0, id_remark.length);
        iLen += id_remark.length;

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

    private String isGreenCard(byte[] bCardInfo) {
        byte[] id_isGreen = new byte[2];
        id_isGreen[0] = bCardInfo[248];
        id_isGreen[1] = bCardInfo[249];
        return ValueUtil.unicode2String(id_isGreen).trim();
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
            while (running) {
                if (needReadCard) {
                    try {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("asd", "" + e.getMessage());
                    }
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
