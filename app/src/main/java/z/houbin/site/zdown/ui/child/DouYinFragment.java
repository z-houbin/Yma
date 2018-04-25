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

import java.util.ArrayList;
import java.util.List;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.info.BaseInfo;
import z.houbin.site.zdown.listener.LoadCallBack;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.DouYin;

/**
 * 抖音
 * https://www.iesdouyin.com/share/video/6546873189920673037/?region=CN&mid=6546873268693912328&titleType=title&utm_source=copy_link&utm_campaign=client_share&utm_medium=android&app=aweme&iid=31077384148&timestamp=1524486143
 */
public class DouYinFragment extends BaseFragment implements LoadCallBack {
    private DouYin douYin = new DouYin();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_douyin, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        douYin.setLoadListener(this);
        setLabel("抖音/Tik Tok");
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

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        String url = iEditText.getText().toString();
        if (!TextUtils.isEmpty(url)) {
            douYin.doInThread(url);
        }
    }

    protected void showInfo(final BaseModule module){
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
        builder.setPositiveButton("下载视频", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                module.download();
            }
        });
        builder.create().show();
    }
}
