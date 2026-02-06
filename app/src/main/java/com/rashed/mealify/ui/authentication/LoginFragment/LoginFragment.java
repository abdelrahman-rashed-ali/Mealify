package com.rashed.mealify.ui.authentication.LoginFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rashed.mealify.R;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.model.AuthUser;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.usecases.auth.LoginUseCase;
import com.rashed.mealify.domain.usecases.auth.LoginWithGoogleUseCase;

public class LoginFragment extends Fragment {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogle;
    private ProgressBar progress;
    private TextView tvGoRegister, tvForgotPassword;

    private LoginUseCase loginUseCase;
    private LoginWithGoogleUseCase loginWithGoogleUseCase;
    private GoogleSignInClient googleClient;

    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                    showLoading(false);
                    return;
                }
                try {
                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                            .getResult(ApiException.class);

                    if (account == null || account.getIdToken() == null) {
                        showLoading(false);
                        toast("Google Sign-In error.");
                        return;
                    }

                    loginWithGoogleUseCase.execute(account.getIdToken(), r -> {
                        showLoading(false);
                        handleAuthResult(r);
                    });

                } catch (ApiException e) {
                    showLoading(false);
                    toast("Google failed: " + e.getStatusCode());
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGoogle = view.findViewById(R.id.btn_google);
        progress = view.findViewById(R.id.progress);
        tvGoRegister = view.findViewById(R.id.tv_go_register);
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);

        FirebaseAuthDataSource ds = new FirebaseAuthDataSource();
        AuthRepository repo = new AuthRepositoryImpl(ds);
        loginUseCase = new LoginUseCase(repo);
        loginWithGoogleUseCase = new LoginWithGoogleUseCase(repo);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(requireContext(), gso);

        btnLogin.setOnClickListener(v -> doLogin());
        btnGoogle.setOnClickListener(v -> doGoogleLogin());

        tvGoRegister.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_register)
        );

        tvForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_reset)
        );
    }

    private void doLogin() {
        String email = safeText(etEmail);
        String password = safeText(etPassword);

        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Required"); return; }

        showLoading(true);
        loginUseCase.execute(email, password, r -> {
            showLoading(false);
            handleAuthResult(r);
        });
    }

    private void doGoogleLogin() {
        showLoading(true);
        googleClient.signOut();
        googleLauncher.launch(googleClient.getSignInIntent());
    }

    private void handleAuthResult(Result<AuthUser> r) {
        if (r instanceof Result.Success) {
            AuthUser user = ((Result.Success<AuthUser>) r).data;
            toast("Welcome " + user.firstName);
        } else if (r instanceof Result.Error) {
            Result.Error<AuthUser> err = (Result.Error<AuthUser>) r;
            if (err.throwable != null && "UNVERIFIED".equals(err.throwable.getMessage())) {
                Navigation.findNavController(requireView()).navigate(R.id.action_login_to_verify);
            } else {
                toast(err.message != null ? err.message : "Authentication failed");
            }
        }
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnGoogle.setEnabled(!show);
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void toast(String m) {
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
    }
}