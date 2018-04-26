package z.houbin.site.zdown.ui.child;

import android.app.AlertDialog;
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
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.Music.MusicModule;
import z.houbin.site.zdown.module.Music.sony.Sony;

/**
 * 索尼
 */
public class SonyFragment extends BaseFragment {
    private Sony sony = new Sony();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_kuaishou, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sony.setLoadListener(this);
        setLabel("索尼");
    }

    @Override
    public void onDownloadClick(View v) {
        super.onDownloadClick(v);
        final String key = iEditText.getText().toString();
        if (!TextUtils.isEmpty(key)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("选择搜索类型");
            CharSequence[] items = new CharSequence[]{"歌曲", "专辑"};
            builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    switch (which) {
                        case 0:
                            sony.search(key);
                            break;
                        case 1:
                            sony.searchAlbum(key);
                            break;
                    }
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onLoadEnd(final BaseModule module) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showMusicList((MusicModule) module);
            }
        });
    }

    private void showMusicList(final MusicModule module) {
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
