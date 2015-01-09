package com.xudongwu.android.yellowpagehelper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PushUriFragment extends Fragment {
    private static final String TAG = "PushUriFragment";

    private static enum Mode {
        CALL_WEBVIEW, CALL_THIRD_APP, SEND_EXPRESS, QUERY_EXPRESS, RECHARGE
    }
    private Mode mMode = Mode.CALL_WEBVIEW;

    private View mCallWebViewLayout;
    private EditText mCallWebViewUri;

    private View mCallThirdAppLayout;
    private EditText mCallThirdAppAction;
    private EditText mCallThirdAppData;
    private EditText mCallThirdAppCategory;

    private EditText mIntentUri;
    private Button mGenerate;
    private Button mCopy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.fragment_push_uri, container, false);
        Spinner spinner = (Spinner) root.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.push_uri_action_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        mMode = Mode.CALL_WEBVIEW;
                        break;
                    case 1:
                        mMode = Mode.CALL_THIRD_APP;
                        break;
                    case 2:
                        mMode = Mode.CALL_THIRD_APP;
                        mCallThirdAppAction.setText("com.miui.yellowpage.action.SEND_EXPRESS");
                        mCallThirdAppCategory.setText("android.intent.category.DEFAULT");
                        break;
                    case 3:
                        mMode = Mode.CALL_THIRD_APP;
                        mCallThirdAppAction.setText("com.miui.yellowppage.express_inquiry");
                        mCallThirdAppCategory.setText("android.intent.category.DEFAULT");
                        break;
                    case 4:
                        mMode = Mode.CALL_THIRD_APP;
                        mCallThirdAppAction.setText("com.miui.yellowppage.recharge");
                        mCallThirdAppCategory.setText("android.intent.category.DEFAULT");
                        break;
                    default:
                        mMode = Mode.CALL_THIRD_APP;
                        break;
                }
                toggleUi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCallWebViewLayout = root.findViewById(R.id.call_webview_layout);
        mCallWebViewUri = (EditText) mCallWebViewLayout.findViewById(R.id.call_webview_uri);

        mCallThirdAppLayout = root.findViewById(R.id.call_thirdapp_layout);
        mCallThirdAppAction = (EditText) mCallThirdAppLayout.findViewById(R.id.call_thirdapp_action);
        mCallThirdAppData = (EditText) mCallThirdAppLayout.findViewById(R.id.call_thirdapp_data);
        mCallThirdAppCategory= (EditText) mCallThirdAppLayout.findViewById(R.id.call_thirdapp_category);

        mGenerate = (Button) root.findViewById(R.id.generate);
        mCopy = (Button) root.findViewById(R.id.copy);
        mIntentUri = (EditText) root.findViewById(R.id.intent_uri);

        mGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateUri();
            }
        });

        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mIntentUri.getText().toString();
                ClipData data = ClipData.newPlainText(text, text);
                ClipboardManager clipboardManager = (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(data);

                Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    private void toggleUi() {
        mCallWebViewLayout.setVisibility(View.GONE);
        mCallThirdAppLayout.setVisibility(View.GONE);

        switch (mMode) {
            case CALL_WEBVIEW:
                mCallWebViewLayout.setVisibility(View.VISIBLE);
                break;
            case CALL_THIRD_APP:
            default:
                mCallThirdAppLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void generateUri() {
        if (mMode == Mode.CALL_WEBVIEW) {
            String url = mCallWebViewUri.getText().toString();
            Intent intent = new Intent();
            if (url.contains("comm.miui.com") || url.contains("huangye.miui.com")) {
                intent.setAction("com.miui.yellowppage.action.LOAD_WEBVIEW");
            } else {
                intent.setAction("com.miui.yellowpage.action.LOAD_OPEN_WEBVIEW");
            }
            intent.putExtra("web_url", url);

            mIntentUri.setText(intent.toUri(0));
        } else if (mMode == Mode.CALL_THIRD_APP) {
            Intent intent = new Intent();
            String action = mCallThirdAppAction.getEditableText().toString();
            String data = mCallThirdAppData.getEditableText().toString();
            String category = mCallThirdAppCategory.getEditableText().toString();
            if (!TextUtils.isEmpty(category)) {
                intent.addCategory(category);
            }
            if (!TextUtils.isEmpty(action)) {
                intent.setAction(action);
            }
            if (!TextUtils.isEmpty(data)) {
                intent.setData(Uri.parse(data));
            }

            mIntentUri.setText(intent.toUri(0));
        }
    }
}
