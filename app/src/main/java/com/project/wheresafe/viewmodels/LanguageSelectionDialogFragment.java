package com.project.wheresafe.viewmodels;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.project.wheresafe.R;

public class LanguageSelectionDialogFragment extends DialogFragment {
    public interface LanguageSelectionListener {
        void onLanguageSelected(String languageCode);
    }
    private LanguageSelectionListener mListener;

    public void setLanguageSelectionListener(LanguageSelectionListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_language_selection, null);

        RadioButton radioButtonEnglish = view.findViewById(R.id.radioButtonEnglish);
        RadioButton radioButtonFrench = view.findViewById(R.id.radioButtonFrench);

        radioButtonEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onLanguageSelected("en");
                dismiss();
            }
        });

        radioButtonFrench.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mListener.onLanguageSelected("fr");
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

}
