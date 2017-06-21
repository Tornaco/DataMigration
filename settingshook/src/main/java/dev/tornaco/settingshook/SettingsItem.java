package dev.tornaco.settingshook;

/**
 * Created by Nick on 2017/6/21 16:31
 */
public class SettingsItem {

    private String key;
    private String name;

    public SettingsItem(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
