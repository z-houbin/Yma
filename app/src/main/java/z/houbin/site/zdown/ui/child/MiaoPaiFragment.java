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

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.info.BaseInfo;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.MiaoPai;

/**
 * 秒拍
 * http://www.miaopai.com/show/ZJPpAOppvT3snYn0JS1oNw__.htm
 */
public class MiaoPaiFragment extends BaseFragment {
    private MiaoPai miaoPai = new MiaoPai();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_miaopai, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        miaoPai.setLoadListener(this);
    }

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        String url = iEditText.getText().toString();
        if (!TextUtils.isEmpty(url)) {
            miaoPai.parse(url);
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

    protected void showInfo(final BaseModule module) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("成功解析下载地址");
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                module.download();
            }
        });
        builder.create().show();
    }
}
