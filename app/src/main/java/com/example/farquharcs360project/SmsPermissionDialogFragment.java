package com.example.farquharcs360project;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SmsPermissionDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("SMS Notification Permission")
                .setMessage("This app can send SMS alerts when inventory reaches zero. Would you like to enable this feature?")
                .setPositiveButton("Allow", (dialog, which) -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).requestSmsPermission();
                    }
                })
                .setNegativeButton("Deny", (dialog, which) -> {
                    Toast.makeText(getActivity(), "SMS notifications disabled.", Toast.LENGTH_SHORT).show();
                });

        return builder.create();
    }
}
