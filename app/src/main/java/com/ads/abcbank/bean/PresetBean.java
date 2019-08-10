package com.ads.abcbank.bean;

import java.util.ArrayList;
import java.util.List;

public class PresetBean {
    public String appId;
    public String trCode;
    public String trVersion;
    public String resCode;
    public String resMessage;
    public String terminalId;
    public long timestamp;
    public String uniqueId;
    public String flowNum;
    public Data data;

    public static class Data {
        public SaveRate saveRate;
        public LoanRate loanRate;
        public BIAOFE buyInAndOutForeignExchange;
    }

    public static class RateBase {
        public String title;
        public boolean enable;
        public String lastModified;
        public String rem;
        public String startDate;
    }

    public static class SaveRate extends RateBase {
        public List<SaveRateItem> entry = new ArrayList<>();

        public static class SaveRateItem {
            public String no;
            public String placeholder;
            public String item;
            public String saveRate;
            public String saveCode;
            public String saveCodeDep;
        }
    }

    public static class LoanRate extends RateBase {
        public List<LoanRateItem> entry = new ArrayList<>();

        public static class LoanRateItem {
            public String no;
            public String placeholder;
            public String item;
            public String loanRate;
            public String loanCode;
            public String loanCodeDep;
        }
    }

    public static class BIAOFE extends RateBase {
        public List<BIAOFEItem> entry = new ArrayList<>();

        public static class BIAOFEItem {
            public String no;
            public String placeholder;
            public String currCName;
            public String buyPrice;
            public String sellPrice;
            public String cashPrice;
        }
    }

}
