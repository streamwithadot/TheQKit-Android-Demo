# TheQKit Android

## Requirements

* Android SDK >= 21
* AndroidX

## Installation

TheQKit is available on our private Jitpack repo.

```gradle
repositories {
  maven {
    url 'https://jitpack.io'
    credentials { username <JITPACK_AUTH_TOKEN> }
  }
}

dependencies {
  implementation 'live.stream:theqkit-android:0.1.2'
}
```

## Usage

### Initialize Demo App
To quickly use the demo, copy the below lines in gradle.properties and replace the <>'s with real values. Keep in mind, you will have to add your app package and key signature hash to the Account Kit console.

```
jitpackAuthToken=<JITPACK_AUTH_TOKEN>
theqkit_facebookAppId=<FACEBOOK_APP_ID>
theqkit_accountKitClientToken=<ACCOUNT_KIT_CLIENT_TOKEN>
theqkit_baseUrl=<THEQKIT_BASE_URL>
theqkit_partnerCode=<THEQKIT_PARTNER_CODE>
```

### Initialize SDK

All SDK actions are contained in an application wide Singleton (TheQKit). TheQKit must first be initialized before calling any methods. The SDK was written in Kotlin, but is fully comptiable with both Kotlin and Java.

As shown below, create your TheQConfig configuration object, then pass this to your TheQKit instance's init method. This code should be placed in your custom application class, providing the application context to the builder.
```kotlin
TheQConfig theQConfig = new TheQConfig.Builder(context)
    .baseUrl(<BASE_URL>)
    .partnerCode(<PARTNER_CODE>)
    .debuggable(<true/false>)
    .build();

TheQKit.getInstance()
    .init(theQConfig);
```

### User Management

We currently support AccountKit and Firebase SMS for login and signup. You must implement one of the providers in your application, then pass along the values to the methods below.
 
Login/Signup are both called via the same methods. The context required is an AppCompatActivity, as we launch the login dialog via an AppCompatDialogFragment. 

There are 2 signup flows that can be used for TheQ. The first code block below shows the flow that will launch a dialog that will allow the user to type a username during signup.

```kotlin
// com.facebook.accountkit.AccessToken provides the accountId and access token.
TheQKit.getInstance()
    .loginWithAccountKit(context, accountId, accessToken,
        new LoginResponseListener() {
          @Override public void onSuccess() {
              // handle success
          }

          @Override public void onFailure(@NonNull ApiError apiError) {
              // handle failure  
          }
        }
    );

TheQKit.getInstance()
    .loginWithFirebase(context, accountId, accessToken,
        new LoginResponseListener() {
          @Override public void onSuccess() {
              // handle success  
          }

          @Override public void onFailure(@NonNull ApiError apiError) {
              // handle failure
          }
        }
    );
```

The second option is to pass a suggested username as a signup parameter. Instead of prompting the user to type their username, we will create a user account with the suggested username provided. If that username is already taken, we will append a unique auto-incremented suffix on the end of the suggested username. 

```kotlin
// com.facebook.accountkit.AccessToken provides the accountId and access token.
TheQKit.getInstance()
    .loginWithAccountKit(context, accountId, accessToken, <suggestedUsername>,
        new LoginResponseListener() {
          @Override public void onSuccess() {
              // handle success
          }

          @Override public void onFailure(@NonNull ApiError apiError) {
              // handle failure  
          }
        }
    );

TheQKit.getInstance()
    .loginWithFirebase(context, accountId, accessToken, <suggestedUsername>,
        new LoginResponseListener() {
          @Override public void onSuccess() {
              // handle success  
          }

          @Override public void onFailure(@NonNull ApiError apiError) {
              // handle failure
          }
        }
    );
```

Check if a user is currently authenticated.

```kotlin
TheQKit.getInstance().isAuthenticated();
```

Launch the Cashout dialog. This would typically be called on a button click. The context required is an AppCompatActivity, as we launch the cashout dialog via an AppCompatDialogFragment.

```kotlin
TheQKit.getInstance().launchCashoutDialog(context);
```

Logout can be called to log the user out of TheQ server-side, as well as delete all local shared preferences stored in the sdk.

```kotlin
TheQKit.getInstance().logout();
```

### Game Management

Fetch a list of games. The list will return live games and games scheduled in the future. See GameResponse in the documentation to see the data that is returned with each game.

```kotlin
TheQKit.getInstance().fetchGames(new GameResponseListener() {
    @Override public void onSuccess(@NonNull List<GameResponse> games) {
        // handle success
    }

    @Override public void onFailure(@NonNull ApiError apiError) {
        // handle failure
    }
});
```

To launch a game, provide a single GameResponse object provided from the list. You should only attempt to launch the game if it returned in the list as live.

```kotlin
TheQKit.getInstance().launchGameActivity(context, gameResponse);
```

### Other

TheQKit defaults to using USD for currency. This can be overriden by proving a custom currency format in your strings.xml.

```xml
<string name="theqkit_full_currency_format">$###,###,##0.00</string>
<string name="theqkit_rounded_currency_format">$###,###,##0</string>
```

We also expose the CurrencyHelper class methods of getExactCurrency() and getRoundedCurrency() to use in your own code. These methods will use the string resources shown for formatting.