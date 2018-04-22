package z.houbin.site.zdown.ui.child;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import z.houbin.site.zdown.R;

public class BaseFragment extends Fragment implements View.OnClickListener {
    protected EditText iEditText;
    protected Button iDownload;
    protected Button iPaste;
    protected Button iClear;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iEditText = view.findViewById(R.id.edit);
        iPaste = view.findViewById(R.id.btnPaste);
        iClear = view.findViewById(R.id.btnClear);
        iDownload = view.findViewById(R.id.download);

        if(iClear != null){
            iClear.setOnClickListener(this);
        }
        if(iPaste != null){
            iPaste.setOnClickListener(this);
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
}
