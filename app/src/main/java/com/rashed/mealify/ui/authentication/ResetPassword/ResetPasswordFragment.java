package com.rashed.mealify.ui.authentication.ResetPassword;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.rashed.mealify.R;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.usecases.auth.SendPasswordResetUseCase;

public class ResetPasswordFragment extends Fragment {

    private TextInputEditText etEmail;
    private Button btnSend;
    private ProgressBar progress;
    private TextView tvBack;
    private SendPasswordResetUseCase useCase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.et_email);
        btnSend = view.findViewById(R.id.btn_send_reset);
        progress = view.findViewById(R.id.progress);
        tvBack = view.findViewById(R.id.tv_back_login);

        useCase = new SendPasswordResetUseCase(new AuthRepositoryImpl(new FirebaseAuthDataSource()));

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Required");
                return;
            }

            showLoading(true);
            useCase.execute(email, result -> {
                showLoading(false);
                if (result instanceof Result.Success) {
                    Toast.makeText(getContext(), "Reset link sent to email.", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(view).popBackStack();
                } else {
                    Toast.makeText(getContext(), "Failed: " + ((Result.Error<?>) result).message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSend.setEnabled(!show);
    }
}