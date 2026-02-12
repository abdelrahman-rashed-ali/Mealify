package com.rashed.mealify.ui.authentication.LoginFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.rashed.mealify.ui.home.MainActivity;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.usecases.auth.LoginAnonymouslyUseCase;
import com.rashed.mealify.domain.usecases.auth.LoginUseCase;
import com.rashed.mealify.domain.usecases.auth.LoginWithGoogleUseCase;

public class LoginFragment extends Fragment implements LoginContract.View {

    private LoginContract.Presenter presenter;

    // UI Components
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogle;
    private ProgressBar progress;
    private TextView tvGoRegister, tvForgotPassword;
    private Button btnGuest;
    private View rootView;

    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    presenter.handleGoogleResult(result.getData());
                } else {
                    hideLoading();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initViews(view);
        initPresenter();
    }

    private void initViews(View view) {
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGoogle = view.findViewById(R.id.btn_google);
        progress = view.findViewById(R.id.progress);
        tvGoRegister = view.findViewById(R.id.tv_go_register);
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        btnGuest = view.findViewById(R.id.btn_guest);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
            presenter.login(email, password);
        });

        btnGoogle.setOnClickListener(v -> presenter.onGoogleSignInClicked());
        btnGuest.setOnClickListener(v -> presenter.loginGuest());
        tvGoRegister.setOnClickListener(v -> presenter.onRegisterClicked());
        tvForgotPassword.setOnClickListener(v -> presenter.onForgotPasswordClicked());
    }

    private void initPresenter() {
        AuthRepository repo = new AuthRepositoryImpl(new FirebaseAuthDataSource());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleClient = GoogleSignIn.getClient(requireContext(), gso);

        presenter = new LoginPresenter(
                new LoginUseCase(repo),
                new LoginWithGoogleUseCase(repo),
                new LoginAnonymouslyUseCase(repo),
                googleClient
        );
        presenter.attach(this);
    }


    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnGoogle.setEnabled(false);
        btnGuest.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progress.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnGoogle.setEnabled(true);
        btnGuest.setEnabled(true);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void navigateToRegister() {
        Navigation.findNavController(rootView).navigate(R.id.action_login_to_register);
    }

    @Override
    public void navigateToForgotPassword() {
        Navigation.findNavController(rootView).navigate(R.id.action_login_to_reset);
    }

    @Override
    public void navigateToVerifyEmail() {
        Navigation.findNavController(rootView).navigate(R.id.action_login_to_verify);
    }

    @Override
    public void launchGoogleSignIn(Intent signInIntent) {
        googleLauncher.launch(signInIntent);
    }

    @Override
    public void setEmailError(String error) {
        etEmail.setError(error);
        etEmail.requestFocus();
    }

    @Override
    public void setPasswordError(String error) {
        etPassword.setError(error);
        etPassword.requestFocus();
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }
}