package z.houbin.site.zdown.ui.child;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.listener.LoadCallBack;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.Music.MusicModule;
import z.houbin.site.zdown.module.Music.QQMusic;

/**
 * QQ音乐
 */
public class QQMusicFragment extends BaseFragment implements TextWatcher, View.OnClickListener, LoadCallBack {
    private Handler handler = new Handler();
    private QQMusic music = new QQMusic();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_qqmusic, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iEditText = view.findViewById(R.id.edit);
        iEditText.addTextChangedListener(this);

        iDownload = view.findViewById(R.id.download);
        iDownload.setOnClickListener(this);
        music.setLoadListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download:
                String input = iEditText.getText().toString();
                if (input.contains("/n/yqq/album") || input.contains("url.cn")) {
                    //专辑
                    music.parseAlbum(input);
                } else if (input.contains("n/yqq/playsquare") | input.contains("n/yqq/playlist")||input.contains("y.qq.com/w")) {
                    //歌单
                    music.parsePlayList(input);
                } else {
                    //搜索
                    music.search(input);
                }
                break;
        }
        super.onClick(v);
    }

    @Override
    public void onLoadStart(BaseModule module) {

    }

    @Override
    public void onLoadEnd(BaseModule module) {
        if (module instanceof MusicModule) {
            final MusicModule music = (MusicModule) module;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showMusicList(music);
                }
            });
        }
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

    @Override
    public void onLoadError(BaseModule module, Exception e) {

    }
}
