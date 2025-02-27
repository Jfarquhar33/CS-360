package com.example.farquharcs360project;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class AddItemFragment extends DialogFragment {
    private EditText editTextItemName, editTextStock;
    private DatabaseHelper dbHelper;
    private OnItemAddedListener callback;

    public interface OnItemAddedListener {
        void onItemAdded();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemAddedListener) {
            callback = (OnItemAddedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnItemAddedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        dbHelper = new DatabaseHelper(getActivity());

        editTextItemName = view.findViewById(R.id.editTextItemName);
        editTextStock = view.findViewById(R.id.editTextStock);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(v -> {
            String itemName = editTextItemName.getText().toString().trim();
            String stockText = editTextStock.getText().toString().trim();

            if (itemName.isEmpty() || stockText.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int stock = Integer.parseInt(stockText);
            dbHelper.addItem(itemName, stock);
            Toast.makeText(getActivity(), "Item added", Toast.LENGTH_SHORT).show();

            if (callback != null) {
                callback.onItemAdded();
            }
            dismiss();
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Add New Item");
        return dialog;
    }
}
