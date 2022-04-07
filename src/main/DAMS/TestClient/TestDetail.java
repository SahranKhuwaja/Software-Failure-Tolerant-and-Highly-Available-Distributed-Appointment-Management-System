package DAMS.TestClient;

public class TestDetail {
    int count = 0;
    String description ="";
    int passedCount = 0;
    int failedCount = 0;

    public TestDetail() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public int getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(int passedCount) {
        this.passedCount = passedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestDetail init(String description) {
        setCount(count + 1);
        setDescription(description);
        return this;
    }
}
