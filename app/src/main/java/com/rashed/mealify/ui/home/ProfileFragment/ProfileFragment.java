package com.rashed.mealify.ui.home.ProfileFragment;

import android.content.Intent;
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

import com.google.android.material.snackbar.Snackbar;
import com.rashed.mealify.R;
import com.rashed.mealify.datasource.auth.FirebaseAuthDataSource;
import com.rashed.mealify.datasource.repository.AuthRepositoryImpl;
import com.rashed.mealify.domain.repository.AuthRepository;
import com.rashed.mealify.domain.usecases.auth.GetCurrentUserUseCase;
import com.rashed.mealify.domain.usecases.auth.LogoutUseCase;
import com.rashed.mealify.ui.authentication.AuthActivity;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private ProfileContract.Presenter presenter;

    private TextView tvUserName;
    private Button btnLogout, btnSync;
    private ProgressBar progressBar;
    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initViews(view);
        initPresenter();
        presenter.loadUserProfile();
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnSync = view.findViewById(R.id.btn_sync);
        progressBar = view.findViewById(R.id.progress_bar);

        btnLogout.setOnClickListener(v -> presenter.logout());
        btnSync.setOnClickListener(v -> presenter.onSyncClicked());
    }

    private void initPresenter() {
        AuthRepository repo = new AuthRepositoryImpl(new FirebaseAuthDataSource());
        presenter = new ProfilePresenter(
                new LogoutUseCase(repo),
                new GetCurrentUserUseCase(repo)
        );
        presenter.attach(this);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogout.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnLogout.setEnabled(true);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showUserName(String name) {
        tvUserName.setText(name);
    }

    @Override
    public void navigateToAuth() {
        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }
}