package com.example.samplefrapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.Logger;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.callback.Callback;
import org.forgerock.android.auth.callback.HiddenValueCallback;
import org.forgerock.android.auth.callback.NameCallback;
import org.forgerock.android.auth.callback.TextOutputCallback;


public class MainActivity extends AppCompatActivity implements NodeListener<FRSession> {

    Button authnButton, logoutButton;
    TextView status;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authnButton = findViewById(R.id.authenticateButton);
        logoutButton = findViewById(R.id.logoutButton);
        status = findViewById(R.id.statusText);

        Logger.set(Logger.Level.DEBUG);
        FRAuth.start(this);

        updateStatus();

        authnButton.setOnClickListener(view -> {

            Logger.debug(TAG, "authN button is pressed");

            FRSession.authenticate(getApplicationContext(), "Regula-Demo", new NodeListener<FRSession>() {
                @Override
                public void onCallbackReceived(@NonNull Node node) {
                    Logger.warn(TAG, "callback received in flow");
                    MainActivity.this.onCallbackReceived(node);
                }

                @Override
                public void onSuccess(FRSession frSession) {
                    Logger.warn(TAG, "onSuccess in flow");
                    updateStatus();
                }

                @Override
                public void onException(@NonNull Exception e) {
                    Logger.error(TAG, "Exception: " + e.getMessage());
                }
            });
        });

        logoutButton.setOnClickListener(view -> {
            Logger.debug(TAG, "Logout button is pressed");
            try {
                FRUser.getCurrentUser().logout();
            } catch (Exception e) {
                Logger.error(TAG, e.getMessage(), e);
            }
            updateStatus();
        });
    }

    @Override
    public void onCallbackReceived(@NonNull Node node) {
        runOnUiThread(() -> {

            Callback callback = node.getCallbacks().get(0);

            if (callback instanceof NameCallback) {
                NameOnlyDialogFragment fragment = NameOnlyDialogFragment.newInstance(node);
                fragment.show(getSupportFragmentManager(), NameOnlyDialogFragment.class.getName());
            } else if (callback instanceof HiddenValueCallback) {
                // Regula SDK gets transaction ID...
                // Set transactionID value in callback
                ((HiddenValueCallback) callback).setValue("123456");
                node.next(MainActivity.this, MainActivity.this);

            } else if (callback instanceof TextOutputCallback) {
                status.setText(((TextOutputCallback) callback).getMessage());
            }

        });

    }

    @Override
    public void onSuccess(FRSession frSession) {
        Logger.debug(TAG, "onSuccess in MainActivity");
        updateStatus();
    }

    @Override
    public void onException(@NonNull Exception e) {
        Logger.error(TAG, "Exception: " + e.getMessage());
    }

    private void updateStatus() {
        runOnUiThread(() -> {
            if (FRUser.getCurrentUser() == null) {
                status.setText("Not authenticated");
                authnButton.setEnabled(true);
                logoutButton.setEnabled(false);
            } else {
                status.setText("Authenticated");
                authnButton.setEnabled(false);
                logoutButton.setEnabled(true);
            }
        });
    }

}
