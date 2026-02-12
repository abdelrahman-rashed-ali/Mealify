package com.rashed.mealify.ui.authentication.VerifyFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.usecases.auth.CheckEmailVerifiedUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;
import com.rashed.mealify.domain.usecases.auth.SendEmailVerificationUseCase;

public class VerifyFragment extends Fragment implements VerifyContract.View {

    private VerifyContract.Presenter presenter;

    private Button btnCheck, btnResend;
    private TextView tvLogout;
    private ProgressBar progress;
    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initViews(view);
        initPresenter();
    }

    private void initViews(View view) {
        btnCheck = view.findViewById(R.id.btn_check_verified);
        btnResend = view.findViewById(R.id.btn_resend_email);
        tvLogout = view.findViewById(R.id.tv_logout);
        progress = view.findViewById(R.id.progress);

        btnCheck.setOnClickListener(v -> presenter.checkVerificationStatus());
        btnResend.setOnClickListener(v -> presenter.resendVerificationEmail());
        tvLogout.setOnClickListener(v -> presenter.logout());
    }

    private void initPresenter() {
        AuthRepository repo = new AuthRepositoryImpl(new FirebaseAuthDataSource());

        presenter = new VerifyPresenter(
                new SendEmailVerificationUseCase(repo),
                new CheckEmailVerifiedUseCase(repo),
                new LogoutUseCase(repo)
        );
        presenter.attach(this);
    }


    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        btnCheck.setEnabled(false);
        btnResend.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progress.setVisibility(View.GONE);
        btnCheck.setEnabled(true);
        btnResend.setEnabled(true);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void navigateToLogin() {
        Navigation.findNavController(rootView).navigate(R.id.action_verify_to_login);
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }
}