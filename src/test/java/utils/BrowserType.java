package utils;
/**
 * 浏览器类型枚举
 */
public enum BrowserType {
    CHROME,
    EDGE_IE_MODE;

    /**
     * 将字符串转换为BrowserType枚举
     */
    public static BrowserType fromString(String browserName) {
        if (browserName == null) {
            return CHROME; // 默认使用Chrome
        }

        switch (browserName.toLowerCase()) {
            case "edge-ie-mode":
            case "edge_ie_mode":
                return EDGE_IE_MODE;
            case "chrome":
            default:
                return CHROME;
        }
    }
}
