package com.trustedoffer.messapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class FeedbackFragment extends Fragment implements View.OnClickListener {
    private EditText etFeedback;
    private MaterialButton btSend;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_feedback, container, false);
        findId(view);
        btSend.setOnClickListener(this);
        return  view;
    }

    private void findId(View view) {
        etFeedback = view.findViewById(R.id.feedback_edtxt_id);
        btSend = view.findViewById(R.id.send_feedback_bn_id);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_feedback_bn_id:
                send();
                break;
            default:
                break;
        }
    }
    /**
     * Feed Method : Send Using Email
     */
    private void send() {
        String receiver="alaminislam3555@gmail.com";
        String[] recevername=receiver.split(",");
        String subject="Mess App";
        String message=etFeedback.getText().toString();
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,recevername);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,message);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose App For Send"));
    }
}
