import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLManger {

    private static boolean accountTableExist = false;

    private SQLManger() {
    }

    private static int columnNumber(String tableName) {
        int columnNum = 0;
        String sqlStatement = "SELECT * FROM " + tableName;
        try {
            Connection conn = DriverManager.getConnection(FinanceContract.DB_URL);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlStatement);
            while (resultSet.next()) {
                columnNum += 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnNum;
    }

    public static void createAccountTable() {
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            Statement state = conn.createStatement();
            String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + FinanceContract.AccountTable.TABLE_NAME + " ("
                    + FinanceContract.AccountTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FinanceContract.AccountTable.COLUMN_TYPE + " TEXT NOT NULL, "
                    + FinanceContract.AccountTable.COLUMN_DATE + " TEXT NOT NULL, "
                    + FinanceContract.AccountTable.COLUMN_NAME + " TEXT NOT NULL, "
                    + FinanceContract.AccountTable.COLUMN_VALUE + " DOUBLE DEFAULT 0);";
            state.executeUpdate(sqlCreateTable);
            accountTableExist = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void deleteAllAccountTable() {
        String sqlStatement = "DELETE FROM " + FinanceContract.AccountTable.TABLE_NAME;
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            Statement state = conn.createStatement();
            state.executeUpdate(sqlStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<CategoryData> getTypeAccountTable(CategoryTypes categoryType) {
        List<CategoryData> dataList = new ArrayList<>();
        if (!accountTableExist) {
            createAccountTable();
        }
        String sqlStatement = "SELECT * FROM " + FinanceContract.AccountTable.TABLE_NAME +
                " WHERE " + FinanceContract.AccountTable.COLUMN_TYPE +
                " LIKE ?";
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            PreparedStatement prep = conn.prepareStatement(sqlStatement);
            prep.setString(1, categoryType.toString());
            ResultSet resultSet = prep.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(FinanceContract.AccountTable.COLUMN_NAME);
                Double value = resultSet.getDouble(FinanceContract.AccountTable.COLUMN_VALUE);
                String date = resultSet.getString(FinanceContract.AccountTable.COLUMN_DATE);
                int id = resultSet.getInt(FinanceContract.AccountTable._ID);
                CategoryData categoryData = new CategoryData(categoryType, name, value, date, -1, id);
                dataList.add(categoryData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static void addDataAccountTable(CategoryData categoryData) {
        if (!accountTableExist) {
            createAccountTable();
        }
        String sqlStatement = "INSERT INTO " + FinanceContract.AccountTable.TABLE_NAME +
                "(" + FinanceContract.AccountTable.COLUMN_TYPE + ","
                + FinanceContract.AccountTable.COLUMN_DATE + ","
                + FinanceContract.AccountTable.COLUMN_NAME + ","
                + FinanceContract.AccountTable.COLUMN_VALUE + ") VALUES (?,?,?,?);";
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            PreparedStatement prep = conn.prepareStatement(sqlStatement);
            prep.setString(1, categoryData.getType().toString());
            prep.setString(2, categoryData.getDate());
            prep.setString(3, categoryData.getName());
            prep.setDouble(4, categoryData.getValue());
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDataAccountTable(int id) {
        String sqlStatement = "DELETE FROM " + FinanceContract.AccountTable.TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            PreparedStatement prep = conn.prepareStatement(sqlStatement);
            prep.setInt(1, id);
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDataAccountTable(int id, String name, Double value) {
        if (!accountTableExist) {
            createAccountTable();
        }
        String sqlStatement = "UPDATE " + FinanceContract.AccountTable.TABLE_NAME + " SET "
                + FinanceContract.AccountTable.COLUMN_NAME + " = ? ,"
                + FinanceContract.AccountTable.COLUMN_VALUE + " = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            PreparedStatement prep = conn.prepareStatement(sqlStatement);
            prep.setString(1, name);
            prep.setDouble(2, value);
            prep.setInt(3, id);
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateAccountBase(int id, Double value) {
        if (!accountTableExist) {
            createAccountTable();
        }
        String sqlStatement = "UPDATE " + FinanceContract.AccountTable.TABLE_NAME + " SET "
                + FinanceContract.AccountTable.COLUMN_VALUE + " = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            PreparedStatement prep = conn.prepareStatement(sqlStatement);
            prep.setDouble(1, value);
            prep.setInt(2, id);
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createAssetTable(Asset[] assets) {
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            String SqlCreateTable = "CREATE TABLE IF NOT EXISTS " + FinanceContract.CoinsAssetsTable.TABLE_NAME + " ("
                    + FinanceContract.CoinsAssetsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FinanceContract.CoinsAssetsTable.COLUMN_COIN_NAME + " TEXT, "
                    + FinanceContract.CoinsAssetsTable.COLUMN_ASSET_ID + " TEXT NOT NULL, "
                    + FinanceContract.CoinsAssetsTable.COLUMN_TYPE_IS_CRPTO + " BIT DEFAULT 0);";
            Statement state = conn.createStatement();
            state.executeUpdate(SqlCreateTable);
            if (assets != null) {
                String sqlStatement = "INSERT INTO " + FinanceContract.CoinsAssetsTable.TABLE_NAME +
                        "(" + FinanceContract.CoinsAssetsTable.COLUMN_ASSET_ID + ","
                        + FinanceContract.CoinsAssetsTable.COLUMN_COIN_NAME + ","
                        + FinanceContract.CoinsAssetsTable.COLUMN_TYPE_IS_CRPTO + ") VALUES (?,?,?);";
                PreparedStatement prep = conn.prepareStatement(sqlStatement);
                for (int i = 0; i < assets.length; i++) {
                    prep.setString(1, assets[i].getAssetId());
                    prep.setString(2, assets[i].getName());
                    if (assets[i].isTypeCrypto()) {
                        prep.setString(3, "1");
                    } else {
                        prep.setString(3, "0");
                    }
                    prep.addBatch();
                }
                conn.setAutoCommit(false);
                prep.executeBatch();
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateAssetTable(Asset[] assets) {
        if (assets != null) {
            try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
                String sqlStatement = "DELETE FROM " + FinanceContract.CoinsAssetsTable.TABLE_NAME;
                Statement state = conn.createStatement();
                state.executeUpdate(sqlStatement);
                sqlStatement = "INSERT INTO " + FinanceContract.CoinsAssetsTable.TABLE_NAME +
                        "(" + FinanceContract.CoinsAssetsTable.COLUMN_ASSET_ID + ","
                        + FinanceContract.CoinsAssetsTable.COLUMN_COIN_NAME + ","
                        + FinanceContract.CoinsAssetsTable.COLUMN_TYPE_IS_CRPTO + ") VALUES (?,?,?);";
                PreparedStatement prep = conn.prepareStatement(sqlStatement);
                for (int i = 0; i < assets.length; i++) {
                    prep.setString(1, assets[i].getAssetId());
                    prep.setString(2, assets[i].getName());
                    if (assets[i].isTypeCrypto()) {
                        prep.setString(3, "1");
                    } else {
                        prep.setString(3, "0");
                    }
                    prep.addBatch();
                }
                conn.setAutoCommit(false);
                prep.executeBatch();
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Asset findAsset(String assetName) {
        Asset asset = null;
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            String sqlStatement = "SELECT * FROM " + FinanceContract.CoinsAssetsTable.TABLE_NAME +
                    " WHERE " + FinanceContract.CoinsAssetsTable.COLUMN_COIN_NAME +
                    " LIKE ?";
            PreparedStatement prep = conn.prepareStatement(sqlStatement);
            prep.setString(1, assetName);
            ResultSet resultSet = prep.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(FinanceContract.CoinsAssetsTable.COLUMN_COIN_NAME);
                String assetId = resultSet.getString(FinanceContract.CoinsAssetsTable.COLUMN_ASSET_ID);
                asset = new Asset(assetId, name, true);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return asset;
    }

    public static List<CategoryData> getTypeDatesAccountTable(CategoryTypes categoryType, String firstDate, String lastDate) {
        List<CategoryData> dataList = new ArrayList<>();
        if (!accountTableExist) {
            createAccountTable();
        }
        String sqlStatement = "SELECT * FROM " + FinanceContract.AccountTable.TABLE_NAME +
                " WHERE " + FinanceContract.AccountTable.COLUMN_TYPE +
                " LIKE ? AND " + FinanceContract.AccountTable.COLUMN_DATE + " BETWEEN ? AND ?";
        try (Connection conn = DriverManager.getConnection(FinanceContract.DB_URL)) {
            PreparedStatement prep = conn.prepareStatement(sqlStatement);
            prep.setString(1, categoryType.toString());
            prep.setString(2, firstDate);
            prep.setString(3, lastDate);
            ResultSet resultSet = prep.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(FinanceContract.AccountTable.COLUMN_NAME);
                Double value = resultSet.getDouble(FinanceContract.AccountTable.COLUMN_VALUE);
                String date = resultSet.getString(FinanceContract.AccountTable.COLUMN_DATE);
                int id = resultSet.getInt(FinanceContract.AccountTable._ID);
                CategoryData categoryData = new CategoryData(categoryType, name, value, date, -1, id);
                dataList.add(categoryData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
