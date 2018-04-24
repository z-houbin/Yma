package z.houbin.site.zdown.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import z.houbin.site.zdown.R;
import z.houbin.site.zdown.util.IntentUtil;

/**
 * 下载历史
 */
public class HistoryAct extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private RecyclerView list;
    private GridLayoutManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show);

        list = findViewById(R.id.list);
        manager = new GridLayoutManager(getApplicationContext(), 3);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("下载记录");

        list.setLayoutManager(manager);
        list.addItemDecoration(new SpacesItemDecoration(5));

        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Yma/");
        if (dir.exists()) {
            final List<File> files = getListFiles(dir);

            RecyclerAdapter adapter = new RecyclerAdapter(files);
            adapter.setItemClickListener(this);
            list.setAdapter(adapter);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (files.get(position).isDirectory()) {
                        return 3;
                    }
                    return 1;
                }
            });
        }
    }

    private List<File> getListFiles(File dir) {
        List<File> resFiles = new ArrayList<>();
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                return false;
            }
        });
        //目录排序
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o2.getName().compareTo(o1.getName());
            }
        });
        for (File file : files) {
            if (file.isDirectory()) {
                resFiles.add(file);
                File[] itemFiles = file.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.endsWith(".temp")) {
                            return false;
                        }
                        return true;
                    }
                });
                resFiles.addAll(Arrays.asList(itemFiles));
            }
        }
        return resFiles;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecyclerAdapter adapter = (RecyclerAdapter) list.getAdapter();
        File file = adapter.getItem(position);
        if (file.exists() && !file.isDirectory()) {
            Intent intent = IntentUtil.openFile(file);
            if (intent == null) {
                Toast.makeText(getApplicationContext(), "打开失败，原因：文件已经被移动或者删除", Toast.LENGTH_SHORT).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                }
                startActivity(intent);
            }
        }
    }


    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {

        private List<File> files = new ArrayList<>();
        private List<Integer> dirPosition = new ArrayList<>();
        private AdapterView.OnItemClickListener itemClickListener;

        public RecyclerAdapter(List<File> files) {
            this.files = files;
            for (int i = 0; i < files.size(); i++) {
                if (files.get(i).isDirectory()) {
                    dirPosition.add(i);
                }
            }
        }

        public void setItemClickListener(AdapterView.OnItemClickListener clickListener) {
            this.itemClickListener = clickListener;
        }

        public File getItem(int position) {
            return files.get(position);
        }


        @Override
        public int getItemViewType(int position) {
            if (dirPosition.contains(position)) {
                return 0;
            } else {
                return 1;
            }
        }

        private String getFileType(File file) {
            String fName = file.getName();
            int dotIndex = fName.lastIndexOf(".");
            if (dotIndex < 0) {
                return null;
            }
            /* 获取文件的后缀名*/
            String end = fName.substring(dotIndex + 1, fName.length()).toLowerCase();
            switch (end) {
                case "3gp":
                case "mp4":
                case "avi":
                case "rmvb":
                    return "视频";
                case "mp3":
                case "m4a":
                case "ogg":
                case "wav":
                case "wmv":
                case "ape":
                case "flac":
                    return "音乐";
                case "bmp":
                case "jpg":
                case "png":
                case "gif":
                case "jpeg":
                    return "图片";
                default:
                    return "未知";
            }
        }

        @NonNull
        @Override
        public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
            RecyclerHolder holder = new RecyclerHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(null, holder.itemView, position, 0);
                    }
                }
            });
            if (getItemViewType(position) == 0) {
                holder.title.setText(files.get(position).getName());
                holder.title.setVisibility(View.VISIBLE);
                holder.pic.setVisibility(View.GONE);
                holder.icon.setVisibility(View.GONE);
            } else {
                holder.title.setVisibility(View.GONE);
                holder.pic.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load("file://" + files.get(position)).into(holder.pic);
                String t = getFileType(files.get(position));
                if (TextUtils.isEmpty(t)) {
                    holder.icon.setVisibility(View.GONE);
                } else {
                    holder.icon.setVisibility(View.VISIBLE);
                    if (t.contains("音乐")) {
                        holder.icon.setText(files.get(position).getName());
                        holder.icon.setLines(2);
                        Glide.with(getApplicationContext()).load(R.drawable.ic_music_circle_black_36dp).into(holder.pic);
                    } else {
                        holder.icon.setText(t);
                        holder.icon.setLines(2);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return files.size();
        }
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder {
        public ImageView pic;
        public TextView title;
        private TextView icon;

        public RecyclerHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }

}
