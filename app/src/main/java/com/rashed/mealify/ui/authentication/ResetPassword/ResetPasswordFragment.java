package com.rashed.mealify.ui.authentication.ResetPassword;

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
import com.google.android.material.textfield.TextInputEditText;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.usecases.auth.SendPasswordResetUseCase;

public class ResetPasswordFragment extends Fragment implements ResetPasswordContract.View {

    private ResetPasswordContract.Presenter presenter;

    private TextInputEditText etEmail;
    private Button btnSend;
    private ProgressBar progress;
    private TextView tvBack;
    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
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
        btnSend = view.findViewById(R.id.btn_send_reset);
        progress = view.findViewById(R.id.progress);
        tvBack = view.findViewById(R.id.tv_back_login);

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            presenter.resetPassword(email);
        });

        tvBack.setOnClickListener(v -> presenter.onBackClicked());
    }

    private void initPresenter() {
        SendPasswordResetUseCase useCase = new SendPasswordResetUseCase(
                new AuthRepositoryImpl(new FirebaseAuthDataSource())
        );
        presenter = new ResetPasswordPresenter(useCase);
        presenter.attach(this);
    }


    @Override
    public void showLoading() {
        progress.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progress.setVisibility(View.GONE);
        btnSend.setEnabled(true);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void navigateBack() {
        Navigation.findNavController(rootView).popBackStack();
    }

    @Override
    public void setEmailError(String error) {
        etEmail.setError(error);
        etEmail.requestFocus();
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }
}