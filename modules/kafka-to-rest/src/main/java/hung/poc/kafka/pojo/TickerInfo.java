package hung.poc.kafka.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

@Data
public class TickerInfo {
    private String ticker;
    private String name;
    private Market market;
    private Locale locale;
    @JsonAlias({"primary_exchange"})
    private String primaryExchange;
    private String type;
    private Boolean active;
    @JsonAlias({"currency_name"})
    private String currency;
    //cik
    //composite_figi
    //share_class_figi
    @JsonAlias({"market_cap"})
    private Long marketCap;
    @JsonAlias({"phone_number"})
    private String phone;
    private Address address;
    private String description;

    //https://www.sec.gov/corpfin/division-of-corporation-finance-standard-industrial-classification-sic-code-list
    private SECIndustryCode sic;

    @JsonSetter("sic_code")
    public void setSicCode(String code) {
        if (sic == null) {
            sic = new SECIndustryCode();
        }
        sic.setCode(code);
    }

    @JsonSetter("sic_description")
    public void setSicDes(String desc) {
        if (sic == null) {
            sic = new SECIndustryCode();
        }
        sic.setDescription(desc);
    }

    //ticker_root
    //ticker_suffix
    @JsonAlias({"homepage_url"})
    private URL homepage;
    @JsonAlias({"total_employees"})
    private Long ttlEmployees;
    @JsonAlias({"list_date"})
    private LocalDate listDate;
    @JsonProperty("delisted_utc")
    private Optional<LocalDate> delistDate;
    private Branding branding;
    @JsonAlias({"share_class_shares_outstanding"})
    private Long shareClassSharesOS;
    @JsonAlias({"weighted_shares_outstanding"})
    private Long weightedSharesOS;
    @JsonAlias({"round_lot"})
    private Long lotSize;

    enum Market {
        stocks,
        crypto,
        fx,
        otc
    }

    enum Locale {
        us,
        global
    }

    @Data
    static public class Address {

        @JsonAlias({"address1"})
        private String firstLine;

        private String city;

        private String state;

        @JsonAlias({"postal_code"})
        private String postalCode;
    }

    @Data
    static public class SECIndustryCode {
        private String code;
        private String description;
    }

    @Data
    static public class Branding {
        @JsonAlias({"logo_url"})
        private URL logoUrl;
        @JsonAlias({"icon_url"})
        private URL iconUrl;
    }
}