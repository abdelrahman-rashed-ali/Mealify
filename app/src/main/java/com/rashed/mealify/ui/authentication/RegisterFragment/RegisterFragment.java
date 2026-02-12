package com.rashed.mealify.ui.authentication.RegisterFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.usecases.auth.RegisterUseCase;

public class RegisterFragment extends Fragment implements RegisterContract.View {

    private RegisterContract.Presenter presenter;

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progress;
    private TextView tvGoLogin;
    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initViews(view);
        initPresenter();
    }

    private void initViews(View view) {
        etFirstName = view.findViewById(R.id.et_first_name);
        etLastName = view.findViewById(R.id.et_last_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        progress = view.findViewById(R.id.progress);
        tvGoLogin = view.findViewById(R.id.tv_go_login);

        btnRegister.setOnClickListener(v -> {
            String fName = safeText(etFirstName);
            String lName = safeText(etLastName);
            String email = safeText(etEmail);
            String pass = safeText(etPassword);
            String confirm = safeText(etConfirmPassword);

            presenter.register(fName, lName, email, pass, confirm);
        });

        tvGoLogin.setOnClickListener(v -> presenter.onLoginClicked());
    }

    private void initPresenter() {
        AuthRepository repo = new AuthRepositoryImpl(new FirebaseAuthDataSource());
        RegisterUseCase useCase = new RegisterUseCase(repo);
        presenter = new RegisterPresenter(useCase);
        presenter.attach(this);
    }


    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progress.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void navigateToLogin() {
        Navigation.findNavController(rootView).navigate(R.id.action_register_to_login);
    }

    @Override
    public void navigateToVerifyEmail() {
        Navigation.findNavController(rootView).navigate(R.id.action_register_to_verify);
    }

    @Override
    public void setFirstNameError(String error) {
        etFirstName.setError(error);
        etFirstName.requestFocus();
    }

    @Override
    public void setLastNameError(String error) {
        etLastName.setError(error);
        etLastName.requestFocus();
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
    public void setConfirmPasswordError(String error) {
        etConfirmPassword.setError(error);
        etConfirmPassword.requestFocus();
    }


    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }
}