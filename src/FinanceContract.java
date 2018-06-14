
public class FinanceContract {
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DB_URL = "jdbc:sqlite:finance.db";

    private FinanceContract() {
    }

    public static abstract class AccountTable {

        public final static String TABLE_NAME = "account";
        public final static String _ID = "ID";
        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_VALUE = "value";
        public final static String COLUMN_CURRENT = "current";
    }

    public static abstract class CoinsAssetsTable {

        public final static String TABLE_NAME = "assets";
        public final static String _ID = "ID";
        public final static String COLUMN_COIN_NAME = "name";
        public final static String COLUMN_ASSET_ID = "assetId";
        public final static String COLUMN_TYPE_IS_CRPTO = "typeIsCrypto";
    }
}
