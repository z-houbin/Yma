package z.houbin.site.zdown.ui.child;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.MeiPai;

/**
 * 美拍
 * http://www.meipai.com/media/952173910
 */
public class MeiPaiFragment extends BaseFragment {
    private MeiPai meiPai = new MeiPai();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_meipai, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        meiPai.setLoadListener(this);
        setLabel("美拍");
    }

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        String url = iEditText.getText().toString();
        if (!TextUtils.isEmpty(url)) {
            meiPai.parse(url);
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
