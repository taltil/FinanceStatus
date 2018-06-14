
public class Asset {
    private String assetId;
    private String name;
    private boolean typeIsCrypto;

    public Asset(String assetId, String name, boolean typeIsCrypto) {
        this.assetId = assetId;
        this.name = name;
        this.typeIsCrypto = typeIsCrypto;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getName() {
        return name;
    }

    public boolean isTypeCrypto() {
        return typeIsCrypto;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "asset id='" + assetId + '\'' +
                ", name='" + name + '\'' +
                ", type is crypto=" + typeIsCrypto +
                '}';
    }
}
