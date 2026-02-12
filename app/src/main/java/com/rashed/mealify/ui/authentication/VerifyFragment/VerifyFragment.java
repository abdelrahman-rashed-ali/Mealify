package com.rashed.mealify.ui.authentication.VerifyFragment;

import android.os.Bundle;
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

import com.rashed.mealify.R;
import com.rashed.mealify.common.Result;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.usecases.auth.CheckEmailVerifiedUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;
import com.rashed.mealify.domain.usecases.auth.SendEmailVerificationUseCase;

public class VerifyFragment extends Fragment {

    private Button btnCheck, btnResend;
    private TextView tvLogout;
    private ProgressBar progress;

    private SendEmailVerificationUseCase sendEmailUseCase;
    private CheckEmailVerifiedUseCase checkEmailUseCase;
    private LogoutUseCase logoutUseCase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCheck = view.findViewById(R.id.btn_check_verified);
        btnResend = view.findViewById(R.id.btn_resend_email);
        tvLogout = view.findViewById(R.id.tv_logout);
        progress = view.findViewById(R.id.progress);

        FirebaseAuthDataSource ds = new FirebaseAuthDataSource();
        AuthRepository repo = new AuthRepositoryImpl(ds);
        sendEmailUseCase = new SendEmailVerificationUseCase(repo);
        checkEmailUseCase = new CheckEmailVerifiedUseCase(repo);
        logoutUseCase = new LogoutUseCase(repo);

        btnResend.setOnClickListener(v -> resendEmail());
        btnCheck.setOnClickListener(v -> checkStatus());
        tvLogout.setOnClickListener(v -> {
            logoutUseCase.execute();
            Navigation.findNavController(v).navigate(R.id.action_verify_to_login);
        });
    }

    private void resendEmail() {
        showLoading(true);
        sendEmailUseCase.execute(result -> {
            showLoading(false);
            if (result instanceof Result.Success) {
                Toast.makeText(getContext(), "Verification email sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to send: " + ((Result.Error<?>) result).message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkStatus() {
        showLoading(true);
        checkEmailUseCase.execute(result -> {
            showLoading(false);
            if (result instanceof Result.Success) {
                boolean verified = ((Result.Success<Boolean>) result).data;
                if (verified) {
                    Toast.makeText(getContext(), "Verified!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_verify_to_login);
                } else {
                    Toast.makeText(getContext(), "Not yet verified. Please check your email.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error checking status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCheck.setEnabled(!show);
        btnResend.setEnabled(!show);
    }
}