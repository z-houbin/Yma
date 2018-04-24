package z.houbin.site.zdown.ui.child;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.listener.LoadCallBack;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.Music.Kuwo.Kuwo;
import z.houbin.site.zdown.module.Music.MusicModule;
import z.houbin.site.zdown.ui.InstagramWebActivity;

/**
 * 酷我
 * https://www.iesdouyin.com/share/video/6546873189920673037/?region=CN&mid=6546873268693912328&titleType=title&utm_source=copy_link&utm_campaign=client_share&utm_medium=android&app=aweme&iid=31077384148&timestamp=1524486143
 */
public class KuwoFragment extends BaseFragment implements LoadCallBack {
    private Kuwo kuwo = new Kuwo();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_kuwo, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kuwo.setLoadListener(this);
        setLabel("酷我");
    }

    @Override
    public void onLoadEnd(final BaseModule module) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showInfo((MusicModule) module);
            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().startsWith("http") && s.length() > 10) {
            iDownload.setText("解析");
        } else {
            iDownload.setText("搜索");
        }
    }

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        String url = iEditText.getText().toString();
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("/n/yqq/album") || url.contains("url.cn")) {
                //专辑
                //kuwo.parseAlbum(input);
            } else if (url.contains("n/yqq/playsquare") | url.contains("n/yqq/playlist") || url.contains("y.qq.com/w")) {
                //歌单
                //music.parsePlayList(input);
            } else {
                //搜索
                kuwo.search(url);
            }
        }
    }

    protected void showInfo(final MusicModule module) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] items = module.getShowList();
        final List<Integer> checkList = new ArrayList<>();
        if (!TextUtils.isEmpty(module.title)) {
            builder.setTitle(module.title);
        }
        builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    if (!checkList.contains(which)) {
                        checkList.add(which);
                    }
                } else {
                    if (checkList.contains(which)) {
                        checkList.remove((Object) which);
                    }
                }
            }
        });
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int[] index = new int[checkList.size()];
                for (int i = 0; i < checkList.size(); i++) {
                    index[i] = checkList.get(i);
                }
                module.download(index);
            }
        });
        builder.setNegativeButton("全部下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                module.downloadAll();
            }
        });
        builder.create().show();
    }
}
