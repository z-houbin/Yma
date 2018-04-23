package z.houbin.site.zdown.ui.child;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.KuaiShou;

/**
 * 快手
 */
public class KuaiShouFragment extends BaseFragment {
    private KuaiShou kuaiShou = new KuaiShou();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_kuaishou, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kuaiShou.setLoadListener(this);
    }

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        String url = iEditText.getText().toString();
        if (!TextUtils.isEmpty(url)) {
            kuaiShou.parse(url);
        }
    }

    @Override
    public void onLoadEnd(final BaseModule module) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showInfo(module);
            }
        });
    }
}
