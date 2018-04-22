package z.houbin.site.zdown;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import z.houbin.site.zdown.adapter.MenuAdapter;
import z.houbin.site.zdown.info.BaseInfo;
import z.houbin.site.zdown.listener.LoadCallBack;
import z.houbin.site.zdown.module.BaseModule;
import z.houbin.site.zdown.module.Music.MusicModule;
import z.houbin.site.zdown.ui.InstagramWebActivity;
import z.houbin.site.zdown.ui.ShowAct;
import z.houbin.site.zdown.ui.child.BaseFragment;
import z.houbin.site.zdown.ui.child.DouYinFragment;
import z.houbin.site.zdown.ui.child.InstagramFragment;
import z.houbin.site.zdown.ui.child.KuaiShouFragment;
import z.houbin.site.zdown.ui.child.MeiPaiFragment;
import z.houbin.site.zdown.ui.child.MiaoPaiFragment;
import z.houbin.site.zdown.ui.child.NetEaseFragment;
import z.houbin.site.zdown.ui.child.QQMusicFragment;
import z.houbin.site.zdown.ui.child.QuanMingFragment;
import z.houbin.site.zdown.ui.child.XiguaFragment;
import z.houbin.site.zdown.util.DownloadManager;


public class MainActivity extends AppCompatActivity implements LoadCallBack, DownloadManager.DownloadStatusUpdater, AdapterView.OnItemClickListener {
    private Handler handler = new Handler();
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private BaseFragment[] fragments = new BaseFragment[10];

    private int currentChildFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }

        //实现左侧home图标“菜单”样式与“返回”样式的动画切换(需要在xml中配置相关样式)
        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        DownloadManager.getImpl().addUpdater(this);

        checkPermission();

        ListView menuList = findViewById(R.id.menu);
        menuList.setAdapter(new MenuAdapter(getApplicationContext()));
        menuList.setOnItemClickListener(this);

        fragments[0] = new QQMusicFragment();
        fragments[1] = new NetEaseFragment();
        fragments[2] = new QuanMingFragment();
        fragments[3] = new DouYinFragment();
        fragments[4] = new InstagramFragment();
        fragments[5] = new MeiPaiFragment();
        fragments[6] = new MiaoPaiFragment();
        fragments[7] = new KuaiShouFragment();
        fragments[8] = new XiguaFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                transaction.add(R.id.frame, fragment);
                transaction.hide(fragment);
            }
        }
        transaction.commit();
        setChild(0);
    }

    private void setChild(int child) {
        if (child == currentChildFragment) {
            getFragmentManager().beginTransaction().show(fragments[child]).commit();
            return;
        }
        getFragmentManager().beginTransaction().hide(fragments[currentChildFragment]).commit();
        getFragmentManager().beginTransaction().show(fragments[child]).commit();
        currentChildFragment = child;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawer(Gravity.START);
            } else {
                drawerLayout.openDrawer(Gravity.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            fragments[currentChildFragment].pasteText(intent.getStringExtra(Intent.EXTRA_TEXT));
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
        String input = "";
//        new Kuwo("");
//        if (!TextUtils.isEmpty(input)) {
//           /* if (input.contains("url.cn")) {
//                Toast.makeText(getApplicationContext(),"不支持短链接,请用浏览器打开短链接后复制网址",Toast.LENGTH_LONG).show();
//            } else*/
//            if (input.contains("www.instagram.com")) {
//                Instagram instagram = new Instagram(input);
//                instagram.doInBackground();
//                instagram.setLoadListener(this);
//            } else if (input.contains("www.meipai.com")) {
//                MeiPai meiPai = new MeiPai(input);
//                meiPai.doInBackground();
//                meiPai.setLoadListener(this);
//            } else if (input.contains("www.miaopai.com")) {
//                MiaoPai miaoPai = new MiaoPai(input);
//                miaoPai.doInBackground();
//                miaoPai.setLoadListener(this);
//            } else if (input.contains("douyin.com")) {
//                DouYin douYin = new DouYin(input);
//                douYin.doInBackground();
//                douYin.setLoadListener(this);
//            } else if (input.contains("www.gifshow.com") || input.contains("www.kuaishou.com")) {
//                KuaiShou kuaiShou = new KuaiShou(input);
//                kuaiShou.doInBackground();
//                kuaiShou.setLoadListener(this);
//            } else if (input.contains("365yg.com")) {
//                XiGua xiGua = new XiGua(input);
//                xiGua.doInBackground();
//                xiGua.setLoadListener(this);
//            } else if (input.contains("y.qq.com") || input.contains("url.cn")) {
//                if (input.contains("/n/yqq/album") || input.contains("url.cn")) {
//                    //专辑
//                    QQMusicAlbum qqMusicAlbum = new QQMusicAlbum(input);
//                    qqMusicAlbum.doInBackground();
//                    qqMusicAlbum.setLoadListener(this);
//                } else if (input.contains("n/yqq/playsquare") | input.contains("n/yqq/playlist")) {
//                    //歌单
//                    QQMusicPlayList musicPlayList = new QQMusicPlayList(input);
//                    musicPlayList.doInBackground();
//                    musicPlayList.setLoadListener(this);
//                }
//            } else if (input.contains("www.tiktokv.com")) {
//                TikTok tikTok = new TikTok(input);
//                tikTok.doInBackground();
//                tikTok.setLoadListener(this);
//            }
//            if (input.contains("kg2.qq.com") || input.contains("kg.qq.com")) {
//                QMKg kg = new QMKg(input);
//                kg.downloadAll();
//                kg.setLoadListener(this);
//            } else {
//                Kugou kugou = new Kugou(input);
//                kugou.search();
//                kugou.setLoadListener(this);
////                QQMusic music = new QQMusic(input);
////                music.search();
////                music.setLoadListener(this);
//            }
//        }
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
        System.out.println(task.getFilename() + " / " + task.getSmallFileTotalBytes() + " / " + task.getSmallFileSoFarBytes());
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        drawerLayout.closeDrawer(Gravity.START);
        setChild(position);
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                Intent intent = new Intent(getApplicationContext(), InstagramWebActivity.class);
                startActivity(intent);
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
        }
    }
}
