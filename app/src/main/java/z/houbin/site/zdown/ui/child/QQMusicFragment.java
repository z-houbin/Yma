package z.houbin.site.zdown.ui.child;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
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
public class QQMusicFragment extends BaseFragment implements View.OnClickListener, LoadCallBack {
    private QQMusic music = new QQMusic();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_qqmusic, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        music.setLoadListener(this);
        setLabel("QQ音乐");
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
                final String mInput = iEditText.getText().toString();
                if (mInput.contains("url.cn")) {
                    //短网址打开后再解析
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Document document = Jsoup.connect(mInput).get();
                                download(document.location());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    download(mInput);
                }
                break;
        }
        super.onClick(v);
    }

    private void download(String input) {
        if (input.contains("/n/yqq/album") || input.contains("/w/album.html")) {
            //专辑
            music.parseAlbum(input);
        } else if (input.contains("n/yqq/playsquare") | input.contains("n/yqq/playlist") || input.contains("y.qq.com/w")) {
            //歌单
            music.parsePlayList(input);
        } else if (!input.contains("http")) {
            //搜索
            music.search(input);
        } else if (input.contains("n/yqq/song") || input.contains("/playsong.html")) {
            //单曲
            music.parseSong(input);
        }
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
