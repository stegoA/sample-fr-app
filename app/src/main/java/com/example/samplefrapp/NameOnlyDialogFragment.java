package com.example.samplefrapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.callback.NameCallback;
import org.forgerock.android.auth.callback.PasswordCallback;

/**
 * Reference implementation of handing Advice with {@link DialogFragment}
 */
public class NameOnlyDialogFragment extends DialogFragment {

    private MainActivity listener;
    private Node node;

    public static NameOnlyDialogFragment newInstance(Node node) {
        NameOnlyDialogFragment fragment = new NameOnlyDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("NODE", node);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name_only_dialog_fragment, container, false);
        node = (Node) getArguments().getSerializable("NODE");

        TextInputEditText username = view.findViewById(R.id.name);
        Button next = view.findViewById(R.id.next);
        next.setOnClickListener(v -> {
            dismiss();
            node.getCallback(NameCallback.class).setName(username.getText().toString());
            node.next(getContext(), listener);

        });
        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            listener = (MainActivity) context;
        }
    }
}

