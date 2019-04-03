package live.stream.theqkit.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import live.stream.theq.theqkit.TheQKit;
import live.stream.theq.theqkit.data.sdk.ApiError;
import live.stream.theq.theqkit.listener.LoginResponseListener;

public class LoginActivity extends AppCompatActivity {

  public static int APP_REQUEST_CODE = 99;

  private Button accountKitLoginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    accountKitLoginButton = findViewById(R.id.accountKitLogin);


    if (TheQKit.getInstance().isAuthenticated()) {
      startMainActivity();
      return;
    }

    accountKitLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        AccessToken accountKitAccessToken = AccountKit.getCurrentAccessToken();
        if (accountKitAccessToken != null) {
          loginToTheQ(accountKitAccessToken);
        } else {
          requestAccountKitCredentials();
        }
      }
    });

  }

  public void startMainActivity() {
    Intent mainActivityIntent = new Intent(this, MainActivity.class);
    mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(mainActivityIntent);
  }

  public void loginToTheQ(AccessToken accessToken) {
    TheQKit.getInstance()
        .loginWithAccountKit(this, accessToken.getAccountId(), accessToken.getToken(),
            new LoginResponseListener() {
              @Override public void onSuccess() {
                startMainActivity();
              }

              @Override public void onFailure(@NonNull ApiError apiError) {

              }
            });
  }

  public void requestAccountKitCredentials() {
    final Intent intent = new Intent(this, AccountKitActivity.class);
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
        new AccountKitConfiguration.AccountKitConfigurationBuilder(
            LoginType.PHONE,
            AccountKitActivity.ResponseType.TOKEN);
    intent.putExtra(
        AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
        configurationBuilder.build());
    startActivityForResult(intent, APP_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == APP_REQUEST_CODE) {
      AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
      if (loginResult.getError() != null) {
        String toastMessage = loginResult.getError().getErrorType().getMessage();
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
      } else if (loginResult.wasCancelled()) {
        Toast.makeText(this, "Login Cancelled", Toast.LENGTH_LONG).show();
      } else {
        AccessToken accessToken = loginResult.getAccessToken();
        if (accessToken != null) {
          loginToTheQ(accessToken);
        } else {
          Toast.makeText(this, "No AccessToken received", Toast.LENGTH_LONG).show();
        }
      }
    }
  }
}
