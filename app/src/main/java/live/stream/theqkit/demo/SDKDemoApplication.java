package live.stream.theqkit.demo;

import android.app.Application;
import live.stream.theq.theqkit.TheQConfig;
import live.stream.theq.theqkit.TheQKit;

public class SDKDemoApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    TheQConfig theQConfig = new TheQConfig.Builder(this)
        .baseUrl(BuildConfig.THEQKIT_BASE_URL)
        .partnerCode(BuildConfig.THEQKIT_PARTNER_CODE)
        .debuggable(BuildConfig.DEBUG)
        .build();

    TheQKit.getInstance()
        .init(theQConfig);
  }
}
