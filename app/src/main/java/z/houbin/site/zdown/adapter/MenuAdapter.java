package z.houbin.site.zdown.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import z.houbin.site.zdown.R;

public class MenuAdapter extends BaseAdapter {
    private Context context;

    private String[] menuItems = new String[]{"QQ音乐\n(歌单,专辑,搜索)", "网易云\n(歌单,专辑,搜索)", "酷我", "全民K歌", "抖音(无水印)", "Instagram", "美拍", "秒拍", "快手"};
    private Integer[] menuIcons = new Integer[]{R.drawable.icon_qqmusic, R.drawable.icon_netease, R.drawable.icon_kuwo, R.drawable.icon_qmkg, R.drawable.icon_douyin, R.drawable.icon_instagram, R.drawable.icon_meipai, R.drawable.icon_miaopai, R.drawable.icon_kuaishou};

    public MenuAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuItems.length;
    }

    @Override
    public String getItem(int position) {
        return menuItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_menu, null);
        ImageView itemIcon = view.findViewById(R.id.menu_icon);
        TextView itemTitle = view.findViewById(R.id.menu_title);
        Glide.with(context).load(menuIcons[position]).into(itemIcon);
        itemTitle.setText(menuItems[position]);
        return view;
    }
}
