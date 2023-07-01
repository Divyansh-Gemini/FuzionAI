package in.gems.fuzionai.model;

public class Setting {
    private Integer icon;
    private String settingTitle;
    private String settingSubtitle;

    // Default constructor
    public Setting()
    {   }

    public Setting(Integer icon, String settingTitle, String settingSubtitle) {
        this.icon = icon;
        this.settingTitle = settingTitle;
        this.settingSubtitle = settingSubtitle;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getSettingTitle() {
        return settingTitle;
    }

    public void setSettingTitle(String settingTitle) {
        this.settingTitle = settingTitle;
    }

    public String getSettingSubtitle() {
        return settingSubtitle;
    }

    public void setSettingSubtitle(String settingSubtitle) {
        this.settingSubtitle = settingSubtitle;
    }
}