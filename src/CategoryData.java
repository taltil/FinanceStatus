public class CategoryData implements Comparable<CategoryData> {
    private String date;
    private CategoryTypes type;
    private String name;
    private double value;
    private double valuePrice; //for coins and stocks this variable present value in money
    private int id;

    public CategoryData(CategoryTypes type, String name, double value, String date, double valuePrice, int id) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.valuePrice = valuePrice;
        this.date = date;
        this.id = id;
    }

    public CategoryData(CategoryTypes type, String name, double value, String date, double valuePrice) {
        this(type, name, value, date, valuePrice, -1);
    }

    public CategoryData(CategoryTypes type, String name, double value, String date) {
        this(type, name, value, date, -1, -1);
    }

    public CategoryData(CategoryTypes type, String name, double value) {
        this(type, name, value, null, -1, -1);
    }

    @Override
    public int compareTo(CategoryData comparedCategory) {
        return getName().compareTo(comparedCategory.getName());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public CategoryTypes getType() {
        return type;
    }

    public void setType(CategoryTypes type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValuePrice() {
        return valuePrice;
    }

    public void setValuePrice(double valuePrice) {
        this.valuePrice = valuePrice;
    }

    @Override
    public String toString() {
        return "CategoryData{" +
                "date='" + date + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", valuePrice=" + valuePrice +
                ", id=" + id +
                '}';
    }
}


