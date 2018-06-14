import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;

import java.util.List;

public class CategoryInfoModel {

    private List<CategoryData> list;
    private ObservableList<CategoryData> observableList;
    private TableView<CategoryData> table;
    private Double total;
    private Label label;

    public List<CategoryData> getList() {
        return list;
    }

    public void setList(List<CategoryData> list) {
        this.list = list;
    }

    public ObservableList<CategoryData> getObservableList() {
        return observableList;
    }

    public void setObservableList(ObservableList<CategoryData> observableList) {
        this.observableList = observableList;
    }

    public TableView<CategoryData> getTable() {
        return table;
    }

    public void setTable(TableView<CategoryData> table) {
        this.table = table;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "CategoryInfoModel{" +
                "list=" + list +
                ", observableList=" + observableList +
                ", table=" + table +
                ", total=" + total +
                ", label=" + label +
                '}';
    }
}
