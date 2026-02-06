package com.rashed.mealify.ui.authentication.RegisterFragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rashed.mealify.R;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.usecases.auth.RegisterUseCase;

public class RegisterFragment extends Fragment {

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ProgressBar progress;
    private View tvGoLogin;
    private RegisterUseCase registerUseCase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFirstName = view.findViewById(R.id.et_first_name);
        etLastName = view.findViewById(R.id.et_last_name);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        progress = view.findViewById(R.id.progress);
        tvGoLogin = view.findViewById(R.id.tv_go_login);

        FirebaseAuthDataSource ds = new FirebaseAuthDataSource();
        AuthRepository repo = new AuthRepositoryImpl(ds);
        registerUseCase = new RegisterUseCase(repo);

        btnRegister.setOnClickListener(v -> doRegister());
        tvGoLogin.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_register_to_login));
    }

    private void doRegister() {
        String firstName = safeText(etFirstName);
        String lastName = safeText(etLastName);
        String email = safeText(etEmail);
        String password = safeText(etPassword);
        String confirm = safeText(etConfirmPassword);

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)) {
            Toast.makeText(getContext(), "Names required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Email/Pass required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Mismatch");
            return;
        }

        showLoading(true);
        registerUseCase.execute(email, password, firstName, lastName, result -> {
            showLoading(false);
            if (result instanceof Result.Success) {
                Navigation.findNavController(requireView()).navigate(R.id.action_register_to_verify);
            } else {
                String msg = ((Result.Error<?>) result).message;
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}