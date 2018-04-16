package z.houbin.site.zdown;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import z.houbin.site.zdown.info.BaseInfo;
import z.houbin.site.zdown.listener.LoadCallBack;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.DouYin;
import z.houbin.site.zdown.module.Instagram;
import z.houbin.site.zdown.module.KuaiShou;
import z.houbin.site.zdown.module.MeiPai;
import z.houbin.site.zdown.module.MiaoPai;
import z.houbin.site.zdown.module.Music.MusicModule;
import z.houbin.site.zdown.module.Music.QQMusic;
import z.houbin.site.zdown.module.Music.QQMusicPlayList;
import z.houbin.site.zdown.module.Music.QQMusicAlbum;
import z.houbin.site.zdown.module.TikTok;
import z.houbin.site.zdown.module.XiGua;
import z.houbin.site.zdown.ui.ShowAct;
import z.houbin.site.zdown.util.DownloadManager;


public class MainActivity extends AppCompatActivity implements LoadCallBack, DownloadManager.DownloadStatusUpdater {
    private EditText mInput;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileDownloader.setup(getApplicationContext());

        mInput = findViewById(R.id.edit);
        DownloadManager.getImpl().addUpdater(this);

        checkPermission();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            pasteText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        0
                );
            }

        }
    }

    public void download(View view) {
        String input = mInput.getText().toString();
        if (!TextUtils.isEmpty(input)) {
           /* if (input.contains("url.cn")) {
                Toast.makeText(getApplicationContext(),"不支持短链接,请用浏览器打开短链接后复制网址",Toast.LENGTH_LONG).show();
            } else*/
            if (input.contains("www.instagram.com")) {
                Instagram instagram = new Instagram(input);
                instagram.doInBackground();
                instagram.setLoadListener(this);
            } else if (input.contains("www.meipai.com")) {
                MeiPai meiPai = new MeiPai(input);
                meiPai.doInBackground();
                meiPai.setLoadListener(this);
            } else if (input.contains("www.miaopai.com")) {
                MiaoPai miaoPai = new MiaoPai(input);
                miaoPai.doInBackground();
                miaoPai.setLoadListener(this);
            } else if (input.contains("douyin.com")) {
                DouYin douYin = new DouYin(input);
                douYin.doInBackground();
                douYin.setLoadListener(this);
            } else if (input.contains("www.gifshow.com") || input.contains("www.kuaishou.com")) {
                KuaiShou kuaiShou = new KuaiShou(input);
                kuaiShou.doInBackground();
                kuaiShou.setLoadListener(this);
            } else if (input.contains("365yg.com")) {
                XiGua xiGua = new XiGua(input);
                xiGua.doInBackground();
                xiGua.setLoadListener(this);
            } else if (input.contains("y.qq.com") || input.contains("url.cn")) {
                if (input.contains("/n/yqq/album") || input.contains("url.cn")) {
                    //专辑
                    QQMusicAlbum qqMusicAlbum = new QQMusicAlbum(input);
                    qqMusicAlbum.doInBackground();
                    qqMusicAlbum.setLoadListener(this);
                } else if (input.contains("n/yqq/playsquare") | input.contains("n/yqq/playlist")) {
                    //歌单
                    QQMusicPlayList musicPlayList = new QQMusicPlayList(input);
                    musicPlayList.doInBackground();
                    musicPlayList.setLoadListener(this);
                }
            } else if (input.contains("www.tiktokv.com")) {
                TikTok tikTok = new TikTok(input);
                tikTok.doInBackground();
                tikTok.setLoadListener(this);
            } else {
                QQMusic music = new QQMusic(input);
                music.search();
                music.setLoadListener(this);
            }
        }
    }

    @Override
    public void onLoadStart(BaseModule module) {

    }

    @Override
    public void onLoadEnd(BaseModule module) {
        System.out.println(module.toString());

        if (module instanceof MusicModule) {
            final MusicModule music = (MusicModule) module;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showMusicList(music);
                }
            });
        }

        Looper.prepare();
        BaseInfo info = module.getInfo();
        if (info != null) {
            List<String> image = info.image;
            Toast.makeText(getApplicationContext(), "图片:" + image.size(), Toast.LENGTH_SHORT).show();
            for (String path : image) {
                DownloadManager.getImpl().startDownload(path, ".jpg");
            }
            if (!TextUtils.isEmpty(info.video)) {
                Toast.makeText(getApplicationContext(), "视频: 1", Toast.LENGTH_SHORT).show();
                DownloadManager.getImpl().startDownload(info.video, ".mp4");
            }
            if (!TextUtils.isEmpty(info.music)) {
                Toast.makeText(getApplicationContext(), "音乐: 1", Toast.LENGTH_SHORT).show();
                DownloadManager.getImpl().startDownload(info.music, ".mp3");
            }
        }
        Looper.loop();
    }

    private void showMusicList(final MusicModule module) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] items = module.getShowList();
        final List<Integer> checkList = new ArrayList<>();
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
        System.out.println(e.getMessage());
    }

    @Override
    public void blockComplete(BaseDownloadTask task) {

    }

    @Override
    public void complete(BaseDownloadTask task) {
        try {
            String path = task.getPath();
            if (path.endsWith(".jpg") || path.endsWith(".png") | path.endsWith(".gif") || path.endsWith(".webp")) {
                //插入图片
                MediaStore.Images.Media.insertImage(getContentResolver(), path, "title", "description");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(task.getPath()))));
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(task.getPath()).getParentFile())));
        Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void update(BaseDownloadTask task) {
        System.out.println();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().removeUpdater(this);
    }

    public void show(View view) {
        Intent intent = new Intent(getApplicationContext(), ShowAct.class);
        startActivity(intent);
    }

    public void paste(View view) {
        ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = mClipboardManager.getPrimaryClip();
        if (clip.getItemCount() != 0) {
            if (mInput != null) {
                String text = clip.getItemAt(0).getText().toString();
                pasteText(text);
            }
        }
    }

    private void pasteText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Pattern pattern = Pattern.compile("(http[https]?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            mInput.setText(matcher.group());
            System.out.println(matcher.group());
        }
    }

    public void clear(View view) {
        if (mInput != null) {
            mInput.getEditableText().clear();
        }
    }
}
