import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {

    private Utils() {
    }

    public static Double valuesValidation(String valueString) {
        Double value = null;
        try {
            value = Double.parseDouble(valueString);
            if (value < 0) {
                value = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static double getAccountBase() {
        List<CategoryData> list = SQLManger.getTypeAccountTable(CategoryTypes.BASE_ACCOUNT);
        if (list.size() == 1) {
            return list.get(0).getValue();
        }
        return 0;
    }

    public static void updateAccountBase(double value) {
        try {
            List<CategoryData> list = SQLManger.getTypeAccountTable(CategoryTypes.BASE_ACCOUNT);
            SQLManger.updateAccountBase(list.get(0).getId(), list.get(0).getValue() + value);
        } catch (Exception e) {
            addData(createCategoryData(CategoryTypes.BASE_ACCOUNT, "base_account", value));
        }
    }

    public static double calculateTotal(CategoryTypes type, List<CategoryData> list) {
        double sum = 0;
        switch (type) {
            case COIN: {
                double dollarPrice;
                double valuePrice;
                for (CategoryData item : list) {
                    Asset asset = findAsset(item.getName());
                    ExchangeRate exchangeRate = CryptoApi.getExchangeRate(asset.getAssetId(), "USD");
                    dollarPrice = exchangeRate.get_rate();
                    valuePrice = dollarPrice * item.getValue();
                    sum += valuePrice;
                }
                break;
            }
            case STOCK: {
                double dollarPrice;
                double valuePrice;
                for (CategoryData item : list) {
                    dollarPrice = StocksAPI.getStockPrice(item.getName());
                    valuePrice = dollarPrice * item.getValue();
                    sum += valuePrice;
                }
                break;
            }
            default:
                for (CategoryData item : list) {
                    sum += item.getValue();
                }
                break;
        }
        return sum;
    }

    public static CategoryData createCategoryData(CategoryTypes type, String name, Double value) {
        CategoryData categoryData;
        if (value == null) {
            categoryData = null;
        } else {
            if (type == CategoryTypes.COIN && findAsset(name) == null) {
                categoryData = null;
            } else if (type == CategoryTypes.STOCK && !(isStockExist(name))) {
                categoryData = null;
            } else {
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date currentDate = new Date();
                String date = dateFormat.format(currentDate);
                categoryData = new CategoryData(type, name, value, date);
            }
        }
        return categoryData;
    }

    public static boolean nameValidation(CategoryTypes type, String name) {
        boolean valid = true;
        if (type == CategoryTypes.COIN && findAsset(name) == null) {
            valid = false;
        } else if (type == CategoryTypes.STOCK && !(isStockExist(name))) {
            valid = false;
        }
        return valid;
    }

    public static boolean deleteData(CategoryData categoryData) {
        boolean check = true;
        try {
            SQLManger.deleteDataAccountTable(categoryData.getId());
        } catch (Exception e) {
            check = false;
        }
        return check;
    }

    public static boolean deleteAllData() {
        boolean check = true;
        try {
            SQLManger.deleteAllAccountTable();
        } catch (Exception e) {
            check = false;
        }
        return check;
    }

    public static boolean updateData(int id, String name, Double value) {
        boolean check = true;
        try {
            SQLManger.updateDataAccountTable(id, name, value);
        } catch (Exception e) {
            check = false;
        }
        return check;
    }

    public static boolean addData(CategoryData categoryData) {
        boolean check = true;
        try {
            SQLManger.addDataAccountTable(categoryData);
        } catch (Exception e) {
            check = false;
        }
        return check;
    }

    public static List<CategoryData> getTable(CategoryTypes type) {
        List<CategoryData> data;
        if (type == CategoryTypes.INCOME || type == CategoryTypes.OUTCOME) {
            DateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
            Date currentDate = new Date();
            String date = dateFormat.format(currentDate);
            data = getSpecificMonthData(type, date);
        } else {
            data = SQLManger.getTypeAccountTable(type);
            double dollarPrice;
            double valuePrice;
            for (CategoryData item : data) {
                if (item.getType() == CategoryTypes.COIN) {
                    Asset asset = findAsset(item.getName());
                    ExchangeRate exchangeRate = CryptoApi.getExchangeRate(asset.getAssetId(), "USD");
                    dollarPrice = exchangeRate.get_rate();
                    valuePrice = dollarPrice * item.getValue();
                    item.setValuePrice(valuePrice);
                } else if (item.getType() == CategoryTypes.STOCK) {
                    dollarPrice = StocksAPI.getStockPrice(item.getName());
                    valuePrice = dollarPrice * item.getValue();
                    item.setValuePrice(valuePrice);
                }
            }
        }
        return data;
    }

    public static void createAssetTable() {
        Asset[] assets = CryptoApi.getAssets();
        SQLManger.createAssetTable(assets);
    }

    private static Asset findAsset(String assetName) {
        Asset asset;
        asset = SQLManger.findAsset(assetName);
        if (asset == null) {
            Asset[] assets = CryptoApi.getAssets();
            SQLManger.updateAssetTable(assets);
            asset = SQLManger.findAsset(assetName);
        }
        return asset;
    }

    public static void needUpdate() {
        Date today = new Date();
        if (hasMonthPassed(today)) {
            String lastUpdate = getLastUpdate();
            String lastUpdateMonthYear = lastUpdate.substring(0, 3) + lastUpdate.substring(6);
            if (lastUpdate != null) {
                List<CategoryData> incomeList = getSpecificMonthData(CategoryTypes.INCOME, lastUpdateMonthYear);
                List<CategoryData> outcomeList = getSpecificMonthData(CategoryTypes.OUTCOME, lastUpdateMonthYear);
                double sum = 0;
                String filename = "last_update.txt";
                for (CategoryData item : incomeList) {
                    sum += item.getValue();
                }
                for (CategoryData item : outcomeList) {
                    sum -= item.getValue();
                }
                updateAccountBase(sum);
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {
                    String todayStr = new SimpleDateFormat("MM/dd/yyyy").format(today);
                    bufferedWriter.write(todayStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Error in getting last update");
            }
        }
    }

    private static boolean hasMonthPassed(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String todayStr = formatter.format(date);
        String lastDayMonthStr = getLastDayOfMonth(todayStr);
        Date lastDayMonth = null;
        Date lastUpdateDate = null;
        String lastUpdate = getLastUpdate();
        try {
            lastDayMonth = formatter.parse(lastDayMonthStr);
            lastUpdateDate = formatter.parse(lastUpdate);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastUpdateDate.compareTo(lastDayMonth) < 0;
    }

    private static String getLastUpdate() {
        String filename = "last_update.txt";
        String line = null;
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date lastUpdateDate = null;
        String lastUpdate = null;
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            lastUpdateDate = formatter.parse(line);
            lastUpdate = formatter.format(lastUpdateDate);
        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastUpdate;
    }

    private static boolean isStockExist(String name) {
        return !(StocksAPI.getStockPrice(name) == 0.0);
    }

    private static String getLastDayOfMonth(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate convertedDate = LocalDate.parse(date, formatter);
        convertedDate = convertedDate.withDayOfMonth(
                convertedDate.getMonth().length(convertedDate.isLeapYear()));
        return convertedDate.format(formatter);
    }

    public static List<String> getMonths() {
        Set<String> set = new HashSet<String>();
        List<CategoryData> income = SQLManger.getTypeAccountTable(CategoryTypes.INCOME);
        List<CategoryData> outcome = SQLManger.getTypeAccountTable(CategoryTypes.OUTCOME);
        String date;
        for (CategoryData item : income) {
            date = item.getDate().substring(0, 2) + item.getDate().substring(5);
            set.add(date);
        }
        for (CategoryData item : outcome) {
            date = item.getDate().substring(0, 2) + item.getDate().substring(5);
            set.add(date);
        }
        List<String> list = new ArrayList(set);
        Collections.sort(list);
        return list;
    }

    public static List<CategoryData> getSpecificMonthData(CategoryTypes type, String month) {
        String firstDay = month.substring(0, 2) + "/01" + month.substring(2);
        String lastDay = getLastDayOfMonth(firstDay);
        List<CategoryData> data = SQLManger.getTypeDatesAccountTable(type, firstDay, lastDay);
        return data;
    }
}
