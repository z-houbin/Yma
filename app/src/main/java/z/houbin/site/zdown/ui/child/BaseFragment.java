package z.houbin.site.zdown.ui.child;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.info.BaseInfo;
import z.houbin.site.zdown.listener.LoadCallBack;
import z.houbin.site.zdown.module.BaseModule;

public class BaseFragment extends Fragment implements View.OnClickListener, TextWatcher, LoadCallBack {
    protected Handler handler = new Handler();
    protected EditText iEditText;
    protected Button iDownload;
    protected Button iPaste;
    protected Button iClear;
    protected String label;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iEditText = view.findViewById(R.id.edit);
        iPaste = view.findViewById(R.id.btnPaste);
        iClear = view.findViewById(R.id.btnClear);
        iDownload = view.findViewById(R.id.download);
        if (iEditText != null) {
            iEditText.addTextChangedListener(this);
        }
        if (iClear != null) {
            iClear.setOnClickListener(this);
        }
        if (iPaste != null) {
            iPaste.setOnClickListener(this);
        }
        if (iDownload != null) {
            iDownload.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                clear(v);
                break;
            case R.id.btnPaste:
                paste(v);
                break;
            case R.id.download:
                onDownloadClick(v);
                break;
        }
    }

    protected String getInput(){
        if(iEditText != null){
            return iEditText.getText().toString();
        }else{
            return null;
        }
    }

    public void clear(View v) {
        if (iEditText != null) {
            iEditText.getEditableText().clear();
        }
    }

    public void paste(View view) {
        ClipboardManager mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = mClipboardManager.getPrimaryClip();
        if (clip.getItemCount() != 0 && iEditText != null) {
            if (iEditText.getText() != null) {
                String text = clip.getItemAt(0).getText().toString();
                pasteText(text);
            }
        }
    }

    public void pasteText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Pattern pattern = Pattern.compile("(http[https]?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            iEditText.setText(matcher.group());
            System.out.println(matcher.group());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void onDownloadClick(View v) {

    }

    @Override
    public void onLoadStart(BaseModule module) {

    }

    @Override
    public void onLoadEnd(BaseModule module) {

    }

    @Override
    public void onLoadError(BaseModule module, Exception e) {
        System.out.println(e.getMessage());
    }

    protected void showInfo(final BaseModule module) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        BaseInfo info = module.getInfo();
        StringBuilder str = new StringBuilder();
        str.append("\r\n");
        str.append(info.author);
        str.append("\r\n");
        str.append(info.description);

        if (!TextUtils.isEmpty(module.getInfo().title)) {
            builder.setTitle(module.getInfo().title);
        }
        builder.setMessage(str.toString());
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                module.download();
            }
        });
        builder.create().show();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
