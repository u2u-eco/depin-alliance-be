package xyz.telegram.depinalliance.common.models.response;

/**
 * @author holden on 26-Sep-2024
 */
public class SettingResponse {
  public boolean enableNotification;
  public boolean enableMusicTheme;
  public boolean enableSoundEffect;

  public SettingResponse(boolean enableNotification, boolean enableMusicTheme, boolean enableSoundEffect) {
    this.enableNotification = enableNotification;
    this.enableMusicTheme = enableMusicTheme;
    this.enableSoundEffect = enableSoundEffect;
  }
}
