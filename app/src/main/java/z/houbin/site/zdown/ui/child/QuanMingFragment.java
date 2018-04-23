package z.houbin.site.zdown.ui.child;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.info.BaseInfo;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.DouYin;
import z.houbin.site.zdown.module.Music.Qmkg;

/**
 * 全民K歌
 */
public class QuanMingFragment extends BaseFragment {
    private Qmkg qmkg = new Qmkg();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_qmkg, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qmkg.setLoadListener(this);
    }

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        String url = iEditText.getText().toString();
        if (!TextUtils.isEmpty(url)) {
            qmkg.parse(url);
        }
    }

    @Override
    public void onLoadEnd(final BaseModule module) {
        super.onLoadEnd(module);
        handler.post(new Runnable() {
            @Override
            public void run() {
                showInfo(module);
            }
        });
    }
}
