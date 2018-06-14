
public class ExchangeRate {
    private String time;
    private String asset_id_base;
    private String asset_id_quote;
    private double rate;

    public ExchangeRate(String time, String asset_id_base, String asset_id_quote, double rate) {
        this.time = time;
        this.asset_id_base = asset_id_base;
        this.asset_id_quote = asset_id_quote;
        this.rate = rate;
    }

    public String get_time() {
        return time;
    }

    public String get_asset_id_base() {
        return asset_id_base;
    }

    public String get_asset_id_quote() {
        return asset_id_quote;
    }

    public double get_rate() {
        return rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "time='" + time + '\'' +
                ", asset_id_base='" + asset_id_base + '\'' +
                ", asset_id_quote='" + asset_id_quote + '\'' +
                ", rate=" + rate +
                '}';
    }
}
